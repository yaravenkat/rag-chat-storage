
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
X-API-KEY : **super-secret-api-key**


### Application URLs

- http://localhost:8080/swagger-ui/index.html#/
- http://localhost:8080/actuator/health
- http://localhost:8080/actuator/info
- http://localhost:8080/swagger-ui/index.html
- http://localhost:8080/v3/api-docs


 DB healthcheck or direct connect 
=====================================
http://localhost:8081/ 


Adminer login

In Adminer (or any DB client):
================================
System	    PostgreSQL
Server	    postgres (the service name)
Username	postgres
Password	postgres
Database	ragchat (the database name)


<img width="419" height="264" alt="image" src="https://github.com/user-attachments/assets/a0241abc-106e-4b6a-ac60-0b1151684f16" />
<img width="769" height="377" alt="image" src="https://github.com/user-attachments/assets/3082d0fc-6296-4612-be1e-b9edaaa410af" />


Once you click the Authorize button  you can see the pop up below like this you enter the secret key : super-secret-api-key

<img width="386" height="154" alt="image" src="https://github.com/user-attachments/assets/6cfcae8c-e562-4bab-87e1-bb8c20b800f1" />


<img width="363" height="143" alt="image" src="https://github.com/user-attachments/assets/e801cc3c-1146-4a84-b784-0217050db77f" />
​

​<img width="361" height="146" alt="image" src="https://github.com/user-attachments/assets/c37634ea-0de6-4148-9176-e136ed7a3599" />

List Sessions for a User
========================
curl -X GET "http://localhost:8080/api/sessions?userId=venkat" ^
  -H "X-API-KEY:super-secret-api-key" ^
  -H "Accept:application/json"


Get a Single Session
====================
curl -X GET "http://localhost:8080/api/sessions/session1" ^
  -H "X-API-KEY:super-secret-api-key" ^
  -H "Accept:application/json"


Create a Chat Session
======================


curl -X POST http://localhost:8080/api/sessions ^
  -H "Content-Type: application/json" ^
  -H "X-API-KEY: super-secret-api-key" ^
  -d "{\"userId\":\"venkat\",\"sessionName\":\"My First Session\"}"


Rename a Session
=================

curl -X PATCH "http://localhost:8080/api/sessions/session1/rename" ^
-H "Content-Type:application/json" ^
-H "X-API-KEY:super-secret-api-key" ^
-d "{\"sessionName\":\"Updated Session Name\"}"

Set Favorite
=============
curl -X PATCH "http://localhost:8080/api/sessions/session1/favorite" ^
-H "Content-Type:application/json" ^
-H "X-API-KEY:super-secret-api-key" ^
-d "{\"favorite\":true}"

Add a Message to a Session
==========================
curl -X POST "http://localhost:8080/api/sessions/session1/messages" ^
-H "Content-Type:application/json" ^
-H "X-API-KEY:super-secret-api-key" ^
-d "{\"sender\":\"USER\",\"content\":\"Hello AI\",\"context\":\"optional context text\"}"

Invalidate Session Summary Cache for User
==========================================
curl -X POST "http://localhost:8080/api/sessions/users/venkat/invalidate-summary" ^
-H "X-API-KEY:super-secret-api-key"

Paginaion 
=============

curl -X GET "http://localhost:8080/api/sessions/session1/messages?page=0&size=20" ^
-H "X-API-KEY: super-secret-api-key" ^
-H "Accept: application/json"


Delete 
======
curl -X DELETE "http://localhost:8080/api/sessions/session1" ^ -H "X-API-KEY:super-secret-api-key"


Response
=========

<img width="369" height="80" alt="image" src="https://github.com/user-attachments/assets/7d605c82-31a8-472d-bb7f-6725b1270577" />

<img width="382" height="118" alt="image" src="https://github.com/user-attachments/assets/3d3ed6c2-b6d1-44b0-8d97-c9200798d84e" />

<img width="400" height="128" alt="image" src="https://github.com/user-attachments/assets/0d2af080-88e8-437c-b570-db281bba4d5c" />





