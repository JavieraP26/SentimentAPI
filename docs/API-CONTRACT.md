# Contrato de la API - Sentiment API

**Proyecto:** SENTIMENT API — Análisis Inteligente de Feedback de Clientes  
**Equipo:** H12-25-L-Equipo 63  
**Período:** Diciembre 2025 – Enero 2026

---

## 1. Información General

Esta documentación describe el contrato de la API REST para el análisis de sentimiento de comentarios de clientes.

**Base URL:** (A definir según entorno)  
**Formato:** JSON  
**Idioma de mensajes:** Español  
**Idioma de entrada:** Inglés (requerido)

### 1.1 Contrato de Integración (Requerimiento del Cliente - NoCountry)

**⚠️ IMPORTANTE: Formato Obligatorio**

Según las especificaciones del cliente (NoCountry), el formato JSON de entrada y salida **DEBE** cumplir estrictamente con el siguiente contrato:

**Entrada:**
```json
{"text": "..."}
```

**Salida (Obligatoria):**
```json
{
  "prevision": "Positivo",
  "probabilidad": 0.9
}
```

**Campos requeridos en la respuesta:**
- `prevision` (String): Clasificación del sentimiento. Valores: `"Positivo"`, `"Neutro"`, `"Negativo"`
- `probabilidad` (Number): Probabilidad del resultado entre 0 y 1

**Requerimiento:** La API **DEBE** implementar este formato exacto. No se aceptan variaciones como `sentiment`/`probability` u otros formatos alternativos. El contrato debe cumplirse tal como lo exige el cliente.

---

## 2. Endpoints Disponibles

### 2.1 Analizar Sentimiento

Analiza el sentimiento de un texto y devuelve la clasificación con su probabilidad asociada.

#### Request

**Método:** `POST`  
**Ruta:** `/sentiment`  
**Content-Type:** `application/json`

**Body:**

```json
{
  "text": "The service was excellent and very helpful"
}
```

**Campos:**

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `text` | String | Sí | Texto a analizar (en inglés) |

#### Response - Éxito (200 OK)

**⚠️ FORMATO OBLIGATORIO (Requerimiento del Cliente - NoCountry):**

```json
{
  "prevision": "Positivo",
  "probabilidad": 0.87
}
```

**Campos de respuesta (Obligatorios):**

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `prevision` | String | **Sí** | Clasificación del sentimiento. Valores posibles: `"Positivo"`, `"Neutro"`, `"Negativo"` |
| `probabilidad` | Number | **Sí** | Probabilidad del resultado. Valor entre 0 y 1 (inclusive) |

**Requerimiento del cliente:** Este formato **DEBE** ser implementado exactamente como se especifica. La API debe devolver `prevision` y `probabilidad` (en español) según las especificaciones de NoCountry.

**Ejemplo de respuesta positiva:**

```json
{
  "prevision": "Positivo",
  "probabilidad": 0.92
}
```

**Ejemplo de respuesta negativa:**

```json
{
  "prevision": "Negativo",
  "probabilidad": 0.85
}
```

**Ejemplo de respuesta neutra:**

```json
{
  "prevision": "Neutro",
  "probabilidad": 0.65
}
```

---

## 3. Reglas de Validación

### 3.1 Validaciones Requeridas

El campo `text` debe cumplir con las siguientes reglas:

1. **Campo obligatorio**: El campo `text` es requerido y no puede estar ausente
2. **No vacío**: El campo `text` no puede ser una cadena vacía (`""`)
3. **Longitud mínima**: El texto debe tener al menos 10 caracteres
4. **Longitud máxima**: El texto debe tener como máximo 200 caracteres (configurable)
5. **Idioma**: El texto debe estar escrito en inglés (validación de idioma)
6. **Caracteres permitidos**: El sistema puede restringir caracteres no válidos según acuerdos del equipo

### 3.2 Orden de Validación

Las validaciones se ejecutan en el siguiente orden mediante una cadena de responsabilidad (ValidationChain):

1. Validación de existencia del campo
2. Validación de texto no vacío
3. Validación de longitud (mínima y máxima)
4. Validación de formato/caracteres permitidos
5. Validación de idioma (inglés)

Si alguna validación falla, se detiene el proceso y se devuelve un error inmediatamente.

---

## 4. Manejo de Errores

La API devuelve errores en un formato uniforme para facilitar el manejo desde el frontend.

### 4.1 Formato de Error Estándar

Todos los errores siguen el siguiente formato estandarizado:

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción clara del problema en español",
  "path": "/sentiment",
  "details": {
    "text": "El campo 'text' es obligatorio"
  }
}
```

**Campos:**

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `timestamp` | String (ISO 8601) | Sí | Fecha y hora en que ocurrió el error |
| `status` | Number | Sí | Código de estado HTTP |
| `error` | String | Sí | Tipo de error (nombre del estado HTTP) |
| `message` | String | Sí | Mensaje descriptivo del error en español |
| `path` | String | Sí | Ruta del endpoint donde ocurrió el error |
| `details` | Object | No | Detalles adicionales del error. Presente en errores de validación con mapeo campo → mensaje |

### 4.2 Códigos de Estado HTTP

| Código | Significado | Cuándo se usa |
|--------|-------------|---------------|
| `200` | OK | Request exitoso, análisis completado |
| `400` | Bad Request | Error de validación (campo faltante, formato incorrecto, idioma no válido, etc.) |
| `500` | Internal Server Error | Error interno del servidor (fallo en integración con DS, error inesperado, etc.) |
| `503` | Service Unavailable | Servicio de Data Science no disponible (opcional) |

### 4.3 Ejemplos de Errores

#### 4.3.1 Error: Campo `text` faltante

**Request:**

```json
{}
```

**Response (400 Bad Request):**

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación en los datos de entrada",
  "path": "/sentiment",
  "details": {
    "text": "El campo 'text' es obligatorio"
  }
}
```

#### 4.3.2 Error: Texto vacío

**Request:**

```json
{
  "text": ""
}
```

**Response (400 Bad Request):**

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación en los datos de entrada",
  "path": "/sentiment",
  "details": {
    "text": "El campo 'text' no puede estar vacío"
  }
}
```

#### 4.3.3 Error: Longitud insuficiente

**Request:**

```json
{
  "text": "short"
}
```

**Response (400 Bad Request):**

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación en los datos de entrada",
  "path": "/sentiment",
  "details": {
    "text": "El campo 'text' debe tener al menos 10 caracteres"
  }
}
```

#### 4.3.4 Error: Longitud excedida

**Request:**

```json
{
  "text": "Este es un texto muy largo que excede el límite máximo permitido de caracteres para el análisis de sentimiento..."
}
```

**Response (400 Bad Request):**

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación en los datos de entrada",
  "path": "/sentiment",
  "details": {
    "text": "El campo 'text' no puede exceder 200 caracteres"
  }
}
```

#### 4.3.5 Error: Idioma no válido

**Request:**

```json
{
  "text": "El servicio fue excelente"
}
```

**Response (400 Bad Request):**

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación en los datos de entrada",
  "path": "/sentiment",
  "details": {
    "text": "El texto debe estar escrito en inglés. El sistema solo procesa textos en inglés para garantizar la exactitud del análisis."
  }
}
```

#### 4.3.6 Error: Error interno del servidor

**Response (500 Internal Server Error):**

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Error interno del servidor",
  "path": "/sentiment"
}
```

#### 4.3.7 Error: Servicio de Data Science no disponible

**Response (503 Service Unavailable):**

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 503,
  "error": "Service Unavailable",
  "message": "El servicio de análisis de sentimiento no está disponible en este momento. Por favor, intenta más tarde.",
  "path": "/sentiment"
}
```

### 4.4 Implementación de Manejo de Errores

La API implementa manejo centralizado de errores mediante `@RestControllerAdvice` en Spring Boot (`GlobalExceptionHandler`).

**Handlers implementados:**

| Excepción | Código HTTP | Cuándo se activa |
|-----------|-------------|------------------|
| `MethodArgumentNotValidException` | 400 | Errores de validación en DTOs con `@Valid` (campos faltantes, longitud, formato) |
| `ConstraintViolationException` | 400 | Violaciones de restricciones en parámetros de URL/path |
| `HttpMessageNotReadableException` | 400 | JSON mal formado o no deserializable |
| `ServiceUnavailableException` | 503 | Servicio de Data Science no disponible |
| `Exception` (genérico) | 500 | Cualquier error no capturado por handlers específicos |

**Características:**
- Formato uniforme de errores con `ApiErrorResponse`
- Detalles de validación por campo en el objeto `details`
- Logging de errores para debugging
- Mensajes en español para facilitar comprensión
- No expone detalles internos del sistema al cliente (seguridad)

---

## 5. Ejemplos de Uso

### 5.1 Ejemplo con cURL

**Request exitoso:**

```bash
curl -X POST http://localhost:8080/sentiment \
  -H "Content-Type: application/json" \
  -d '{"text": "The product quality is outstanding and the customer service is excellent"}'
```

**Response:**

```json
{
  "prevision": "Positivo",
  "probabilidad": 0.94
}
```

**Request con error:**

```bash
curl -X POST http://localhost:8080/sentiment \
  -H "Content-Type: application/json" \
  -d '{"text": "short"}'
```

**Response:**

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación en los datos de entrada",
  "path": "/sentiment",
  "details": {
    "text": "El campo 'text' debe tener al menos 10 caracteres"
  }
}
```

### 5.3 Ejemplo con Postman

1. **Método:** POST
2. **URL:** `http://localhost:8080/sentiment`
3. **Headers:**
   - `Content-Type: application/json`
4. **Body (raw JSON):**

```json
{
  "text": "I am very disappointed with the product quality"
}
```

5. **Response esperado:**

```json
{
  "prevision": "Negativo",
  "probabilidad": 0.89
}
```

---

## 6. Consideraciones Importantes

### 6.1 Idioma

- **Requerido:** El texto debe estar en inglés
- **Validación:** El backend valida el idioma antes de procesar
- **Razón:** El modelo de Data Science está entrenado exclusivamente con datos en inglés para garantizar precisión

### 6.2 Formato de Respuesta

- Todas las respuestas exitosas **DEBEN** devolver `prevision` y `probabilidad` (formato obligatorio según especificaciones del cliente)
- El campo `prevision` siempre será uno de: `"Positivo"`, `"Neutro"`, `"Negativo"`
- El campo `probabilidad` siempre será un número entre 0 y 1 (inclusive)

### 6.3 Consistencia

- La API mantiene un formato consistente para errores para que el dashboard pueda mostrar mensajes claros
- Los mensajes de error están en español para facilitar la comprensión
- Los códigos de estado HTTP siguen las convenciones estándar

---

## 7. Nota sobre Documentación Futura

Esta documentación está escrita en Markdown para facilitar el desarrollo del MVP. 
En futuras iteraciones, la documentación migrará a OpenAPI/Swagger para:

- Generación automática de clientes SDK
- Interfaz interactiva de pruebas (Swagger UI)
- Validación automática de contratos
- Mejor integración con herramientas de CI/CD (En un futuro)

La estructura actual facilita esta transición, ya que toda la información necesaria 
está documentada y puede mapearse directamente a un spec OpenAPI.

---

## 8. Versión de la API

**Versión actual:** 1.0.0 (`/v1/sentiment`).
