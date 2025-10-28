
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
INSERT INTO Incident_type (name, type) VALUES
 ('Instalaciones',          'location'),
 ('Plataforma',             'description'),
 ('Asuntos Economicos',     'description'),
 ('Otros',                  'description');

-- === Incident ===
INSERT INTO Incident (user_id, inc_code, description, created_at) VALUES
 (1,  1, 'Caso de prueba 1',  '2025-10-20T10:15:00'),
 (2,  2, 'Caso de prueba 2',  '2025-10-20T11:00:00'),
 (3,  3, 'Caso de prueba 3',  '2025-10-21T09:40:00'),
 (4,  4, 'Caso de prueba 4',  '2025-10-21T18:25:30'),
 (5,  1, 'Caso de prueba 5',  '2025-10-22T08:05:10'),
 (6,  2, 'Caso de prueba 6',  '2025-10-22T12:47:55'),
 (7,  3, 'Caso de prueba 7',  '2025-10-23T17:12:00'),
 (8,  4, 'Caso de prueba 8',  '2025-10-24T07:33:20'),
 (9,  1, 'Caso de prueba 9',  '2025-10-24T15:44:02'),
 (10, 2, 'Caso de prueba 10', '2025-10-25T19:09:45');
