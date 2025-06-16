FROM openjdk:11
EXPOSE 9090
ADD target/CloudStorage-0.0.1-SNAPSHOT.jar CloudStorage.jar
ENTRYPOINT ["java", "-jar", "CloudStorage.jar"]