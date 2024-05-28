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
ARG TARGETARCH
WORKDIR /
ADD --chmod=755 https://github.com/icloud-photos-downloader/icloud_photos_downloader/releases/download/v1.19.1/icloudpd-1.19.1-linux-$TARGETARCH /bin/icloudpd 
COPY --from=build "$APP/build/libs/icloudpd-discord.jar" .
CMD java -jar /icloudpd-discord.jar
