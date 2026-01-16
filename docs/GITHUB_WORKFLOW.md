# Flujo de Trabajo en GitHub - Sentiment API

**Proyecto:** SENTIMENT API — Análisis Inteligente de Feedback de Clientes  
**Equipo:** H12-25-L-Equipo 63  
**Período:** Diciembre 2025 – Enero 2026

---

## 1. Estructura del Repositorio

Utilizamos un **monorepo** (un solo repositorio) con carpetas claras:

```
SentimentAPI/
├── README.md           # Documentación raíz del proyecto
├── backend/            # API Spring Boot
├── datascience/        # Notebooks y datasets
├── dashboard/          # Frontend básico
└── docs/               # Arquitectura, contrato de API, flujo de trabajo
```

---

## 2. Estrategia de Ramas

### 2.1 Ramas Principales

#### `main`
- **Propósito:** Código estable, listo para demo
- **Protección:** 
  - ❌ **Nada de commits directos**
  - ✅ Solo se actualiza mediante Pull Requests desde `developer`
  - ✅ Requiere revisión antes de merge

#### `developer`
- **Propósito:** Integración previa donde se juntan las features antes de pasar a `main`
- **Uso:** Rama de integración continua
- **Flujo:** Las ramas de feature hacen PR a `developer`

### 2.2 Ramas de Feature

Todas las nuevas funcionalidades se desarrollan en ramas de feature siguiendo el patrón:

```
feature/nombre-descriptivo
```

**Ejemplos:**
- `feature/backend-sentiment` - Endpoint de análisis de sentimiento
- `feature/ds-modelo` - Modelo de Data Science
- `feature/dashboard` - Dashboard de visualización
- `feature/validation-chain` - Implementación de cadena de validaciones
- `feature/error-handling` - Manejo centralizado de errores

**Convención de nombres:**
- Usar minúsculas
- Separar palabras con guiones (`-`)
- Ser descriptivo y específico
- Prefijo `feature/` para nuevas funcionalidades

---

## 3. Flujo de Trabajo (Workflow)

### 3.1 Flujo General

```
1. Crear rama feature desde developer
   ↓
2. Desarrollar funcionalidad
   ↓
3. Commit con mensajes claros
   ↓
4. Push de la rama feature
   ↓
5. Crear Pull Request a developer
   ↓
6. Revisión de código (al menos 1 reviewer)
   ↓
7. Merge a developer
   ↓
8. Testing en developer
   ↓
9. Cuando esté listo para demo → PR de developer a main
```

### 3.2 Reglas Importantes

✅ **SIEMPRE hacer:**
- Trabajar en ramas de feature
- Crear Pull Requests para integrar código
- Solicitar al menos una revisión antes de merge
- Actualizar documentación si es necesario
- Escribir commits descriptivos

❌ **NUNCA hacer:**
- Commits directos a `main`
- Commits directos a `developer` (excepto en casos excepcionales acordados)
- Merge sin revisión
- Push de código incompleto o con errores evidentes

---

## 4. Crear una Nueva Rama

### 4.1 Desde la Terminal

```bash
# 1. Asegurarse de estar en developer y actualizado
git checkout developer
git pull origin developer

# 2. Crear nueva rama feature desde developer
git checkout -b feature/nombre-de-la-feature

# 3. Verificar que estás en la rama correcta
git branch

# 4. Trabajar en tu código...

# 5. Hacer commits
git add .
git commit -m "feat: descripción del cambio"

# 6. Push de la rama (primera vez)
git push -u origin feature/nombre-de-la-feature
```

### 4.2 Desde GitHub (Web UI)

1. Ir al repositorio en GitHub
2. Click en el dropdown de ramas
3. Escribir el nombre de la nueva rama: `feature/nombre-de-la-feature`
4. Seleccionar "Create branch: feature/nombre-de-la-feature from 'developer'"
5. Clonar o hacer checkout de la nueva rama localmente

---

## 5. Estilo de Commits

Seguimos el estándar **Conventional Commits** para mantener un historial claro y consistente.

### 5.1 Formato

```
<tipo>: <descripción breve>

[descripción opcional más detallada]

[referencias opcionales]
```

### 5.2 Tipos de Commit

| Tipo | Cuándo usar | Ejemplo |
|------|-------------|---------|
| `feat` | Nueva funcionalidad | `feat: agregar endpoint POST /v1/sentiment` |
| `fix` | Corrección de bugs | `fix: corregir validación de longitud mínima` |
| `docs` | Cambios en documentación | `docs: actualizar API-CONTRACT.md con nuevos ejemplos` |
| `style` | Cambios de formato (espacios, comas, etc.) | `style: formatear código según estándares` |
| `refactor` | Refactorización de código | `refactor: extraer lógica de validación a clase separada` |
| `test` | Agregar o modificar tests | `test: agregar tests unitarios para validaciones Bean` |
| `chore` | Tareas de mantenimiento | `chore: actualizar dependencias Maven` |
| `perf` | Mejoras de rendimiento | `perf: optimizar consulta a base de datos` |

### 5.3 Ejemplos de Commits

**Buenos ejemplos:**

```
feat: agregar endpoint POST /v1/sentiment para análisis de sentimiento

docs: actualizar ARCHITECTURE.md con diagrama de flujo

fix: corregir validación de longitud en Bean Validation

refactor: ajustar validaciones con Bean Validation

test: agregar tests unitarios para SentimentService

chore: actualizar dependencias Spring Boot a versión 3.2.0
```

**Ejemplos a evitar:**

```
❌ "cambios"
❌ "fix bug"
❌ "actualizar"
❌ "WIP" (Work In Progress - mejor hacer commit cuando esté completo)
```

### 5.4 Buenas Prácticas

- **Ser descriptivo pero conciso:** La primera línea debe ser clara y completa
- **Usar imperativo:** "agregar" no "agrega" o "agregué"
- **Un commit = un cambio lógico:** Si haces varios cambios no relacionados, sepáralos en commits distintos
- **Revisar antes de commit:** Usa `git status` y `git diff` para ver qué estás commiteando

---

## 6. Pull Requests (PR)

### 6.1 Crear un Pull Request

#### Desde GitHub (Recomendado)

1. Ir al repositorio en GitHub
2. Click en "Pull requests"
3. Click en "New pull request"
4. Seleccionar:
   - **Base:** `developer` (o `main` si es PR final)
   - **Compare:** Tu rama `feature/nombre-de-la-feature`
5. Click en "Create pull request"

#### Desde la Terminal (con GitHub CLI)

```bash
gh pr create --base developer --head feature/nombre-de-la-feature --title "feat: agregar endpoint /sentiment" --body "Descripción detallada"
```

### 6.2 Título del Pull Request

Seguir el mismo formato que los commits:

```
feat: agregar endpoint /v1/sentiment
fix: corregir validación de longitud
docs: actualizar API-CONTRACT.md
```

### 6.3 Descripción del Pull Request

Incluir:

1. **Qué hace:** Descripción clara de los cambios
2. **Por qué:** Justificación o contexto del cambio
3. **Cómo probar:** Instrucciones para probar la funcionalidad
4. **Checklist:** Lista de verificación

**Plantilla sugerida:**

```markdown
## Descripción
Breve descripción de qué hace este PR.

Cambios realizados
- Cambio 1
- Cambio 2
- Cambio 3

## ¿Por qué este cambio?
Explicar el contexto o problema que resuelve.

## ¿Cómo probar? (Si aplica, documentación no necesita pruebas en si)
1. Paso 1
2. Paso 2
3. Paso 3

```

## Checklist
- [ ] El código sigue los estándares del proyecto
- [ ] Se agregaron tests (si aplica)
- [ ] La documentación fue actualizada (si aplica)
- [ ] No hay errores de compilación
- [ ] Los tests pasan correctamente


### 6.4 Revisión de Código

#### Como Autor del PR

- **Solicitar revisión:** Asignar al menos un revisor
- **Responder comentarios:** Revisar feedback y hacer cambios necesarios
- **Marcar como listo:** Cuando hayas abordado todos los comentarios

#### Como Revisor

- **Revisar el código:**
  - ¿Sigue las convenciones del proyecto?
  - ¿Está bien estructurado?
  - ¿Hay errores obvios?
  - ¿Falta documentación?
  - ¿Cumple con el contrato de la API o de DS?
- **Dar feedback constructivo:**
  - Ser respetuoso y claro
  - Explicar el "por qué" de las sugerencias
  - Aprobar si está bien o solicitar cambios
- **Marcar como aprobado:** Cuando el código cumple con los estándares

### 6.5 Proceso de Merge

1. **PR creado** → Esperar revisión
2. **Revisión aprobada** → Merge permitido
3. **Merge a `developer`** → Código integrado
4. **Testing** → Verificar que todo funciona en `developer`
5. **Cuando esté listo para demo** → PR de `developer` a `main`

**Regla de oro:** Al menos **una revisión aprobada** antes de hacer merge.

---

## 9. Buenas Prácticas Adicionales

### 9.1 Antes de Empezar a Trabajar

- ✅ Asegurarse de estar actualizado con `developer`
- ✅ Crear rama desde `developer` actualizado
- ✅ Verificar que no hay conflictos antes de empezar

### 9.2 Durante el Desarrollo

- ✅ Hacer commits frecuentes y descriptivos
- ✅ Hacer push regularmente para respaldar tu trabajo
- ✅ Mantener tu rama sincronizada con `developer`

### 9.3 Antes de Crear PR

- ✅ Revisar tus propios cambios
- ✅ Ejecutar tests localmente (si aplica)
- ✅ Verificar que no hay errores de compilación
- ✅ Actualizar documentación si es necesario

### 9.4 Después del Merge

- ✅ Eliminar rama local (opcional): `git branch -d feature/nombre-rama`
- ✅ Eliminar rama remota (opcional): `git push origin --delete feature/nombre-rama`
- ✅ Actualizar `developer` local: `git checkout developer && git pull`

---

## 10. Ejemplo Completo de Flujo

### Ejemplo: Agregar nuevo endpoint

```bash
# 1. Actualizar developer local
git checkout developer
git pull origin developer

# 2. Crear rama feature
git checkout -b feature/sentiment-endpoint

# 3. Desarrollar funcionalidad
# ... editar archivos ...

# 4. Hacer commits
git add src/main/java/com/sentiment/backend/controller/SentimentController.java
git commit -m "feat: agregar controller para endpoint /sentiment"

git add src/main/java/com/sentiment/backend/service/SentimentService.java
git commit -m "feat: implementar servicio de análisis de sentimiento"

git add docs/API-CONTRACT.md
git commit -m "docs: documentar endpoint POST /sentiment"

# 5. Push
git push -u origin feature/sentiment-endpoint

# 6. Crear PR en GitHub (desde la web)
# - Base: developer
# - Compare: feature/sentiment-endpoint
# - Título: "feat: agregar endpoint POST /sentiment"
# - Descripción: (seguir plantilla)

# 7. Esperar revisión y aprobación

# 8. Merge (desde GitHub o cuando el revisor apruebe)
```

---

## 12. Referencias y Recursos

- [GitHub Flow](https://guides.github.com/introduction/flow/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Git Branching Strategies](https://www.atlassian.com/git/tutorials/comparing-workflows)

---

## 13. Resumen Rápido

```bash
# Trabajo diario
git checkout developer && git pull
git checkout -b feature/mi-feature
# ... desarrollar ...
git add .
git commit -m "tipo: descripción"
git push -u origin feature/mi-feature
# Crear PR en GitHub → Esperar revisión → Merge
```

**Regla de oro:** Nunca commits directos a `main`. Todo debe pasar por PRs y revisión.

