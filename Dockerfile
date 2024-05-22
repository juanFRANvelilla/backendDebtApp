FROM cgr.dev/chainguard/wolfi-base

RUN apk update && apk add openjdk-20 maven~3.9 openjdk-20-default-jvm

WORKDIR /app
ENV JAVA_HOME=/usr/lib/jvm/java-20-openjdk

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src
RUN ./mvnw package

USER nonroot

COPY /target/jwtAcces-0.0.1-SNAPSHOT.jar /

WORKDIR /

ENTRYPOINT ["java", "-jar", "/jwtAcces-0.0.1-SNAPSHOT.jar"]
