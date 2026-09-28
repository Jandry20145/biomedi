CREATE TABLE Roles (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    Nombre VARCHAR(80) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE Personal (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_rol BIGINT UNSIGNED NOT NULL,
    usuario VARCHAR(80) NOT NULL,
    clave VARCHAR(100) NOT NULL,
    nombres VARCHAR(120) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (usuario),
    CONSTRAINT fk_users_role FOREIGN KEY (id_rol) REFERENCES Roles (id)
) ENGINE = InnoDB;

CREATE TABLE patients (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    clinical_record_number VARCHAR(40) NOT NULL,
    identification VARCHAR(40) NOT NULL,
    first_names VARCHAR(120) NOT NULL,
    last_names VARCHAR(120) NOT NULL,
    birth_date DATE NOT NULL,
    sex VARCHAR(30) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(160) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by BIGINT UNSIGNED NULL,
    updated_by BIGINT UNSIGNED NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_patients_identification UNIQUE (identification),
    CONSTRAINT uk_patients_clinical_record_number UNIQUE (clinical_record_number),
    CONSTRAINT fk_patients_created_by FOREIGN KEY (created_by) REFERENCES Personal (id),
    CONSTRAINT fk_patients_updated_by FOREIGN KEY (updated_by) REFERENCES Personal (id),
    CONSTRAINT ck_patients_soft_delete CHECK (
        (active = TRUE AND deleted_at IS NULL)
        OR (active = FALSE AND deleted_at IS NOT NULL)
    ),
    INDEX idx_patients_first_names (first_names),
    INDEX idx_patients_last_names (last_names),
    INDEX idx_patients_name_search (last_names, first_names)
) ENGINE = InnoDB;

CREATE TABLE clinical_records (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    patient_id BIGINT UNSIGNED NOT NULL,
    opened_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by BIGINT UNSIGNED NULL,
    updated_by BIGINT UNSIGNED NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_clinical_records_patient UNIQUE (patient_id),
    CONSTRAINT fk_clinical_records_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_clinical_records_created_by FOREIGN KEY (created_by) REFERENCES Personal (id),
    CONSTRAINT fk_clinical_records_updated_by FOREIGN KEY (updated_by) REFERENCES Personal (id),
    CONSTRAINT ck_clinical_records_status CHECK (status IN ('ACTIVE', 'CLOSED'))
) ENGINE = InnoDB;

CREATE TABLE clinical_backgrounds (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    patient_id BIGINT UNSIGNED NOT NULL,
    background_type VARCHAR(40) NOT NULL,
    description TEXT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by BIGINT UNSIGNED NULL,
    updated_by BIGINT UNSIGNED NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_clinical_backgrounds_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_clinical_backgrounds_created_by FOREIGN KEY (created_by) REFERENCES Personal (id),
    CONSTRAINT fk_clinical_backgrounds_updated_by FOREIGN KEY (updated_by) REFERENCES Personal (id),
    CONSTRAINT ck_clinical_backgrounds_type CHECK (background_type IN (
        'PERSONAL', 'FAMILY', 'PATHOLOGICAL', 'SURGICAL', 'ALLERGY',
        'CURRENT_MEDICATION', 'DENTAL'
    )),
    INDEX idx_clinical_backgrounds_patient_type (patient_id, background_type, active)
) ENGINE = InnoDB;

CREATE TABLE dental_consultations (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    clinical_record_id BIGINT UNSIGNED NOT NULL,
    consultation_date DATETIME(6) NOT NULL,
    reason TEXT NOT NULL,
    current_illness TEXT NULL,
    observations TEXT NULL,
    extraoral_exam TEXT NULL,
    intraoral_exam TEXT NULL,
    treatment TEXT NULL,
    medication TEXT NULL,
    evolution TEXT NULL,
    recommendations TEXT NULL,
    next_control_date DATE NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by BIGINT UNSIGNED NOT NULL,
    updated_by BIGINT UNSIGNED NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_dental_consultations_record FOREIGN KEY (clinical_record_id) REFERENCES clinical_records (id),
    CONSTRAINT fk_dental_consultations_created_by FOREIGN KEY (created_by) REFERENCES Personal (id),
    CONSTRAINT fk_dental_consultations_updated_by FOREIGN KEY (updated_by) REFERENCES Personal (id),
    INDEX idx_dental_consultations_record_date (clinical_record_id, consultation_date DESC, id DESC),
    INDEX idx_dental_consultations_date (consultation_date)
) ENGINE = InnoDB;

CREATE TABLE vital_signs (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    consultation_id BIGINT UNSIGNED NOT NULL,
    systolic_bp SMALLINT UNSIGNED NULL,
    diastolic_bp SMALLINT UNSIGNED NULL,
    heart_rate SMALLINT UNSIGNED NULL,
    respiratory_rate SMALLINT UNSIGNED NULL,
    temperature_c DECIMAL(4, 1) NULL,
    oxygen_saturation DECIMAL(5, 2) NULL,
    weight_kg DECIMAL(6, 2) NULL,
    height_cm DECIMAL(5, 2) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by BIGINT UNSIGNED NOT NULL,
    updated_by BIGINT UNSIGNED NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_vital_signs_consultation UNIQUE (consultation_id),
    CONSTRAINT fk_vital_signs_consultation FOREIGN KEY (consultation_id) REFERENCES dental_consultations (id),
    CONSTRAINT fk_vital_signs_created_by FOREIGN KEY (created_by) REFERENCES Personal (id),
    CONSTRAINT fk_vital_signs_updated_by FOREIGN KEY (updated_by) REFERENCES Personal (id),
    CONSTRAINT ck_vital_signs_oxygen CHECK (oxygen_saturation IS NULL OR oxygen_saturation BETWEEN 0 AND 100),
    CONSTRAINT ck_vital_signs_positive CHECK (
        (weight_kg IS NULL OR weight_kg > 0)
        AND (height_cm IS NULL OR height_cm > 0)
        AND (temperature_c IS NULL OR temperature_c > 0)
    )
) ENGINE = InnoDB;

CREATE TABLE diagnoses (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    consultation_id BIGINT UNSIGNED NOT NULL,
    description VARCHAR(500) NOT NULL,
    icd10_code VARCHAR(20) NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_diagnoses_consultation FOREIGN KEY (consultation_id) REFERENCES dental_consultations (id),
    CONSTRAINT fk_diagnoses_created_by FOREIGN KEY (created_by) REFERENCES Personal (id),
    INDEX idx_diagnoses_consultation (consultation_id, is_primary)
) ENGINE = InnoDB;

CREATE TABLE dental_piece_catalog (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    internal_code VARCHAR(20) NOT NULL,
    position_code VARCHAR(40) NOT NULL,
    arch VARCHAR(20) NOT NULL,
    side VARCHAR(20) NOT NULL,
    piece_type VARCHAR(40) NOT NULL,
    visual_order SMALLINT UNSIGNED NOT NULL,
    model_node_name VARCHAR(80) NOT NULL,
    fdi_code VARCHAR(10) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_dental_piece_internal_code UNIQUE (internal_code),
    CONSTRAINT uk_dental_piece_visual_order UNIQUE (visual_order),
    CONSTRAINT uk_dental_piece_model_node UNIQUE (model_node_name),
    CONSTRAINT uk_dental_piece_fdi_code UNIQUE (fdi_code),
    CONSTRAINT ck_dental_piece_arch CHECK (arch IN ('UPPER', 'LOWER')),
    CONSTRAINT ck_dental_piece_side CHECK (side IN ('RIGHT', 'LEFT')),
    CONSTRAINT ck_dental_piece_visual_order CHECK (visual_order BETWEEN 1 AND 36)
) ENGINE = InnoDB;

CREATE TABLE odontogram_status_catalog (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT uk_odontogram_status_code UNIQUE (code)
) ENGINE = InnoDB;

CREATE TABLE odontogram_surfaces (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT uk_odontogram_surfaces_code UNIQUE (code)
) ENGINE = InnoDB;

CREATE TABLE odontogram_events (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    consultation_id BIGINT UNSIGNED NOT NULL,
    dental_piece_id BIGINT UNSIGNED NOT NULL,
    status_id BIGINT UNSIGNED NOT NULL,
    observations TEXT NULL,
    event_date DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_odontogram_events_consultation FOREIGN KEY (consultation_id) REFERENCES dental_consultations (id),
    CONSTRAINT fk_odontogram_events_piece FOREIGN KEY (dental_piece_id) REFERENCES dental_piece_catalog (id),
    CONSTRAINT fk_odontogram_events_status FOREIGN KEY (status_id) REFERENCES odontogram_status_catalog (id),
    CONSTRAINT fk_odontogram_events_created_by FOREIGN KEY (created_by) REFERENCES Personal (id),
    INDEX idx_odontogram_events_consultation (consultation_id, event_date DESC),
    INDEX idx_odontogram_events_piece_date (dental_piece_id, event_date DESC, id DESC)
) ENGINE = InnoDB;

CREATE TABLE odontogram_event_surfaces (
    event_id BIGINT UNSIGNED NOT NULL,
    surface_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (event_id, surface_id),
    CONSTRAINT fk_event_surfaces_event FOREIGN KEY (event_id) REFERENCES odontogram_events (id),
    CONSTRAINT fk_event_surfaces_surface FOREIGN KEY (surface_id) REFERENCES odontogram_surfaces (id)
) ENGINE = InnoDB;

CREATE TABLE dental_procedures (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    consultation_id BIGINT UNSIGNED NOT NULL,
    dental_piece_id BIGINT UNSIGNED NULL,
    name VARCHAR(160) NOT NULL,
    description TEXT NULL,
    performed_at DATETIME(6) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_dental_procedures_consultation FOREIGN KEY (consultation_id) REFERENCES dental_consultations (id),
    CONSTRAINT fk_dental_procedures_piece FOREIGN KEY (dental_piece_id) REFERENCES dental_piece_catalog (id),
    CONSTRAINT fk_dental_procedures_created_by FOREIGN KEY (created_by) REFERENCES Personal (id),
    INDEX idx_dental_procedures_consultation_date (consultation_id, performed_at DESC)
) ENGINE = InnoDB;

CREATE TABLE treatment_plans (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    clinical_record_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(160) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    started_on DATE NULL,
    completed_on DATE NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by BIGINT UNSIGNED NOT NULL,
    updated_by BIGINT UNSIGNED NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_treatment_plans_record FOREIGN KEY (clinical_record_id) REFERENCES clinical_records (id),
    CONSTRAINT fk_treatment_plans_created_by FOREIGN KEY (created_by) REFERENCES Personal (id),
    CONSTRAINT fk_treatment_plans_updated_by FOREIGN KEY (updated_by) REFERENCES Personal (id),
    CONSTRAINT ck_treatment_plans_status CHECK (status IN ('DRAFT', 'ACTIVE', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT ck_treatment_plans_dates CHECK (completed_on IS NULL OR started_on IS NULL OR completed_on >= started_on),
    INDEX idx_treatment_plans_record_status (clinical_record_id, status)
) ENGINE = InnoDB;

CREATE TABLE treatment_plan_items (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    treatment_plan_id BIGINT UNSIGNED NOT NULL,
    dental_piece_id BIGINT UNSIGNED NULL,
    description VARCHAR(500) NOT NULL,
    priority SMALLINT UNSIGNED NOT NULL DEFAULT 3,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    estimated_date DATE NULL,
    completed_procedure_id BIGINT UNSIGNED NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by BIGINT UNSIGNED NOT NULL,
    updated_by BIGINT UNSIGNED NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_treatment_items_plan FOREIGN KEY (treatment_plan_id) REFERENCES treatment_plans (id),
    CONSTRAINT fk_treatment_items_piece FOREIGN KEY (dental_piece_id) REFERENCES dental_piece_catalog (id),
    CONSTRAINT fk_treatment_items_procedure FOREIGN KEY (completed_procedure_id) REFERENCES dental_procedures (id),
    CONSTRAINT fk_treatment_items_created_by FOREIGN KEY (created_by) REFERENCES Personal (id),
    CONSTRAINT fk_treatment_items_updated_by FOREIGN KEY (updated_by) REFERENCES Personal (id),
    CONSTRAINT ck_treatment_items_priority CHECK (priority BETWEEN 1 AND 5),
    CONSTRAINT ck_treatment_items_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    INDEX idx_treatment_items_plan_status (treatment_plan_id, status, priority)
) ENGINE = InnoDB;

CREATE TABLE dental_evolutions (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    consultation_id BIGINT UNSIGNED NOT NULL,
    description TEXT NOT NULL,
    recorded_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_dental_evolutions_consultation FOREIGN KEY (consultation_id) REFERENCES dental_consultations (id),
    CONSTRAINT fk_dental_evolutions_created_by FOREIGN KEY (created_by) REFERENCES Personal (id),
    INDEX idx_dental_evolutions_consultation_date (consultation_id, recorded_at DESC, id DESC)
) ENGINE = InnoDB;

CREATE TABLE clinical_files (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    patient_id BIGINT UNSIGNED NOT NULL,
    consultation_id BIGINT UNSIGNED NULL,
    original_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(120) NOT NULL,
    storage_key VARCHAR(500) NOT NULL,
    checksum_sha256 CHAR(64) NOT NULL,
    file_size_bytes BIGINT UNSIGNED NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_clinical_files_storage_key UNIQUE (storage_key),
    CONSTRAINT fk_clinical_files_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_clinical_files_consultation FOREIGN KEY (consultation_id) REFERENCES dental_consultations (id),
    CONSTRAINT fk_clinical_files_created_by FOREIGN KEY (created_by) REFERENCES Personal (id),
    CONSTRAINT ck_clinical_files_size CHECK (file_size_bytes > 0),
    INDEX idx_clinical_files_patient (patient_id, created_at DESC)
) ENGINE = InnoDB;

CREATE TABLE audit_log (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NULL,
    action VARCHAR(60) NOT NULL,
    entity_type VARCHAR(80) NOT NULL,
    entity_id BIGINT UNSIGNED NULL,
    details JSON NULL,
    occurred_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_audit_log_user FOREIGN KEY (user_id) REFERENCES Personal (id),
    INDEX idx_audit_log_entity (entity_type, entity_id, occurred_at DESC),
    INDEX idx_audit_log_user_date (user_id, occurred_at DESC)
) ENGINE = InnoDB;
