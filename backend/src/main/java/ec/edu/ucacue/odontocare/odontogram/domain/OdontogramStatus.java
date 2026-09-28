package ec.edu.ucacue.odontocare.odontogram.domain;

import java.util.Arrays;

public enum OdontogramStatus {
    SANA("HEALTHY"), CARIES("CARIES"), RESTAURACION("RESTORATION"),
    OBTURACION_TEMPORAL("TEMPORARY_FILLING"), OBTURACION_DEFINITIVA("DEFINITIVE_FILLING"),
    FRACTURA("FRACTURE"), AUSENTE("MISSING"), EXTRACCION_INDICADA("EXTRACTION_INDICATED"),
    EXTRAIDA("EXTRACTED"), ENDODONCIA("ENDODONTICS"), CORONA("CROWN"), PROTESIS("PROSTHESIS"),
    IMPLANTE("IMPLANT"), SELLANTE("SEALANT"), MOVILIDAD("MOBILITY"),
    PLACA_BACTERIANA("BACTERIAL_PLAQUE"), CALCULO_DENTAL("DENTAL_CALCULUS"),
    LESION_PERIODONTAL("PERIODONTAL_LESION"), OBSERVACION("OBSERVATION");

    private final String catalogCode;
    OdontogramStatus(String catalogCode) { this.catalogCode = catalogCode; }
    public String catalogCode() { return catalogCode; }
    public static OdontogramStatus fromCatalogCode(String code) {
        return Arrays.stream(values()).filter(value -> value.catalogCode.equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Estado de catálogo no soportado: " + code));
    }
}
