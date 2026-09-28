package ec.edu.ucacue.odontocare.odontogram.presentation;

public record CurrentOdontogramPieceResponse(String pieceCode, String position, String arch, String side,
        String pieceType, short visualOrder, String modelNodeName, String fdiCode,
        OdontogramEventResponse currentEvent) {}
