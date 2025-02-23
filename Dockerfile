FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/weather-station-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
