# URL Shortener

A simple URL Shortener built using Spring Boot. It allows users to create short links, manage them, and view click analytics. The application uses JWT authentication, PostgreSQL for persistence, and Redis for caching frequently accessed URLs.

## Features

- User Registration & Login
- JWT-based Authentication
- Create short URLs
- Redirect short URL to original URL
- Update existing URLs
- Delete URLs
- View all URLs created by a user
- URL click analytics within a date range
- Total click count by date
- Redis caching for faster URL lookups

---

## Tech Stack

**Backend**
- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- PostgreSQL
- Redis
- JWT Authentication
- Maven

---

## Project Structure

```
src
 ├── controllers
 ├── service
 ├── repository
 ├── models
 ├── dtos
 ├── security
 ├── config
 ├── exceptions
 └── utils
```

---

## Prerequisites

Before running the project, make sure you have:

- Java 21
- Maven
- PostgreSQL
- Redis

---

## Environment Variables

Create the following environment variables.

| Variable | Description |
|----------|-------------|
| DB_URL | PostgreSQL URL |
| DB_USERNAME | Database username |
| DB_PASSWORD | Database password |
| JWT_SECRET | Secret key used for JWT |
| JWT_EXPIRATION | Token expiry time (optional) |
| REDIS_URL | Redis connection URL |

Example:

```
DB_URL=jdbc:postgresql://localhost:5432/url_shortener
DB_USERNAME=postgres
DB_PASSWORD=password
JWT_SECRET=your-secret-key
JWT_EXPIRATION=172800000
REDIS_URL=redis://localhost:6379
```

---

## Running the Project

Clone the repository

```bash
git clone <repository-url>
```

Move into the project directory

```bash
cd URL-Shortner
```

Run the application

```bash
./mvnw spring-boot:run
```

or

```bash
mvn spring-boot:run
```

The application starts on

```
http://localhost:9090
```

---

## Main APIs

### Authentication

| Method | Endpoint |
|---------|----------|
| POST | `/api/auth/public/register` |
| POST | `/api/auth/public/login` |

### URL Management

| Method | Endpoint |
|---------|----------|
| POST | `/api/urls/shorten` |
| GET | `/api/urls/myurls` |
| PUT | `/api/urls/{shortUrl}` |
| DELETE | `/api/urls/{shortUrl}` |

### Analytics

| Method | Endpoint |
|---------|----------|
| GET | `/api/urls/analytics/{shortUrl}` |
| GET | `/api/urls/totalClicks` |

### Redirect

```
GET /{shortUrl}
```

Redirects the user to the original URL.

---

## Caching

Redis is used to cache URL mappings so that repeated redirects don't always hit the database. Cached entries automatically expire after the configured TTL.

---

## Database

The application uses PostgreSQL with Hibernate.

```
spring.jpa.hibernate.ddl-auto=update
```

Tables are created automatically when the application starts.

---

## Postman Collection

A Postman collection is included in the repository.

```
URL Shortner Local.postman_collection.json
```

Import it into Postman to test the APIs quickly.

---

## Docker

A Dockerfile and Docker Compose configuration are included.

Build the image

```bash
docker build -t url-shortner .
```

Run the container

```bash
docker run -p 9090:9090 url-shortner
```

---

## Future Improvements

Some enhancements planned for future releases:

- **Dashboard Summary Cards**
    - Display total URLs created.
    - Display total clicks across all URLs.

- **Better Analytics Chart**
    - Show click trends for the last 7 or 30 days by default.
    - If only one day of data is available, display a message like:
      > "More data will appear as your links receive clicks."

- **Search & Filters**
    - Search URLs by original (long) URL or description.
    - Filter URLs by:
        - Most Clicked
        - Newest
        - Oldest

- **Display Destination Domain**
    - Instead of showing the complete original URL, display only the destination domain for better readability.

  Example:

  ```
  https://www.google.com/search?q=springboot
  → google.com
  
  https://comparingwebhosting.com/reviews
  → comparingwebhosting.com
  ```