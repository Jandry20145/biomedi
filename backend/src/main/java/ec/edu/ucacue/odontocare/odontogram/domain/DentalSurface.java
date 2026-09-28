package ec.edu.ucacue.odontocare.odontogram.domain;

import java.util.Arrays;

public enum DentalSurface {
    OCLUSAL("OCCLUSAL"), VESTIBULAR("BUCCAL"), LINGUAL("LINGUAL"), PALATINA("PALATAL"),
    MESIAL("MESIAL"), DISTAL("DISTAL"), CERVICAL("CERVICAL"), RAIZ("ROOT"),
    PIEZA_COMPLETA("WHOLE_TOOTH");

    private final String catalogCode;
    DentalSurface(String catalogCode) { this.catalogCode = catalogCode; }
    public String catalogCode() { return catalogCode; }
    public static DentalSurface fromCatalogCode(String code) {
        return Arrays.stream(values()).filter(value -> value.catalogCode.equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Superficie de catálogo no soportada: " + code));
    }
}
