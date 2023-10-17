FROM ubuntu:22.04 AS build

COPY . .

RUN apt update && apt install openjdk-17-jdk maven -y

RUN mvn clean install

RUN cp /target/todolist-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]

EXPOSE 8080