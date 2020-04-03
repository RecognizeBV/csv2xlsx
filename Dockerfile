FROM openjdk:8-jdk-alpine AS jarbuild

RUN mkdir /app
WORKDIR /app
COPY . .
RUN ./gradlew shadowJar

FROM openjdk:8-jdk-alpine
RUN mkdir /app
WORKDIR /app
COPY --from=jarbuild /app/build/libs/ /app

CMD ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/app/app.jar"]
