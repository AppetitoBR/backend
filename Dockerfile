FROM openjdk:21-jdk

WORKDIR /app

COPY *.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
