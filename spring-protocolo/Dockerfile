FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /app

# Copia apenas o pom primeiro (cache de dependências)
COPY pom.xml .

RUN mvn dependency:go-offline

# código será montado via volume
EXPOSE 9003

CMD ["mvn", "spring-boot:run"]