# Meeting Room Reservation API

API REST para la gestión y reserva de salas de reuniones, construida con **Java 17** y **Spring Boot 4**.

## Stack

- Java 17, Maven
- Spring Boot 4 (Web, Data JPA, Security, Validation)
- PostgreSQL 11 (local)
- Flyway
- H2 en memoria (solo tests)
- JUnit 5, Mockito, MockMvc, `@DataJpaTest`

## Requisitos previos

- JDK 17+
- Maven 3.9+
- PostgreSQL 11+ ejecutándose en `localhost:5432`

## Configuración de la base de datos

```sql
CREATE DATABASE meetingroom;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE meetingroom TO postgres;
```

Ajusta `src/main/resources/application.yml` con tus credenciales si difieren.

Flyway creará automáticamente el esquema (`rooms`, `reservations`) al arrancar.

## Ejecutar

```bash
# Compilar y arrancar (aplica migraciones Flyway)
.\mvnw spring-boot:run

# Ejecutar tests (H2 en memoria, sin Docker)
.\mvnw test
```

La API queda disponible en `http://localhost:8080`.