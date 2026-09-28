# OdontoCare 3D

OdontoCare 3D es una aplicacion web academica para la gestion de historia clinica odontologica. El sistema integra registro de pacientes, seguridad por roles, consultas clinicas, odontograma tridimensional, procedimientos, planes de tratamiento, evolucion clinica y generacion de PDF.

El proyecto esta organizado como una aplicacion de tres capas:

```text
frontend React
  -> HTTP
backend Spring Boot
  -> JPA / Flyway
base de datos MySQL
```

Tambien se incluye una configuracion PostgreSQL para validaciones locales y preparacion de migracion.

## Modulos principales

- Autenticacion con JWT.
- Roles de recepcion, auxiliar odontologico y odontologo.
- Gestion de pacientes.
- Historia clinica por paciente.
- Registro de antecedentes.
- Consultas odontologicas asociadas a historia clinica.
- Signos vitales, diagnosticos y evolucion.
- Odontograma 3D con 36 piezas configurables.
- Historial de eventos por pieza dental.
- Procedimientos odontologicos.
- Plan de tratamiento.
- Reporte clinico en PDF.
- Migraciones SQL con Flyway.

## Estructura del repositorio

```text
backend/      API REST con Spring Boot
frontend/     Aplicacion web con React, Vite y Three.js
database/     Migraciones, datos de desarrollo y scripts SQL
assets/       Recursos usados para el odontograma 3D
compose.yml   Entorno local con MySQL, backend y frontend
```

## Tecnologias

Backend:

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Flyway
- MySQL
- PostgreSQL
- OpenPDF
- Swagger / OpenAPI

Frontend:

- React
- TypeScript
- Vite
- React Router
- React Hook Form
- React Three Fiber
- Three.js
- Vitest

Infraestructura local:

- Docker
- Docker Compose
- Nginx para servir el frontend en contenedor

## Requisitos

Para ejecutar con Docker:

- Docker Desktop
- Docker Compose

Para ejecutar manualmente:

- Java 17 o superior
- Node.js 20 o superior
- npm
- MySQL 8 o superior

El backend incluye Maven Wrapper, por lo que no es obligatorio instalar Maven globalmente.

## Variables de entorno

El archivo `.env` no se versiona. Para iniciar el proyecto, cree un archivo `.env` a partir de `.env.example` y reemplace los valores marcados como locales.

Variables principales:

```env
MYSQL_DATABASE=odontocare
MYSQL_USER=odontocare
MYSQL_PASSWORD=CAMBIAR_PASSWORD_LOCAL
MYSQL_ROOT_PASSWORD=CAMBIAR_ROOT_PASSWORD_LOCAL
MYSQL_PORT=3307
BACKEND_PORT=18080
FRONTEND_PORT=5173
JWT_SECRET=CAMBIAR_SECRETO_LOCAL_MINIMO_32_CARACTERES
JWT_EXPIRATION_SECONDS=1800
ALLOWED_ORIGIN=http://localhost:5173
REPORT_INSTITUTION_NAME=Institucion academica
REPORT_SYSTEM_NAME=OdontoCare 3D
```

No se deben subir archivos `.env`, contrasenas reales, tokens ni claves privadas.

## Ejecucion con Docker Compose

Desde la raiz del repositorio:

```powershell
Copy-Item .env.example .env
docker compose up --build -d
docker compose ps
```

Servicios principales:

- Frontend: `http://localhost:5173`
- Backend health: `http://localhost:18080/api/health`
- Swagger UI: `http://localhost:18080/swagger-ui.html`
- OpenAPI: `http://localhost:18080/api-docs`
- MySQL: `localhost:3307`

Para detener el entorno:

```powershell
docker compose down
```

Si se desea eliminar tambien los datos locales:

```powershell
docker compose down -v
```

## Ejecucion manual del backend

Primero inicie MySQL local o el contenedor de base de datos:

```powershell
docker compose up -d mysql
```

Luego ejecute el backend:

```powershell
Set-Location backend
$env:SPRING_PROFILES_ACTIVE='dev'
$env:DB_URL='jdbc:mysql://localhost:3307/odontocare?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
$env:DB_USERNAME='odontocare'
$env:DB_PASSWORD='CAMBIAR_PASSWORD_LOCAL'
$env:JWT_SECRET='CAMBIAR_SECRETO_LOCAL_MINIMO_32_CARACTERES'
.\mvnw.cmd spring-boot:run
```

Por defecto el backend manual usa el puerto `8080`. Para cambiarlo:

```powershell
$env:SERVER_PORT='18080'
.\mvnw.cmd spring-boot:run
```

## Ejecucion manual del frontend

```powershell
Set-Location frontend
npm ci
npm run dev
```

Si el backend esta en `18080`, configure el proxy:

```powershell
$env:VITE_PROXY_TARGET='http://localhost:18080'
npm run dev
```

## Usuarios locales de desarrollo

Con el perfil `dev` se crean usuarios de prueba para validar permisos:

| Usuario | Rol |
|---|---|
| `recepcion` | RECEPCION |
| `auxiliar` | AUXILIAR_ODONTOLOGICO |
| `odontologo` | ODONTOLOGO |

Las contrasenas locales se definen para desarrollo y deben cambiarse antes de cualquier entorno compartido.

## Base de datos

Las migraciones principales estan en:

```text
database/migrations/
```

Incluyen:

- roles y usuarios;
- pacientes;
- historias clinicas;
- antecedentes;
- consultas;
- signos vitales;
- diagnosticos;
- procedimientos;
- planes de tratamiento;
- evolucion;
- catalogo de 36 piezas dentales;
- eventos del odontograma;
- superficies;
- archivos clinicos;
- auditoria.

Flyway ejecuta las migraciones automaticamente al iniciar el backend.

## Odontograma 3D

El odontograma esta implementado en el frontend con React Three Fiber y Three.js. Representa 36 piezas configurables con identificadores tecnicos `TOOTH_01` a `TOOTH_36`.

Caracteristicas:

- seleccion de pieza;
- vista actual;
- historial por pieza;
- estado clinico visual;
- panel de detalle;
- lista accesible alternativa;
- integracion con eventos persistidos en backend.

Los colores del frontend son una representacion visual. El dato clinico principal se guarda como estado estructurado en base de datos.

## Endpoints principales

Autenticacion:

- `POST /api/v1/auth/login`
- `GET /api/v1/auth/me`

Pacientes:

- `GET /api/v1/patients`
- `GET /api/v1/patients/{id}`
- `POST /api/v1/patients`
- `PUT /api/v1/patients/{id}`

Historia clinica y consultas:

- `GET /api/clinical-records/{id}/consultations`
- `POST /api/clinical-records/{id}/consultations`
- `GET /api/clinical-records/{id}/consultations/{consultationId}`

Odontograma:

- `GET /api/clinical-records/{id}/odontogram/current`
- `GET /api/clinical-records/{id}/odontogram/consultations/{consultationId}`
- `GET /api/clinical-records/{id}/odontogram/pieces/{pieceCode}/history`
- `POST /api/clinical-records/{id}/consultations/{consultationId}/odontogram/events`

Procedimientos, tratamientos y evolucion:

- `GET /api/clinical-records/{id}/treatment/summary`
- `POST /api/clinical-records/{id}/consultations/{consultationId}/procedures`
- `POST /api/clinical-records/{id}/treatment-plans`
- `POST /api/clinical-records/{id}/consultations/{consultationId}/evolutions`

Reporte PDF:

- `POST /api/clinical-records/{id}/report`

La lista completa se puede revisar en Swagger UI.

## Pruebas y validacion

Backend:

```powershell
Set-Location backend
.\mvnw.cmd test
```

Frontend:

```powershell
Set-Location frontend
npm ci
npm run lint
npm run typecheck
npm test -- --run
npm run build
npm audit --omit=dev
```

Base de datos:

- Las restricciones principales se validan mediante migraciones y pruebas SQL.
- El catalogo dental debe conservar exactamente 36 piezas.
- Las consultas clinicas no se eliminan fisicamente.
- Los eventos historicos del odontograma no se sobrescriben.

## Seguridad

- Autenticacion mediante JWT.
- Contrasenas con BCrypt.
- Control de acceso por roles.
- CORS configurable por variable de entorno.
- Secrets fuera del repositorio.
- `.env` ignorado por Git.
- Errores de backend sin stacktrace expuesto al cliente.

## Alcance actual

El repositorio contiene el proyecto completo desarrollado para la entrega academica. Las funcionalidades clinicas principales estan integradas en backend, frontend y base de datos. Para un uso fuera del entorno academico se deben revisar credenciales, politicas de despliegue, respaldo de datos, controles legales y validaciones clinicas oficiales.
