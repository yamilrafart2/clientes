# Clientes - Spring Boot & Spring Batch (UTN FRFS)

Proyecto backend desarrollado para la asignatura **Fundamentos de Desarrollo Backend**.
Incluye una API REST para la gestión de clientes y Jobs de procesamiento en lotes con Spring Batch.

---

## 🛠️ Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 3.3.0**
- **Spring Data JPA** (Hibernate 6)
- **Spring Batch 5**
- **Flyway** (Migraciones DDL/DML)
- **PostgreSQL 16** (Docker Container)
- **Project Lombok**
- **Springdoc OpenAPI** (Swagger UI)
- **Gradle 8+**

---

## 📁 Estructura del Proyecto

```text
com.fube.clientes
├── batch            # Configuraciones, Processors, Readers, Writers y Runners de Spring Batch
├── controladores    # Capa de exposición REST (Endpoints HTTP)
├── dto              # Objetos de Transferencia de Datos (CSV Import, Export, REST)
├── mapper           # Conversión entre Entidades JPA y DTOs
├── modelos          # Entidades JPA de dominio (@Entity Cliente)
├── repositorio      # Interfaces de acceso a datos (JpaRepository)
└── servicios        # Lógica de negocio de la aplicación
```

---

## 🚀 Endpoints de la API REST

| Método   | Endpoint                | Descripción                                  | Estado HTTP                |
|:---------|:------------------------|:---------------------------------------------|:---------------------------|
| `GET`    | `/api/v1/clientes`      | Obtiene el listado completo de clientes      | `200 OK`                   |
| `GET`    | `/api/v1/clientes/{id}` | Busca un cliente por su identificador único  | `200 OK` / `404 Not Found` |
| `POST`   | `/api/v1/clientes`      | Crea un nuevo cliente en el sistema          | `201 Created`              |
| `PUT`    | `/api/v1/clientes/{id}` | Actualiza completamente un cliente existente | `200 OK` / `404 Not Found` |
| `DELETE` | `/api/v1/clientes/{id}` | Elimina físicamente un cliente por ID        | `204 No Content`           |

---

## 🗄️ Migraciones de Base de Datos (Flyway)

Las migraciones son gestionadas automáticamente por **Flyway** al iniciar la aplicación (`spring.flyway.enabled=true`):

| Versión | Archivo Script                  | Descripción                                        |
|:--------|:--------------------------------|:---------------------------------------------------|
| `V1`    | `V1__create_clientes_table.sql` | Creación DDL de la tabla `clientes` en PostgreSQL. |
| `V2`    | `V2__seed_clientes.sql`         | Seed DML inicial con 92 registros de clientes.     |

---

## ⚙️ Procesos Loteados (Spring Batch - Guía Nro. 2)

El proyecto cuenta con tres Jobs configurados para procesamiento masivo:

### 1. `fillClientesJob` (Unidad 3)
Actualiza en la BD aquellos clientes con campos nulos, asignando `"0000-0000"` a `telefono` y `"Sin domicilio"` a `direccion`.

### 2. `importClienteCsvJob` (Guía 2 - Ejercicio 1)
- **Fuente**: `src/main/resources/clientes.csv` (200 filas).
- **Componentes**: `FlatFileItemReader` $\rightarrow$ `ClienteCsvItemProcessor` $\rightarrow$ `JpaItemWriter`.
- **Lógica de Negocio**: Verifica duplicados en PostgreSQL por email (`ClienteRepositorio.existsByEmail`). Si existe, loguea la causa y retorna `null` descartando la fila.
- **Transaccionalidad**: Procesamiento en bloques (*chunks*) de a 10 ítems.

### 3. `exportClienteCsvJob` (Guía 2 - Ejercicio 2 & Requerimiento Extra)
- **Fuente / Destino**: PostgreSQL (`clientesdb`) $\rightarrow$ `clientes_exportados.csv`.
- **Componentes**: `RepositoryItemReader` $\rightarrow$ `ClienteExportItemProcessor` $\rightarrow$ `FlatFileItemWriter`.
- **Lógica de Negocio**: Exige un `Sort` explícito por `id` para lecturas determinísticas. Filtra aquellos clientes sin dirección cargada (retornando `null` e incrementando el `filterCount`).
- **Métricas**: Loguea en el listener el total de leídos, exportados y excluidos por falta de domicilio.

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

## 💻 Cómo Ejecutar el Proyecto

1. Iniciar el contenedor de PostgreSQL: `docker start clientes-db`
2. Ejecutar la aplicación con el wrapper de Gradle:

```bash
./gradlew bootRun
```

3. **Swagger UI**: Accede a la documentación interactiva en [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).