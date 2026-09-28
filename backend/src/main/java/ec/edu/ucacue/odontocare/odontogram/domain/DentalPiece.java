package ec.edu.ucacue.odontocare.odontogram.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "dental_piece_catalog")
public class DentalPiece {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "internal_code", nullable = false, unique = true) private String internalCode;
    @Column(name = "position_code", nullable = false) private String positionCode;
    @Column(nullable = false) private String arch;
    @Column(nullable = false) private String side;
    @Column(name = "piece_type", nullable = false) private String pieceType;
    @Column(name = "visual_order", nullable = false) private Short visualOrder;
    @Column(name = "model_node_name", nullable = false) private String modelNodeName;
    @Column(name = "fdi_code") private String fdiCode;
    @Column(nullable = false) private boolean active;
    protected DentalPiece() {}
    public Long getId() { return id; }
    public String getInternalCode() { return internalCode; }
    public String getPositionCode() { return positionCode; }
    public String getArch() { return arch; }
    public String getSide() { return side; }
    public String getPieceType() { return pieceType; }
    public Short getVisualOrder() { return visualOrder; }
    public String getModelNodeName() { return modelNodeName; }
    public String getFdiCode() { return fdiCode; }
    public boolean isActive() { return active; }
}
