FROM openjdk:20
COPY ./build/libs/aeris-coding-assignment-0.0.1-SNAPSHOT.jar /tmp
COPY ./build/resources/main /tmp
WORKDIR /tmp
ENTRYPOINT ["java","-jar","aeris-coding-assignment-0.0.1-SNAPSHOT.jar"]
