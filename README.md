# JavaFX_Hilos_Practica

Práctica guiada **Hilos en JavaFX** — Programación de Aplicaciones de Escritorio,
Unidad III (Programación concurrente), Universidad Americana.

Caso de estudio: **NicaData Solutions S.A.** necesita una aplicación de escritorio
que simule el procesamiento de registros de clientes sin bloquear la interfaz.

## Estructura

```
src/main/java/ni/edu/uam/practicahilos/
    MainApp.java                 Inicia JavaFX y carga la vista
    ProcesadorController.java    Lógica de hilos, actualización visual e interrupción
    Launcher.java                Punto de entrada para ejecutar desde IDE / jar
src/main/resources/ni/edu/uam/practicahilos/
    procesador-view.fxml         Interfaz (lblEstado, barraProgreso, lblPorcentaje, botones)
```

## Cómo ejecutar

```bash
./mvnw javafx:run
```

Requiere JDK 21. JavaFX se descarga vía Maven.

## Hilos implementados

| Elemento | Detalle |
|----------|---------|
| Creación | `Runnable` con la tarea + `new Thread(tarea)` |
| Tarea | 100 iteraciones, `Thread.sleep(100)` por registro (~10 s en total) |
| Actualización de UI | `Platform.runLater()` para la barra y las etiquetas |
| Detención | `interrupt()` desde el botón Detener; el hilo lo detecta con `isInterrupted()` / `InterruptedException` |
| Cierre de ventana | `stage.setOnCloseRequest` interrumpe el hilo; además es `daemon` |
| Doble inicio | Se evita comprobando `hiloProceso.isAlive()` y deshabilitando Iniciar |

## Pruebas (Paso 5)

- [ ] Iniciar el proceso y comprobar el avance porcentual.
- [ ] Mover la ventana mientras se procesan los registros.
- [ ] Detener el proceso antes del 100%.
- [ ] Reiniciar el progreso a 0%.
- [ ] Ejecutar nuevamente hasta completar el 100%.

## Análisis del bloqueo de la interfaz (Paso 6)

Método analizado:

```java
@FXML
private void procesoBloqueante() {
    lblEstado.setText("Procesando...");
    try {
        Thread.sleep(5000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
    lblEstado.setText("Finalizado");
}
```

**¿Por qué no debemos ejecutar procesos prolongados directamente dentro de los eventos de JavaFX?**

Los manejadores de eventos (como `onAction` de un botón) se ejecutan en el
*JavaFX Application Thread*, el único hilo que dibuja la interfaz y atiende
los eventos del usuario. Si dentro del manejador se hace una espera o un
cálculo largo, ese hilo queda ocupado y no puede repintar ni procesar clics,
así que la ventana se congela ("no responde") durante todo el proceso.

En el ejemplo, `Thread.sleep(5000)` detiene el hilo de JavaFX 5 segundos: el
texto "Procesando..." ni siquiera llega a mostrarse (la escena solo se repinta
cuando el manejador termina), y la ventana no se puede mover ni cerrar. La
solución es mover el trabajo a otro hilo (`Thread` + `Runnable`) y volver al
hilo de JavaFX solo para actualizar los controles con `Platform.runLater()`.

## Reto final: procesos concurrentes

La aplicación se amplió a un **Monitor de procesos** de NicaData Solutions con
dos hilos independientes, cada uno con su barra de progreso y sus botones:

| Proceso | Registros | Pausa por registro | Duración aprox. |
|---------|-----------|--------------------|-----------------|
| Clientes | 100 | 100 ms | 10 s |
| Productos | 50 | 150 ms | 7,5 s |

- Cada proceso crea su propio `Thread` a partir de un `Runnable` (`Hilo-Clientes`, `Hilo-Productos`).
- La interfaz se actualiza únicamente con `Platform.runLater()`.
- Cada hilo se detiene individualmente con su botón **Detener** (`interrupt()`).
- No se puede iniciar un proceso dos veces: se comprueba `isAlive()` y el botón Iniciar se deshabilita.
- Al cerrar la ventana se interrumpen ambos hilos.
- La clase interna `Proceso` agrupa los controles y el hilo de cada proceso para reutilizar la misma lógica.

## Evidencia

Capturas del funcionamiento en la carpeta `capturas/` (pendiente de agregar).
