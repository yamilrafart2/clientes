# Clientes - Spring Boot, Spring Batch & Testing Suite (UTN FRFS)

Proyecto backend desarrollado para la asignatura **Fundamentos de Desarrollo Backend (FUBE)** - UTN Facultad Regional Santa Fe.
Incluye una API REST completa para la gestión de clientes, Jobs de procesamiento en lotes con Spring Batch y una Suite Integral de Pruebas Automatizadas (JUnit 5, Mockito, MockServer y JaCoCo).

---

## 🛠️ Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 3.3.0**
- **Spring Data JPA** (Hibernate 6)
- **Spring Batch 5**
- **Flyway** (Migraciones DDL/DML de Base de Datos)
- **PostgreSQL 16** (Docker Container) & **H2 Database** (Base de Datos en Memoria para Tests)
- **Project Lombok**
- **Springdoc OpenAPI** (Swagger UI)
- **JUnit 5** & **Mockito** (Testing Unitario, TDD y Mocks)
- **MockServer** (Simulación de Servicios HTTP Externos)
- **JaCoCo** (Medición de Cobertura de Código)
- **Gradle 8+**

---

## 📁 Estructura del Proyecto

```text
com.fube.clientes
├── batch            # Configuraciones, Processors, Readers, Writers y Runners de Spring Batch
├── cliente          # Clientes HTTP de Infraestructura (ValidadorEmailExternoClient)
├── config           # Beans de Configuración (AppConfig con RestTemplate)
├── controladores    # Capa de exposición REST (Endpoints HTTP)
├── dto              # Objetos de Transferencia de Datos (CSV Import, Export, REST, ErrorResponse)
├── excepciones      # Manejo Global de Excepciones (@ControllerAdvice, RecursoNoEncontradoException)
├── mapper           # Conversión entre Entidades JPA y DTOs (ClienteMapper)
├── modelos          # Entidades JPA de dominio (@Entity Cliente)
├── repositorio      # Interfaces de acceso a datos (JpaRepository)
├── servicios        # Lógica de negocio e interfaces de servicios
└── validador        # Lógica de validación de dominio (ClienteValidador)
```

---

## 🚀 Endpoints de la API REST

| Método   | Endpoint                | Descripción                                  | Estado HTTP                |
|----------|-------------------------|----------------------------------------------|----------------------------|
| `GET`    | `/api/v1/clientes`      | Obtiene el listado completo de clientes      | `200 OK`                   |
| `GET`    | `/api/v1/clientes/{id}` | Busca un cliente por su identificador único  | `200 OK` / `404 Not Found` |
| `POST`   | `/api/v1/clientes`      | Crea un nuevo cliente en el sistema          | `201 Created`              |
| `PUT`    | `/api/v1/clientes/{id}` | Actualiza completamente un cliente existente | `200 OK` / `404 Not Found` |
| `DELETE` | `/api/v1/clientes/{id}` | Elimina físicamente un cliente por ID        | `204 No Content`           |

---

## 🗄️ Migraciones de Base de Datos (Flyway)

Las migraciones son gestionadas automáticamente por **Flyway** al iniciar la aplicación (`spring.flyway.enabled=true`):

| Versión | Archivo Script                  | Descripción                                        |
|---------|---------------------------------|----------------------------------------------------|
| `V1`    | `V1__create_clientes_table.sql` | Creación DDL de la tabla `clientes` en PostgreSQL. |
| `V2`    | `V2__seed_clientes.sql`         | Seed DML inicial con 92 registros de clientes.     |

---

## ⚙️ Procesos Loteados (Spring Batch)

El proyecto cuenta con tres Jobs configurados para procesamiento masivo:

### 1. `fillClientesJob`

Actualiza en la BD aquellos clientes con campos nulos, asignando `"0000-0000"` a `telefono` y `"Sin domicilio"` a `direccion`.

### 2. `importClienteCsvJob`

* **Fuente**: `src/main/resources/clientes.csv` (200 filas).
* **Componentes**: `FlatFileItemReader` $\rightarrow$ `ClienteCsvItemProcessor` $\rightarrow$ `JpaItemWriter`.
* **Lógica de Negocio**: Verifica duplicados en PostgreSQL por email (`ClienteRepositorio.existsByEmail`). Si existe, loguea la causa y retorna `null` descartando la fila.
* **Transaccionalidad**: Procesamiento en bloques (*chunks*) de a 10 ítems.

### 3. `exportClienteCsvJob`

* **Fuente / Destino**: PostgreSQL (`clientesdb`) $\rightarrow$ `clientes_exportados.csv`.
* **Componentes**: `RepositoryItemReader` $\rightarrow$ `ClienteExportItemProcessor` $\rightarrow$ `FlatFileItemWriter`.
* **Lógica de Negocio**: Exige un `Sort` explícito por `id` para lecturas determinísticas. Filtra aquellos clientes sin dirección cargada (retornando `null` e incrementando el `filterCount`).
* **Métricas**: Loguea en el listener el total de leídos, exportados y excluidos por falta de domicilio.

---

## 🧪 Estrategia de Testing Automatizado (Unidad 4)

El proyecto implementa una suite integral de 26 pruebas automatizadas divididas en 7 bloques temáticos:

### Cobertura de la Suite de Pruebas:

1. **Bloque A (JUnit 5 & Assertions):** `ClienteMapperTest` evalúa la conversión bidireccional entre Entidades JPA y DTOs, verificando preservación de nulos y excepciones con `assertThrows`.
2. **Bloque B (TDD - Test Driven Development):** `ClienteValidadorTest` implementa desarrollo guiado por pruebas para la validación sintáctica de emails (`@ParameterizedTest`) y reglas de negocio sobre atributos obligatorios.
3. **Bloque C (Aislamiento con Mockito):** `ClienteServicioImplTest` valida la lógica de negocio simulando la capa de persistencia (`@Mock` / `@InjectMocks`), verificando interacciones con `verify()`.
4. **Bloque D (Persistencia con `@DataJpaTest`):** `ClienteRepositorioTest` ejercita la comunicación real contra una base de datos H2 en memoria, probando restricciones de unicidad (`DataIntegrityViolationException`) y consultas compuestas JPQL/SQL (`@Sql`).
5. **Bloque E (Integración REST con `MockMvc`):** `ClienteControladorTest` realiza solicitudes HTTP simuladas contra `@SpringBootTest`, validando códigos de estado (`200 OK`, `201 Created`, `204 No Content`, `404 Not Found`) y estructuras JSON de error (`ErrorResponse`).
6. **Bloque F (Simulación HTTP con `MockServer`):** `ValidadorEmailExternoClientTest` levanta un servidor HTTP efímero (`ClientAndServer`) para verificar el contrato del cliente de infraestructura, evaluando resiliencia **Fail-Closed** ante errores `500 Internal Server Error` y latencias/timeouts de red.
7. **Bloque G (Análisis de Cobertura con `JaCoCo`):** Configuración del plugin de cobertura superando el 62% de instrucciones globales y 29% de ramas (Branches), cumpliendo con la regla estricta de verificación `jacocoTestCoverageVerification`.

---

## 🐳 Entorno Local con Docker

Levanta el contenedor de PostgreSQL expuesto en el puerto `5432`:

```bash
docker run --name clientes-db \
  -e POSTGRES_DB=clientesdb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -v clientes-db-data:/var/lib/postgresql/data \
  --restart unless-stopped \
  -d postgres:16
```

---

## 💻 Comandos de Ejecución y Pruebas

### 1. Iniciar la aplicación

```bash
./gradlew bootRun
```

* **Swagger UI**: Documentación interactiva disponible en [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).

### 2. Ejecutar la suite completa de tests

```bash
./gradlew test
```

### 3. Generar y verificar el reporte de cobertura con JaCoCo

```bash
./gradlew test jacocoTestReport jacocoTestCoverageVerification
```

* **Reporte HTML**: Abrir en el navegador con:
```bash
open build/reports/jacoco/test/html/index.html
```