# ==========================================================
# Dockerfile multi-estagio: serve dev e producao.
#
#   dev  -> mvn spring-boot:run com o codigo montado por volume
#   prod -> jar empacotado sobre JRE (estagio final = padrao)
#
# O estagio final e' o "prod", entao um `docker build` sem
# --target continua produzindo a imagem de producao.
# ==========================================================

# ----------------------------------------------------------
# deps: cache das dependencias maven (compartilhado)
# ----------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21 AS deps
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B


# ----------------------------------------------------------
# dev: hot reload. O codigo vem de bind mount (compose).
# ----------------------------------------------------------
FROM deps AS dev
WORKDIR /app

EXPOSE 8080

CMD ["mvn", "spring-boot:run"]


# ----------------------------------------------------------
# build: empacota o jar
# ----------------------------------------------------------
FROM deps AS build
WORKDIR /app

COPY src ./src
RUN mvn clean package -DskipTests


# ----------------------------------------------------------
# prod: runtime enxuto (estagio final)
# ----------------------------------------------------------
FROM eclipse-temurin:21-jre-jammy AS prod
WORKDIR /app

# A imagem base ja traz curl, usado pelo healthcheck do compose.
# Nao instalamos nada aqui de proposito: um apt-get durante o build
# transformaria qualquer instabilidade de rede em falha de deploy.

COPY --from=build /app/target/*.jar app.jar

# A aplicacao escuta em 8080 (o compose e' quem mapeia a porta externa)
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java","-Xms128m","-Xmx384m","-XX:+UseG1GC","-XX:+UseContainerSupport","-jar","app.jar"]
