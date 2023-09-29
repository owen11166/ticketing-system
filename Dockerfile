FROM maven:3.9.4-eclipse-temurin-17-alpine as builder
LABEL maintainer=ParinLai
WORKDIR /usr/src/app
COPY . .
RUN mvn package

FROM openjdk:17-alpine
WORKDIR /app
RUN apk update && apk upgrade
COPY --from=builder /usr/src/app/ .

ENTRYPOINT ["java","-jar","./target/ticketing-system-0.0.1-SNAPSHOT.jar"]