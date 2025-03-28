# Usando a imagem oficial do OpenJDK 21
FROM openjdk:21-jdk-slim

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo JAR para dentro do container
COPY api-cardapio-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta que sua API usa (mude se necessário)
EXPOSE 8080

# Comando para rodar a API
CMD ["java", "-jar", "app.jar"]
