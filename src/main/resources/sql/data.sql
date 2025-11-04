-- USERS
INSERT INTO Users (name, surname, email, iban) VALUES
  ('Ana',   'López',    'ana@example.com',   'ES6621000418401234567891'),
  ('Luis',  'Pérez',    'luis@example.com',  'ES9321000418451234567892'),
  ('Marta', 'Gómez',    'marta@example.com', 'ES1921000418481234567893'),
  ('Pablo', 'Ruiz',     'pablo@example.com', 'ES7721000418491234567894'),
  ('Sara',  'Nunes',    'sara@example.com',  'PT50002700000012345678901'),
  ('João',  'Silva',    'joao@example.com',  'PT50002700000012345678902'),
  ('Elena', 'Díaz',     'elena@example.com', 'ES2321000418431234567895'),
  ('Hugo',  'Martins',  'hugo@example.com',  'PT50002700000012345678903'),
  ('Carla', 'Sousa',    'carla@example.com', 'PT50002700000012345678904'),
  ('Diego', 'Torres',   'diego@example.com', 'ES6621000418401234567899');

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
  ('R-2025-1001', 1, 2000, '2025-10-01', '2025-10-05', '2025-10', 'Cuota mensual gimnasio', 'PAID', 1),
  ('R-2025-1002', 2, 2500, '2025-10-01', '2025-10-06', '2025-10', 'Cuota mensual piscina', 'PAID', 1),
  ('R-2025-1003', 3, 1500, '2025-10-01', '2025-10-07', '2025-10', 'Cuota mensual pilates', 'PAID', 1),
  ('R-2025-1004', 4, 3000, '2025-10-01', '2025-10-08', '2025-10', 'Cuota mensual spa', 'CANCELED', 1),
  ('R-2025-1005', 5, 2600, '2025-10-01', '2025-10-09', '2025-10', 'Cuota mensual gimnasio', 'PAID', 1),
  ('R-2025-1101', 6, 2000, '2025-11-01', '2025-11-05', '2025-11', 'Cuota mensual gimnasio', 'GENERATED', 2),
  ('R-2025-1102', 7, 2500, '2025-11-01', '2025-11-05', '2025-11', 'Cuota mensual piscina', 'GENERATED', 2),
  ('R-2025-1103', 8, 1500, '2025-11-01', '2025-11-05', '2025-11', 'Cuota mensual pilates', 'GENERATED', 2),
  ('R-2025-1104', 9, 3000, '2025-11-01', '2025-11-05', '2025-11', 'Cuota mensual spa', 'GENERATED', 2),
  ('R-2025-1105', 10, 2600, '2025-11-01', '2025-11-05', '2025-11', 'Cuota mensual gimnasio', 'GENERATED', 2);
