FROM maven:3-jdk-8 AS builder

LABEL MAINTAINER="4ra1n"

WORKDIR /app

COPY . .
COPY ./settings.xml /root/.m2/settings.xml

RUN cd /app && mvn -U clean package -Dmaven.test.skip=true

FROM openjdk:8-jre

ENV JAR_OBF_VERSION=0.1.0

LABEL MAINTAINER="4ra1n"

COPY --from=builder /app/target/jar-obfuscator-$JAR_OBF_VERSION-jar-with-dependencies.jar /jar-obfuscator.jar

CMD ["echo","build success file location: /jar-obfuscator.jar"]