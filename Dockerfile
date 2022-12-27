# base on java 17 image
FROM openjdk:17-jdk-alpine

# copy the jar file to the container
COPY build/libs/RedBot-all.jar app/RedBot-all.jar

# run the jar file
ENTRYPOINT ["java","-jar","/app/RedBot-all.jar"]