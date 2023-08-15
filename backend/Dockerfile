#FROM arm64v8/openjdk:17-ea-16-jdk # for local
FROM eclipse-temurin:17

VOLUME /tmp

COPY build/libs/backend*SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]