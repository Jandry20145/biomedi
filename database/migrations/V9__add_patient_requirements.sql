ALTER TABLE patients
    ADD COLUMN responsible_name VARCHAR(120) NULL AFTER identification;

UPDATE patients SET responsible_name = 'Mismo Paciente' WHERE responsible_name IS NULL;

ALTER TABLE patients
    MODIFY COLUMN responsible_name VARCHAR(120) NOT NULL;

ALTER TABLE patients
    MODIFY COLUMN identification VARCHAR(255) NOT NULL,
    MODIFY COLUMN phone VARCHAR(255) NOT NULL,
    MODIFY COLUMN email VARCHAR(255) NOT NULL,
    MODIFY COLUMN address VARCHAR(500) NOT NULL;
