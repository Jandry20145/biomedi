CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    dentist_id BIGINT NOT NULL,
    appointment_date TIMESTAMP NOT NULL,
    reason VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NULL,
    CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_appointments_dentist FOREIGN KEY (dentist_id) REFERENCES personal (id),
    CONSTRAINT fk_appointments_created_by FOREIGN KEY (created_by) REFERENCES personal (id),
    CONSTRAINT fk_appointments_updated_by FOREIGN KEY (updated_by) REFERENCES personal (id),
    CONSTRAINT ck_appointments_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED'))
);

CREATE INDEX idx_appointments_patient ON appointments (patient_id, appointment_date DESC);
CREATE INDEX idx_appointments_dentist ON appointments (dentist_id, appointment_date DESC);
CREATE INDEX idx_appointments_date ON appointments (appointment_date);
