FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Metadados
LABEL maintainer="Celso Ladislau Junior <celsojr.ladislau@gmail.com>"
LABEL description="Oncovit Sankhya Med-Sync API"

# Copiar o executavel JAR e os scripts de banco
COPY oncovit-sankhya-med-sync/target/oncovit-sankhya-med-sync-1.0.0.jar app.jar
COPY database/ ./database/

# Porta HTTP padrao da aplicacao Spring Boot
EXPOSE 8080

# Configuracoes de runtime JVM e fuso horario
ENV JAVA_OPTS="-Xms128m -Xmx384m"
ENV TZ="America/Sao_Paulo"

# Healthcheck nativo para Portainer / Docker
HEALTHCHECK --interval=30s --timeout=5s --start-period=25s --retries=3 \
  CMD wget -q -O - http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

# Comando de inicializacao
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
