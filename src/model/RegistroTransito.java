package com.tecnomovil.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modelo inmutable que representa un registro individual de tránsito en TecnoMovil Data.
 * Implementado como un Record de Java para garantizar inmutabilidad absoluta y ausencia de efectos secundarios.
 */
public record RegistroTransito(
    String idUsuario,
    String ruta,
    String estacion,
    String accion,
    LocalDateTime timestamp
) {
    public RegistroTransito {
        Objects.requireNonNull(idUsuario, "idUsuario no puede ser nulo");
        Objects.requireNonNull(ruta, "ruta no puede ser nula");
        Objects.requireNonNull(estacion, "estacion no puede ser nula");
        Objects.requireNonNull(accion, "accion no puede ser nula");
        Objects.requireNonNull(timestamp, "timestamp no puede ser nulo");
    }
}