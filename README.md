# Billing Service

## Summary 
Involves implementing a REST API for managing a simple account billing system. 
The system includes features for creating, updating, deleting, and querying accounts 
bills, as well as importing account data from a CSV file. 
The implementation uses Java 17+, Spring Boot, and PostgreSQL, with the entire 
application containerized using Docker and orchestrated with Docker Compose.

## Key Features:
* CRUD Operations: Manage billings with APIs to create, update, delete, and list accounts.
* Data Import: Import accounts from a CSV file via an API.
* Pagination: All query APIs support pagination.
* Authentication: With Spring Security, JWT, Role based access, and encryption.
* Domain Driven Design: Organized using DDD principles.
* Database Migration: Uses Flyway for managing database schema changes.
* JPA for Persistence: Utilizes JPA for database operations.
* Resilience: Custom error handling, detailed logging, and 80% Unit and Integration tests coverage.

## Setup
1. ./gradlew build
2. docker-compose build
3. docker-compose up

## API
#### API Documentation: http://localhost:8080/swagger-ui/index.html#/
#### !!! Attention !!! : All requests must first be authenticated through authentication endpoint
Example GET request:

`curl -X 'GET' \
'http://localhost:8080/api/billing/12' \
-H 'accept: */*' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3cml0ZVVzZXIiLCJyb2xlcyI6WyJSRUFEIiwiV1JJVEUiXSwiaWF0IjoxNzE3OTc2NzM4LCJleHAiOjE3MTgwMTI3Mzh9.OmtCSa7OEClgJaHXBLyYcszGwUJ8YFu9pQyHvHbFIkg'`

Example POST request:

`curl -X 'POST' \
'http://localhost:8080/authenticate' \
-H 'accept: */*' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3cml0ZVVzZXIiLCJyb2xlcyI6WyJSRUFEIiwiV1JJVEUiXSwiaWF0IjoxNzE3OTc2NzM4LCJleHAiOjE3MTgwMTI3Mzh9.OmtCSa7OEClgJaHXBLyYcszGwUJ8YFu9pQyHvHbFIkg' \
-H 'Content-Type: application/json' \
-d '{
"username": "readUser",
"password": "password"
}'`

Example POST Import request:

`curl --location 'http://localhost:8080/api/billing/import' \
--header 'accept: */*' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3cml0ZVVzZXIiLCJyb2xlcyI6WyJSRUFEIiwiV1JJVEUiXSwiaWF0IjoxNzE3OTgwMTEyLCJleHAiOjE3MTgwMTYxMTJ9.3JzoB9XiPOO-Do3_TUcJF4-vuHDN2rA-Xj19pos3v-Y' \
--form 'file=@"/C:/java_dev/billing-service/src/main/resources/csv/exampleCSV.csv"'`

#### Available Users:
* 'readUser', password: password
* 'writeUser', password: password

#### For more details refer to Swagger API Documentation
