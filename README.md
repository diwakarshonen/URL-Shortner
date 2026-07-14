# URL Shortener & Link Analytics

A Spring Boot based URL shortening service that converts long URLs into short codes and redirects users to the original URL.

## Tech Stack

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- H2 Database
- Maven
- JUnit 5

## Features

- Shorten long URLs into unique short codes
- Redirect short URLs to the original URL using HTTP 301 redirect
- Support custom aliases
- Validate incoming URLs
- Persist URL mappings in a database
- Return 404 for unknown short codes
- Automated tests for core functionality and edge cases

## Project Setup

### Prerequisites

- Java 21
- Maven

### Run Application

Clone the repository and navigate to the project folder.

Start the application using:

`bash
./mvnw spring-boot:run

The application will start on:

http://localhost:8080

# API Endpoints

1. ## Shorten URL

POST

/shorten

## Request:

{
  "url": "https://www.google.com"
}

## Response:

{
  "shortCode": "y9Vc0Sd",
  "shortUrl": "http://localhost:8080/y9Vc0Sd"
}


---

2. ## Redirect URL

GET

/{code}

Example:

GET /y9Vc0Sd

## Response:

HTTP 301 Moved Permanently
Location: Original URL

The service retrieves the original URL using the short code and redirects the user.


---

3. ## Custom Alias

Users can provide a custom alias while shortening a URL.

## Request:

{
  "url": "https://github.com",
  "customAlias": "github"
}

### Response:

{
  "shortCode": "github",
  "shortUrl": "http://localhost:8080/github"
}

## Database

The application uses H2 database for storing URL mappings.

The database stores:

-> Original URL

-> Short code

-> Custom alias


The short code acts as the key to find the original URL during redirection.

## Design Decisions

### Short Code Generation

The service generates URL-safe alphanumeric short codes and checks uniqueness before storing them to avoid collisions.

## Duplicate URL Handling

If the same URL is shortened multiple times, the existing short code is returned instead of creating a new entry.

## Custom Alias Handling

Custom aliases are supported. If an alias already exists, the request is rejected to avoid conflicts.

# Testing

Run automated tests using:

./mvnw test

The test cases cover:

Successful URL shortening

Redirect functionality

Unknown short code returns 404

Duplicate URL handling

Custom alias creation


# Future Improvements

Possible improvements:

Add click analytics

Add URL expiration

Add authentication and user management

Use production databases like MySQL/PostgreSQL

Add caching for faster redirects

Add rate limiting