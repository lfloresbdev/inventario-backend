package pe.albrugroup.inventario_service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Empresa {
    LYBTEL,
    RUNA;

    @JsonValue
    public String toJson() {
        return this.name();
    }

    @JsonCreator
    public static Empresa fromJson(String value) {
        return Empresa.valueOf(value);
    }
}
