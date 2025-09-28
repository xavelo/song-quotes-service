FROM openjdk:21
WORKDIR /app
COPY application/target/*.jar /app/myapp.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
