pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-northeast-2'
        ECR_REPO = 'dodam-ecr'
        EC2_NAME_TAG = 'dodam-app-ec2'
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
                        def semver = ~/v\\d+\\.\\d+\\.\\d+/
                        def versions = existingTags.findAll { it ==~ semver }

                        if (versions && versions.size() > 0) {
                            versions.sort { a, b ->
                                def aParts = a.replace('v','').split('\\.').collect { it.toInteger() }
                                def bParts = b.replace('v','').split('\\.').collect { it.toInteger() }
                                for (int i=0; i<3; i++) {
                                    def diff = bParts[i] - aParts[i]
                                    if (diff != 0) return diff
                                }
                                return 0
                            }
                            def last = versions[0].replace('v','').split('\\.').collect { it.toInteger() }
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
                    sh "chmod +x gradlew"
                    sh "./gradlew clean build -x test"
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
                    echo "Deploying ${env.DOCKER_IMAGE} to EC2 instances tagged with Name=${env.EC2_NAME_TAG}"
                    sh """
                        aws ssm send-command \
                            --targets "Key=tag:Name,Values=${env.EC2_NAME_TAG}" \
                            --document-name "AWS-RunShellScript" \
                            --comment "Deploy latest Docker image" \
                            --parameters 'commands=[
                                "docker pull ${env.DOCKER_IMAGE}",
                                "docker stop dodam-app || true",
                                "docker rm dodam-app || true",
                                "docker run -d -p 8080:8080 --name dodam-app ${env.DOCKER_IMAGE}"
                            ]' \
                            --region ${env.AWS_REGION}
                    """
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
