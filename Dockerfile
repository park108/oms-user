# Image for OMS
FROM amazoncorretto:17

# Build package path
COPY build/libs/*SNAPSHOT.jar app.jar

# Add Author info
LABEL maintainer="park108@gmail.com"

# Service port
EXPOSE 8082

# Set entry point
ENTRYPOINT ["java"
    , "-Xmx400M"
    , "-Djava.security.egd=file:/dev/./urandom"
    , "-jar"
    , "/app.jar"
    , "--spring.profiles.active=docker"
]