
# Liveasy Load & Booking Management System

A comprehensive backend system using Spring Boot and PostgreSQL to manage Load & Booking operations efficiently, optimized for performance, security, and scalability.

## Architecture

This project implements a microservices architecture with the following components:

1. **Discovery Service (Eureka)**: Service registry for microservices
2. **API Gateway**: Entry point for all client requests with routing and JWT authentication
3. **Auth Service**: Handles user authentication and authorization
4. **Load Service**: Manages load operations
5. **Booking Service**: Manages booking operations
6. **Monitoring**: Prometheus and Grafana for metrics and monitoring

## Technologies

- **Spring Boot**: For creating microservices
- **Spring Cloud**: For service discovery and API gateway
- **PostgreSQL**: Persistent data storage
- **Redis**: For caching to improve performance
- **Apache Kafka**: For event-driven communication between services
- **JWT**: For authentication and authorization
- **Zipkin**: For distributed tracing
- **Prometheus & Grafana**: For monitoring

## Setup Instructions

### Prerequisites

- Docker and Docker Compose
- Java 11+
- Maven

## API Documentation

### Auth Service

#### Register a new user
```
POST /api/auth/register
{
  "username": "user123",
  "password": "password",
  "email": "user@example.com",
  "role": "SHIPPER" // or "TRANSPORTER"
}
```

#### Login
```
POST /api/auth/login
{
  "username": "user123",
  "password": "password"
}
```

### Load Management

#### Create a new load
```
POST /api/load
{
  "facility": {
    "loadingPoint": "Delhi",
    "unloadingPoint": "Mumbai",
    "loadingDate": "2023-04-20T10:30:00",
    "unloadingDate": "2023-04-22T18:00:00"
  },
  "productType": "Electronics",
  "truckType": "Open",
  "noOfTrucks": 2,
  "weight": 1500,
  "comment": "Fragile goods"
}
```

#### Get all loads
```
GET /api/load
```

#### Filter loads by shipper
```
GET /api/load?shipperId=123e4567-e89b-12d3-a456-426614174000
```

#### Filter loads by truck type
```
GET /api/load?truckType=Open
```

#### Get a specific load
```
GET /api/load/{loadId}
```

#### Update a load
```
PUT /api/load/{loadId}
{
  "facility": {
    "loadingPoint": "Updated Location",
    "unloadingPoint": "Mumbai",
    "loadingDate": "2023-04-20T10:30:00",
    "unloadingDate": "2023-04-22T18:00:00"
  },
  "productType": "Electronics",
  "truckType": "Open",
  "noOfTrucks": 3,
  "weight": 2000,
  "comment": "Updated comment"
}
```

#### Delete a load
```
DELETE /api/load/{loadId}
```

### Booking Management

#### Create a new booking
```
POST /api/booking
{
  "loadId": "123e4567-e89b-12d3-a456-426614174000",
  "proposedRate": 25000,
  "comment": "Available for immediate transport"
}
```

#### Get all bookings
```
GET /api/booking
```

#### Filter bookings by load
```
GET /api/booking?loadId=123e4567-e89b-12d3-a456-426614174000
```

#### Filter bookings by transporter
```
GET /api/booking?transporterId=123e4567-e89b-12d3-a456-426614174001
```

#### Get a specific booking
```
GET /api/booking/{bookingId}
```

#### Update a booking
```
PUT /api/booking/{bookingId}
{
  "proposedRate": 30000,
  "comment": "Updated offer",
  "status": "ACCEPTED" // or "REJECTED" or "PENDING"
}
```

#### Delete a booking
```
DELETE /api/booking/{bookingId}
```

## Business Rules

1. **Load Status Management**:
   - When a load is created, its status is set to "POSTED"
   - When a booking is made, the load status changes to "BOOKED"
   - If a booking is deleted, the load status is set to "CANCELLED"

2. **Booking Validation**:
   - A booking cannot be created if the load is already CANCELLED
   - When a booking is accepted, its status is updated to "ACCEPTED"

## Security

All endpoints (except auth endpoints) require JWT authentication. The token should be included in the Authorization header:

```
Authorization: Bearer <token>
```

## Monitoring and Tracing

- Prometheus metrics are available at `/actuator/prometheus` on each service
- Zipkin traces can be viewed at http://localhost:9411
- Grafana dashboards can be created to visualize metrics

## Assumptions and Design Decisions

1. **Microservices Boundaries**: Services are designed around business capabilities
2. **Data Consistency**: Using event-driven architecture with Kafka for eventual consistency across services
3. **Caching Strategy**: Redis is used for caching frequently accessed data
4. **Security**: JWT tokens are used for authentication with role-based authorization
5. **Error Handling**: Comprehensive error handling with appropriate HTTP status codes
6. **Logging**: Centralized logging for easy debugging and monitoring
7. **Scalability**: Services can be scaled independently based on load

