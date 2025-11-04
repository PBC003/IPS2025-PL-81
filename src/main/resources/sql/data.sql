-- === Users ===
INSERT INTO Users (name, email) VALUES
 ('Ana López',      'ana@example.com'),
 ('Luis Pérez',     'luis@example.com'),
 ('Marta Gómez',    'marta@example.com'),
 ('Pablo Ruiz',     'pablo@example.com'),
 ('Sara Nunes',     'sara@example.com'),
 ('João Silva',     'joao@example.com'),
 ('Elena Díaz',     'elena@example.com'),
 ('Hugo Martins',   'hugo@example.com'),
 ('Carla Sousa',    'carla@example.com'),
 ('Diego Torres',   'diego@example.com');

-- === Incident_type ===
INSERT INTO Incident_type (name) VALUES
 ('Instalaciones'),
 ('Plataforma'),
 ('Asuntos Economicos'),
 ('Otros');

-- ==== LOCATION ====
INSERT INTO Location (name) VALUES
  ('Gym'),
  ('Piscina'),
  ('Pilates'),
  ('Vestuario Masculino'),
  ('Vestuario Femenino'),
  ('SPA'),
  ('Sala de Spining'),
  ('Recepcion'),
  ('Parking');

-- === Incident ===
INSERT INTO Incident (user_id, inc_code, description, created_at, status, location_id) VALUES
  (1, 1, 'Caso de prueba 1',  '2025-10-27T09:00:00', 'OPEN',            1),
  (2, 1, 'Caso de prueba 2',  '2025-10-28T11:15:00', 'OPEN',            2),
  (3, 2, 'Caso de prueba 3',  '2025-10-28T14:30:00', 'ASSIGNED',        NULL),
  (4, 3, 'Caso de prueba 4',  '2025-10-29T10:05:00', 'WAITING_REPLY',   NULL),
  (5, 4, 'Caso de prueba 5',  '2025-10-29T16:40:00', 'OPEN',            NULL),
  (1, 1, 'Caso de prueba 6',  '2025-10-30T08:20:00', 'OPEN',            3),
  (2, 2, 'Caso de prueba 7',  '2025-10-30T12:50:00', 'CLOSED',          NULL),
  (3, 1, 'Caso de prueba 8',  '2025-10-31T09:10:00', 'ASSIGNED',        4),
  (4, 3, 'Caso de prueba 9',  '2025-10-31T13:00:00', 'OPEN',            NULL),
  (5, 1, 'Caso de prueba 10', '2025-11-01T10:25:00', 'OPEN',            5),
  (1, 4, 'Caso de prueba 11', '2025-11-01T18:45:00', 'CLOSED',          NULL),
  (2, 1, 'Caso de prueba 12', '2025-11-02T09:35:00', 'WAITING_REPLY',   1);
