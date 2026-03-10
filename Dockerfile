# ---------- build stage ----------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# install Gradle wrapper
COPY build.gradle ./
COPY src ./src

# generate a wrapper so we don't need Gradle installed in the image
RUN apt-get update && apt-get install -y --no-install-recommends unzip curl \
    && curl -fsSL https://services.gradle.org/distributions/gradle-7.6-bin.zip -o gradle.zip \
    && unzip -q gradle.zip \
    && ./gradle-7.6/bin/gradle shadowJar --no-daemon \
    && rm -rf gradle.zip gradle-7.6

# ---------- runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/build/libs/EJ-Student-System-1.0-all.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
