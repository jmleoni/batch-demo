FROM maven:3.6-jdk-11 as backbuilder
COPY . /app
WORKDIR /app

# Build JAR
RUN mvn -B -V clean package -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -Dpackaging=jar

FROM adoptopenjdk/openjdk11:alpine-jre

COPY --from=backbuilder ./app/target/batch-demo-0.0.1-SNAPSHOT.jar /app/batch-demo.jar

CMD ["java", "-jar", "/app/batch-demo.jar"]

