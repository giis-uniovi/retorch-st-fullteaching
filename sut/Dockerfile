# Stage 1: Build Angular frontend
FROM node:22-alpine AS frontend-build
WORKDIR /app
COPY src/main/angular/package.json ./
RUN npm install --legacy-peer-deps
COPY src/main/angular/ ./
RUN node_modules/.bin/ng build

# Stage 2: Build Spring Boot WAR
FROM maven:3.9-eclipse-temurin-21 AS backend-build
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline -B
RUN mvn dependency:copy -B \
    -Dartifact=org.jacoco:org.jacoco.agent:0.8.13:jar:runtime \
    -DoutputDirectory=/app/jacoco \
    && mvn dependency:copy -B \
    -Dartifact=org.jacoco:org.jacoco.cli:0.8.13:jar:nodeps \
    -DoutputDirectory=/app/jacoco
COPY src/ ./src/
# Replace pre-built static assets with freshly built Angular output
COPY --from=frontend-build /app/dist/ ./src/main/resources/static/
RUN mvn package -DskipTests -B

# Stage 3: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=backend-build /app/target/full-teaching-1.0.0.war /app.jar
COPY --from=backend-build /app/jacoco/org.jacoco.agent-0.8.13-runtime.jar /jacoco-agent.jar
COPY --from=backend-build /app/jacoco/org.jacoco.cli-0.8.13-nodeps.jar /org.jacoco.cli-0.8.13-nodeps.jar
COPY src/main/docker/init.sh /init.sh
COPY src/main/docker/wait.sh /wait.sh
RUN sed -i 's/\r//' /init.sh /wait.sh \
    && chmod +x /init.sh /wait.sh \
    && apk add --no-cache netcat-openbsd
EXPOSE 5000
CMD ["/bin/sh", "-c", "/wait.sh && /init.sh"]
