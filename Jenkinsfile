pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-northeast-2'
        ECR_REPO = 'dodam-ecr'
        EC2_NAME_TAG = 'dodam-app-ec2'
        DB_SECRETS_MANAGER_NAME="dodam-db-secrets"
        JWT_SECRETS_MANAGER_NAME="dodam-jwt-secrets"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Git Checkout"
                checkout scm
            }
        }

        stage('Get AWS Account ID') {
            steps {
                script {
                    env.AWS_ACCOUNT_ID = sh(
                        script: "aws sts get-caller-identity --query Account --output text",
                        returnStdout: true
                    ).trim()
                    echo "AWS Account ID: ${env.AWS_ACCOUNT_ID}"
                }
            }
        }

        stage('Get Environment Variable on Secrets Manager') {
            steps {
                script {
                    def jwtSecretJson = ''
                    try {
                        jwtSecretJson = sh(
                            script: "aws secretsmanager get-secret-value --secret-id ${env.JWT_SECRETS_MANAGER_NAME} --query SecretString --output text --region ${env.AWS_REGION}",
                            returnStdout: true
                        ).trim()
                    } catch (Exception e) {
                        echo "JWT secrets not found at ${env.JWT_SECRETS_MANAGER_NAME}: ${e.getMessage()}"
                        jwtSecretJson = ''
                    }

                    def secretJson = ''
                    try {
                        secretJson = sh(
                            script: "aws secretsmanager get-secret-value --secret-id ${env.DB_SECRETS_MANAGER_NAME} --query SecretString --output text --region ${env.AWS_REGION}",
                            returnStdout: true
                        ).trim()
                    } catch (Exception e) {
                        error("Failed to read DB secrets from ${env.DB_SECRETS_MANAGER_NAME}: ${e.getMessage()}")
                    }

                    def secret = new groovy.json.JsonSlurper().parseText(secretJson)

                    env.RDS_DB_ADDRESS = secret.RDS_DB_ADDRESS?.toString()
                    env.RDS_DB_PORT = secret.RDS_DB_PORT?.toString()
                    env.RDS_DB_NAME = secret.RDS_DB_NAME?.toString()
                    env.RDS_DB_USER = secret.RDS_DB_USER?.toString()
                    env.RDS_DB_PASSWORD = secret.RDS_DB_PASSWORD?.toString()

                    env.JWT_SECRET_KEY = ''
                    if (jwtSecretJson) {
                        def jwtSecret = new groovy.json.JsonSlurper().parseText(jwtSecretJson)
                        env.JWT_SECRET_KEY = jwtSecret.JWT_SECRET_KEY?.toString() ?: jwtSecret.jwtSecret?.toString() ?: jwtSecret.jwt_secret?.toString() ?: jwtSecret.JWT_SECRET?.toString() ?: ''
                        echo "Loaded JWT secret from ${env.JWT_SECRETS_MANAGER_NAME}"
                    }

                    if (!env.JWT_SECRET_KEY || env.JWT_SECRET_KEY.trim().isEmpty()) {
                        env.JWT_SECRET_KEY = secret.JWT_SECRET_KEY?.toString() ?: secret.jwtSecret?.toString() ?: secret.jwt_secret?.toString() ?: secret.JWT_SECRET?.toString() ?: ''
                        if (env.JWT_SECRET_KEY) {
                            echo "Loaded JWT secret from ${env.DB_SECRETS_MANAGER_NAME}"
                        }
                    }

                    if (!env.JWT_SECRET_KEY || env.JWT_SECRET_KEY.trim().isEmpty()) {
                        echo "No JWT secret found in Secrets Manager — generating an ephemeral secret for this deploy."
                        def random = new byte[64]
                        new java.security.SecureRandom().nextBytes(random)
                        env.JWT_SECRET_KEY = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(random)
                    }
                }
            }
        }

        stage('Determine Next Image Tag') {
            steps {
                script {
                    def stdout = sh(
                        script: "aws ecr list-images --region ${env.AWS_REGION} --repository-name ${env.ECR_REPO} --no-paginate --query 'imageIds[*].imageTag' --output text",
                        returnStdout: true
                    ).trim()

                    echo "ECR raw tags: '${stdout}'"

                    if (!stdout || stdout == 'null' || stdout.trim().isEmpty()) {
                        echo "No existing images found. Starting with v1.0.0"
                        env.IMAGE_TAG = 'v1.0.0'
                    } else {
                        def existingTags = stdout.split("\\s+")
                        def semver = ~/v\d+\.\d+\.\d+/
                        def versions = existingTags.findAll { it ==~ semver }

                        if (versions && versions.size() > 0) {
                            def latest = versions.max { tag ->
                                def parts = tag.replace('v','').split('\\.').collect { it.toInteger() }
                                parts[0] * 10000 + parts[1] * 100 + parts[2]
                            }

                            echo "Latest tag found: ${latest}"

                            def last = latest.replace('v','').split('\\.').collect { it.toInteger() }
                            last[2] += 1
                            env.IMAGE_TAG = "v${last.join('.')}"
                        } else {
                            env.IMAGE_TAG = 'v1.0.0'
                        }
                    }

                    env.DOCKER_IMAGE = "${env.AWS_ACCOUNT_ID}.dkr.ecr.${env.AWS_REGION}.amazonaws.com/${env.ECR_REPO}:${env.IMAGE_TAG}"
                    echo "Next Docker Image Tag: ${env.IMAGE_TAG}"
                }
            }
        }


        stage('Build Spring Boot') {
            steps {
                echo "Building Spring Boot App with Gradle"
                script {
                    sh '''
                    chmod +x gradlew
                    export JAVA_HOME=/usr/lib/jvm/java-17-amazon-corretto.x86_64
                    export PATH=$JAVA_HOME/bin:$PATH
                    ./gradlew build --build-cache -x test -Dorg.gradle.java.home=$JAVA_HOME
                    '''
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    echo "Building and Pushing Docker Image: ${env.DOCKER_IMAGE}"
                    sh """
                        docker build -t ${env.DOCKER_IMAGE} .
                        aws ecr get-login-password --region ${env.AWS_REGION} \
                            | docker login --username AWS --password-stdin ${env.AWS_ACCOUNT_ID}.dkr.ecr.${env.AWS_REGION}.amazonaws.com
                        docker push ${env.DOCKER_IMAGE}
                    """
                }
            }
        }

        stage('Deploy to EC2 via SSM') {
            steps {
                script {
                    echo "1. Sending Deployment Command to EC2 instances tagged with Name=${env.EC2_NAME_TAG}"

                    def commands = [
                        "aws ecr get-login-password --region ${env.AWS_REGION} | docker login --username AWS --password-stdin ${env.AWS_ACCOUNT_ID}.dkr.ecr.${env.AWS_REGION}.amazonaws.com",
                        "docker pull ${env.DOCKER_IMAGE}",
                        "docker stop dodam-cnt || true",
                        "docker rm dodam-cnt || true",
                        "cat > /tmp/app_env <<'ENV'\nSPRING_DATASOURCE_URL=jdbc:mysql://${env.RDS_DB_ADDRESS}:${env.RDS_DB_PORT}/${env.RDS_DB_NAME}?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8\nSPRING_DATASOURCE_USERNAME=${env.RDS_DB_USER}\nSPRING_DATASOURCE_PASSWORD=${env.RDS_DB_PASSWORD}\nSPRING_PROFILES_ACTIVE=prod\nJWT_SECRET_KEY=${env.JWT_SECRET_KEY}\nJWT_SECRETKEY=${env.JWT_SECRET_KEY}\nJWT_SECRET=${env.JWT_SECRET_KEY}\nSPRING_APPLICATION_JSON={\"jwt\":{\"secretKey\":\"${env.JWT_SECRET_KEY}\"}}\nENV",
                        "chmod 600 /tmp/app_env",
                        "docker run -d -p 8080:8080 --name dodam-cnt --env-file /tmp/app_env ${env.DOCKER_IMAGE}",
                        "sh -c 'rm -f /tmp/app_env'" 
                    ]

                    def esc = { s ->
                        if (s == null) return ""
                        return s.replace('\\','\\\\')
                                .replace('"','\\"')
                                .replace('\r','\\r')
                                .replace('\n','\\n')
                    }
                    def commandsJsonArray = '[' + commands.collect { '"' + esc(it) + '"' }.join(',') + ']'
                    def paramsJson = '{"commands":' + commandsJsonArray + '}'

                    def tmpFile = "/tmp/ssm_params_${env.BUILD_ID ?: env.BUILD_NUMBER ?: 'tmp'}.json"
                    sh(script: "cat > ${tmpFile} <<'JSON'\n${paramsJson}\nJSON")

                    def commandOutput = sh(
                        script: """
                            aws ssm send-command \\
                                --targets 'Key=tag:Name,Values=${env.EC2_NAME_TAG}' \\
                                --document-name 'AWS-RunShellScript' \\
                                --comment 'Deploy latest Docker image: ${env.DOCKER_IMAGE}' \\
                                --parameters file://${tmpFile} \\
                                --region ${env.AWS_REGION}
                        """,
                        returnStdout: true
                    ).trim()

                    def commandId = new groovy.json.JsonSlurper().parseText(commandOutput).Command.CommandId
                    env.SSM_COMMAND_ID = commandId
                    echo "SSM Command ID: ${env.SSM_COMMAND_ID}"

                    sh(script: "rm -f ${tmpFile} || true")

                    echo "2. Polling SSM Command Status for ID: ${env.SSM_COMMAND_ID}"
                    
                    retry(10) { 
                        sleep(time: 10, unit: 'SECONDS')

                        def statusOutput = sh(
                            script: "aws ssm list-commands --command-id ${env.SSM_COMMAND_ID} --query 'Commands[0].Status' --output text --region ${env.AWS_REGION}",
                            returnStdout: true
                        ).trim()

                        echo "Current Status: ${statusOutput}"

                        if (statusOutput == 'Success') {
                            echo "Deployment Successful on all targets!"
                        } else if (statusOutput == 'Failed' || statusOutput == 'Cancelled' || statusOutput == 'TimedOut') {
                            error("Deployment FAILED. Final Status: ${statusOutput}")
                        } else {
                            echo "Deployment still in progress. Retrying..."
                            error("Command not yet completed (Status: ${statusOutput})")
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Build, Push, and Deploy completed: ${env.DOCKER_IMAGE}"
        }
        failure {
            echo "Pipeline failed!"
        }
    }
}