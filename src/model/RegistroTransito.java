package com.tecnovalle.transporte;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Registro inmutable que representa la validación de un usuario en el sistema.
 */
public record RegistroTransito(
    String idUsuario,
    String ruta,
    String estacion,
    String accion, // "ENTRADA" o "SALIDA"
    LocalDateTime timestamp
) {}

/**
 * Servicio funcional con funciones puras para el análisis masivo de movilidad.
 */
public class ProcesadorEstadisticas {

    // a) Cálculo de afluencia por estación (Conteo de entradas)
    public static Map<String, Long> calcularAfluenciaPorEstacion(List<RegistroTransito> registros) {
        return registros.stream()
            .filter(r -> "ENTRADA".equalsIgnoreCase(r.accion()))
            .collect(Collectors.groupingBy(RegistroTransito::estacion, Collectors.counting()));
    }

    // b) Identificación de horas pico (Agrupamiento por hora del día 0-23)
    public static Map<Integer, Long> calcularHorasPico(List<RegistroTransito> registros) {
        return registros.stream()
            .collect(Collectors.groupingBy(
                r -> r.timestamp().getHour(),
                Collectors.counting()
            ));
    }

    // c) Rutas más utilizadas ordenadas descendentemente
    public static Map<String, Long> obtenerRutasMasUtilizadas(List<RegistroTransito> registros) {
        return registros.stream()
            .collect(Collectors.groupingBy(RegistroTransito::ruta, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }

// d) Patrones de viaje por usuario (Secuencia cronológica de estaciones)
public static Map<String, List<String>> obtenerPatronesDeViaje(List<RegistroTransito> registros) {
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

// e) Cálculo de tiempo promedio entre estaciones consecutivas (en minutos)
public static double calcularTiempoPromedioEntreEstaciones(List<RegistroTransito> registros) {
    Map<String, List<RegistroTransito>> porUsuario = registros.stream()
        .collect(Collectors.groupingBy(RegistroTransito::idUsuario));

    return porUsuario.values().stream()
        .flatMapToDouble(listaUsuario -> {
            List<RegistroTransito> ordenados = listaUsuario.stream()
                .sorted(Comparator.comparing(RegistroTransito::timestamp))
                .toList();
            List<Double> minDiferencias = new ArrayList<>();
            for (int i = 0; i < ordenados.size() - 1; i++) {
                RegistroTransito actual = ordenados.get(i);
                RegistroTransito siguiente = ordenados.get(i + 1);
                if ("ENTRADA".equalsIgnoreCase(actual.accion()) && "SALIDA".equalsIgnoreCase(siguiente.accion())) {
                    long mins = Duration.between(actual.timestamp(), siguiente.timestamp()).toMinutes();
                    if (mins >= 0) minDiferencias.add((double) mins);
                }
            }
            return minDiferencias.stream().mapToDouble(Double::doubleValue);
        })
        .average()
        .orElse(0.0);
    }

// f) Detección de sobrecarga en rutas según umbral
public static Map<String, String> detectarSobrecargaRutas(List<RegistroTransito> registros, long umbral) {
    return registros.stream()
        .filter(r -> "ENTRADA".equalsIgnoreCase(r.accion()))
        .collect(Collectors.groupingBy(RegistroTransito::ruta, Collectors.counting()))
        .entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            e -> e.getValue() > umbral ? "CRÍTICA (" + e.getValue() + " pasajeros)" : "NORMAL (" + e.getValue() + " pasajeros)"
        ));
    }
}