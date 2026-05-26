# 🐕 Laboratorio 2 – Programación Concurrente: Carrera de Galgos

## Arquitectura de Software (ARSW)

### Objetivo
El objetivo de este laboratorio es que el estudiante **analice, corrija y diseñe una solución concurrente**, identificando **problemas de sincronización**, **regiones críticas** y aplicando **mecanismos adecuados de control de concurrencia** en Java.

El ejercicio se basa en una simulación de una **carrera de galgos**, donde cada galgo se ejecuta como un hilo independiente y avanza por un carril hasta completar la pista.

---

## Contexto del problema
En la simulación:

- Cada **galgo** corre de manera concurrente (un hilo por galgo).
- Todos los galgos comparten un **registro de llegada**.
- El sistema permite **iniciar**, **detener** y **reanudar** la carrera.
- Al finalizar la carrera, se debe mostrar el **orden de llegada (ranking)** de forma consistente.

La aplicación presenta inicialmente **problemas de sincronización** que deben ser analizados y corregidos.

---

## Estructura general del proyecto

El proyecto sigue una **separación por capas**, consistente con el laboratorio anterior:

```
src
 ├── main
 │   └── java
 │       └── edu.eci.arsw.dogsrace
 │           ├── app        -> Punto de entrada y orquestación
 │           ├── threads    -> Hilos de ejecución (galgos)
 │           ├── control    -> Control de la ejecución concurrente
 │           ├── domain     -> Modelo y estado compartido
 │           └── ui         -> Interfaz gráfica
 └── test
     └── java
         └── edu.eci.arsw.dogsrace
```

---

## Actividades a desarrollar

### 1️⃣ Sincronización de finalización de hilos
Corrija la aplicación para que el aviso de resultados se muestre **únicamente cuando todos los hilos de los galgos hayan finalizado su ejecución**.

**Pistas:**
- La acción de iniciar la carrera y mostrar resultados se realiza desde `MainCanodromo`.
- Puede utilizar el método `join()` de la clase `Thread`.

---

### 2️⃣ Identificación de inconsistencias y regiones críticas
Ejecute la aplicación varias veces e identifique **inconsistencias en el ranking**.

**Tareas:**
- Identificar las regiones críticas.
- Explicar por qué generan inconsistencias.
- Sincronizar únicamente dichas regiones.

---

### 3️⃣ Funcionalidades de pausa y continuación
Implemente las funcionalidades **Stop** y **Continue**.

**Comportamiento esperado:**
- **Stop**: todos los galgos suspenden su ejecución.
- **Continue**: todos los galgos reanudan la carrera.

**Restricciones:**
- Usar mecanismos de sincronización del lenguaje.
- Utilizar un **monitor común**.
- Emplear `wait()` y `notifyAll()`.

---

## Criterios de evaluación

### Funcionalidad
- Ejecución detenida y reanudada consistentemente.
- Ranking sin inconsistencias.

### Diseño
- Sincronización solo de regiones críticas.
- Reactivación con un único llamado usando un monitor común.

---

## Entregables
- Código fuente funcional.
- Explicación breve de las regiones críticas y sincronización usada.
- Evidencia de ejecución correcta.

---

## Observaciones finales
Este laboratorio refuerza conceptos clave de **programación concurrente**, **diseño correcto de sincronización** y **arquitectura por capas**, que serán reutilizados en laboratorios posteriores.

---

## Solución Implementada y Justificación Técnica

A continuación, se justifica cómo la solución implementada cumple con los criterios de evaluación definidos.

### 1. Concurrencia Correcta

La correcta concurrencia se garantiza mediante las siguientes estrategias:

- **Ausencia de Condiciones de Carrera (`Data Races`)**:
    - **`ArrivalRegistry`**: Toda variable compartida (`nextPosition`, `winner` y la lista `arrivals`) se accede exclusivamente a través de métodos `synchronized`. Para la lista de llegadas, se utiliza `Collections.synchronizedList`, garantizando que las operaciones de adición sean atómicas y seguras para hilos.
      ```java
      // Ejemplo en ArrivalRegistry.java
      public synchronized ArrivalSnapshot registerArrival(String dogName) {
          // ...
          final int position = nextPosition++; 
          if (position == 1) {
              winner = dogName;
          }
          arrivals.add(dogName + " llego en la posicion " + position); // Acceso seguro
          return new ArrivalSnapshot(position, winner);
      }
      ```
    - **`RaceControl`**: Para la finalización de la carrera, se utiliza un `AtomicBoolean finished`. Esta clase del paquete `java.util.concurrent.atomic` está diseñada para ser actualizada atómicamente sin necesidad de bloques `synchronized`, lo cual es más eficiente. La pausa (`paused`) sí se gestiona con un bloque `synchronized` tradicional, ya que requiere el uso de `wait()` y `notifyAll()`.

- **Sincronización Bien Localizada**: La lógica de sincronización no está dispersa por el código. Está encapsulada en las dos clases que gestionan el estado compartido: `RaceControl` para el control del flujo de la carrera y `ArrivalRegistry` para los resultados. Los hilos `Galgo` simplemente utilizan los métodos públicos de estas clases, sin conocer los detalles de la implementación de la sincronización.

- **Sin Espera Activa (`Busy-Waiting`)**: La funcionalidad de pausa se implementa con `monitor.wait()`, lo que hace que los hilos `Galgo` se bloqueen y liberen el procesador hasta que se les notifique con `monitor.notifyAll()`. Esto es mucho más eficiente que un bucle que comprueba constantemente una variable (espera activa).
  ```java
  // En RaceControl.java
  public void awaitIfPaused() throws InterruptedException {
      synchronized (monitor) {
          while (paused) {
              monitor.wait(); // El hilo se bloquea eficientemente
          }
      }
  }
  ```

### 2. Funcionalidad de Pausa y Reanudación

- **Consistencia del Estado**: La llamada a `awaitIfPaused()` se encuentra al inicio del bucle de cada `Galgo`. Esto asegura que un galgo solo puede ser pausado *antes* de realizar un nuevo paso. No puede ser interrumpido a mitad de la actualización de su posición en el carril. Al reanudar, todos los hilos son notificados y continúan su ejecución desde el mismo punto, manteniendo el estado de la carrera íntegro.

### 3. Robustez

- **Escalabilidad (N alto)**: La solución escala bien con un número elevado de galgos. El uso de `AtomicBoolean` y la sincronización localizada en monitores específicos minimiza la contención. No existen cuellos de botella centralizados que degraden el rendimiento a medida que aumenta el número de hilos.
- **Ausencia de `ConcurrentModificationException`**: En `MainCanodromo`, para mostrar los resultados, se obtiene una copia de la lista de llegadas (`new ArrayList<>(arrivals)` en `getArrivals()`). Esto permite iterar sobre los resultados de forma segura, incluso si teóricamente la lista original pudiera ser modificada (aunque en este punto de la ejecución, ya no lo es).
- **Ausencia de `Deadlocks`**: La lógica de bloqueo es simple y no anidada. `RaceControl` y `ArrivalRegistry` utilizan sus propios monitores privados e independientes. Un hilo nunca necesita adquirir ambos bloqueos simultáneamente, lo que elimina la posibilidad de un interbloqueo (`deadlock`).

### 4. Calidad del Código y Arquitectura

- **Arquitectura Clara**: El proyecto mantiene una clara separación de responsabilidades, dividida en paquetes (`app`, `control`, `domain`, `threads`, `ui`), lo que facilita su comprensión y mantenimiento.
- **Separación UI/Lógica**: La interfaz de usuario (`Canodromo`) está desacoplada de la lógica de la carrera. La UI solo reacciona a eventos y delega las acciones a `RaceControl` y `MainCanodromo`. La actualización de la UI se realiza a través de la clase `Carril`, que actúa como un intermediario (modelo-vista) entre el hilo `Galgo` y los componentes gráficos.
- **Interfaz de Resultados**: Se añadió un `JTextArea` para mostrar el ranking final, mejorando la usabilidad y permitiendo visualizar el orden de llegada de todos los participantes, no solo del ganador.


# EVIDENCIAS ✅✅

## Problemas Iniciales Identificados
*1.* Condiciones de carrera: Los 17 galgos accedían a nextPosition y winner al mismo
tiempo, causando posiciones duplicadas.

*2.* Resultados prematuros: Se mostraba el ganador antes de que todos los galgos terminaran (faltaba join()).

*3.* Sin control de pausa: No había forma de pausar/reanudar la carrera (faltaba wait()/notifyAll())

##  Regiones Críticas

En la aplicación existen secciones donde varios hilos acceden a recursos compartidos al mismo tiempo. Estas secciones se denominan regiones críticas y, si no se controlan, pueden producir condiciones de carrera.

###  ArrivalRegistry (Registro de llegada)

**Problema:**  
Varios galgos pueden leer el valor de `nextPosition` antes de que este sea incrementado, provocando que dos o más galgos obtengan la misma posición de llegada.

**Solución:**  
Se protege el método `registerArrival` usando `synchronized`, garantizando que solo un hilo pueda ejecutarlo a la vez.

```java
public synchronized ArrivalSnapshot registerArrival(String dogName) {
    final int position = nextPosition++;
    if (position == 1) {
        winner = dogName;
    }
    return new ArrivalSnapshot(position, winner);
}
```
### RaceControl (Control de pausa)

**Problema:**  
La interfaz gráfica (UI) puede modificar el valor de la variable `paused` mientras los hilos (galgos) la están leyendo, lo que provoca que algunos galgos no respeten correctamente la pausa de la carrera.

**Solución:**  
Se implementa un monitor común utilizando los mecanismos `wait()` y `notifyAll()` para sincronizar a los hilos.

```java
public void awaitIfPaused() throws InterruptedException {
    synchronized (monitor) {
        while (paused) {
            monitor.wait();
        }
    }
}

public void resume() {
    synchronized (monitor) {
        paused = false;
        monitor.notifyAll();
    }
}
```
## Evidencias De Ejecucion 💻👾

<img width="1364" height="762" alt="image" src="https://github.com/user-attachments/assets/5e110442-4aa0-4ed9-b78c-7bf85ca271d3" />
<img width="1355" height="581" alt="image" src="https://github.com/user-attachments/assets/43874899-f456-4419-98d1-85df755cbbb7" />

![img.png](img.png)

## Cobertura de Pruebas Jacoco ⚡😎

<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/ccc133e8-8dd5-47b2-8625-6a190d2514b0" />







