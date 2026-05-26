# ARSW – Laboratorio 1 

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Jacoco](https://img.shields.io/badge/Jacoco-black?style=for-the-badge&logo=sonarcloud&logoColor=F3702A)
![SonarQ](https://img.shields.io/badge/SonarCloud-black?style=for-the-badge&logo=sonarcloud&logoColor=F3702A)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?style=for-the-badge&logo=spring&logoColor=white)

## Paralelismo, Arquitectura y Calidad desde el Día 1  
**Spring Boot 3.x – Java 21 – REST – Testing – OpenAPI**

---

## 🎯 Objetivo del laboratorio

Este laboratorio introduce al estudiante en los **fundamentos del paralelismo y la concurrencia**, integrándolos desde el primer día con buenas prácticas de **Arquitectura de Software**:

- Diseño por capas  
- Servicios REST bien definidos  
- Pruebas automatizadas  
- Cobertura de código  
- Documentación de APIs  
- Decisiones técnicas justificadas  

El laboratorio se desarrolla en **dos fases dentro del mismo ejercicio**:
- **Fase 0:** implementación base (secuencial)
- **Fase 1:** modificación obligatoria para agregar paralelismo usando hilos

---

## 🧠 Contexto

El cálculo de los dígitos de π (Pi) es un problema clásico usado para ilustrar **cómputo intensivo y paralelismo**.  
En este laboratorio, dicho cálculo se expone como un **servicio REST moderno**, el cual será evolucionado progresivamente.

> ⚠️ El paralelismo **NO se introduce desde el inicio**.  
> Primero se diseña correctamente la solución, luego se paraleliza.

---

## 🏗️ Arquitectura base del proyecto

El proyecto sigue una arquitectura por capas:

```
api/            → Controllers REST, DTOs, contratos  
core/           → Lógica de negocio y algoritmos  
concurrency/    → Estrategias de paralelismo  
monitoring/     → Medición de tiempos y métricas básicas  
```

Regla clave:
- El **Controller NO crea hilos**
- El **Service orquesta**
- Las **estrategias ejecutan la concurrencia**

---

## 🌐 API REST

### Endpoint base

```
GET /api/v1/pi/digits?start={int}&count={int}
```

### Endpoint extendido (Fase 1)

```
GET /api/v1/pi/digits?start=&count=&threads=&strategy=
```

Parámetros:
- `start` ≥ 0  
- `count` > 0  
- `threads` > 0 (opcional, default: availableProcessors)  
- `strategy` (opcional): `sequential`, `threads`  

---

## 📘 OpenAPI / Swagger

Swagger debe estar disponible en:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🧪 FASE 0 – Implementación base (secuencial)

### Objetivo
Construir un servicio REST funcional, bien diseñado y cubierto por pruebas.

### Actividades
1. Analizar la estructura del proyecto suministrado.
2. Implementar el endpoint REST secuencial.
3. Validar parámetros de entrada.
4. Manejar errores correctamente.
5. Documentar el API con Swagger.
6. Implementar pruebas unitarias y de integración.

### Criterio de aceptación
- El endpoint responde correctamente.
- Swagger está accesible.
- `mvn clean test` pasa sin errores.

---

## ⚙️ FASE 1 – Modificación obligatoria: agregar hilos

### Objetivo
Evolucionar la solución para soportar paralelismo **sin romper la arquitectura**.

### Actividades obligatorias

#### 1. Extender el endpoint
Agregar soporte para:
- `threads`
- `strategy=threads`

Si no se envían estos parámetros, el sistema debe comportarse de forma secuencial.

---

#### 2. Crear la interfaz de estrategia

```java
public interface ParallelStrategy {
    String calculate(int start, int count, int threads);
    String name();
}
```

---

#### 3. Implementar `ThreadJoinStrategy` (OBLIGATORIO)

- Crear N hilos (platform threads).
- Dividir el trabajo en segmentos.
- Ejecutar cada segmento en paralelo.
- Sincronizar usando `join()`.
- Concatenar resultados en orden.

El resultado **debe ser idéntico** al secuencial.

---

#### 4. Modificar el Service
- Delegar el cálculo a la estrategia.
- Mantener el cálculo secuencial como fallback.

---

## 🧪 Pruebas y calidad (OBLIGATORIO)

### Pruebas requeridas

#### Controller
- Casos válidos (200 OK)
- Casos inválidos (400 Bad Request)
- Validación de parámetros

#### Service
- Equivalencia secuencial vs paralelo
- Determinismo
- No deadlocks (tests con timeout)

### Cobertura
- Cobertura mínima de líneas: **80%**
- `mvn clean verify` debe pasar

---

## 📊 Experimentos y análisis

### Actividades
Medir tiempos de ejecución para un `count` grande usando:

- strategy=sequential
- strategy=threads con:
  - 1 hilo
  - availableProcessors()
  - 2 × availableProcessors()
  - 200
  - 500

### Reporte (PDF) escrito con LateX
- Objetivo 
- Tabla de tiempos
- Análisis de resultados
- Interpretación de la Ley de Amdahl
- Conclusiones técnicas

---

## 📦 Entregables

1. Código fuente  
2. Pruebas automatizadas  
3. Cobertura cumplida  
4. Swagger documentado  
5. Reporte PDF con análisis  

---

## 📝 Rúbrica de evaluación

| Criterio | Peso |
|--------|------|
| Fase 0 – Implementación base | 20% |
| Fase 1 – Paralelismo con hilos | 25% |
| Arquitectura y diseño | 20% |
| Pruebas y cobertura | 20% |
| Análisis y conclusiones | 15% |
| **Total** | **100%** |

---

## ❌ Causales de nota cero

- El proyecto no compila  
- Las pruebas fallan  
- No se cumple la cobertura mínima  
- Resultados hardcodeados  
- Copia entre equipos  

---

## 🎓 Mensaje final

> *El paralelismo no es una optimización automática.*  
> *Es una decisión arquitectónica con costos y límites.*

Bienvenidos a **Arquitectura de Software (ARSW)**.


# Evidencias ✅


### Tecnologias Usadas 💻

- *☕ Java OpenJDK 21*: Lenguaje de programación principal
- *🍃 Spring Boot 3.3.4*: Framework principal para desarrollo de aplicaciones web
- *📊 JaCoCo*: Análisis de cobertura de código
- *🔍 SonarQ*: Análisis estático de código
- *🐳 Docker*: Containerización de la aplicación
- *📖 Swagger UI*: Interfaz interactiva para probar la API


### 📖 Swagger UI

<img width="859" height="482" alt="image" src="https://github.com/user-attachments/assets/27d979e6-8ef8-4204-98c8-210f83c2a711" />

<img width="901" height="478" alt="image" src="https://github.com/user-attachments/assets/6658f73d-12f0-445d-969b-7da67b72b6a8" />

### 📊 JaCoCo

<img width="864" height="470" alt="image" src="https://github.com/user-attachments/assets/9c322e11-c1a3-41ab-840b-802086fed412" />

### 🔍 SonarQ

<img width="859" height="477" alt="image" src="https://github.com/user-attachments/assets/4f94b4f4-2408-4ea4-80e7-92160727ad3a" />

### ⌛Tiempos 

<img width="868" height="478" alt="image" src="https://github.com/user-attachments/assets/b12a764c-4b6e-4fd6-b78d-cd16baea531d" />



