CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    NEW.updated_at := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;

CREATE TRIGGER a_users_updated_at BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER a_patients_updated_at BEFORE UPDATE ON patients
FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER a_clinical_records_updated_at BEFORE UPDATE ON clinical_records
FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER a_clinical_backgrounds_updated_at BEFORE UPDATE ON clinical_backgrounds
FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER a_dental_consultations_updated_at BEFORE UPDATE ON dental_consultations
FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER a_vital_signs_updated_at BEFORE UPDATE ON vital_signs
FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER a_dental_piece_catalog_updated_at BEFORE UPDATE ON dental_piece_catalog
FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER a_treatment_plans_updated_at BEFORE UPDATE ON treatment_plans
FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER a_treatment_plan_items_updated_at BEFORE UPDATE ON treatment_plan_items
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE OR REPLACE FUNCTION reject_immutable_change()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    RAISE EXCEPTION 'La información clínica histórica de % es inmutable', TG_TABLE_NAME
        USING ERRCODE = 'check_violation';
END;
$$;

CREATE TRIGGER trg_dental_consultations_prevent_delete
BEFORE DELETE ON dental_consultations FOR EACH ROW EXECUTE FUNCTION reject_immutable_change();
CREATE TRIGGER trg_odontogram_events_prevent_update
BEFORE UPDATE ON odontogram_events FOR EACH ROW EXECUTE FUNCTION reject_immutable_change();
CREATE TRIGGER trg_odontogram_events_prevent_delete
BEFORE DELETE ON odontogram_events FOR EACH ROW EXECUTE FUNCTION reject_immutable_change();
CREATE TRIGGER trg_dental_evolutions_prevent_update
BEFORE UPDATE ON dental_evolutions FOR EACH ROW EXECUTE FUNCTION reject_immutable_change();
CREATE TRIGGER trg_dental_evolutions_prevent_delete
BEFORE DELETE ON dental_evolutions FOR EACH ROW EXECUTE FUNCTION reject_immutable_change();

CREATE OR REPLACE FUNCTION protect_dental_consultation_content()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF ROW(
        OLD.clinical_record_id, OLD.consultation_date, OLD.consultation_type, OLD.reason,
        OLD.current_illness, OLD.observations, OLD.facial_symmetry, OLD.lymph_nodes,
        OLD.temporomandibular_joint, OLD.mouth_opening, OLD.facial_pain, OLD.lips,
        OLD.soft_tissues, OLD.extraoral_observations, OLD.oral_mucosa, OLD.tongue,
        OLD.palate, OLD.mouth_floor, OLD.gums, OLD.periodontium, OLD.oral_hygiene,
        OLD.occlusion, OLD.saliva, OLD.lesions, OLD.intraoral_observations,
        OLD.treatment, OLD.medication, OLD.evolution, OLD.recommendations,
        OLD.next_control_date, OLD.created_by, OLD.created_at
    ) IS DISTINCT FROM ROW(
        NEW.clinical_record_id, NEW.consultation_date, NEW.consultation_type, NEW.reason,
        NEW.current_illness, NEW.observations, NEW.facial_symmetry, NEW.lymph_nodes,
        NEW.temporomandibular_joint, NEW.mouth_opening, NEW.facial_pain, NEW.lips,
        NEW.soft_tissues, NEW.extraoral_observations, NEW.oral_mucosa, NEW.tongue,
        NEW.palate, NEW.mouth_floor, NEW.gums, NEW.periodontium, NEW.oral_hygiene,
        NEW.occlusion, NEW.saliva, NEW.lesions, NEW.intraoral_observations,
        NEW.treatment, NEW.medication, NEW.evolution, NEW.recommendations,
        NEW.next_control_date, NEW.created_by, NEW.created_at
    ) THEN
        RAISE EXCEPTION 'El contenido clínico de la consulta es inmutable'
            USING ERRCODE = 'check_violation';
    END IF;

    IF OLD.lifecycle_status = 'CLOSED' AND NEW.lifecycle_status <> 'CLOSED' THEN
        RAISE EXCEPTION 'Una consulta cerrada no puede reabrirse'
            USING ERRCODE = 'check_violation';
    END IF;

    IF OLD.lifecycle_status = 'DRAFT' AND NEW.lifecycle_status = 'CLOSED'
       AND (NEW.closed_at IS NULL OR NEW.closed_by IS NULL) THEN
        RAISE EXCEPTION 'El cierre requiere fecha y profesional'
            USING ERRCODE = 'check_violation';
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER z_dental_consultations_protect_content
BEFORE UPDATE ON dental_consultations
FOR EACH ROW EXECUTE FUNCTION protect_dental_consultation_content();

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
    JOIN dental_consultations dc ON dc.id = oe.consultation_id
    JOIN clinical_records cr ON cr.id = dc.clinical_record_id
    JOIN dental_piece_catalog dpc ON dpc.id = oe.dental_piece_id
    JOIN odontogram_status_catalog osc ON osc.id = oe.status_id
) ranked
WHERE ranked.row_position = 1;

-- El backend accede mediante conexión PostgreSQL directa. PostgREST no recibe acceso implícito.
ALTER TABLE roles ENABLE ROW LEVEL SECURITY;
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE patients ENABLE ROW LEVEL SECURITY;
ALTER TABLE clinical_records ENABLE ROW LEVEL SECURITY;
ALTER TABLE clinical_backgrounds ENABLE ROW LEVEL SECURITY;
ALTER TABLE dental_consultations ENABLE ROW LEVEL SECURITY;
ALTER TABLE vital_signs ENABLE ROW LEVEL SECURITY;
ALTER TABLE diagnoses ENABLE ROW LEVEL SECURITY;
ALTER TABLE dental_piece_catalog ENABLE ROW LEVEL SECURITY;
ALTER TABLE odontogram_status_catalog ENABLE ROW LEVEL SECURITY;
ALTER TABLE odontogram_surfaces ENABLE ROW LEVEL SECURITY;
ALTER TABLE odontogram_events ENABLE ROW LEVEL SECURITY;
ALTER TABLE odontogram_event_surfaces ENABLE ROW LEVEL SECURITY;
ALTER TABLE procedure_type_catalog ENABLE ROW LEVEL SECURITY;
ALTER TABLE dental_procedures ENABLE ROW LEVEL SECURITY;
ALTER TABLE dental_procedure_pieces ENABLE ROW LEVEL SECURITY;
ALTER TABLE treatment_plans ENABLE ROW LEVEL SECURITY;
ALTER TABLE treatment_plan_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE dental_evolutions ENABLE ROW LEVEL SECURITY;
ALTER TABLE clinical_files ENABLE ROW LEVEL SECURITY;
ALTER TABLE audit_log ENABLE ROW LEVEL SECURITY;

DO $$
BEGIN
    IF to_regrole('anon') IS NOT NULL THEN
        EXECUTE 'REVOKE ALL ON ALL TABLES IN SCHEMA public FROM anon';
        EXECUTE 'REVOKE ALL ON ALL SEQUENCES IN SCHEMA public FROM anon';
    END IF;
    IF to_regrole('authenticated') IS NOT NULL THEN
        EXECUTE 'REVOKE ALL ON ALL TABLES IN SCHEMA public FROM authenticated';
        EXECUTE 'REVOKE ALL ON ALL SEQUENCES IN SCHEMA public FROM authenticated';
    END IF;
END;
$$;
