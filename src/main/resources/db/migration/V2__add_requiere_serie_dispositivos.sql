-- Permite marcar por dispositivo si el número de serie es obligatorio al dar de
-- alta inventario físico. Se exige por defecto: los dispositivos sin serie
-- (cables, cargadores) se desmarcan desde Admin > Dispositivos.
--
-- El DEFAULT TRUE es necesario para poder añadir la columna como NOT NULL
-- sobre tablas que ya tengan filas.

ALTER TABLE dispositivos
    ADD COLUMN requiere_serie BOOLEAN NOT NULL DEFAULT TRUE;
