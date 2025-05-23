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

3. Download and import postal code data:
   - Download the UK postal codes CSV file from [ukpostcodes.zip](https://data.freemaptools.com/download/full-uk-postcodes/ukpostcodes.zip)
   - The CSV file should contain columns for postcode, latitude, and longitude
   - Import the data using the following SQL command:
```sql
LOAD DATA INFILE '/path/to/ukpostcodes.csv'
INTO TABLE postcodelatlng
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(postcode, latitude, longitude);
```

Note: If you encounter the "The MySQL server is running with the --secure-file-priv option" error, you can:
1. Place your CSV file in the secure directory (usually `/var/lib/mysql-files/` on Linux or `C:\ProgramData\MySQL\MySQL Server 8.0\Uploads\` on Windows)
2. Or temporarily disable secure-file-priv in your MySQL configuration

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
- **Success Response**:
```json
{
    "token": "jwt-token-here"
}
```
- **Error Response**:
```json
{
    "path": "/api/auth/login",
    "message": "Authentication required: Incorrect username or password",
    "error": "Unauthorized",
    "status": 401
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

#### Update Postal Code Coordinates
- **URL**: `/api/postal-codes/{postcode}`
- **Method**: `PUT`
- **Headers**: `Authorization: Bearer your-jwt-token`
- **Body**:
```json
{
    "latitude": 51.5036,
    "longitude": -0.1278
}
```
- **Success Response**:
```json
{
    "postcode": "SW1A 1AA",
    "latitude": 51.5036,
    "longitude": -0.1278
}
```
- **Error Response** (Postal code not found):
```json
{
    "path": "/api/postal-codes/INVALID",
    "message": "Postal code not found: INVALID",
    "error": "Not Found",
    "status": 404
}
```

#### Get All Postal Codes
- **URL**: `/api/postal-codes`
- **Method**: `GET`
- **Headers**: `Authorization: Bearer your-jwt-token`
- **Query Parameters**:
  - `page` (optional): Page number (default: 0)
  - `size` (optional): Number of items per page (default: 10)
  - `sortBy` (optional): Field to sort by (default: postcode)
- **Success Response**:
```json
{
    "content": [
        {
            "postcode": "EC2A 2AH",
            "latitude": 51.5200,
            "longitude": -0.0800
        },
        {
            "postcode": "SW1A 1AA",
            "latitude": 51.5035,
            "longitude": -0.1277
        }
    ],
    "totalElements": 2,
    "totalPages": 1,
    "size": 10,
    "number": 0
}
```

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

## Request Logging

The application implements structured logging for postal code distance calculations. Each request is tracked with a unique identifier and includes detailed information for monitoring and analytics.

### Log Format
```
POSTAL_CODE_REQUEST|TIMESTAMP|REQUEST_ID|EVENT_TYPE|POSTCODE1|POSTCODE2|STATUS
```

### Fields Description
- `TIMESTAMP`: Request timestamp in format 'yyyy-MM-dd HH:mm:ss.SSS'
- `REQUEST_ID`: Unique UUID for tracking the request throughout its lifecycle
- `EVENT_TYPE`: Type of operation (get_postalcode_latlong_distance)
- `POSTCODE1`: First postal code in the request
- `POSTCODE2`: Second postal code in the request
- `STATUS`: Current status of the request (REQUEST_RECEIVED or REQUEST_COMPLETED)

### Example Log Entries
```
POSTAL_CODE_REQUEST|2024-03-14 10:30:45.123|550e8400-e29b-41d4-a716-446655440000|get_postalcode_latlong_distance|SW1A 1AA|EC2A 2AH|REQUEST_RECEIVED
POSTAL_CODE_REQUEST|2024-03-14 10:30:45.234|550e8400-e29b-41d4-a716-446655440000|get_postalcode_latlong_distance|SW1A 1AA|EC2A 2AH|REQUEST_COMPLETED
```

### Log Analysis
The structured logging format enables easy:
- Request tracking using the unique REQUEST_ID
- Performance monitoring by calculating duration between REQUEST_RECEIVED and REQUEST_COMPLETED
- Usage analytics for most frequently requested postal codes
- System monitoring and troubleshooting
- Data aggregation for reporting purposes

## Testing

The application includes comprehensive test coverage for both service and controller layers.

### Service Layer Tests

#### AuthService Tests
- `login_ValidCredentials_ReturnsToken`: Verifies successful login returns a valid JWT token
- `login_InvalidCredentials_ThrowsException`: Verifies invalid credentials throw an authentication exception
- `login_AuthenticationException_ThrowsException`: Verifies authentication failures are properly handled

#### PostalCodeService Tests
- `calculateDistance_ValidPostcodes_ReturnsCorrectDistance`: Verifies distance calculation between valid postcodes
- `calculateDistance_InvalidPostcode_ThrowsException`: Verifies invalid postcode handling
- `createOrUpdatePostalCode_NewPostcode_CreatesNewRecord`: Verifies creation of new postal code records
- `createOrUpdatePostalCode_ExistingPostcode_UpdatesRecord`: Verifies updating existing postal code records

### Controller Layer Tests

#### AuthController Tests
- `login_ValidCredentials_ReturnsSuccessResponse`: Verifies successful login API response
- `login_InvalidCredentials_ReturnsErrorResponse`: Verifies error response format for invalid credentials

#### PostalCodeLoggingAspect Tests
- `logDistanceRequest_ValidRequest_LogsCorrectly`: Verifies proper logging of postal code distance requests

### Running Tests

To run all tests:
```bash
mvn test
```

To run a specific test class:
```bash
mvn test -Dtest=AuthServiceTest
```

To run a specific test method:
```bash
mvn test -Dtest=AuthServiceTest#login_ValidCredentials_ReturnsToken
```

## Contributing

Feel free to submit issues and enhancement requests.