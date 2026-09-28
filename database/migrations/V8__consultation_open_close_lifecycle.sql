DROP TRIGGER IF EXISTS trg_dental_consultations_prevent_update;

ALTER TABLE dental_consultations
    ADD COLUMN lifecycle_status VARCHAR(20) NOT NULL DEFAULT 'CLOSED' AFTER consultation_type,
    ADD COLUMN closed_at DATETIME(6) NULL AFTER next_control_date,
    ADD COLUMN closed_by BIGINT UNSIGNED NULL AFTER closed_at,
    ADD CONSTRAINT fk_dental_consultations_closed_by FOREIGN KEY (closed_by) REFERENCES Personal (id),
    ADD CONSTRAINT ck_dental_consultations_lifecycle CHECK (lifecycle_status IN ('DRAFT', 'CLOSED')),
    ADD CONSTRAINT ck_dental_consultations_closed_fields CHECK (
        (lifecycle_status = 'DRAFT' AND closed_at IS NULL AND closed_by IS NULL)
        OR (lifecycle_status = 'CLOSED')
    ),
    ADD INDEX idx_dental_consultations_record_lifecycle (clinical_record_id, lifecycle_status, consultation_date DESC);

ALTER TABLE dental_consultations ALTER COLUMN lifecycle_status SET DEFAULT 'DRAFT';

DELIMITER $$
CREATE TRIGGER trg_dental_consultations_protect_clinical_content
BEFORE UPDATE ON dental_consultations
FOR EACH ROW
BEGIN
    IF NOT (
        OLD.clinical_record_id <=> NEW.clinical_record_id AND OLD.consultation_date <=> NEW.consultation_date
        AND OLD.consultation_type <=> NEW.consultation_type AND OLD.reason <=> NEW.reason
        AND OLD.current_illness <=> NEW.current_illness AND OLD.observations <=> NEW.observations
        AND OLD.facial_symmetry <=> NEW.facial_symmetry AND OLD.lymph_nodes <=> NEW.lymph_nodes
        AND OLD.temporomandibular_joint <=> NEW.temporomandibular_joint AND OLD.mouth_opening <=> NEW.mouth_opening
        AND OLD.facial_pain <=> NEW.facial_pain AND OLD.lips <=> NEW.lips AND OLD.soft_tissues <=> NEW.soft_tissues
        AND OLD.extraoral_observations <=> NEW.extraoral_observations AND OLD.oral_mucosa <=> NEW.oral_mucosa
        AND OLD.tongue <=> NEW.tongue AND OLD.palate <=> NEW.palate AND OLD.mouth_floor <=> NEW.mouth_floor
        AND OLD.gums <=> NEW.gums AND OLD.periodontium <=> NEW.periodontium AND OLD.oral_hygiene <=> NEW.oral_hygiene
        AND OLD.occlusion <=> NEW.occlusion AND OLD.saliva <=> NEW.saliva AND OLD.lesions <=> NEW.lesions
        AND OLD.intraoral_observations <=> NEW.intraoral_observations AND OLD.treatment <=> NEW.treatment
        AND OLD.medication <=> NEW.medication AND OLD.evolution <=> NEW.evolution
        AND OLD.recommendations <=> NEW.recommendations AND OLD.next_control_date <=> NEW.next_control_date
        AND OLD.created_by <=> NEW.created_by AND OLD.created_at <=> NEW.created_at
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El contenido clínico de la consulta es inmutable';
    END IF;
    IF OLD.lifecycle_status = 'CLOSED' AND NEW.lifecycle_status <> 'CLOSED' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Una consulta cerrada no puede reabrirse';
    END IF;
    IF OLD.lifecycle_status = 'DRAFT' AND NEW.lifecycle_status = 'CLOSED'
       AND (NEW.closed_at IS NULL OR NEW.closed_by IS NULL) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El cierre requiere fecha y profesional';
    END IF;
END$$
DELIMITER ;
