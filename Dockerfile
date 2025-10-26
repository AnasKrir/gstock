# -------- Build stage --------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Maven wrapper + pom d'abord (cache deps)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -B -DskipTests dependency:go-offline

# Puis le code
COPY src ./src
RUN ./mvnw -q -B -DskipTests package

# -------- Run stage --------
FROM eclipse-temurin:17-jre
WORKDIR /app

# copie le jar produit
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

# Render donne PORT => on le pousse à Spring + profil prod
ENV JAVA_OPTS="-Dserver.port=${PORT} -Dspring.profiles.active=prod"
# Réduit un peu la conso mémoire du JDK sur l’instance Free
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0"

CMD ["sh","-c","java $JAVA_OPTS -jar app.jar"]
