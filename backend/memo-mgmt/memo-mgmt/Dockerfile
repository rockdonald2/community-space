FROM openjdk:17-jdk-alpine

MAINTAINER zsolt.lukacs

ARG JAR_FILE=memo-mgmt-0.0.1-SNAPSHOT.jar
COPY build/libs/$JAR_FILE /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]