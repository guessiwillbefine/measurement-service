FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw dependency:resolve
CMD ["./mvnw", "spring-boot:run"]