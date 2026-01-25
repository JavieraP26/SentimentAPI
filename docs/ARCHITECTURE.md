# Arquitectura del Sistema - Sentiment API

**Proyecto:** SENTIMENT API — Análisis Inteligente de Feedback de Clientes  
**Equipo:** H12-25-L-Equipo 63  
**Período:** Diciembre 2025 – Enero 2026

---

## 1. Visión del Proyecto

Estamos creando un sistema que ayuda a empresas a entender el sentimiento de los comentarios de sus clientes (reseñas, feedback, redes sociales):

- El sistema recibe textos por clientes
- Los clasifica como **Positivo** / **Negativo** con una probabilidad asociada
- Muestra resultados en un dashboard sencillo, entendible para personas no técnicas con un lenguaje humano

### Objetivo del Hackathon

Demostrar integración real entre:
- **Data Science** (modelo en Colab/Python)
- **Back-End** (API REST en Java/Spring Boot)
- **Frontend** (Dashboard de visualización)

Entregar un MVP funcional, bien organizado y con buenas prácticas de colaboración (ramas, PR y documentación).

---

## 2. Equipo y Roles

| Integrante | Rol                          |
|------------|------------------------------|
| Mauricio Flores | Líder general + Data Science |
| Yazmani Reyes | Data Science                 |
| Leandro Díaz | Data Science                 |
| Domingo Gálaz | Data Science                 |
| Javiera Pulgar | Back-End + Front-End         |
| Fernanda Fonseca | Back-End                     |

**Principio:** Cada persona se especializa en su área, pero todos entienden el flujo completo.

---

## 3. Flujo Funcional (Historia del Comentario)

### Flujo Completo

1. **Cliente** escribe una opinión en el dashboard
2. **Dashboard** envía la opinión a la API con un JSON: `{ "text": "..." }`
3. **API** valida mediante Bean Validation en DTOs:
   - Que exista el campo `text`
   - Que no esté vacío
  - Cumple con longitud mínima y máxima (ejemplo: 10–500 caracteres)
   - Si falla alguna regla → devuelve un mensaje de error claro a la app (código 400)
4. Si el texto es válido, la **API** manda el texto al microservicio de Data Science
5. El **modelo de sentimiento** analiza el texto:
   - Lo normaliza
   - Traduce automáticamente
   - Se vectoriza
   - Clasifica el sentimiento
   - Devuelve:
        - Una etiqueta (Positivo / Negativo)
        - Una probabilidad (0–1)
        - El texto traducido (si aplica)
        - Palabras clave (si aplica)
6. La **API** recibe ese resultado y lo traduce a un JSON estable según el formato requerido por el cliente, por ejemplo:
   ```json
   { "prevision": "Negativo", "probabilidad": 0.92 }
   ```
   **Nota:** El formato usa `prevision` y `probabilidad` según las especificaciones del cliente (NoCountry). Ver [API-CONTRACT.md](./API-CONTRACT.md) para más detalles.
7. El **Dashboard** consume ese JSON y muestra la información en lenguaje humano: sentimiento, probabilidades y estadísticas simples

### Flujo Resumido

```
Cliente → Dashboard → API (validación) → Modelo DS → API (formatea) → Dashboard
```

---

## 4. Arquitectura General

### 4.1 Bloques Principales

#### Cliente
- Escribe comentario/reseña en el dashboard
- Envía el texto a la API vía POST `/v1/sentiment`

#### API REST (Back-End)
**Validación de entrada (Bean Validation):**
- Verifica estructura del JSON
- Valida reglas de negocio (longitud y campo requerido)

**Capa de integración DS:**
- Envía el texto al microservicio de Data Science
- Recibe los resultados del modelo (etiqueta y probabilidad)

**Capa de respuesta API:**
- Arma un JSON claro y estable para el dashboard
- Maneja errores (por ejemplo, si DS no responde)

#### Modelo de Data Science (Python / Flask)
- Entrenado con un dataset de comentarios de clientes en inglés
- Pipeline típico:
  - Limpieza de texto
  - Traducción automática
  - Extracción de features (vectorización TF-IDF)
  - Modelo supervisado (Logistic Regression)
- Expone endpoints HTTP que reciben texto y devuelven etiqueta de sentimiento y probabilidad
- Se documentan métricas básicas: Accuracy, Precision, Recall, F1 score

#### Dashboard (React + Vite)
- Consume la API para enviar textos, consultar historial y batch
- Muestra:
  - Sentimiento de un texto específico
  - Estadísticas y gráficos en tiempo real
  - Historial con filtros y exportación CSV
  - Resultados batch con paginación y detalle
- **No ejecuta modelos; solo visualiza** lo que envía la API

### 4.2 Arquitectura en Capas (Backend)

El backend implementa una arquitectura en capas alineada con buenas prácticas de Spring Boot:

- **Controller**: Endpoints REST, recibe requests HTTP
- **Validation**: Validaciones de lógica de negocio (Bean Validation)
- **Service**: Lógica de negocio principal
- **DTO**: Objetos de entrada/salida para la API
- **Domain**: Entidades de negocio
- **Infrastructure**: Integraciones externas (BD, servicios DS, etc.)
- **Exception**: Manejo centralizado de errores

**Justificación:**
- Separación clara de responsabilidades
- Facilita mantenimiento y pruebas
- Escalable ante crecimiento del proyecto
- Compatible con buenas prácticas de Spring Boot

### 4.3 Validación con Bean Validation

Se adopta Bean Validation para manejar las validaciones de entrada en los DTOs.

**Motivos:**
- Reducir código manual de validación
- Declarar reglas cerca del modelo de entrada
- Integración nativa con Spring Boot y mensajes claros de error

**Validaciones aplicadas:**
- Campo requerido (`@NotBlank`)
- Longitud mínima y máxima (`@Size`)

### 4.4 Backend como Fuente Única de Verdad

El backend es responsable exclusivo de:
- Validaciones de entrada
- Lógica de negocio
- Procesamiento del análisis de sentimiento
- Interpretación de resultados del modelo
- Generación de mensajes comprensibles para humanos

El frontend no toma decisiones de dominio ni realiza cálculos. Esta separación reduce acoplamiento y facilita cambios futuros.

---

## 5. Estructura del Repositorio

Un único repositorio (monorepo) organizado así:

```
SentimentAPI/
├── README.md                       # Este archivo
│
├── backend/                        # API REST (Spring Boot)
│   ├── src/
│   │   ├── main/java/com/sentiment/backend/
│   │   │   ├── controller/         # Endpoints REST
│   │   │   ├── service/            # Lógica de negocio
│   │   │   ├── dto/                # Objetos de entrada/salida
│   │   │   ├── domain/             # Entidades de negocio
│   │   │   ├── config/             # Configuraciones
│   │   │   ├── mapper/             # Mappers entre capas
│   │   │   └── exception/          # Manejo de errores
│   │   └── resources/
│   │       └── application.yml
│   └── pom.xml
│
├── datascience/                    # Microservicio DS
│   └── python_service/
│       ├── app.py                  # API Flask (predict/predict_batch)
│       ├── requirements.txt        # Dependencias Python
│       ├── sentiment_model_v1.pkl  # Modelo entrenado serializado
│       └── tfidf_vectorizer_v1.pkl # Vectorizador serializado
│
├── dashboard/                      # Frontend (UI)
│   ├── src/
│   │   ├── pages/
│   │   ├── components/
│   │   └── services/
│   └── package.json
│
└── docs/                           # Documentación
    ├── ARCHITECTURE.md             # Arquitectura del sistema
    ├── API-CONTRACT.md             # Contrato de la API
    ├── GITHUB_WORKFLOW.md          # Flujo de trabajo
    ├── RFC-001.md                  # Decisiones arquitectónicas
    └── RFC-002.md                  # Decisiones de Data Science
```

---

## 6. Decisiones Arquitectónicas Clave

Para información detallada sobre decisiones arquitectónicas, alternativas consideradas y trade-offs, consultar el documento [RFC-001](RFC-001.md).

### Resumen de Decisiones:

- **Arquitectura en capas**: Separación clara de responsabilidades
- **Backend como fuente única de verdad**: Toda la lógica de negocio en el backend
- **Frontend pasivo**: El dashboard solo visualiza datos
- **Bean Validation**: Para validaciones de entrada
- **Idioma del modelo**: El DS traduce automáticamente a inglés
- **Idioma del código**: Backend en inglés, mensajes API en español, documentación en español

---

## 7. Diagrama de Flujo de Datos

```
┌─────────┐
│ Cliente │
└────┬────┘
     │
     │ POST { "text": "..." }
     ▼
┌─────────────────────────────────────────┐
│          Dashboard (Frontend)           │
│  - Recibe input del usuario             │
│  - Envía request a API                  │
│  - Visualiza resultados                 │
└────┬────────────────────────────────────┘
     │
     │ POST /v1/sentiment
     ▼
┌─────────────────────────────────────────┐
│      API REST (Spring Boot)             │
│  ┌──────────────────────────────────┐   │
│  │ Bean Validation                  │   │
│  │  - Validar estructura JSON       │   │
│  │  - Validar longitud              │   │
│  │  - Validar campo requerido       │   │
│  └──────┬───────────────────────────┘   │
│         │                               │
│         │ Texto validado                │
│         ▼                               │
│  ┌──────────────────────────────────┐   │
│  │ Service Layer                    │   │
│  │  - Integración con DS            │   │
│  │  - Formateo de respuesta         │   │
│  └──────┬───────────────────────────┘   │
└─────────┼───────────────────────────────┘
          │
          │ POST texto
          ▼
┌─────────────────────────────────────────┐
│  Modelo Data Science (Python)           │
│  - Limpieza de texto                    │
│  - Extracción de features               │
│  - Predicción de sentimiento            │
│  - Retorna: etiqueta + probabilidad     │
└──────┬──────────────────────────────────┘
       │
       │ Resultado del modelo (etiqueta + probabilidad)
       ▼
┌─────────────────────────────────────────┐
│      API REST (Formatea respuesta)      │
│  - Traduce a formato requerido          │
│    (prevision + probabilidad)           │
│  - Maneja errores                       │
└──────┬──────────────────────────────────┘
       │
       │ JSON Response
       ▼
┌─────────────────────────────────────────┐
│          Dashboard (Visualiza)          │
│  - Muestra sentimiento                  │
│  - Muestra probabilidad                 │
│  - Estadísticas y gráficos              │
└─────────────────────────────────────────┘
```

---

## 8. Consideraciones Técnicas

### 8.1 Idioma y Validación

- El modelo está entrenado exclusivamente con datasets en **inglés**
- El microservicio de Data Science traduce automáticamente los textos
- El backend no rechaza textos por idioma

### 8.2 Integración con Data Science

- Inicialmente, el modelo puede simularse con un stub o endpoint sencillo
- La integración ideal sería mediante un endpoint HTTP que el backend consume
- El backend maneja errores si el servicio de DS no responde o falla

### 8.3 Manejo de Errores

- Todos los errores se manejan de forma centralizada mediante `@RestControllerAdvice` (`GlobalExceptionHandler`)
- Formato uniforme de errores con `ApiErrorResponse` que incluye:
  - `timestamp`: Fecha y hora del error
  - `status`: Código HTTP
  - `error`: Tipo de error
  - `message`: Mensaje descriptivo en español
  - `path`: Ruta del endpoint
  - `details`: Detalles adicionales (opcional, para errores de validación)

**Handlers implementados:**
- `MethodArgumentNotValidException` → 400 (validaciones de DTOs)
- `ConstraintViolationException` → 400 (validaciones de parámetros)
- `HttpMessageNotReadableException` → 400 (JSON mal formado)
- `ServiceUnavailableException` → 503 (servicio DS no disponible)
- `Exception` (genérico) → 500 (errores inesperados)

**Excepciones personalizadas:**
- `ServiceUnavailableException`: Para cuando el servicio de Data Science no está disponible

Ver detalles completos en [API-CONTRACT.md](./API-CONTRACT.md)

---

## 9. Principios de Diseño

Se adopta explícitamente:

- **Principio de Responsabilidad Única (SRP)**: Cada clase tiene una responsabilidad clara
- **Separación de capas**: Cada capa tiene responsabilidades bien definidas
- **Clean Code**: Código legible, mantenible y expresivo
- **Documentación clara**: Cada componente debe estar documentado

Estas decisiones buscan:
- Código mantenible
- Facilidad de testing
- Claridad para nuevos integrantes
- Escalabilidad del proyecto

