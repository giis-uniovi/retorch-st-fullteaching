#!/bin/sh

JACOCO_OPTS=""
if [ "${JACOCO_ENABLED:-false}" = "true" ]; then
    JACOCO_OPTS="-javaagent:/jacoco-agent.jar=output=tcpserver,port=6300,address=localhost"
fi

java -Djava.security.egd=file:/dev/./urandom \
     -Dspring.profiles.active=container \
     $JACOCO_OPTS \
     -jar /app.jar
