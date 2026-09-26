-- Estado de los items de inventario.
--
-- Sólo las condiciones se persisten de forma efectiva: EN_ALMACEN / OPERATIVO en el físico y
-- SIN_ASIGNAR / ASIGNADO en el lógico se recalculan en cada lectura a partir de la asignación
-- a estación, así que un valor de ubicación almacenado nunca puede quedar desincronizado.
--
-- Los identificadores van sin acentos ni eñes, igual que el resto de enums del proyecto.

ALTER TABLE inventario_fisico
    ADD COLUMN estado VARCHAR(255) NOT NULL DEFAULT 'EN_ALMACEN';

ALTER TABLE inventario_fisico
    ADD CONSTRAINT ck_fisico_estado CHECK (estado IN
        ('EN_ALMACEN', 'OPERATIVO', 'DANADO', 'EN_REPARACION', 'OBSOLETO'));

ALTER TABLE inventario_logico
    ADD COLUMN estado VARCHAR(255) NOT NULL DEFAULT 'SIN_ASIGNAR';

ALTER TABLE inventario_logico
    ADD CONSTRAINT ck_logico_estado CHECK (estado IN
        ('SIN_ASIGNAR', 'ASIGNADO', 'BLOQUEADO', 'NUMERO_PERDIDO', 'SIN_ACCESO'));
