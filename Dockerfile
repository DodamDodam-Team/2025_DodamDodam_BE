FROM gradle:8.14.3-jdk17-alpine AS builder

WORKDIR /app

COPY build.gradle settings.gradle gradle.properties ./
RUN gradle clean build -x test --no-daemon || true

COPY . .

RUN gradle clean build -x test --no-daemon

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar ./app.jar

RUN apk add --no-cache curl

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]