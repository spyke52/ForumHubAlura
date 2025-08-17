# 🚀 ForumHub API 2.0

API REST completa para sistema de foro de discusión desarrollada con Spring Boot. Una solución robusta y escalable que incluye autenticación JWT, gestión avanzada de usuarios, sistema de respuestas, notificaciones, búsqueda full-text y análisis estadístico.

## 📋 Tabla de Contenidos

- [Características](#características)
- [Tecnologías](#tecnologías)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Uso](#uso)
- [Endpoints de la API](#endpoints-de-la-api)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Base de Datos](#base-de-datos)
- [Seguridad](#seguridad)
- [Optimizaciones](#optimizaciones)
- [Contribuir](#contribuir)

## ✨ Características

### Core Features
- **Autenticación JWT**: Sistema de autenticación seguro con tokens JWT
- **Gestión Completa de Usuarios**: Registro, autenticación y roles (Usuario, Moderador, Admin)
- **Sistema de Tópicos**: CRUD completo con validación de duplicados
- **Sistema de Respuestas**: Los usuarios pueden responder a tópicos y marcar soluciones
- **Sistema de Roles**: Control granular de permisos por rol de usuario

### Características Avanzadas
- **Búsqueda Full-Text**: Búsqueda optimizada en títulos y mensajes usando MySQL FULLTEXT
- **Sistema de Notificaciones**: Notificaciones automáticas para nuevas respuestas
- **Dashboard de Estadísticas**: Métricas completas del foro y usuarios más activos
- **Trending Topics**: Algoritmo para detectar tópicos populares
- **Rate Limiting**: Protección contra abuso de API
- **Cache Inteligente**: Sistema de cache personalizado con TTL configurable
- **Auditoría y Logs**: Sistema completo de logging a base de datos

### Seguridad y Optimización
- **Validación de Fortaleza de Contraseñas**: Reglas estrictas de contraseñas
- **CORS Configurado**: Soporte multi-dominio configurable
- **Queries Optimizadas**: Consultas nativas optimizadas para mejor rendimiento
- **Connection Pooling**: HikariCP configurado para alta concurrencia
- **Paginación Eficiente**: Paginación en todos los listados

## 🛠 Tecnologías

- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Security** (JWT + Role-based authorization)
- **Spring Data JPA** (Con queries nativas optimizadas)
- **MySQL 8.0+** (Con índices FULLTEXT)
- **Flyway** (Migraciones de BD)
- **Caffeine Cache** (Sistema de cache)
- **SpringDoc OpenAPI 3** (Documentación Swagger)
- **HikariCP** (Connection pooling)
- **Lombok** (Reducción de boilerplate)
- **Bean Validation** (Validaciones declarativas)
- **Maven** (Gestión de dependencias)

## 🚀 Instalación

### Prerrequisitos

- Java 17 o superior
- Maven 3.6+
- MySQL 8.0+

### Pasos de instalación

1. **Clona el repositorio**
```bash
git clone https://github.com/spyke52/ForumHubAlura.git
cd ForumHubAlura
```

2. **Crea la base de datos**
```sql
CREATE DATABASE forumhub_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **Configura el archivo de propiedades**
Edita `src/main/resources/application.properties` y cambia estos valores:
```properties
# Cambia por tu configuración de MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/forumhub_db
spring.datasource.password=TU_PASSWORD_MYSQL

# Cambia por un secreto seguro (debe coincidir exactamente con el formato)
JWT_SECRET=D#9fKpZ7!qRtX1wLm$3yN2vB8@hTjU5o
```

4. **Instala las dependencias**
```bash
mvn clean install
```

5. **Ejecuta la aplicación**
```bash
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## ⚙️ Configuración

### Configuración Principal (application.properties)

```properties
# Base de datos - Ajusta según tu configuración
spring.datasource.url=jdbc:mysql://localhost:3306/forumhub_db
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD_AQUI

# Optimizaciones HikariCP
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# JWT - Usa un secreto seguro
JWT_SECRET=D#9fKpZ7!qRtX1wLm$3yN2vB8@hTjU5o
api.security.token.expiration-minutes=120

# CORS
cors.allowed-origins=http://localhost:3000,https://frontend-forumhub.com

# Cache
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=30m
```

## 📖 Uso

### Documentación de la API

Accede a la documentación Swagger en: `http://localhost:8080/swagger-ui.html`

### Flujo de uso completo

1. **Registrar usuario**
2. **Iniciar sesión** para obtener token JWT
3. **Crear tópicos** usando el token
4. **Responder a tópicos** de otros usuarios
5. **Marcar respuestas como solución** (solo autor del tópico)
6. **Buscar contenido** con búsqueda full-text
7. **Ver estadísticas** del dashboard

## 🛡 Endpoints de la API

### Autenticación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/login` | Autenticar usuario |
| POST | `/usuarios` | Registrar nuevo usuario |

### Usuarios
| Método | Endpoint | Descripción | Permisos |
|--------|----------|-------------|----------|
| PUT | `/usuarios/{id}/rol` | Actualizar rol de usuario | Solo ADMIN |

### Tópicos
| Método | Endpoint | Descripción | Permisos |
|--------|----------|-------------|----------|
| GET | `/topicos` | Listar tópicos (paginado) | Autenticado |
| POST | `/topicos` | Crear nuevo tópico | Autenticado |
| GET | `/topicos/{id}` | Obtener tópico por ID | Autenticado |
| PUT | `/topicos/{id}` | Actualizar tópico | Solo autor |
| DELETE | `/topicos/{id}` | Eliminar tópico | Solo autor |
| GET | `/topicos/buscar` | Búsqueda avanzada con filtros | Autenticado |

### Respuestas
| Método | Endpoint | Descripción | Permisos |
|--------|----------|-------------|----------|
| POST | `/topicos/{id}/respuestas` | Añadir respuesta a tópico | Autenticado |
| PUT | `/topicos/{topicoId}/respuestas/{respuestaId}/solucion` | Marcar como solución | Solo autor del tópico |

### Búsqueda
| Método | Endpoint | Descripción | Permisos |
|--------|----------|-------------|----------|
| GET | `/api/search?q={query}` | Búsqueda full-text | Autenticado |

### Estadísticas
| Método | Endpoint | Descripción | Permisos |
|--------|----------|-------------|----------|
| GET | `/api/stats/dashboard` | Estadísticas generales | Autenticado |
| GET | `/api/stats/trending` | Tópicos trending | Autenticado |

### Ejemplos de uso

#### 1. Registrar Usuario con Contraseña Segura
```bash
curl -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "login": "usuario1",
    "clave": "MiPassword123!"
  }'
```

#### 2. Crear Tópico
```bash
curl -X POST http://localhost:8080/topicos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer tu_jwt_token" \
  -d '{
    "titulo": "¿Cómo optimizar consultas JPA?",
    "mensaje": "Necesito ayuda para optimizar queries con @Query",
    "curso": "Spring Data JPA"
  }'
```

#### 3. Responder a un Tópico
```bash
curl -X POST http://localhost:8080/topicos/1/respuestas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer tu_jwt_token" \
  -d '{
    "mensaje": "Puedes usar queries nativas con @Query(nativeQuery = true)"
  }'
```

#### 4. Búsqueda Full-Text
```bash
curl -X GET "http://localhost:8080/api/search?q=Spring&limit=10" \
  -H "Authorization: Bearer tu_jwt_token"
```

#### 5. Ver Dashboard
```bash
curl -X GET http://localhost:8080/api/stats/dashboard \
  -H "Authorization: Bearer tu_jwt_token"
```

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/alura/forum/forumHub/
│   │   ├── domain/
│   │   │   ├── controller/          # Controladores REST
│   │   │   │   ├── AutenticacionController.java
│   │   │   │   ├── TopicoController.java
│   │   │   │   ├── RespuestaController.java
│   │   │   │   ├── UsuarioController.java
│   │   │   │   ├── SearchController.java
│   │   │   │   └── EstadisticasController.java
│   │   │   ├── topico/              # Entidades y DTOs de tópicos
│   │   │   │   ├── service/         # TopicoService
│   │   │   │   ├── Topico.java
│   │   │   │   └── TopicoRepository.java
│   │   │   ├── usuario/             # Entidades y DTOs de usuarios
│   │   │   │   ├── Usuario.java
│   │   │   │   └── UsuarioRepository.java
│   │   │   ├── respuesta/           # Sistema de respuestas
│   │   │   │   ├── Respuesta.java
│   │   │   │   └── RespuestaRepository.java
│   │   │   ├── notification/        # Sistema de notificaciones
│   │   │   │   ├── NotificationService.java
│   │   │   │   └── Notification.java
│   │   │   ├── service/             # Servicios
│   │   │   │   ├── BusquedaService.java
│   │   │   │   └── EstadisticasService.java
│   │   │   └── stats/               # DTOs de estadísticas
│   │   │       └── DashboardStatsDTO.java
│   │   ├── infra/
│   │   │   ├── config/              # Configuraciones
│   │   │   │   ├── SecurityConfigurations.java
│   │   │   │   ├── OpenAPIConfiguration.java
│   │   │   │   └── CacheConfiguration.java
│   │   │   ├── cache/               # Sistema de cache personalizado
│   │   │   │   └── SimpleCache.java
│   │   │   ├── exceptions/          # Manejo de excepciones
│   │   │   │   ├── TratadorDeErrores.java
│   │   │   │   └── ValidacionException.java
│   │   │   ├── security/            # Seguridad JWT y Rate Limiting
│   │   │   │   ├── TokenService.java
│   │   │   │   ├── SecurityFilter.java
│   │   │   │   └── RateLimitService.java
│   │   │   └── log/                 # Sistema de logging
│   │   │       └── DatabaseLogService.java
│   │   └── ForumHubApplication.java
│   └── resources/
│       ├── db/migration/            # Migraciones Flyway (V1-V8)
│       │   ├── V1__create_table_usuarios.sql
│       │   ├── V2__create_table_topicos.sql
│       │   ├── V3__create_table_respuestas.sql
│       │   ├── V4__add_indexes.sql
│       │   ├── V5__create_rate_limiting.sql
│       │   ├── V6__create_system_logs.sql
│       │   ├── V7__create_notifications.sql
│       │   └── V8__optimize_queries.sql
│       ├── application.properties
│       └── application.yml
```

## 🗄️ Base de Datos

### Esquema Completo

#### Tabla `usuarios`
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `login` (VARCHAR(100), UNIQUE, NOT NULL)
- `clave` (VARCHAR(255), NOT NULL)
- `rol` (ENUM: USUARIO, MODERADOR, ADMIN)

#### Tabla `topicos`
- `id`, `titulo`, `mensaje`, `fecha_creacion`, `status`, `curso`, `usuario_id`
- **Índices FULLTEXT** en `titulo` y `mensaje` para búsqueda optimizada

#### Tabla `respuestas`
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `mensaje` (TEXT, NOT NULL)
- `fecha_creacion` (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- `solucion` (BOOLEAN, DEFAULT FALSE)
- `topico_id` (BIGINT, FK)
- `usuario_id` (BIGINT, FK)

#### Tablas de Sistema
- `notifications` - Sistema de notificaciones
- `rate_limits` - Control de límites de API
- `system_logs` - Auditoría completa

### Migraciones Flyway

8 migraciones progresivas desde V1 hasta V8:
- V1-V3: Tablas básicas
- V4: Índices de optimización y FULLTEXT
- V5-V7: Características avanzadas
- V8: Optimizaciones finales de queries

## 🔐 Seguridad

### Validación de Contraseñas
- Mínimo 8 caracteres
- Al menos 1 mayúscula
- Al menos 1 número  
- Al menos 1 carácter especial `!@#$%^&*()-+`

### Sistema de Roles
- **USUARIO**: Operaciones básicas
- **MODERADOR**: Gestión extendida
- **ADMIN**: Control total del sistema

### Rate Limiting
- 100 requests por minuto por IP
- Configuración por endpoint
- Almacenamiento en base de datos

### Autenticación JWT
- **Algoritmo**: HMAC256 con secreto configurable
- **Duración**: 2 horas (configurable)
- **Claims**: rol de usuario incluido

## ⚡ Optimizaciones

### Base de Datos
- **Índices estratégicos** en todas las columnas de búsqueda frecuente
- **FULLTEXT indexes** para búsqueda de contenido
- **Connection pooling** optimizado con HikariCP
- **Queries nativas** para operaciones complejas

### Cache System
- **Cache personalizado** con TTL configurable
- **Caffeine** como provider para máximo rendimiento
- Cache de tópicos frecuentemente accedidos

### Performance
- **Lazy loading** controlado para evitar N+1 queries
- **Batch processing** habilitado en Hibernate  
- **Paginación eficiente** en todos los listados
- **Entity Graphs** para fetch optimizado de relaciones

## 🔧 Nuevas Características vs v1.0

### ✅ Agregado en v2.0
- Sistema completo de respuestas y soluciones
- Búsqueda full-text avanzada
- Dashboard de estadísticas
- Sistema de notificaciones
- Rate limiting y seguridad avanzada
- Sistema de roles granular
- Cache inteligente
- Logging a base de datos
- Trending topics algorithm
- Validación robusta de contraseñas
- Queries optimizadas
- Configuración CORS avanzada

### 🔄 Mejorado de v1.0
- Arquitectura más modular y escalable
- Manejo de excepciones más robusto
- Documentación Swagger más detallada
- Configuraciones más flexibles


## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

### Guías de Desarrollo
- Sigue las convenciones de naming de Spring Boot
- Añade tests para nuevas funcionalidades
- Actualiza la documentación Swagger
- Verifica que las migraciones Flyway sean compatibles

## 📄 Licencia

Distribuido bajo la Licencia MIT.

## 👨‍💻 Autor

**spyke52**

- GitHub: [@spyke52](https://github.com/spyke52)
- Proyecto: [ForumHub API](https://github.com/spyke52/ForumHubAlura)

## 🏆 Agradecimientos

- [Alura](https://www.alura.com.br/) - Por el desafío y la oportunidad de aprendizaje


---

⭐ **Si este proyecto te fue útil, no olvides darle una estrella!**

📊 **Estadísticas del proyecto:**
- 50+ archivos de código
- 8 migraciones de base de datos  
- 15+ endpoints REST
- Arquitectura escalable y modular
- Cobertura completa de funcionalidades de foro
