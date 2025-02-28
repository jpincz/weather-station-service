FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/weather-station-service.jar app.jar
ENTRYPOINT ["java", "-Xms64m", "-Xmx256m", "-XX:+UseSerialGC", "-Xss256k", "-jar", "app.jar"]
