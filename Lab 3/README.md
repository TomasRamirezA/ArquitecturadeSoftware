# Laboratorio 03 ARSW: Immortals & Synchronization
### María Paula Rodríguez Muñoz-Juan Andrés Suárez-Juan Pablo Nieto-Tomás Felipe Ramírez

# Problemas Iniciales Identificados

### *1. Condiciones de carrera:* 
Varios hilos modificaban simultáneamente la salud de los
inmortales, lo que generaba inconsistencias en los valores y violaciones del invariante.

### *2. Lecturas inconsistentes durante la pausa:* 
Al presionar Pause & Check, no siempre
se garantizaba que todos los hilos estuvieran realmente detenidos antes de leer los
valores de salud.

### *3. Riesgo de deadlock:* 
Al sincronizar múltiples inmortales sin un orden definido, la
aplicación podía quedar bloqueada cuando dos hilos intentaban adquirir los mismos
locks en distinto orden

 # Parte I: wait/notify: Productor/Consumidor
 
## 4.1. Análisis de CPU: Busy-Wait (Modo Spin)
En la implementación inicial, basada en la clase BusySpinQueue, se observó un consumo
de CPU extremadamente alto.

### *Hallazgos:*

*Clase causante:* BusySpinQueue.java.

*Razón:* El hilo entra en un bucle while(true) preguntando constantemente por el
estado de la cola, lo que constituye un caso de espera activa (busy-wait). Donde el
procesador nunca descansa, incluso cuando no hay nada que producir o consumir

<img width="798" height="447" alt="image" src="https://github.com/user-attachments/assets/af1e04f7-def9-4829-be2c-45875f13e06b" />

## 4.2. Solución Eficiente: Monitores (Modo Monitor)

Para solucionar el problema de la espera activa, se implementó una versión eficiente de
la cola mediante la clase EfficientQueue, apoyándose en el uso de BoundedBuffer para
manejar la sincronización con monitores.

### Mejoras realizadas:
Uso de synchronized para proteger el acceso a los datos compartidos.

*Uso de wait():* los hilos liberan el CPU y entran se "duermen"hasta que haya trabajo.

*Uso de notifyAll():* los hilos son despertados únicamente cuando el estado de la cola
cambia y pueden reanudar su trabajo.

### Resultados:
El consumo de CPU disminuyó a niveles cercanos al 0 %.
Los hilos permanecen la mayor parte del tiempo en estado WAITING, siendo "despertados"solo cuando la cola cambia de estado.

<img width="805" height="451" alt="image" src="https://github.com/user-attachments/assets/6a3c8776-939b-482e-ace0-c3d86469a1a6" />

## 4.3. Pruebas de Cola Acotada

Finalmente, se validó el límite de stock con un productor rápido y un consumidor lento.

En este escenario se observó que:
El productor se detiene correctamente al alcanzar la capacidad máxima (capacity) de
la cola.

No se presenta desbordamiento de memoria.

No existe consumo innecesario de CPU durante los periodos de espera.

<img width="883" height="494" alt="image" src="https://github.com/user-attachments/assets/4276c23c-9671-4d98-b55a-cb013cfed3da" />

<img width="880" height="492" alt="image" src="https://github.com/user-attachments/assets/7795be5d-69ff-4f30-933c-93324a0507d4" />

# Parte II: Búsqueda Distribuida y Condición de Parada

Se reescribió el buscador de listas negras para que la búsqueda se detenga tan pronto el
conjunto de hilos detecte el número de ocurrencias definido por BLACK_LIST_ALARM_COUNT.

## Finalización anticipada

Se implementó una condición de parada global que permite que todos los hilos detengan
su ejecución cuando el contador alcanza el umbral establecido. De esta manera, no se recorren
los servidores restantes y el método retorna inmediatamente el resultado.

## Control de condiciones de carrera
Para garantizar ausencia de condiciones de carrera sobre el contador compartido, se utilizó:

AtomicInteger para realizar incrementos atómicos, o
Sincronización mínima sobre la región crítica donde se incrementa el contador.

Con esto se asegura consistencia en el valor del contador aun cuando múltiples hilos lo
actualicen simultáneamente.

<img width="857" height="479" alt="image" src="https://github.com/user-attachments/assets/4bb98a9f-b5ab-4848-920e-3be5fe090df4" />

<img width="861" height="369" alt="image" src="https://github.com/user-attachments/assets/d11c3cee-c46e-49da-8da4-e6b849e48978" />

<img width="541" height="506" alt="image" src="https://github.com/user-attachments/assets/31ace118-51ef-4184-bcba-db414d27e580" />

# Parte III —Sincronización y Deadlocks con Highlander Simulator

## 6.1. Descripción de la simulación

Se revisa la simulación con N inmortales, donde cada uno ataca a otro. El atacante resta
M puntos de vida al contrincante y suma M/2 a su propia vida.

## 6.2. Invariante del sistema
Con N inmortales y salud inicial H, la suma total de la salud debería permanecer constante (salvo durante un update). El valor del invariante se calcula como:

*Salud total inicial = N × H*

Este valor se utiliza para validar la consistencia de la simulación durante la ejecución. En este
caso la suma total de la salud de todos los inmortales no es constante durante la simulación,
ya que con cada ataque se pierde M/2 de la salud total del sistema. El invariante clave es quela suma total de la salud debe permanecer constante mientras la simulación está en pausa.

## 6.3. Prueba con “Pause & Check”

Se ejecuta la interfaz gráfica y se prueba la opción “Pause & Check”. Se verifica si el
invariante se cumple al pausar la simulación y se explica el resultado observado.

<img width="622" height="425" alt="image" src="https://github.com/user-attachments/assets/5df62d09-d6c7-456c-b7c7-30ede3d526df" />

<img width="624" height="439" alt="image" src="https://github.com/user-attachments/assets/b74030a6-c4b1-4ef1-b4c5-bcf464c821a0" />

En este caso, el invariante dice que la suma total de la vida de todos los inmortales debería
ser N×H, pero al oprimir “Pause Check” se puede ver que la suma total no coincide con ese
valor. Esto pasa porque en cada pelea un inmortal pierde M puntos de vida y el otro solo
gana M/2. Si todo estuviera bien sincronizado y las operaciones se hicieran de forma correcta,
el total debería mantenerse.

## 6.4. Implementación de una pausa correcta

Para que la pausa sea efectiva, es necesario que todos los hilos se detengan antes de calcular
la salud total. La implementación actual usa un PauseController, pero para garantizar la
consistencia, se debería esperar un breve momento después de pausar para que las operaciones
en curso finalicen. La función de reanudar ya está implementada.

## 6.5. Validación con múltiples clics

Se hace clic repetidamente en “Pause & Check” y se valida la consistencia de los resultados.
Se analiza si el invariante se mantiene en cada verificación.

## 6.6. Regiones críticas y sincronización

Las secciones críticas son los métodos de lucha (fightNaive y fightOrdered).
fightNaive puede causar deadlocks porque los locks se adquieren en un orden inconsistente.
fightOrdered previene los deadlocks al adquirir los locks en un orden predefinido y
consistente (basado en el nombre del inmortal).

<img width="619" height="343" alt="image" src="https://github.com/user-attachments/assets/e36634a2-bb79-4b82-881c-860fdc02d59a" />

<img width="623" height="368" alt="image" src="https://github.com/user-attachments/assets/3bf3f726-bfbb-4bfb-b436-490e6b5a3504" />

## 6.7. Diagnóstico de deadlocks

Si la aplicación se congela, se puede usar jps -l para obtener el ID del proceso y jstack
<PID> para analizar los hilos.

El comando jstack revelará si hay un deadlock y qué hilos están involucrados.

<img width="618" height="289" alt="image" src="https://github.com/user-attachments/assets/cb753b2f-66bb-4005-b864-0fb2bf2889ac" />

## 6.8. Estrategia para corregir deadlocks
La estrategia principal para corregir deadlocks es usar un orden de bloqueo total, como
se implementa en fightOrdered.

Alternativamente, se podría usar tryLock con un tiempo de espera y reintentos para
evitar que los hilos se bloqueen indefinidamente.

## 6.9. Validación con gran número de inmortales
Al probar con un gran número de inmortales (100, 1000, etc.), el modo ordered es más
robusto.

<img width="623" height="450" alt="image" src="https://github.com/user-attachments/assets/80c9c136-b344-442f-8d38-1627bbb43417" />

<img width="625" height="444" alt="image" src="https://github.com/user-attachments/assets/b8847cb1-48b7-4376-a1bc-649415afec18" />

<img width="625" height="451" alt="image" src="https://github.com/user-attachments/assets/0356031e-3dd0-40f9-8ea3-eb579ef2a299" />

## 6.10. Eliminación de inmortales muertos sin bloquear la simulación

Los inmortales muertos se eliminan de una lista concurrente (CopyOnWriteArrayList),
lo que evita la necesidad de un bloqueo global. Esta estructura de datos es segura para
subprocesos y previene condiciones de carrera durante la eliminación, aunque puede tener un
costo de rendimiento si las eliminaciones son muy frecuentes

<img width="626" height="455" alt="image" src="https://github.com/user-attachments/assets/05f3f5da-4523-4d5c-8084-10bc3a2b3c4a" />


## 6.11. Implementación de STOP (apagado ordenado)

Se ha implementado un método stop() que detiene la simulación de forma ordenada.
Este método detiene a todos los inmortales, apaga el ExecutorService para interrumpir
los hilos y limpia los recursos, asegurando un cierre limpio de la aplicación


<img width="624" height="454" alt="image" src="https://github.com/user-attachments/assets/0e445d44-4936-4d7a-ad83-7f3843412af6" />

