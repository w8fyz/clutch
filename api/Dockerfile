FROM gradle:8.10-jdk17-alpine AS build
WORKDIR /app

COPY gradle/ gradle/
COPY gradlew settings.gradle build.gradle ./

COPY src ./src
COPY libs ./libs

RUN gradle shadowJar --no-daemon -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /opt/app

RUN addgroup --system javauser && adduser -S -s /usr/sbin/nologin -G javauser javauser

COPY --from=build /app/build/libs/*-all.jar app.jar

RUN chown -R javauser:javauser .
USER javauser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
