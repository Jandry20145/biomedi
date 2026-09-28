DELIMITER $$

CREATE TRIGGER trg_patients_birth_date_insert
BEFORE INSERT ON patients
FOR EACH ROW
BEGIN
    IF NEW.birth_date > CURRENT_DATE() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La fecha de nacimiento no puede ser futura';
    END IF;
END$$

CREATE TRIGGER trg_patients_birth_date_update
BEFORE UPDATE ON patients
FOR EACH ROW
BEGIN
    IF NEW.birth_date > CURRENT_DATE() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La fecha de nacimiento no puede ser futura';
    END IF;
END$$

CREATE TRIGGER trg_dental_consultations_prevent_delete
BEFORE DELETE ON dental_consultations
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Las consultas clínicas no se eliminan físicamente';
END$$

CREATE TRIGGER trg_odontogram_events_prevent_update
BEFORE UPDATE ON odontogram_events
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Los eventos del odontograma son inmutables';
END$$

CREATE TRIGGER trg_odontogram_events_prevent_delete
BEFORE DELETE ON odontogram_events
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Los eventos del odontograma no se eliminan';
END$$

CREATE TRIGGER trg_dental_evolutions_prevent_update
BEFORE UPDATE ON dental_evolutions
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Las evoluciones odontológicas son inmutables';
END$$

CREATE TRIGGER trg_dental_evolutions_prevent_delete
BEFORE DELETE ON dental_evolutions
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Las evoluciones odontológicas no se eliminan';
END$$

DELIMITER ;

CREATE VIEW v_current_odontogram_state AS
SELECT
    ranked.patient_id,
    ranked.clinical_record_id,
    ranked.consultation_id,
    ranked.event_id,
    ranked.dental_piece_id,
    ranked.internal_code,
    ranked.status_id,
    ranked.status_code,
    ranked.status_name,
    ranked.observations,
    ranked.event_date,
    ranked.created_by
FROM (
    SELECT
        cr.patient_id,
        cr.id AS clinical_record_id,
        dc.id AS consultation_id,
        oe.id AS event_id,
        dpc.id AS dental_piece_id,
        dpc.internal_code,
        osc.id AS status_id,
        osc.code AS status_code,
        osc.name AS status_name,
        oe.observations,
        oe.event_date,
        oe.created_by,
        ROW_NUMBER() OVER (
            PARTITION BY cr.patient_id, dpc.id
            ORDER BY oe.event_date DESC, oe.id DESC
        ) AS row_position
    FROM odontogram_events oe
    INNER JOIN dental_consultations dc ON dc.id = oe.consultation_id
    INNER JOIN clinical_records cr ON cr.id = dc.clinical_record_id
    INNER JOIN dental_piece_catalog dpc ON dpc.id = oe.dental_piece_id
    INNER JOIN odontogram_status_catalog osc ON osc.id = oe.status_id
) ranked
WHERE ranked.row_position = 1;

