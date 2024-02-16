FROM openjdk:17-oracle
VOLUME /tmp
EXPOSE 8888
COPY target/homeWorkDiplom-0.0.1-SNAPSHOT.jar homeWorkDiplom.jar
ADD src/main/resources/application.properties src/main/resources/application.properties
ENTRYPOINT ["java", "-jar", "/homeWorkDiplom.jar"]