FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradlew .

RUN ./gradlew --no-daemon dependencies

COPY src src
COPY config config

RUN ./gradlew --no-daemon build

ENV JAVA_OPTS="-Xmx512M -Xms512M"
EXPOSE 7070

CMD ./build/install/app/bin/app