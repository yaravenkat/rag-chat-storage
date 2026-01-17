
# RAG Chat Storage Microservice

Spring Boot 3.x + Java 17 microservice for storing RAG chatbot conversations.

## Features
- Chat session management
- Message storage
- API Key security
- Swagger UI
- Dockerized

## Run Locally
```bash
mvn clean package
docker-compose up --build
```

Swagger:
http://localhost:8080/swagger-ui.html

API requires header:
X-API-KEY
