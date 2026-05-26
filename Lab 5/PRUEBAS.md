# Guía de Pruebas - Blueprints REST API

Este documento detalla cómo verificar el funcionamiento del proyecto.

## 1. Pruebas Automáticas y Cobertura
Ejecuta el siguiente comando para correr todos los tests unitarios y generar el reporte de cobertura con JaCoCo:

```bash
mvn clean test
```

El reporte de cobertura se encontrará en: `target/site/jacoco/index.html`.

## 2. Ejecución de la Aplicación
Puedes ejecutar la aplicación normalmente o activando perfiles específicos para los filtros.

### Ejecución Estándar (Filtro Identidad)
```bash
mvn spring-boot:run
```

### Ejecución con Filtro de Redundancia
```bash
mvn --% -Dspring-boot.run.profiles=redundancy spring-boot:run
```

### Ejecución con Filtro de Submuestreo (Undersampling)
```bash
mvn --% -Dspring-boot.run.profiles=undersampling spring-boot:run
```
*(Nota: El `--%` es necesario en PowerShell para que Maven interprete correctamente los argumentos).*

## 3. Verificación Manual (Endpoints)

###Swagger UI (Documentación Interactiva)
Accede a: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Spring Boot Actuator (Monitoreo)
- Salud del sistema: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- Información: [http://localhost:8080/actuator/info](http://localhost:8080/actuator/info)

### Comandos cURL de ejemplo

**Obtener todos los planos:**
```bash
curl -X GET http://localhost:8080/api/v1/blueprints
```

**Crear un nuevo plano:**
```bash
curl -X POST http://localhost:8080/api/v1/blueprints \
     -H "Content-Type: application/json" \
     -d '{"author":"juan","name":"mi_plano","points":[{"x":10,"y":20}]}'
```

**Agregar un punto a un plano:**
```bash
curl -X PUT http://localhost:8080/api/v1/blueprints/juan/mi_plano/points \
     -H "Content-Type: application/json" \
     -d '{"x":30,"y":40}'
```
