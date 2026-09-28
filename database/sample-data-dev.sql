-- DATOS EXCLUSIVAMENTE DEMOSTRATIVOS. NO CORRESPONDEN A PERSONAS REALES.
-- Este archivo no forma parte de Flyway y debe ejecutarse manualmente solo en desarrollo.

INSERT INTO patients (
    clinical_record_number,
    identification,
    responsible_name,
    first_names,
    last_names,
    birth_date,
    sex,
    address,-
    phone,
    email
) VALUES (
    'DEMO-HC-0001',
    'DEMO-ID-0001',
    'MISMO PACIENTE',
    'PACIENTE',
    'DEMOSTRATIVO',
    '2000-01-01',
    'NO ESPECIFICADO - DEMO',
    'DIRECCIÓN DE PRUEBA',
    '0000000000',
    'paciente.demo@example.test'
)
ON DUPLICATE KEY UPDATE identification = VALUES(identification);

INSERT INTO clinical_records (patient_id)
SELECT id
FROM patients
WHERE identification = 'DEMO-ID-0001'
ON DUPLICATE KEY UPDATE patient_id = VALUES(patient_id);

INSERT INTO clinical_backgrounds (patient_id, background_type, description)
SELECT p.id, 'PERSONAL', 'DATO DEMOSTRATIVO: sin información clínica registrada.'
FROM patients p
WHERE p.identification = 'DEMO-ID-0001'
  AND NOT EXISTS (
      SELECT 1
      FROM clinical_backgrounds cb
      WHERE cb.patient_id = p.id
        AND cb.background_type = 'PERSONAL'
        AND cb.description LIKE 'DATO DEMOSTRATIVO:%'
  );

-- SECUENCIA ODONTOGRAMA DEMOSTRATIVA: TOOTH_36, nunca usar como dato clínico real.
INSERT INTO dental_consultations (clinical_record_id, consultation_date, consultation_type, lifecycle_status, reason, recommendations, closed_at, closed_by, created_by)
SELECT cr.id, '2026-06-01 09:00:00', 'DEMO', 'CLOSED', 'DATO DEMOSTRATIVO: consulta para odontograma TOOTH_36', 'DATO DEMOSTRATIVO: restauración indicada', '2026-06-01 09:30:00', u.id, u.id
FROM clinical_records cr
JOIN patients p ON p.id = cr.patient_id
JOIN users u ON u.username = 'odontologo'
WHERE p.identification = 'DEMO-ID-0001'
  AND NOT EXISTS (SELECT 1 FROM dental_consultations dc WHERE dc.clinical_record_id = cr.id AND dc.reason LIKE 'DATO DEMOSTRATIVO: consulta para odontograma%');

INSERT INTO odontogram_events (consultation_id, dental_piece_id, status_id, observations, event_date, created_by)
SELECT dc.id, dp.id, os.id, 'DATO DEMOSTRATIVO TOOTH_36: hallazgo inicial', '2026-06-01 09:10:00', dc.created_by
FROM dental_consultations dc
JOIN dental_piece_catalog dp ON dp.internal_code = 'TOOTH_36'
JOIN odontogram_status_catalog os ON os.code = 'CARIES'
WHERE dc.reason = 'DATO DEMOSTRATIVO: consulta para odontograma TOOTH_36'
  AND NOT EXISTS (SELECT 1 FROM odontogram_events oe WHERE oe.consultation_id = dc.id AND oe.dental_piece_id = dp.id AND oe.observations LIKE 'DATO DEMOSTRATIVO TOOTH_36: hallazgo inicial%');

INSERT INTO odontogram_event_surfaces (event_id, surface_id)
SELECT oe.id, s.id FROM odontogram_events oe
JOIN odontogram_surfaces s ON s.code IN ('OCCLUSAL', 'DISTAL')
WHERE oe.observations = 'DATO DEMOSTRATIVO TOOTH_36: hallazgo inicial'
ON DUPLICATE KEY UPDATE event_id = VALUES(event_id);

INSERT INTO dental_consultations (clinical_record_id, consultation_date, consultation_type, lifecycle_status, reason, evolution, closed_at, closed_by, created_by)
SELECT cr.id, '2026-06-15 09:00:00', 'CONTROL_DEMO', 'CLOSED', 'DATO DEMOSTRATIVO: control restauración TOOTH_36', 'DATO DEMOSTRATIVO: evolución favorable', '2026-06-15 09:40:00', u.id, u.id
FROM clinical_records cr JOIN patients p ON p.id=cr.patient_id JOIN users u ON u.username='odontologo'
WHERE p.identification='DEMO-ID-0001' AND NOT EXISTS(SELECT 1 FROM dental_consultations dc WHERE dc.clinical_record_id=cr.id AND dc.reason='DATO DEMOSTRATIVO: control restauración TOOTH_36');

INSERT INTO odontogram_events (consultation_id,dental_piece_id,status_id,observations,event_date,created_by)
SELECT dc.id,dp.id,os.id,'DATO DEMOSTRATIVO TOOTH_36: restauración definitiva oclusal','2026-06-15 09:20:00',dc.created_by
FROM dental_consultations dc JOIN dental_piece_catalog dp ON dp.internal_code='TOOTH_36' JOIN odontogram_status_catalog os ON os.code='RESTORATION'
WHERE dc.reason='DATO DEMOSTRATIVO: control restauración TOOTH_36' AND NOT EXISTS(SELECT 1 FROM odontogram_events oe WHERE oe.consultation_id=dc.id AND oe.dental_piece_id=dp.id);

INSERT INTO odontogram_event_surfaces(event_id,surface_id)
SELECT oe.id,s.id FROM odontogram_events oe JOIN odontogram_surfaces s ON s.code='OCCLUSAL'
WHERE oe.observations='DATO DEMOSTRATIVO TOOTH_36: restauración definitiva oclusal'
ON DUPLICATE KEY UPDATE event_id=VALUES(event_id);
