FROM cgr.dev/chainguard/wolfi-base as build

RUN apk update && apk add openjdk-20 maven~3.9 openjdk-20-default-jvm

WORKDIR /app
ENV JAVA_HOME=/usr/lib/jvm/java-20-openjdk

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

# Ensure mvnw has execute permissions
RUN chmod +x mvnw

USER root

RUN ./mvnw package

FROM cgr.dev/chainguard/wolfi-base
RUN apk update && apk add openjdk-20-default-jvm

# Some Java apps may require extra locale files
# RUN apk add glibc-locale-en

USER nonroot

COPY --from=build /app/target/jwtAcces-0.0.1-SNAPSHOT.jar /

#ENTRYPOINT ["java", "-jar", "jwtAcces-0.0.1-SNAPSHOT.jar"]
ENTRYPOINT ["sleep", "infinity"]
