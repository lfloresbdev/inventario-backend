package pe.albrugroup.inventario_service.enums;

/**
 * Estado de un item de inventario físico.
 *
 * <p>Sólo las condiciones (DANADO, EN_REPARACION, OBSOLETO) se persisten de forma efectiva:
 * EN_ALMACEN y OPERATIVO se recalculan en cada lectura a partir de la asignación a estación,
 * de modo que no pueden quedar desincronizados con ella.
 */
public enum EstadoFisico {
    EN_ALMACEN,
    OPERATIVO,
    DANADO,
    EN_REPARACION,
    OBSOLETO;

    /** Condiciones que mandan sobre la ubicación al resolver el estado. */
    public boolean esCondicion() {
        return this == DANADO || this == EN_REPARACION || this == OBSOLETO;
    }
}
