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
