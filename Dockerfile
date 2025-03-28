# Usa a imagem oficial do OpenJDK
FROM openjdk:21-jdk

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR para dentro do contêiner
COPY api-cardapio-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta do serviço
EXPOSE 8080

# Comando para rodar a aplicação
CMD ["java", "-jar", "app.jar"]
