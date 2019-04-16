FROM java:8-jdk-alpine

ENV KAFKA_SERVER localhost \
    KAFKA_TOPIC topic-test \
    KAFKA_COUNT_MSG 10

WORKDIR /usr/app

COPY target/consumer-0.0.1-SNAPSHOT.jar /usr/app/consumer-0.0.1-SNAPSHOT.jar

RUN sh -c 'touch consumer-0.0.1-SNAPSHOT.jar'

ENTRYPOINT ["java", "-Dcom.sun.jndi.ldap.object.disableEndpointIdentification=true", "-jar", "/usr/app/consumer-0.0.1-SNAPSHOT.jar"]
