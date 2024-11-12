FROM openjdk:17-jdk
WORKDIR /app
COPY ./build/libs/AuthTemplete-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080