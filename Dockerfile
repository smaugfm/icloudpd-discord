FROM eclipse-temurin:17 as build
ARG APP=/app
RUN mkdir -p "$APP"
WORKDIR $APP
COPY build.gradle.kts settings.gradle.kts gradlew $APP
COPY gradle $APP/gradle
RUN ./gradlew build
COPY src $APP/src
RUN ./gradlew shadowJar

FROM eclipse-temurin:17
ARG APP=/app
RUN mkdir -p "$APP"
WORKDIR $APP
COPY --from=build "$APP/build/libs/icloudpd-discord.jar" .
CMD java -jar /app/icloudpd-discord.jar
