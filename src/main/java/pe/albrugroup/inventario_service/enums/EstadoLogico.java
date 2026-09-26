package pe.albrugroup.inventario_service.enums;

/**
 * Estado de un item de inventario lógico.
 *
 * <p>Mismo criterio que {@link EstadoFisico}: sólo las condiciones se persisten de forma
 * efectiva; ASIGNADO y SIN_ASIGNAR se derivan de la asignación a estación al leer.
 */
public enum EstadoLogico {
    SIN_ASIGNAR,
    ASIGNADO,
    BLOQUEADO,
    NUMERO_PERDIDO,
    SIN_ACCESO;

    /** Condiciones que mandan sobre la ubicación al resolver el estado. */
    public boolean esCondicion() {
        return this == BLOQUEADO || this == NUMERO_PERDIDO || this == SIN_ACCESO;
    }
}
