FROM cgr.dev/chainguard/wolfi-base

RUN apk update && apk add openjdk-17 maven~3.9 openjdk-17-default-jvm

WORKDIR /app
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src
RUN ./mvnw package

USER nonroot

COPY /app/target/spring-petclinic-3.1.0-SNAPSHOT.jar /

WORKDIR /

ENTRYPOINT ["java", "-jar", "/jwtAcces.jar"]
