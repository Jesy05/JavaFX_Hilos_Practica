package ni.edu.uam.PracticaHilos;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProcesadorController {

    // ----- Proceso de clientes -----
    @FXML
    private Label lblEstadoClientes;
    @FXML
    private Label lblPorcentajeClientes;
    @FXML
    private ProgressBar barraClientes;
    @FXML
    private Button btnIniciarClientes;
    @FXML
    private Button btnDetenerClientes;
    @FXML
    private Button btnReiniciarClientes;

    // ----- Proceso de productos -----
    @FXML
    private Label lblEstadoProductos;
    @FXML
    private Label lblPorcentajeProductos;
    @FXML
    private ProgressBar barraProductos;
    @FXML
    private Button btnIniciarProductos;
    @FXML
    private Button btnDetenerProductos;
    @FXML
    private Button btnReiniciarProductos;

    /**
     * Agrupa los controles y el hilo de un proceso, de modo que
     * cada proceso sea independiente del otro.
     */
    private static class Proceso {
        final String nombre;
        final int totalRegistros;
        final int milisPorRegistro;

        final Label lblEstado;
        final Label lblPorcentaje;
        final ProgressBar barra;
        final Button btnIniciar;
        final Button btnDetener;
        final Button btnReiniciar;

        Thread hilo;

        Proceso(String nombre, int totalRegistros, int milisPorRegistro,
                Label lblEstado, Label lblPorcentaje, ProgressBar barra,
                Button btnIniciar, Button btnDetener, Button btnReiniciar) {
            this.nombre = nombre;
            this.totalRegistros = totalRegistros;
            this.milisPorRegistro = milisPorRegistro;
            this.lblEstado = lblEstado;
            this.lblPorcentaje = lblPorcentaje;
            this.barra = barra;
            this.btnIniciar = btnIniciar;
            this.btnDetener = btnDetener;
            this.btnReiniciar = btnReiniciar;
        }

        boolean enEjecucion() {
            return hilo != null && hilo.isAlive();
        }
    }

    private Proceso clientes;
    private Proceso productos;

    @FXML
    private void initialize() {
        // Tiempos distintos: 100 x 100 ms = ~10 s, 50 x 150 ms = ~7.5 s
        clientes = new Proceso("Clientes", 100, 100,
                lblEstadoClientes, lblPorcentajeClientes, barraClientes,
                btnIniciarClientes, btnDetenerClientes, btnReiniciarClientes);

        productos = new Proceso("Productos", 50, 150,
                lblEstadoProductos, lblPorcentajeProductos, barraProductos,
                btnIniciarProductos, btnDetenerProductos, btnReiniciarProductos);
    }

    // ----- Manejadores de eventos -----

    @FXML
    private void iniciarClientes() {
        iniciar(clientes);
    }

    @FXML
    private void detenerClientes() {
        detener(clientes);
    }

    @FXML
    private void reiniciarClientes() {
        reiniciar(clientes);
    }

    @FXML
    private void iniciarProductos() {
        iniciar(productos);
    }

    @FXML
    private void detenerProductos() {
        detener(productos);
    }

    @FXML
    private void reiniciarProductos() {
        reiniciar(productos);
    }

    // ----- Lógica común a ambos procesos -----

    private void iniciar(Proceso p) {

        // Evita iniciar el mismo proceso dos veces simultáneamente
        if (p.enEjecucion()) {
            return;
        }

        p.barra.setProgress(0);
        p.lblPorcentaje.setText("0%");
        p.lblEstado.setText("Procesando " + p.nombre.toLowerCase() + "...");

        p.btnIniciar.setDisable(true);
        p.btnDetener.setDisable(false);
        p.btnReiniciar.setDisable(true);

        Runnable tarea = () -> {

            try {
                for (int i = 1; i <= p.totalRegistros; i++) {

                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    Thread.sleep(p.milisPorRegistro);

                    final double fraccion = (double) i / p.totalRegistros;

                    Platform.runLater(() -> {
                        p.barra.setProgress(fraccion);
                        p.lblPorcentaje.setText(
                                (int) Math.round(fraccion * 100) + "%");
                    });
                }

                Platform.runLater(() -> {
                    p.lblEstado.setText("Proceso finalizado");
                    habilitarBotones(p);
                });

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                Platform.runLater(() -> {
                    p.lblEstado.setText("Proceso detenido");
                    habilitarBotones(p);
                });
            }
        };

        p.hilo = new Thread(tarea);
        p.hilo.setName("Hilo-" + p.nombre);
        p.hilo.setDaemon(true);
        p.hilo.start();
    }

    private void detener(Proceso p) {

        if (p.enEjecucion()) {

            p.btnDetener.setDisable(true);
            p.lblEstado.setText("Solicitando detención...");

            p.hilo.interrupt();
        }
    }

    private void reiniciar(Proceso p) {

        if (p.enEjecucion()) {
            return;
        }

        p.barra.setProgress(0);
        p.lblPorcentaje.setText("0%");
        p.lblEstado.setText("Esperando inicio");

        habilitarBotones(p);
    }

    private void habilitarBotones(Proceso p) {
        p.btnIniciar.setDisable(false);
        p.btnDetener.setDisable(true);
        p.btnReiniciar.setDisable(false);
    }

    public void detenerAlCerrar() {

        for (Proceso p : new Proceso[]{clientes, productos}) {
            if (p != null && p.enEjecucion()) {
                p.hilo.interrupt();
            }
        }
    }
}
