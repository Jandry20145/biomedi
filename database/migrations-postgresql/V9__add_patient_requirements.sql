ALTER TABLE patients
    ADD COLUMN responsible_name VARCHAR(120) NULL;

UPDATE patients SET responsible_name = 'Mismo Paciente' WHERE responsible_name IS NULL;

ALTER TABLE patients
    ALTER COLUMN responsible_name SET NOT NULL;

ALTER TABLE patients
    ALTER COLUMN identification TYPE VARCHAR(255),
    ALTER COLUMN phone TYPE VARCHAR(255),
    ALTER COLUMN email TYPE VARCHAR(255),
    ALTER COLUMN address TYPE VARCHAR(500);
