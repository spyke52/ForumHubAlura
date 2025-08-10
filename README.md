# 🚀 ForumHub API

API REST para el foro de discusión ForumHub desarrollada con Spring Boot. Este proyecto implementa un sistema completo de gestión de tópicos de foro con autenticación JWT y operaciones CRUD.

## 📋 Tabla de Contenidos

- [Características](#características)
- [Tecnologías](#tecnologías)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Advertencias Importantes](#advertencias-importantes)
- [Uso](#uso)
- [Endpoints de la API](#endpoints-de-la-api)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Base de Datos](#base-de-datos)
- [Seguridad](#seguridad)
- [Contribuir](#contribuir)
- [Licencia](#licencia)
- [Autor](#autor)

## ✨ Características

- **Autenticación JWT**: Sistema de autenticación seguro con tokens JWT
- **Gestión de Usuarios**: Registro y autenticación de usuarios
- **CRUD de Tópicos**: Crear, leer, actualizar y eliminar tópicos
- **Validación de Duplicados**: Previene la creación de tópicos duplicados
- **Autorización**: Control de permisos para modificar/eliminar tópicos propios
- **Paginación**: Listado de tópicos con paginación
- **Documentación API**: Swagger UI integrado
- **Manejo de Excepciones**: Sistema robusto de manejo de errores
- **Migraciones de BD**: Control de versiones de base de datos con Flyway

## 🛠 Tecnologías

- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Security**
- **Spring Data JPA**
- **MySQL**
- **Flyway** (Migraciones)
- **JWT** (Autenticación)
- **Lombok**
- **Bean Validation**
- **SpringDoc OpenAPI** (Swagger)
- **Maven**

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
CREATE DATABASE forumhub_db;
```

3. **Configura las variables de entorno**
```bash
export DB_PASSWORD=tu_password_mysql
export JWT_SECRET=tu_clave_secreta_jwt
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

### Variables de Entorno

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `DB_PASSWORD` | Contraseña de MySQL | - |
| `JWT_SECRET` | Clave secreta para JWT | 12345678 |

### Configuración de Base de Datos

El archivo `application.properties` incluye:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/forumhub_db
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}
```

## ⚠️ Advertencias Importantes

1. **Flyway Clean en Producción**:
   ```properties
   spring.flyway.clean-on-validation-error=true
   ```
   ¡Esta configuración es PELIGROSA en producción! Borrará toda tu base de datos si hay errores de validación. Recomendado solo para entornos de desarrollo. Para producción, cambia a:
   ```properties
   spring.flyway.clean-disabled=true
   spring.flyway.clean-on-validation-error=false
   ```

2. **Endpoint Temporal de Verificación**:
   El endpoint `GET /usuarios/verify-password` es solo para pruebas. ¡NUNCA lo dejes activado en producción! Expone un método para verificar contraseñas que podría ser explotado.

3. **Seguridad JWT**:
   El valor por defecto `JWT_SECRET=12345678` es inseguro. En producción:
   - Usa un secreto de al menos 64 caracteres
   - Genera uno con: `openssl rand -base64 64`

4. **Coincidencia de IDs en Actualización**:
   En `PUT /topicos/{id}` debes asegurarte que el ID en el path coincida con el ID en el cuerpo de la solicitud. Ejemplo correcto:
   ```json
   // PUT /topicos/123
   {
     "id": 123, // DEBE coincidir con el path
     "titulo": "Nuevo título"
   }
   ```

5. **Zona Horaria de Tokens**:
   Los tokens JWT usan UTC-3 (Argentina). Si despliegas en otra zona, modifica la clase `TokenService`.

## 📖 Uso

### Documentación de la API

Accede a la documentación Swagger en: `http://localhost:8080/swagger-ui/index.html`

> ⚠️ **No usar Flyway Clean en producción**:  
> La configuración actual puede BORRAR TODOS TUS DATOS si hay errores. Solo para desarrollo.

### Flujo básico de uso

1. **Registrar usuario**
2. **Iniciar sesión** para obtener token JWT
3. **Crear tópicos** usando el token
4. **Gestionar tópicos** (ver, actualizar, eliminar)

## 🛡 Endpoints de la API

### Autenticación

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/login` | Autenticar usuario |
| POST | `/usuarios` | Registrar nuevo usuario |

### Usuarios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/usuarios/{id}` | Obtener usuario por ID |

### Tópicos (Requiere autenticación)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/topicos` | Listar tópicos (paginado) |
| POST | `/topicos` | Crear nuevo tópico |
| GET | `/topicos/{id}` | Obtener tópico por ID |
| PUT | `/topicos/{id}` | Actualizar tópico |
| DELETE | `/topicos/{id}` | Eliminar tópico |

### Ejemplos de uso

#### 1. Registrar Usuario
```bash
curl -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "login": "usuario1",
    "clave": "password123"
  }'
```

#### 2. Iniciar Sesión
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{
    "login": "usuario1",
    "clave": "password123"
  }'
```

#### 3. Crear Tópico
```bash
curl -X POST http://localhost:8080/topicos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer tu_jwt_token" \
  -d '{
    "titulo": "¿Cómo usar Spring Boot?",
    "mensaje": "Necesito ayuda con Spring Boot",
    "curso": "Spring Framework"
  }'
```

#### 4. Listar Tópicos
```bash
curl -X GET "http://localhost:8080/topicos?page=0&size=10" \
  -H "Authorization: Bearer tu_jwt_token"
```

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/alura/forum/forumHub/
│   │   ├── domain/
│   │   │   ├── controller/          # Controladores REST
│   │   │   ├── topico/              # Entidades y DTOs de tópicos
│   │   │   └── usuario/             # Entidades y DTOs de usuarios
│   │   ├── infra/
│   │   │   ├── exceptions/          # Manejo de excepciones
│   │   │   └── security/            # Configuración de seguridad
│   │   └── ForumHubApplication.java
│   └── resources/
│       ├── db/migration/            # Scripts de migración Flyway
│       └── application.properties
```

## 🗄️ Base de Datos

### Esquema

#### Tabla `usuarios`
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `login` (VARCHAR(100), UNIQUE, NOT NULL)
- `clave` (VARCHAR(255), NOT NULL)

#### Tabla `topicos`
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `titulo` (VARCHAR(150), NOT NULL)
- `mensaje` (TEXT, NOT NULL)
- `fecha_creacion` (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- `status` (VARCHAR(50), DEFAULT 'NO_RESPONDIDO')
- `curso` (VARCHAR(100), NOT NULL)
- `usuario_id` (BIGINT, FK)

### Migraciones

Las migraciones se ejecutan automáticamente con Flyway:
- `V1__create_table_usuarios.sql`
- `V2__create_table_topicos.sql`

## 🔐 Seguridad

### Autenticación JWT

- **Algoritmo**: HMAC256
- **Duración**: 2 horas
- **Zona horaria**: UTC-3 (Argentina)

### Autorización

- Los usuarios solo pueden modificar/eliminar sus propios tópicos
- Validación de duplicados en título y mensaje
- Encriptación de contraseñas con BCrypt

> ⚠️ **Cuidado con el método `verify-password`**:  
> El endpoint temporal `GET /usuarios/verify-password` debe ser ELIMINADO en despliegues de producción. Actualmente permite verificar contraseñas sin autenticación.

### Headers de Autenticación

```
Authorization: Bearer <jwt_token>
```

## 🔧 Validaciones

- **Tópicos duplicados**: Previene la creación de tópicos con el mismo título y mensaje
- **Campos obligatorios**: Validación de campos requeridos
- **Permisos de usuario**: Solo el autor puede modificar sus tópicos
- **Tokens JWT**: Validación de tokens en cada request protegido

> ⚠️ **Validación de IDs duplicados**:  
> Al actualizar un tópico (`PUT /topicos/{id}`), debes incluir el ID tanto en la URL como en el cuerpo JSON, y ambos deben coincidir. Ejemplo:  
> `PUT /topicos/123` con `{"id": 123, ...}`

## 🐛 Manejo de Errores

La API maneja los siguientes tipos de errores:

- **400 Bad Request**: Validación de datos
- **401 Unauthorized**: Token inválido o faltante
- **404 Not Found**: Recurso no encontrado
- **500 Internal Server Error**: Errores del servidor

## 🤝 Contribuir

1. Haz fork del proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commitea tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT.

## 👨‍💻 Autor

**spyke52** 

Proyecto Link: [https://github.com/spyke52/ForumHubAlura](https://github.com/spyke52/ForumHubAlura)

## ⚠️ Notas de Seguridad Críticas

1. **NUNCA uses en producción**:
   - Valor `JWT_SECRET` por defecto
   - Configuración `flyway.clean-on-validation-error=true`
   - Endpoint `GET /usuarios/verify-password`

2. **Protege tu base de datos**:
   - Cambia el usuario `root` por uno con menos privilegios
   - Usa conexiones SSL en producción:
   ```properties
   spring.datasource.url=jdbc:mysql://...?useSSL=true&requireSSL=true
   ```

3. **Revoca tokens comprometidos**:
   Esta implementación no incluye revocación de tokens. Para producción, considera agregar:
   - Listas negras de tokens
   - Tiempos de expiración más cortos
   - Refresh tokens

---

⭐ ¡No olvides dar una estrella al proyecto si te fue útil!
