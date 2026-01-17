# ----------- Build Stage -----------
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace

# 1. Copy pom.xml
COPY pom.xml .

# 2. Optimized dependency download
# go-offline is more thorough than dependency:resolve
RUN mvn dependency:go-offline -B

# 3. Copy source and build
COPY src src
RUN mvn clean package -DskipTests

# 4. Extract layers (The "Magic" for speed)
RUN java -Djarmode=layertools -jar target/*.jar extract

# ----------- Runtime Stage -----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the extracted layers from the build stage
# These layers are cached separately by Docker
COPY --from=build /workspace/dependencies/ ./
COPY --from=build /workspace/spring-boot-loader/ ./
COPY --from=build /workspace/snapshot-dependencies/ ./
COPY --from=build /workspace/application/ ./

# Using a single wait-for if possible, or leveraging Docker Compose healthchecks
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh

EXPOSE 8080

ENTRYPOINT ["/wait-for.sh", "postgres:5432", "--", "/wait-for.sh", "redis:6379", "--", "java", "org.springframework.boot.loader.launch.JarLauncher"]