FROM openjdk:18-slim
# application jar file defined within pom.xml
ARG JAR_FILE

COPY ${JAR_FILE} app.jar

# Execute the application
ENTRYPOINT ["java" , "-jar", "/app.jar"]