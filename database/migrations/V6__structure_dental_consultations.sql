ALTER TABLE dental_consultations
    ADD COLUMN consultation_type VARCHAR(60) NOT NULL DEFAULT 'GENERAL' AFTER consultation_date,
    CHANGE COLUMN extraoral_exam extraoral_observations TEXT NULL,
    ADD COLUMN facial_symmetry TEXT NULL AFTER observations,
    ADD COLUMN lymph_nodes TEXT NULL AFTER facial_symmetry,
    ADD COLUMN temporomandibular_joint TEXT NULL AFTER lymph_nodes,
    ADD COLUMN mouth_opening TEXT NULL AFTER temporomandibular_joint,
    ADD COLUMN facial_pain TEXT NULL AFTER mouth_opening,
    ADD COLUMN lips TEXT NULL AFTER facial_pain,
    ADD COLUMN soft_tissues TEXT NULL AFTER lips,
    CHANGE COLUMN intraoral_exam intraoral_observations TEXT NULL,
    ADD COLUMN oral_mucosa TEXT NULL AFTER extraoral_observations,
    ADD COLUMN tongue TEXT NULL AFTER oral_mucosa,
    ADD COLUMN palate TEXT NULL AFTER tongue,
    ADD COLUMN mouth_floor TEXT NULL AFTER palate,
    ADD COLUMN gums TEXT NULL AFTER mouth_floor,
    ADD COLUMN periodontium TEXT NULL AFTER gums,
    ADD COLUMN oral_hygiene TEXT NULL AFTER periodontium,
    ADD COLUMN occlusion TEXT NULL AFTER oral_hygiene,
    ADD COLUMN saliva TEXT NULL AFTER occlusion,
    ADD COLUMN lesions TEXT NULL AFTER saliva;

ALTER TABLE diagnoses
    ADD COLUMN affected_teeth VARCHAR(255) NULL AFTER icd10_code,
    ADD COLUMN priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' AFTER affected_teeth,
    ADD CONSTRAINT ck_diagnoses_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT'));

ALTER TABLE dental_evolutions
    ADD COLUMN dental_piece_id BIGINT UNSIGNED NULL AFTER consultation_id,
    ADD CONSTRAINT fk_dental_evolutions_piece FOREIGN KEY (dental_piece_id) REFERENCES dental_piece_catalog (id);

DELIMITER $$
CREATE TRIGGER trg_dental_consultations_prevent_update
BEFORE UPDATE ON dental_consultations
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Las consultas clínicas son inmutables';
END$$
DELIMITER ;

