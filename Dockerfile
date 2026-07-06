# --- Etapa de build ---
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copiamos el wrapper y el pom primero para aprovechar la caché de dependencias
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

# --- Etapa de runtime ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Usuario no root
RUN useradd -r -u 1001 appuser
COPY --from=build /app/target/*.jar app.jar
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
