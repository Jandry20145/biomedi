ALTER TABLE clinical_backgrounds DROP CHECK ck_clinical_backgrounds_type;

ALTER TABLE clinical_backgrounds
    ADD CONSTRAINT ck_clinical_backgrounds_type CHECK (background_type IN (
        'PERSONAL', 'FAMILY', 'PATHOLOGICAL', 'SURGICAL', 'ALLERGY',
        'CURRENT_MEDICATION', 'CHRONIC_DISEASES', 'LAST_DENTAL_VISIT',
        'PREVIOUS_DENTAL_TREATMENTS', 'PROSTHESIS_USE', 'ORTHODONTICS_USE',
        'BRUXISM', 'ORAL_HABITS', 'ORAL_HYGIENE', 'BRUSHING_FREQUENCY',
        'DENTAL_FLOSS_USE', 'MOUTHWASH_USE', 'DENTAL_OBSERVATIONS'
    )),
    ADD CONSTRAINT uk_clinical_backgrounds_patient_type UNIQUE (patient_id, background_type);

