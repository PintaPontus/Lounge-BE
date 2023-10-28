FROM openjdk:21
LABEL authors="pintapontus"
WORKDIR app
COPY target/lounge.jar .
ENTRYPOINT ["java", "-jar", "lounge.jar"]