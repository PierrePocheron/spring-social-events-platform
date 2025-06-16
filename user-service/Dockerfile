# 🔹 Étape 1 : Build l'application avec Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 🔹 Étape 2 : Image légère avec JRE uniquement
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/user-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
