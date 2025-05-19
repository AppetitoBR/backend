# Usa a imagem oficial do OpenJDK como builder
FROM openjdk:21-jdk AS builder

# Define o diretório de trabalho
WORKDIR /app

# Copia o código-fonte para dentro do contêiner
COPY . .

# Dá permissão executável para o mvnw
RUN chmod +x mvnw

# Compila a aplicação usando Maven
RUN ./mvnw clean package -DskipTests

# Segunda etapa: imagem menor apenas com o JAR final
FROM openjdk:21-jdk

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR gerado pela fase anterior
COPY api-cardapio-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta da aplicação
EXPOSE 8080

# Comando para rodar a aplicação
CMD ["java", "-jar", "app.jar"]

