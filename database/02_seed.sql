-- Datos de prueba para VetCare
-- Requisito: crear antes los 3 usuarios en Supabase → Authentication → Users

-- 1. Usuarios de la aplicación (el id es el UUID de auth.users; sin contraseñas en plano
INSERT INTO app_users (id, username, full_name, role, active) VALUES
('UUID-DEL-ADMIN',   'admin',      'Administrador Principal', 'ADMIN',        true),
('UUID-DEL-VET',     'vet_carlos', 'Dr. Carlos Mendoza',      'VET',          true),
('UUID-RECEPCION',   'recepcion',  'Ana Gómez',               'RECEPTIONIST', true)
ON CONFLICT (username) DO NOTHING;

-- 2. Dueños
INSERT INTO owners (document_id, full_name, phone, email) VALUES
('8-888-8888', 'Juan Pérez',      '6000-1111', 'juan.perez@email.com'),
('8-999-9999', 'María Rodríguez', '6000-2222', 'maria.rodriguez@email.com')
ON CONFLICT (document_id) DO NOTHING;

-- 3. Veterinarios
INSERT INTO vets (license_number, full_name, specialty) VALUES
('VET-1001', 'Dr. Carlos Mendoza', 'Medicina General'),
('VET-1002', 'Dra. Elena Ríos',    'Cirugía Veterinaria')
ON CONFLICT (license_number) DO NOTHING;

-- 4. Servicios
INSERT INTO services (name, price) VALUES
('Consulta General',        25.00),
('Vacunación Antirrábica',  15.00),
('Desparasitación Interna', 10.00),
('Limpieza Dental',         45.00)
ON CONFLICT (name) DO NOTHING;

-- 5. Mascotas (el dueño se busca por documento, sin IDs fijos)
INSERT INTO pets (owner_id, name, species, breed, birth_date)
SELECT o.id, v.name, v.species, v.breed, v.birth_date
FROM (VALUES
  ('8-888-8888', 'Firulais', 'DOG', 'Golden Retriever', DATE '2021-05-10'),
  ('8-999-9999', 'Michi',    'CAT', 'Siamés',           DATE '2022-01-15')
) AS v(doc, name, species, breed, birth_date)
JOIN owners o ON o.document_id = v.doc
WHERE NOT EXISTS (
  SELECT 1 FROM pets p WHERE p.owner_id = o.id AND p.name = v.name
);

-- 6. Citas (mascota y veterinario se buscan por nombre y licencia)
INSERT INTO appointments (pet_id, vet_id, scheduled_at, reason, status, fee)
SELECT p.id, vt.id, NOW() + v.delta, v.reason, 'SCHEDULED', v.fee
FROM (VALUES
  ('Firulais', 'VET-1001', INTERVAL '1 day',  'Chequeo general de rutina', 25.00),
  ('Michi',    'VET-1002', INTERVAL '2 days', 'Vacunación y revisión',     15.00)
) AS v(pet, lic, delta, reason, fee)
JOIN pets p  ON p.name = v.pet
JOIN vets vt ON vt.license_number = v.lic
WHERE NOT EXISTS (
  SELECT 1 FROM appointments a WHERE a.pet_id = p.id AND a.reason = v.reason
);