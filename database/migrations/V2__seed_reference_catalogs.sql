INSERT INTO Roles (Nombre) VALUES
    ('odontologo'),
    ('auxiliar'),
    ('recepcionista');

INSERT INTO odontogram_status_catalog (code, name) VALUES
    ('HEALTHY', 'Sana'),
    ('CARIES', 'Caries'),
    ('RESTORATION', 'Restauración'),
    ('TEMPORARY_FILLING', 'Obturación temporal'),
    ('DEFINITIVE_FILLING', 'Obturación definitiva'),
    ('FRACTURE', 'Fractura'),
    ('MISSING', 'Ausente'),
    ('EXTRACTION_INDICATED', 'Extracción indicada'),
    ('EXTRACTED', 'Extraída'),
    ('ENDODONTICS', 'Endodoncia'),
    ('CROWN', 'Corona'),
    ('PROSTHESIS', 'Prótesis'),
    ('IMPLANT', 'Implante'),
    ('SEALANT', 'Sellante'),
    ('MOBILITY', 'Movilidad'),
    ('BACTERIAL_PLAQUE', 'Placa bacteriana'),
    ('DENTAL_CALCULUS', 'Cálculo dental'),
    ('PERIODONTAL_LESION', 'Lesión periodontal'),
    ('OBSERVATION', 'Observación');

INSERT INTO odontogram_surfaces (code, name) VALUES
    ('OCCLUSAL', 'Oclusal'),
    ('BUCCAL', 'Vestibular'),
    ('LINGUAL', 'Lingual'),
    ('PALATAL', 'Palatina'),
    ('MESIAL', 'Mesial'),
    ('DISTAL', 'Distal'),
    ('CERVICAL', 'Cervical'),
    ('ROOT', 'Raíz'),
    ('WHOLE_TOOTH', 'Pieza completa');

-- Posición, lado y arcada describen el orden técnico inicial de la escena 3D.
-- No constituyen nomenclatura clínica. fdi_code permanece NULL hasta validación académica.
INSERT INTO dental_piece_catalog (
    internal_code, position_code, arch, side, piece_type, visual_order, model_node_name, fdi_code
) VALUES
    ('TOOTH_01', 'UPPER_POSITION_01', 'UPPER', 'RIGHT', 'CONFIGURABLE', 1, 'tooth_01', NULL),
    ('TOOTH_02', 'UPPER_POSITION_02', 'UPPER', 'RIGHT', 'CONFIGURABLE', 2, 'tooth_02', NULL),
    ('TOOTH_03', 'UPPER_POSITION_03', 'UPPER', 'RIGHT', 'CONFIGURABLE', 3, 'tooth_03', NULL),
    ('TOOTH_04', 'UPPER_POSITION_04', 'UPPER', 'RIGHT', 'CONFIGURABLE', 4, 'tooth_04', NULL),
    ('TOOTH_05', 'UPPER_POSITION_05', 'UPPER', 'RIGHT', 'CONFIGURABLE', 5, 'tooth_05', NULL),
    ('TOOTH_06', 'UPPER_POSITION_06', 'UPPER', 'RIGHT', 'CONFIGURABLE', 6, 'tooth_06', NULL),
    ('TOOTH_07', 'UPPER_POSITION_07', 'UPPER', 'RIGHT', 'CONFIGURABLE', 7, 'tooth_07', NULL),
    ('TOOTH_08', 'UPPER_POSITION_08', 'UPPER', 'RIGHT', 'CONFIGURABLE', 8, 'tooth_08', NULL),
    ('TOOTH_09', 'UPPER_POSITION_09', 'UPPER', 'RIGHT', 'CONFIGURABLE', 9, 'tooth_09', NULL),
    ('TOOTH_10', 'UPPER_POSITION_10', 'UPPER', 'LEFT', 'CONFIGURABLE', 10, 'tooth_10', NULL),
    ('TOOTH_11', 'UPPER_POSITION_11', 'UPPER', 'LEFT', 'CONFIGURABLE', 11, 'tooth_11', NULL),
    ('TOOTH_12', 'UPPER_POSITION_12', 'UPPER', 'LEFT', 'CONFIGURABLE', 12, 'tooth_12', NULL),
    ('TOOTH_13', 'UPPER_POSITION_13', 'UPPER', 'LEFT', 'CONFIGURABLE', 13, 'tooth_13', NULL),
    ('TOOTH_14', 'UPPER_POSITION_14', 'UPPER', 'LEFT', 'CONFIGURABLE', 14, 'tooth_14', NULL),
    ('TOOTH_15', 'UPPER_POSITION_15', 'UPPER', 'LEFT', 'CONFIGURABLE', 15, 'tooth_15', NULL),
    ('TOOTH_16', 'UPPER_POSITION_16', 'UPPER', 'LEFT', 'CONFIGURABLE', 16, 'tooth_16', NULL),
    ('TOOTH_17', 'UPPER_POSITION_17', 'UPPER', 'LEFT', 'CONFIGURABLE', 17, 'tooth_17', NULL),
    ('TOOTH_18', 'UPPER_POSITION_18', 'UPPER', 'LEFT', 'CONFIGURABLE', 18, 'tooth_18', NULL),
    ('TOOTH_19', 'LOWER_POSITION_01', 'LOWER', 'RIGHT', 'CONFIGURABLE', 19, 'tooth_19', NULL),
    ('TOOTH_20', 'LOWER_POSITION_02', 'LOWER', 'RIGHT', 'CONFIGURABLE', 20, 'tooth_20', NULL),
    ('TOOTH_21', 'LOWER_POSITION_03', 'LOWER', 'RIGHT', 'CONFIGURABLE', 21, 'tooth_21', NULL),
    ('TOOTH_22', 'LOWER_POSITION_04', 'LOWER', 'RIGHT', 'CONFIGURABLE', 22, 'tooth_22', NULL),
    ('TOOTH_23', 'LOWER_POSITION_05', 'LOWER', 'RIGHT', 'CONFIGURABLE', 23, 'tooth_23', NULL),
    ('TOOTH_24', 'LOWER_POSITION_06', 'LOWER', 'RIGHT', 'CONFIGURABLE', 24, 'tooth_24', NULL),
    ('TOOTH_25', 'LOWER_POSITION_07', 'LOWER', 'RIGHT', 'CONFIGURABLE', 25, 'tooth_25', NULL),
    ('TOOTH_26', 'LOWER_POSITION_08', 'LOWER', 'RIGHT', 'CONFIGURABLE', 26, 'tooth_26', NULL),
    ('TOOTH_27', 'LOWER_POSITION_09', 'LOWER', 'RIGHT', 'CONFIGURABLE', 27, 'tooth_27', NULL),
    ('TOOTH_28', 'LOWER_POSITION_10', 'LOWER', 'LEFT', 'CONFIGURABLE', 28, 'tooth_28', NULL),
    ('TOOTH_29', 'LOWER_POSITION_11', 'LOWER', 'LEFT', 'CONFIGURABLE', 29, 'tooth_29', NULL),
    ('TOOTH_30', 'LOWER_POSITION_12', 'LOWER', 'LEFT', 'CONFIGURABLE', 30, 'tooth_30', NULL),
    ('TOOTH_31', 'LOWER_POSITION_13', 'LOWER', 'LEFT', 'CONFIGURABLE', 31, 'tooth_31', NULL),
    ('TOOTH_32', 'LOWER_POSITION_14', 'LOWER', 'LEFT', 'CONFIGURABLE', 32, 'tooth_32', NULL),
    ('TOOTH_33', 'LOWER_POSITION_15', 'LOWER', 'LEFT', 'CONFIGURABLE', 33, 'tooth_33', NULL),
    ('TOOTH_34', 'LOWER_POSITION_16', 'LOWER', 'LEFT', 'CONFIGURABLE', 34, 'tooth_34', NULL),
    ('TOOTH_35', 'LOWER_POSITION_17', 'LOWER', 'LEFT', 'CONFIGURABLE', 35, 'tooth_35', NULL),
    ('TOOTH_36', 'LOWER_POSITION_18', 'LOWER', 'LEFT', 'CONFIGURABLE', 36, 'tooth_36', NULL);

INSERT INTO Personal (id, nombres, usuario, clave, id_rol) VALUES
    (1, 'Sthefanie Torres', 'storres', '1234', 1),
    (2, 'Juan Daniel', 'jdaniel', '1234', 2),
    (3, 'Fanny Chuchuca', 'fchuchuca', '1234', 3);

