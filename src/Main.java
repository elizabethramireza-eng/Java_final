import model.RegistroTransito;
import services.ProcesadorEstadisticas;

import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   TECNOMOVIL DATA - PROCESAMIENTO FUNCIONAL    ");
        System.out.println("=================================================\n");

        // 1. Cargar datos simulados
        List<RegistroTransito> dataset = generarDatosSimulados();
        ProcesadorEstadisticas procesador = new ProcesadorEstadisticas();

        // Tarea a: Afluencia por Estación
        System.out.println("--- a) Afluencia por Estación (Entradas) ---");
        procesador.calcularAfluenciaPorEstacion(dataset)
                .forEach((est, cant) -> System.out.println(" • " + est + ": " + cant + " entradas"));

        // Tarea b: Horas Pico
        System.out.println("\n--- b) Identificación de Horas Pico ---");
        procesador.identificarHorasPico(dataset)
                .forEach((hora, cant) -> System.out.printf(" • Hora %02d:00 - %d transacciones\n", hora, cant));

        // Tarea c: Rutas Más Utilizadas
        System.out.println("\n--- c) Rutas Más Utilizadas ---");
        procesador.obtenerRutasMasUtilizadas(dataset)
                .forEach((ruta, cant) -> System.out.println(" • " + ruta + ": " + cant + " usajes"));

        // Tarea d: Patrones de Viaje por Usuario
        System.out.println("\n--- d) Patrones de Viaje por Usuario ---");
        procesador.obtenerPatronesDeViajePorUsuario(dataset)
                .forEach((usr, rutas) -> System.out.println(" • " + usr + ": " + String.join(" -> ", rutas)));

        // Tarea e: Tiempo Promedio entre Estaciones
        System.out.println("\n--- e) Tiempo Promedio entre Estaciones ---");
        double promedio = procesador.calcularTiempoPromedioEntreEstaciones(dataset);
        System.out.printf(" • Tiempo promedio global: %.2f minutos\n", promedio);

        // Tarea f: Detección de Sobrecarga en Rutas
        System.out.println("\n--- f) Detección de Sobrecarga en Rutas (Umbral = 2) ---");
        procesador.detectarSobrecargaEnRutas(dataset, 2)
                .forEach((ruta, estado) -> System.out.println(" • " + ruta + ": " + estado));
    }

    /**
     * Genera registros simulados representativos de un día de operación en TecnoValle.
     */
    private static List<RegistroTransito> generarDatosSimulados() {
        LocalDateTime base = LocalDateTime.of(2026, 9, 27, 6, 0);

        return List.of(
            new RegistroTransito("USR-101", "Ruta 01", "Estación Niquía", "entrada", base),
            new RegistroTransito("USR-101", "Ruta 01", "Estación Poblado", "salida", base.plusMinutes(25)),
            new RegistroTransito("USR-102", "Ruta 02", "Estación San Antonio", "entrada", base.plusMinutes(5)),
            new RegistroTransito("USR-102", "Ruta 02", "Estación Itagüí", "salida", base.plusMinutes(30)),
            new RegistroTransito("USR-103", "Ruta 01", "Estación Niquía", "entrada", base.plusMinutes(10)),
            new RegistroTransito("USR-103", "Ruta 01", "Estación Bello", "salida", base.plusMinutes(18)),
            new RegistroTransito("USR-104", "Ruta 01", "Estación Niquía", "entrada", base.plusMinutes(15))
        );
    }
}