# Sentiment API

**Análisis Inteligente de Feedback de Clientes**

Sistema que ayuda a empresas a entender el sentimiento de los comentarios de sus clientes mediante análisis automático de texto con modelos de Machine Learning.

---

## 📋 Descripción del Proyecto

**Sentiment API** es una API simple que recibe textos (comentarios, reseñas o tweets), aplica un modelo de Data Science para clasificar el sentimiento (**Positivo** / **Negativo**) y devuelve el resultado en formato JSON, permitiendo que las aplicaciones consuman esta predicción automáticamente.

### 🎯 Sector de Negocio

**Atención al cliente / Marketing / Operaciones** — Empresas que recopilan opiniones de clientes (reseñas, comentarios en redes sociales, encuestas de satisfacción) y quieren entender rápidamente si el sentimiento es positivo o negativo.

### 💼 Necesidad del Cliente

Un cliente (empresa) recibe muchos comentarios y no puede leerlos todos manualmente. Necesita:
- ✅ **Saber rápidamente** si los clientes se están quejando o elogiando
- ✅ **Priorizar respuestas** a comentarios negativos
- ✅ **Medir la satisfacción** a lo largo del tiempo

Este proyecto ofrece una solución automática para clasificar mensajes y generar información accionable.

### 📊 Validación de Mercado

El análisis de sentimiento es útil para:

- **Acelerar la atención al cliente**: Identificar urgencias y priorizar respuestas
- **Monitorear campañas de marketing**: Evaluar el impacto de estrategias publicitarias
- **Comparar la imagen de marca**: Analizar la percepción a lo largo del tiempo

Incluso una solución simple (modelo básico) tiene valor: las pequeñas y medianas empresas utilizan herramientas similares para entender los feedbacks sin un equipo dedicado.

---

## 🎓 Contexto del Hackathon

**Organización:** NoCountry / Alura LATAM  
**Equipo:** H12-25-L-Equipo 63  
**Período:** Diciembre 2025 – Enero 2026

### Objetivo Principal

Entregar un **MVP funcional** que demuestre la integración entre Data Science y Back-end:
- Un microservicio de Data Science (Flask) con el modelo cargado
- Una API que consume ese microservicio y responde a las peticiones

### Alcance

## ✨ Características Principales

- 🔍 **Análisis de Sentimiento**: Clasificación automática de textos (Positivo/Negativo)
- 📊 **Probabilidad Asociada**: Cada análisis incluye un nivel de confianza (0-1)
- ✅ **Validación Robusta**: Validación de entrada en capa de API
- 🌐 **API REST**: Endpoints versionados bajo `/v1`
- 📈 **Dashboard Visual**: Interfaz React con navegación por páginas
- 📦 **Batch + Analytics**: Análisis masivo, historial, filtros y exportación CSV
- 🤖 **Modelo ML**: Modelo entrenado con datasets especializados en reseñas de clientes

---

## 📦 Entregables

1. **Microservicio DS (Flask)**: Modelo entrenado serializado (`.pkl`) y endpoints de predicción
2. **Back-End (Spring Boot)**: API que consume el modelo, endpoint `/v1/sentiment`, logs y manejo de errores
3. **Documentación**: README con instrucciones de ejecución, ejemplos y dependencias
4. **Demonstración**: API en acción (Postman/cURL o dashboard) y explicación del modelo

---

## 🎯 Funcionalidades Exigidas (MVP)

- ✅ Endpoint `POST /v1/sentiment` que acepta `{"text": "..."}` y devuelve `{"prevision": "Positivo", "probabilidad": 0.87}`
- ✅ Modelo entrenado y cargable (archivo o microservicio DS)
- ✅ Validación de input (campo `text` obligatorio, 10–500 caracteres)
- ✅ Respuesta clara: label + probabilidad (0-1) y mensajes de error amigables
- ✅ Ejemplos de uso: 2 ejemplos reales (positivo, negativo)

Ver detalles completos del contrato de la API en [docs/API-CONTRACT.md](docs/API-CONTRACT.md).

---

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 21**
- **Spring Boot 4.0.1**
- **Spring Web MVC**
- **Spring Validation**
- **Lombok**
- **Maven**

### Data Science
- **Python 3.8+**
- **Pandas**: Lectura y limpieza de datos
- **scikit-learn**: TF-IDF + Logistic Regression
- **joblib**: Serialización del modelo
- **Flask**: Microservicio de predicción
- **NLTK**: Preprocesamiento de texto
- **deep_translator**: Traducción automática

Para más detalles sobre tecnologías y arquitectura, consulta [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

---

## 📦 Requisitos Previos

### Para Windows

- **Java 21** o superior
- **Maven 3.6+** (o usar el wrapper incluido: `mvnw.cmd`)
- **Docker Desktop para Windows** (para PostgreSQL)
- **Git**
- **Python 3.8+** (para ejecutar el microservicio DS)

---

## 🚀 Instalación y Ejecución

### Windows

#### Paso 1: Levantar servicios con Docker Compose

1. Abre PowerShell o CMD y navega a la raíz del proyecto:
```powershell
cd SentimentAPI
```

2. Levanta los servicios (Postgres + Backend + Data Science + Dashboard):
```powershell
docker compose up -d --build
```

3. Verifica que los contenedores estén corriendo:
```powershell
docker ps
```

Deberías ver `sentiment-postgres`, `sentiment-backend`, `sentiment-datascience` y `sentiment-dashboard`.

4. (Opcional) Ver los logs:
```powershell
docker compose logs -f
```

5. (Opcional) Conectarte a PostgreSQL con psql:
```powershell
docker exec -it sentiment-postgres psql -U postgres -d sentiment_db
```

Para salir de psql, escribe: `\q`

#### Paso 2: Ejecutar la Aplicación Spring Boot (modo local)

1. Asegúrate de estar en la carpeta `backend`:
```powershell
cd backend
```

2. Compila el proyecto:
```powershell
mvnw.cmd clean install
```

3. Ejecuta la aplicación:
```powershell
mvnw.cmd spring-boot:run
```

La API estará disponible en `http://localhost:8080`

#### Dashboard (React)
Si usas Docker, el dashboard estará en `http://localhost:3000`.
Para desarrollo local con recarga en caliente:

```powershell
cd dashboard
npm install
npm run dev
```

### Data Science

```bash
cd datascience/python_service
pip install -r requirements.txt
python app.py
```

---

## 📖 Uso Básico

### Ejemplo Mínimo

```bash
curl -X POST http://localhost:8080/v1/sentiment \
  -H "Content-Type: application/json" \
  -d '{"text": "The service was excellent and very helpful"}'
```

### Respuesta

```json
{
  "prevision": "Positivo",
  "probabilidad": 0.87
}
```

La respuesta puede incluir opcionalmente `textoTraducido` y `palabrasClave`
cuando el microservicio de Data Science las devuelve.


## 🧭 Navegación del Dashboard

El dashboard está dividido en tres páginas principales:
- **Inicio**: análisis individual y métricas en tiempo real.
- **Historial**: consultas por rango, filtro por sentimiento, exportación CSV y paginación.
- **Batch**: carga CSV y resultados masivos con detalle en modal.

Acceso por defecto: `http://localhost:3000`

### Otros endpoints útiles

```bash
# Batch
curl -X POST http://localhost:8080/v1/sentiment/batch \
  -H "Content-Type: application/json" \
  -d '{"texts": ["Great service", "Terrible support"]}'

# Stats
curl http://localhost:8080/v1/stats

# Historial por rango
curl "http://localhost:8080/v1/analytics/range?startDate=01-01-2026&endDate=31-01-2026&sentiment=Positivo&page=0&size=15"

# Export CSV
curl -O -J "http://localhost:8080/v1/analytics/range/export?startDate=01-01-2026&endDate=31-01-2026"
```

Para ejemplos completos, validaciones, códigos de error y más detalles, consulta [docs/API-CONTRACT.md](docs/API-CONTRACT.md).

---

## 🤖 Explicación del modelo

Luego de que la API valide el comentario, este pasa por el microservicio DS donde:
1. **El texto es limpiado y procesado:** hay que normalizar los caracteres especiales (signos, puntuaciones, etc. no son relevantes).
2. **Se traduce al idioma ingles:** Se globaliza el idioma para mayor facilidad de análisis.
3. **Se aplica vectorización TF-IDF:** El modelo solo entiende números; este proceso convierte el texto en una matriz.
4. **El modelo clasifica:** Entrenado con un dataset de calidad en inglés y con ayuda de los pasos anteriores, el modelo entiende datos de forma secuencial a su vez que comprende expresiones, metáforas, etc. para clasificar binariamente y un nivel de confianza en porcentaje (que tan seguro está).
5. **Envia resultados:** Una vez que sabe la categoría del comentario y la probabilidad asociada, este envía estos datos a la API para presentar al dasboard. Adicionalmente, envia el texto traducido al inglés y las palabras clave que el modelo detecte (en inglés también).

Para más detalles sobre las decisiones de la arquitectura del modelado, consulta [RFC-002.md](docs/RFC-002.md).

Para más detalles sobre la historia del comentario en el flujo funcional, consulta [ARCHITECTURE.md](docs/ARCHITECTURE.md).

---

## 📁 Estructura del Proyecto

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

## 📚 Documentación

- **[ARCHITECTURE.md](docs/ARCHITECTURE.md)**: Arquitectura del sistema, flujo de datos y decisiones técnicas
- **[API-CONTRACT.md](docs/API-CONTRACT.md)**: Contrato completo de la API, endpoints, validaciones y ejemplos
- **[GITHUB_WORKFLOW.md](docs/GITHUB_WORKFLOW.md)**: Flujo de trabajo, ramas, commits y Pull Requests
- **[RFC-001](docs/RFC-001.md)**: Decisiones arquitectónicas y justificaciones
- **[RFC-002](docs/RFC-002.md)**: Decisiones del modelado y el equipo de Data Science

---

## 👥 Equipo

**Proyecto:** H12-25-L-Equipo 63  
**Período:** Diciembre 2025 – Enero 2026

| Integrante | Rol |
|------------|-----|
| Mauricio Flores | Líder general + Data Science |
| Yazmani Reyes | Data Science |
| Leandro Díaz | Data Science |
| Domingo Gálaz | Data Science |
| Javiera Pulgar | Back-End + Dashboard |
| Fernanda Fonseca | Back-End |

---

## 🔄 Flujo de Trabajo

Este proyecto sigue un flujo de trabajo basado en Git Flow. Para más detalles sobre ramas, commits y Pull Requests, consulta [docs/GITHUB_WORKFLOW.md](docs/GITHUB_WORKFLOW.md).

**Regla de oro:** ❌ Nunca commits directos a `main`. Todo debe pasar por Pull Requests y revisión.

---

## 🧪 Testing

### Backend

```bash
cd backend
./mvnw test
```

### Data Science

Los tests del modelo se realizan en el notebook de Jupyter, incluyendo métricas de desempeño (Accuracy, Precision, Recall, F1-score).

---

## ⚠️ Limitaciones Actuales

- **Idioma**: El microservicio DS detecta idioma y traduce a inglés para el análisis
- **Longitud**: Textos entre 10 y 500 caracteres
- **Modelo**: TF-IDF + Logistic Regression

---


## 🔗 Enlaces Útiles

- [Documentación de Spring Boot](https://spring.io/projects/spring-boot)
- [scikit-learn Documentation](https://scikit-learn.org/stable/)
- [Conventional Commits](https://www.conventionalcommits.org/)

---

**Desarrollado con ❤️ por H12-25-L-Equipo 63**  
**Hackathon NoCountry / Alura LATAM - Diciembre 2025 - Enero 2026**
