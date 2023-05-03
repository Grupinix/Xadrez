# Build the jar
FROM gradle:7-jdk17 AS xadrez-spring-builder

WORKDIR /usr/src/spring

COPY ./backend/build.gradle ./backend/settings.gradle /usr/src/spring/

RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

COPY ./backend /usr/src/spring/

RUN gradle clean build -x test --no-daemon


# Run the application
FROM openjdk:17-jdk-alpine AS xadrez-spring

LABEL maintainer="yurinogueira@id.uff.br"
LABEL vendor="Grupinix"

RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

ARG JAR_FILE=/usr/src/spring/build/libs/*SNAPSHOT.jar

COPY --from=xadrez-spring-builder ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
