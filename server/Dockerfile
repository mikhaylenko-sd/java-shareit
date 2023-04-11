FROM amazoncorretto:11
#ENV TZ=Europe/Moscow
COPY target/*.jar shareit-server.jar
ENTRYPOINT ["java", "-jar", "shareit-server.jar"]
