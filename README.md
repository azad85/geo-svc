# UK Postal Code Distance Calculator Service

A Spring Boot application that calculates the distance between two UK postal codes and provides postal code management functionality.

## Features

- Calculate distance between two UK postal codes
- Create and update postal code coordinates
- JWT-based authentication
- RESTful API endpoints

## Prerequisites

- Java 17
- MySQL 8.0 or higher
- Maven 3.8 or higher

## Setup Instructions

### 1. Database Setup

1. Create a MySQL database:
```sql
CREATE DATABASE geodb;
```

2. Create the postal code table:
```sql
CREATE TABLE IF NOT EXISTS `postcodelatlng` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `postcode` varchar(8) NOT NULL,
  `latitude` decimal(10,7) NULL,
  `longitude` decimal(10,7) NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### 2. Application Configuration

1. Update the `application.properties` file with your database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/geodb
spring.datasource.username=your_username
spring.datasource.password=your_password
```

2. Set a secure JWT secret key in `application.properties`:
```properties
jwt.secret=your-secure-secret-key-here
```

### 3. Building and Running

1. Build the application:
```bash
mvn clean package
```

2. Run the application:
```bash
mvn spring-boot:run
```

The application will start on port 8080.

## API Documentation

### Authentication

#### Login
- **URL**: `/api/auth/login`
- **Method**: `POST`
- **Body**:
```json
{
    "username": "admin",
    "password": "admin"
}
```
- **Response**:
```json
{
    "token": "your-jwt-token"
}
```

### Postal Code Operations

#### Calculate Distance
- **URL**: `/api/postal-codes/distance`
- **Method**: `POST`
- **Headers**: `Authorization: Bearer your-jwt-token`
- **Body**:
```json
{
    "postcode1": "SW1A 1AA",
    "postcode2": "EC2A 2AH"
}
```
- **Response**:
```json
{
    "location1": {
        "postcode": "SW1A 1AA",
        "latitude": 51.5035,
        "longitude": -0.1277
    },
    "location2": {
        "postcode": "EC2A 2AH",
        "latitude": 51.5200,
        "longitude": -0.0800
    },
    "distance": 3.45,
    "unit": "km"
}
```

#### Create/Update Postal Code
- **URL**: `/api/postal-codes`
- **Method**: `POST`
- **Headers**: `Authorization: Bearer your-jwt-token`
- **Body**:
```json
{
    "postcode": "SW1A 1AA",
    "latitude": 51.5035,
    "longitude": -0.1277
}
```
- **Response**: Returns the created/updated postal code record

## Security

- All endpoints except `/api/auth/login` require JWT authentication
- Default credentials:
  - Username: `admin`
  - Password: `admin`
- JWT tokens expire after 24 hours

## Error Handling

The API returns appropriate HTTP status codes and error messages:
- 400: Bad Request (invalid input)
- 401: Unauthorized (missing or invalid token)
- 404: Not Found (postal code not found)
- 500: Internal Server Error

## Contributing

Feel free to submit issues and enhancement requests. 