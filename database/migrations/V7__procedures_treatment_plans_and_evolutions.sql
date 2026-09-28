CREATE TABLE procedure_type_catalog (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(120) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT uk_procedure_type_code UNIQUE (code)
) ENGINE=InnoDB;

INSERT INTO procedure_type_catalog (code, name) VALUES
('PROPHYLAXIS','Profilaxis'),('FLUORIDE_APPLICATION','Aplicación de flúor'),('SEALANT','Sellante'),
('COMPOSITE_RESTORATION','Restauración con resina'),('TEMPORARY_RESTORATION','Restauración temporal'),
('DEFINITIVE_RESTORATION','Restauración definitiva'),('SIMPLE_EXTRACTION','Extracción simple'),
('SURGICAL_EXTRACTION','Extracción quirúrgica'),('ENDODONTICS','Endodoncia'),('PULP_CHAMBER_OPENING','Apertura cameral'),
('CROWN','Corona'),('PARTIAL_PROSTHESIS','Prótesis parcial'),('TOTAL_PROSTHESIS','Prótesis total'),
('PROSTHETIC_ADJUSTMENT','Ajuste protésico'),('SCALING_ROOT_PLANING','Raspado y alisado radicular'),
('PERIODONTAL_TREATMENT','Tratamiento periodontal'),('ABSCESS_DRAINAGE','Drenaje de absceso'),
('POSTOPERATIVE_CONTROL','Control postoperatorio'),('SPECIALTY_REFERRAL','Referencia a especialidad'),('OTHER','Otro');

ALTER TABLE dental_procedures
    ADD COLUMN procedure_type_id BIGINT UNSIGNED NULL AFTER dental_piece_id,
    ADD COLUMN diagnosis_id BIGINT UNSIGNED NULL AFTER procedure_type_id,
    ADD COLUMN material_used VARCHAR(500) NULL AFTER description,
    ADD COLUMN anesthesia_used VARCHAR(500) NULL AFTER material_used,
    ADD COLUMN post_instructions TEXT NULL AFTER anesthesia_used,
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED' AFTER post_instructions,
    ADD COLUMN next_control_date DATE NULL AFTER performed_at,
    ADD CONSTRAINT fk_procedures_type FOREIGN KEY (procedure_type_id) REFERENCES procedure_type_catalog(id),
    ADD CONSTRAINT fk_procedures_diagnosis FOREIGN KEY (diagnosis_id) REFERENCES diagnoses(id),
    ADD CONSTRAINT ck_procedures_status CHECK (status IN ('PLANNED','IN_PROGRESS','COMPLETED','CANCELLED'));

CREATE TABLE dental_procedure_pieces (
    procedure_id BIGINT UNSIGNED NOT NULL,
    dental_piece_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (procedure_id, dental_piece_id),
    CONSTRAINT fk_procedure_pieces_procedure FOREIGN KEY (procedure_id) REFERENCES dental_procedures(id),
    CONSTRAINT fk_procedure_pieces_piece FOREIGN KEY (dental_piece_id) REFERENCES dental_piece_catalog(id)
) ENGINE=InnoDB;

INSERT INTO dental_procedure_pieces (procedure_id, dental_piece_id)
SELECT id, dental_piece_id FROM dental_procedures WHERE dental_piece_id IS NOT NULL;

ALTER TABLE treatment_plan_items
    ADD COLUMN sessions_estimated SMALLINT UNSIGNED NOT NULL DEFAULT 1 AFTER priority,
    ADD COLUMN observations TEXT NULL AFTER estimated_date,
    ADD CONSTRAINT ck_treatment_sessions CHECK (sessions_estimated BETWEEN 1 AND 99);

ALTER TABLE dental_evolutions
    ADD COLUMN procedure_id BIGINT UNSIGNED NULL AFTER dental_piece_id,
    ADD COLUMN result VARCHAR(30) NOT NULL DEFAULT 'STABLE' AFTER description,
    ADD COLUMN next_control_date DATE NULL AFTER recorded_at,
    ADD CONSTRAINT fk_evolutions_procedure FOREIGN KEY (procedure_id) REFERENCES dental_procedures(id),
    ADD CONSTRAINT ck_evolutions_result CHECK (result IN ('FAVORABLE','STABLE','PENDING','REQUIRES_FOLLOW_UP'));

CREATE INDEX idx_procedure_pieces_piece ON dental_procedure_pieces(dental_piece_id, procedure_id);
CREATE INDEX idx_evolutions_piece ON dental_evolutions(dental_piece_id, recorded_at DESC);
