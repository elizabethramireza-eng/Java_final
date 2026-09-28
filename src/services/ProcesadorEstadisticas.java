package services;

import model.RegistroTransito;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Clase de servicio funcional encargada de procesar datos de tránsito
 * mediante pipelines de Java Streams, Lambdas y Funciones Puras.
 */
public class ProcesadorEstadisticas {

    /**
     * Tarea a: Cálculo de afluencia por estación.
     * Cuenta cuántos usuarios ingresaron (acción "entrada") a cada estación.
     */
    public Map<String, Long> calcularAfluenciaPorEstacion(List<RegistroTransito> registros) {
        return registros.stream()
                .filter(r -> "entrada".equalsIgnoreCase(r.accion()))
                .collect(Collectors.groupingBy(
                        RegistroTransito::estacion,
                        Collectors.counting()
                ));
    }

    /**
     * Tarea b: Identificación de horas pico.
     * Agrupa los registros por la hora del día (0 a 23) y calcula el volumen total de interacciones.
     */
    public Map<Integer, Long> identificarHorasPico(List<RegistroTransito> registros) {
        return registros.stream()
                .collect(Collectors.groupingBy(
                        r -> r.timestamp().getHour(),
                        Collectors.counting()
                ));
    }

    /**
     * Tarea c: Rutas más utilizadas.
     * Determina el volumen de uso por ruta y lo devuelve ordenado descendentemente.
     */
    public Map<String, Long> obtenerRutasMasUtilizadas(List<RegistroTransito> registros) {
        return registros.stream()
                .collect(Collectors.groupingBy(
                        RegistroTransito::ruta,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Tarea d: Patrones de viaje por usuario.
     * Genera un listado ordenado cronológicamente con las estaciones visitadas por cada usuario.
     */
    public Map<String, List<String>> obtenerPatronesDeViajePorUsuario(List<RegistroTransito> registros) {
        return registros.stream()
                .collect(Collectors.groupingBy(
                        RegistroTransito::idUsuario,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                lista -> lista.stream()
                                        .sorted(Comparator.comparing(RegistroTransito::timestamp))
                                        .map(RegistroTransito::estacion)
                                        .collect(Collectors.toList())
                        )
                ));
    }

    /**
     * Tarea e: Cálculo de tiempo promedio entre estaciones consecutivas.
     * Calcula la diferencia promedio en minutos entre un evento de "entrada" y su posterior "salida".
     */
    public double calcularTiempoPromedioEntreEstaciones(List<RegistroTransito> registros) {
        Map<String, List<RegistroTransito>> registrosPorUsuario = registros.stream()
                .collect(Collectors.groupingBy(RegistroTransito::idUsuario));

        return registrosPorUsuario.values().stream()
                .flatMapToDouble(list -> {
                    List<RegistroTransito> ordenados = list.stream()
                            .sorted(Comparator.comparing(RegistroTransito::timestamp))
                            .collect(Collectors.toList());

                    List<Long> diferenciasMinutos = new ArrayList<>();
                    for (int i = 0; i < ordenados.size() - 1; i++) {
                        RegistroTransito actual = ordenados.get(i);
                        RegistroTransito siguiente = ordenados.get(i + 1);

                        if ("entrada".equalsIgnoreCase(actual.accion()) && "salida".equalsIgnoreCase(siguiente.accion())) {
                            long minutos = Duration.between(actual.timestamp(), siguiente.timestamp()).toMinutes();
                            if (minutos >= 0) {
                                diferenciasMinutos.add(minutos);
                            }
                        }
                    }
                    return diferenciasMinutos.stream().mapToDouble(Long::doubleValue);
                })
                .average()
                .orElse(0.0);
    }

    /**
     * Tarea f: Detección de sobrecarga en rutas.
     * Clasifica cada ruta como "CRÍTICA" si el número de pasajeros supera un umbral dado, o "OK" en caso contrario.
     */
    public Map<String, String> detectarSobrecargaEnRutas(List<RegistroTransito> registros, long umbral) {
        Map<String, Long> usoPorRuta = registros.stream()
                .filter(r -> "entrada".equalsIgnoreCase(r.accion()))
                .collect(Collectors.groupingBy(
                        RegistroTransito::ruta,
                        Collectors.counting()
                ));

        return usoPorRuta.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() > umbral 
                                ? "CRÍTICA (" + entry.getValue() + " pasajeros)" 
                                : "OK (" + entry.getValue() + " pasajeros)"
                ));
    }
}