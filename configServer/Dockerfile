# --- Etapa 1: Build ---
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiar archivos del proyecto
COPY pom.xml .
COPY src ./src

# Construir la aplicación (salida: JAR)
RUN mvn clean package -DskipTests

# --- Etapa 2: Runtime ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiar solo el JAR generado desde la etapa anterior
COPY --from=builder /app/target/configServer-0.0.1-SNAPSHOT.jar app.jar

# Exponer puerto
EXPOSE 8888

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]