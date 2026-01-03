# Arquitectura del Sistema - Sentiment API

**Proyecto:** SENTIMENT API — Análisis Inteligente de Feedback de Clientes  
**Equipo:** H12-25-L-Equipo 63  
**Período:** Diciembre 2025 – Enero 2026

---

## 1. Visión del Proyecto

Estamos creando un sistema que ayuda a empresas a entender el sentimiento de los comentarios de sus clientes (reseñas, feedback, redes sociales):

- El sistema recibe textos por clientes
- Los clasifica como **Positivo** / **Neutro** / **Negativo** con una probabilidad asociada
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
| Mauricio | Líder general + Data Science |
| Yazmani | Data Science                 |
| Leandro Diaz | Data Science                 |
| Domingo Galaz | Data Science                 |
| Manuela Mejia | Back-End                     |
| Javiera | Back-End + Front-End         |
| Fernanda | Back-End                     |

**Principio:** Cada persona se especializa en su área, pero todos entienden el flujo completo.

---

## 3. Flujo Funcional (Historia del Comentario)

### Flujo Completo

1. **Cliente** escribe una opinión en el dashboard mediante el endpoint POST
2. **Dashboard** envía la opinión a la API con un JSON: `{ "text": "..." }`
3. **API** valida mediante una capa de ValidationChain:
   - Que exista el campo `text`
   - Que no esté vacío
   - Cumple con longitud mínima y máxima (ejemplo: 10–200 caracteres)
   - No contiene caracteres o formatos no soportados
   - Que cumpla con el comentario escrito en idioma inglés
   - Si falla alguna regla → devuelve un mensaje de error claro a la app (código 400)
4. Si el texto es válido, la **API** manda el texto al modelo de Data Science
5. El **modelo de sentimiento** analiza el texto y devuelve:
   - Una etiqueta (Positivo / Neutro / Negativo)
   - Una probabilidad (0–1)
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
- Envía el texto al dashboard vía POST `/sentiment`

#### API REST (Back-End)
**Capa de validación (ValidationChain):**
- Verifica estructura del JSON
- Valida reglas de negocio (longitud, caracteres permitidos, idioma)

**Capa de integración DS:**
- Envía el texto al modelo de Data Science (por ahora se puede simular con un stub o un endpoint sencillo)
- Recibe los resultados del modelo (etiqueta y probabilidad)

**Capa de respuesta API:**
- Arma un JSON claro y estable para el dashboard
- Maneja errores (por ejemplo, si DS no responde)

#### Modelo de Data Science (Python / Colab)
- Entrenado con un dataset de comentarios de clientes en inglés
- Pipeline típico (propuesta):
  - Limpieza de texto
  - Extracción de features (por ejemplo, TF-IDF)
  - Modelo supervisado (Logistic Regression, Naive Bayes u otro)
- Expone una función de predicción (en notebook) o idealmente un endpoint que reciba texto y devuelva etiqueta de sentimiento y probabilidad
- Se documentan métricas básicas: Accuracy, Precision, Recall, F1 score

#### Dashboard básico
- Consume la API para enviar textos o consultar resultados
- Muestra:
  - Sentimiento de un texto específico
  - Estadísticas simples (porcentaje de positivos/negativos, ejemplos)
- **No ejecuta modelos; solo visualiza** lo que envía la API

### 4.2 Arquitectura en Capas (Backend)

El backend implementa una arquitectura en capas alineada con buenas prácticas de Spring Boot:

- **Controller**: Endpoints REST, recibe requests HTTP
- **Validation**: Validaciones de lógica de negocio (ValidationChain)
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

### 4.3 Patrón Chain of Responsibility

Se adopta el patrón Chain of Responsibility para manejar validaciones y pasos de procesamiento en backend.

**Motivos:**
- Evitar flujos rígidos basados en if/else
- Encapsular responsabilidades individuales
- Facilitar extensión del flujo sin modificar lógica existente
- Mejorar legibilidad y trazabilidad

**Ejemplos de responsabilidades en la cadena:**
- Validación de texto vacío
- Validación de longitud
- Detección de idioma
- Validaciones previas al análisis del modelo

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
├── README.md                    # Documentación raíz
│
├── backend/                     # API Spring Boot
│   ├── src/
│   │   ├── main/java/com/sentiment/backend/
│   │   │   ├── controller/      # Endpoints REST
│   │   │   ├── validation/      # Validaciones de lógica de negocio
│   │   │   ├── service/         # Lógica de negocio
│   │   │   ├── dto/             # Objetos de entrada/salida
│   │   │   ├── domain/          # Entidades de negocio
│   │   │   ├── config/          # Configuraciones
│   │   │   ├── mapper/          # Mappers entre capas
│   │   │   └── exception/       # Manejo de errores
│   │   └── test/java/           # Pruebas
│   └── pom.xml                  # Dependencias Maven
│
├── datascience/                 # Modelos y datasets
│   ├── notebooks/
│   │   └── SentimentModel.ipynb # Colab, convertido a .ipynb
│   └── datasets/
│       ├── training_data.csv
│       └── validation_data.csv
│
├── dashboard/                   # Frontend
│   ├── src/
│   │   ├── pages/
│   │   ├── components/
│   │   └── services/
│   └── package.json
│
└── docs/                        # Documentación
    ├── ARCHITECTURE.md          # Este archivo
    ├── API-CONTRACT.md          # Contrato de la API
    ├── GITHUB_WORKFLOW.md       # Flujo de trabajo en GitHub
    └── RFC-001                  # Decisiones arquitectónicas (RFC)
```

---

## 6. Decisiones Arquitectónicas Clave

Para información detallada sobre decisiones arquitectónicas, alternativas consideradas y trade-offs, consultar el documento [RFC-001](RFC-001.md).

### Resumen de Decisiones:

- **Arquitectura en capas**: Separación clara de responsabilidades
- **Backend como fuente única de verdad**: Toda la lógica de negocio en el backend
- **Frontend pasivo**: El dashboard solo visualiza datos
- **Chain of Responsibility**: Para validaciones y procesamiento
- **Idioma del modelo**: Solo inglés (validado en backend)
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
     │ POST /sentiment
     ▼
┌─────────────────────────────────────────┐
│      API REST (Spring Boot)             │
│  ┌──────────────────────────────────┐   │
│  │ ValidationChain                  │   │
│  │  - Validar estructura JSON       │   │
│  │  - Validar longitud              │   │
│  │  - Validar idioma (inglés)       │   │
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
│  Modelo Data Science (Python/Colab)     │
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
│  - Estadísticas simples                 │
└─────────────────────────────────────────┘
```

---

## 8. Consideraciones Técnicas

### 8.1 Idioma y Validación

- El modelo está entrenado exclusivamente con datasets en **inglés**
- El backend valida que el texto esté en inglés antes de procesarlo
- Textos en otros idiomas son rechazados con un mensaje explicativo (código 400)
- Esta validación se realiza en el backend, no en el frontend

### 8.2 Integración con Data Science

- Inicialmente, el modelo puede simularse con un stub o endpoint sencillo
- La integración ideal sería mediante un endpoint HTTP que el backend consume
- El backend maneja errores si el servicio de DS no responde o falla

### 8.3 Manejo de Errores

- Todos los errores se manejan de forma centralizada mediante `@RestControllerAdvice`
- Formato uniforme de errores para facilitar el consumo desde el frontend
- Ver detalles en [API-CONTRACT.md](./API-CONTRACT.md)

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

