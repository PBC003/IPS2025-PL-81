-- USERS
INSERT INTO Users (name, surname, email, iban, monthly_fee_cents, role) VALUES
  ('Admin',   'Admin',     'Admin@Admin.com',             'ADMIN', 1, 'ADMIN'),
  ('Ana',   'López',     'ana@example.com',               'ES6621000418401234567891', 2600, 'USER'),
  ('Luis',  'Pérez',     'luis@example.com',              'ES9321000418451234567892', 2700, 'USER'),
  ('Marta', 'Gómez',     'marta@example.com',             'ES1921000418481234567893', 2800, 'USER'),
  ('Pablo', 'Ruiz',      'pablo@example.com',             'ES7721000418491234567894', 2900, 'USER'),
  ('Sara',  'Nunes',     'sara@example.com',              'PT50002700000012345678901', 2700, 'USER'),
  ('João',  'Silva',     'joao@example.com',              'PT50002700000012345678902', 2600, 'USER'),
  ('Elena', 'Díaz',      'elena@example.com',             'ES2321000418431234567895', 2000, 'USER'),
  ('Hugo',  'Martins',   'hugo@example.com',              'PT50002700000012345678903', 2600, 'USER'),
  ('Carla', 'Sousa',     'carla@example.com',             'PT50002700000012345678904', 2200, 'USER'),
  ('Diego', 'Torres',    'diego@example.com',             'ES6621000418401234567899', 2400, 'USER'),
  ('Luis',  'Fernández', 'luis.fernandez@example.com',    'ES6621000418401234567890', 2500, 'USER'),
  ('Ana',   'Martínez',  'ana.martinez@example.com',      'ES6621000418401234567900', 2600, 'USER'),
  ('Miguel','García',    'miguel.garcia@example.com',     'ES6621000418401234567901', 2700, 'USER'),
  ('Sofia', 'Rodríguez', 'sofia.rodriguez@example.com',   'ES6621000418401234567902', 2800, 'USER'),
  ('Carlos','Lima',      'carlos.lima@example.com',       'ES6621000418401234567903', 2900, 'USER'),
  ('Isabel','Costa',     'isabel.costa@example.com',      'ES6621000418401234567904', 2000, 'USER'),
  ('Ana',   'Silva',     'ana.silva@example.com',         'ES6621000418401234567896', 2500, 'USER'),
  ('Rui',   'Ferreira',  'rui.ferreira@example.com',      'ES6621000418401234567897', 2600, 'USER'),
  ('Mónica','Almeida',   'monica.almeida@example.com',    'ES6621000418401234567898', 2700, 'USER'),
  ('Tiago', 'Santos',    'tiago.santos@example.com',      'ES6621000418401234567905', 2800, 'USER'),
  ('Laura', 'Gomes',     'laura.gomes@example.com',       'ES6621000418401234567906', 2900, 'USER');

-- INCIDENT_TYPE
INSERT INTO Incident_type (name) VALUES
  ('Instalaciones'),
  ('Plataforma'),
  ('Asuntos Económicos'),
  ('Otros');

-- LOCATION
INSERT INTO Location (name) VALUES
  ('Gym'),
  ('Piscina'),
  ('Pilates'),
  ('Vestuario Masculino'),
  ('Vestuario Femenino'),
  ('SPA'),
  ('Sala de Spinning'),
  ('Recepción'),
  ('Parking');

-- INCIDENT
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

-- RECEIPT_BATCH
INSERT INTO Receipt_batch (charge_month, bank_entity, created_at, status, file_name, total_amount, receipts_cnt) VALUES
  ('2025-10', 'Banco Santander', '2025-10-01T09:00:00', 'EXPORTED', 'batch_oct_2025.csv', 95000, 5),
  ('2025-11', 'CaixaBank',       '2025-11-01T09:00:00', 'GENERATED', 'batch_nov_2025.csv', 0, 0);

-- RECEIPT
INSERT INTO Receipt (receipt_number, user_id, amount_cents, issue_date, value_date, charge_month, concept, status, batch_id) VALUES
  ('AG-202511-1',  1,   2600, '2025-11-01', '2025-11-05', '202511', 'Cuota mensual gimnasio',   'PAID',      null),
  ('AG-202511-2',  2,   2600, '2025-11-01', '2025-11-05', '202511', 'Cuota mensual gimnasio',   'PAID',      null),
  ('AG-202511-3',  3,   2700, '2025-11-01', '2025-11-06', '202511', 'Cuota mensual piscina',    'PAID',      null),
  ('AG-202511-4',  4,   2700, '2025-11-01', '2025-11-06', '202511', 'Cuota mensual piscina',    'PAID',      null),
  ('AG-202511-5',  5,   1500, '2025-11-01', '2025-11-07', '202511', 'Cuota mensual pilates',    'PAID',      null),
  ('AG-202511-6',  6,   1500, '2025-11-01', '2025-11-07', '202511', 'Cuota mensual pilates',    'PAID',      null),
  ('AG-202511-7',  7,   3000, '2025-11-01', '2025-11-08', '202511', 'Cuota mensual spa',        'CANCELED',  null),
  ('AG-202511-8',  8,   3000, '2025-11-01', '2025-11-08', '202511', 'Cuota mensual spa',        'CANCELED',  null),
  ('AG-202511-9',  9,   2800, '2025-11-01', '2025-11-09', '202511', 'Cuota mensual gimnasio',   'PAID',      null),
  ('AG-202511-10', 10,  2000, '2025-11-01', '2025-11-05', '202511', 'Cuota mensual gimnasio',   'PAID',      null);

-- RESERVATION
INSERT INTO Reservation (user_id, location_id, start_time, end_time, minutes, created_at) VALUES
  (2, 1, '2025-11-10T10:00:00', '2025-11-10T11:00:00', 60, '2025-11-01T09:00:00'),
  (3, 2, '2025-11-10T12:30:00', '2025-11-10T13:15:00', 60, '2025-11-01T10:00:00'),
  (4, 3, '2025-11-11T09:00:00', '2025-11-11T10:30:00', 120, '2025-11-02T11:00:00'),
  (5, 4, '2025-11-11T14:00:00', '2025-11-11T15:00:00', 60, '2025-11-02T12:00:00'),
  (6, 5, '2025-11-12T08:30:00', '2025-11-12T09:15:00', 120, '2025-11-03T13:00:00'),
  (7, 6, '2025-11-12T17:00:00', '2025-11-12T18:30:00', 120, '2025-11-03T14:00:00'),
  (8, 7, '2025-11-13T10:15:00', '2025-11-13T11:15:00', 60, '2025-11-04T15:00:00'),
  (9, 8, '2025-11-13T13:45:00', '2025-11-13T14:30:00', 120, '2025-11-04T16:00:00'),
  (10,9, '2025-11-14T09:30:00', '2025-11-14T10:30:00', 60, '2025-11-05T17:00:00');