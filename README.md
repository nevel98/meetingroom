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

## Endpoints & Seguridad

- **GET** `/api/rooms` y `/api/reservations` → públicos.
- **POST** `/api/rooms`, **POST** `/api/reservations`, **DELETE** `/api/reservations/{id}` → requieren autenticación HTTP Basic.
- Credenciales de ejemplo: `admin` / `admin123` (definidas en `SecurityConfig` con `InMemoryUserDetailsManager`).

## Manejo de errores

Todas las respuestas de error son JSON estructurado:

| Código | Cuándo |
|--------|--------|
| 400 | Validación de DTO, JSON malformado, `endTime <= startTime` |
| 401 | Falta de credenciales o inválidas |
| 404 | Sala o reserva inexistente |
| 409 | Solapamiento de horario o nombre de sala duplicado |
| 500 | Error inesperado |

Ejemplo 409:

```json
{
  "timestamp": "2026-01-10T12:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "La sala ya está reservada en ese horario. Conflicto con reserva ID: 7"
}
```

## Decisiones de diseño

- **Detección de solapamiento:** consulta JPQL atómica `start < newEnd AND end > newStart`, ejecutada antes del `save`. Ver `ReservationRepository#findOverlappingReservation`.
- **DTOs con `record`:** inmutabilidad y validación declarativa con Bean Validation.
- **`ddl-auto: validate`:** Flyway es la única fuente de verdad del esquema; Hibernate solo valida.
- **Seguridad stateless:** HTTP Basic sin sesión; `AuthenticationEntryPoint` custom devuelve JSON en 401.
- **Tests sin Docker:** H2 con `MODE=PostgreSQL` permite tests rápidos y aislados.
- **Paginación:** `Pageable` de Spring Data con `@PageableDefault(size=20, sort="startTime")`.

## Mejoras futuras

1. **Concurrencia:** `@Lock(LockModeType.PESSIMISTIC_WRITE)` en la consulta de solapamiento o un índice único parcial en PostgreSQL (`EXCLUDE USING gist` con `tstzrange`) para eliminar condiciones de carrera.
2. **JWT stateless** en lugar de Basic para integración con frontends SPA.
3. **Roles granulares** (`ADMIN` para crear salas, `USER` para reservar).
5. **Notificaciones por correo** al crear/cancelar reserva (Spring Mail + plantillas Thymeleaf).
7. **OpenAPI/Swagger** con `springdoc-openapi`.