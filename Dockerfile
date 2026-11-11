# Build the Java 17 WAR
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY BBankMangement/bankmanagementsystem/pom.xml ./pom.xml
RUN mvn -B -q dependency:go-offline
COPY BBankMangement/bankmanagementsystem/src ./src
RUN mvn -B clean package -DskipTests

# Run on Jetty 11 (Jakarta Servlet 5 compatible)
FROM jetty:11-jre17
COPY --from=build /workspace/target/bankmanagementsystem.war /var/lib/jetty/webapps/ROOT.war
EXPOSE 8080
