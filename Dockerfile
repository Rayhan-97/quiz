FROM arm64v8/openjdk:17-ea-16-jdk

VOLUME /tmp

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]