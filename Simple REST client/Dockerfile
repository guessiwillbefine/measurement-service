FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY . .
RUN ./mvnw dependency:resolve
CMD ["./mvnw", "spring-boot:run"]