
-- 02_seed.sql: Datos de prueba iniciales para VetCare

-- 1. Usuarios de la aplicación (app_users)
INSERT INTO app_users (username, password_hash, full_name, role, active) VALUES
('admin', '$2a$10$e8oBy/T9J6wW6jB/L11S8eX83ZzNl9kI5eY0/aB1cD2e3F4g5H6i7', 'Administrador Principal', 'ADMIN', true),
('vet_carlos', '$2a$10$e8oBy/T9J6wW6jB/L11S8eX83ZzNl9kI5eY0/aB1cD2e3F4g5H6i7', 'Dr. Carlos Mendoza', 'VET', true),
('recepcion', '$2a$10$e8oBy/T9J6wW6jB/L11S8eX83ZzNl9kI5eY0/aB1cD2e3F4g5H6i7', 'Ana Gómez', 'RECEPTIONIST', true)
ON CONFLICT (username) DO NOTHING;

-- 2. Dueños de mascotas (owners)
INSERT INTO owners (document_id, full_name, phone, email) VALUES
('8-888-8888', 'Juan Pérez', '6000-1111', 'juan.perez@email.com'),
('8-999-9999', 'María Rodríguez', '6000-2222', 'maria.rodriguez@email.com')
ON CONFLICT (document_id) DO NOTHING;

-- 3. Mascotas (pets)
INSERT INTO pets (owner_id, name, species, breed, birth_date) VALUES
(1, 'Firulais', 'DOG', 'Golden Retriever', '2021-05-10'),
(2, 'Michi', 'CAT', 'Siamés', '2022-01-15')
ON CONFLICT DO NOTHING;

-- 4. Veterinarios (vets)
INSERT INTO vets (license_number, full_name, specialty) VALUES
('VET-1001', 'Dr. Carlos Mendoza', 'Medicina General'),
('VET-1002', 'Dra. Elena Ríos', 'Cirugía Veterinaria')
ON CONFLICT (license_number) DO NOTHING;

-- 5. Servicios ofrecidos (services)
INSERT INTO services (name, price) VALUES
('Consulta General', 25.00),
('Vacunación Antirrábica', 15.00),
('Desparasitación Interna', 10.00),
('Limpieza Dental', 45.00)
ON CONFLICT (name) DO NOTHING;

-- 6. Citas programadas (appointments)
INSERT INTO appointments (pet_id, vets_id, scheduled_at, reason, status, fee) VALUES
(1, 1, NOW() + INTERVAL '1 day', 'Chequeo general de rutina', 'SCHEDULED', 25.00),
(2, 2, NOW() + INTERVAL '2 days', 'Vacunación y revisión', 'SCHEDULED', 15.00)
ON CONFLICT DO NOTHING;