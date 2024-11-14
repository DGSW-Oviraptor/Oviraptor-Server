FROM openjdk:17-jdk

COPY ./build/libs/AuthTemplate-0.0.1-SNAPSHOT.jar app.jar

ENV TZ=Asia/Seoul

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080