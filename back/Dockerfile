FROM cgr.dev/chainguard/wolfi-base

# Install necessary packages
RUN apk update && apk add openjdk-20 maven~3.9 openjdk-20-default-jvm

# Set working directory
WORKDIR /app

# Set Java Home environment variable
ENV JAVA_HOME=/usr/lib/jvm/java-20-openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

# Copy project files
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

# Ensure mvnw has execute permissions
RUN chmod +x mvnw

# Build the application
RUN ./mvnw package

# Confirm the target directory contains the JAR file
RUN ls target/

# Switch to a non-root user
USER nonroot

# Re-define JAVA_HOME and PATH for nonroot user
ENV JAVA_HOME=/usr/lib/jvm/java-20-openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

# Define entrypoint
ENTRYPOINT ["java", "-jar", "/app/target/jwtAcces-0.0.1-SNAPSHOT.jar"]
