-- USERS
INSERT INTO Users (name, surname, email, iban, monthly_fee_cents ) VALUES
  ('Ana',   'López',    'ana@example.com',   'ES6621000418401234567891', 2600),
  ('Luis',  'Pérez',    'luis@example.com',  'ES9321000418451234567892' , 2700),
  ('Marta', 'Gómez',    'marta@example.com', 'ES1921000418481234567893' , 2800),
  ('Pablo', 'Ruiz',     'pablo@example.com', 'ES7721000418491234567894' , 2900),
  ('Sara',  'Nunes',    'sara@example.com',  'PT50002700000012345678901' , 2700),
  ('João',  'Silva',    'joao@example.com',  'PT50002700000012345678902' , 2600),
  ('Elena', 'Díaz',     'elena@example.com', 'ES2321000418431234567895' , 2000),
  ('Hugo',  'Martins',  'hugo@example.com',  'PT50002700000012345678903' , 2600),
  ('Carla', 'Sousa',    'carla@example.com', 'PT50002700000012345678904' , 2200),
  ('Diego', 'Torres',   'diego@example.com', 'ES6621000418401234567899' , 2400);

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
  ('AG-2025-11-1', 1, 2600, '2025-11-01', '2025-11-05', '202511', 'Cuota mensual gimnasio', 'PAID', null),
  ('AG-2025-11-2', 1, 2600, '2025-11-01', '2025-11-05', '202511', 'Cuota mensual gimnasio', 'PAID', null),
  ('AG-2025-11-3', 2, 2700, '2025-11-01', '2025-11-06', '202511', 'Cuota mensual piscina', 'PAID', null),
  ('AG-2025-11-4', 2, 2700, '2025-11-01', '2025-11-06', '202511', 'Cuota mensual piscina', 'PAID', null),
  ('AG-2025-11-5', 3, 1500, '2025-11-01', '2025-11-07', '202511', 'Cuota mensual pilates', 'PAID', null),
  ('AG-2025-11-6', 3, 1500, '2025-11-01', '2025-11-07', '202511', 'Cuota mensual pilates', 'PAID', null),
  ('AG-2025-11-7', 4, 3000, '2025-11-01', '2025-11-08', '202511', 'Cuota mensual spa', 'CANCELED', null),
  ('AG-2025-11-8', 4, 3000, '2025-11-01', '2025-11-08', '202511', 'Cuota mensual spa', 'CANCELED', null),
  ('AG-2025-11-9', 5, 2800, '2025-11-01', '2025-11-09', '202511', 'Cuota mensual gimnasio', 'PAID', null),
  ('AG-2025-11-10', 1, 2000, '2025-11-01', '2025-11-05', '202511', 'Cuota mensual gimnasio', 'PAID', null),
  ('AG-2025-11-11', 1, 2000, '2025-11-01', '2025-11-05', '202511', 'Cuota mensual gimnasio', 'PAID', null),
  ('AG-2025-11-12', 2, 2500, '2025-11-01', '2025-11-06', '202511', 'Cuota mensual piscina', 'PAID', null),
  ('AG-2025-11-13', 3, 1500, '2025-11-01', '2025-11-07', '202511', 'Cuota mensual pilates', 'PAID', null),
  ('AG-2025-11-14', 4, 3000, '2025-11-01', '2025-11-08', '202511', 'Cuota mensual spa', 'CANCELED', null),
  ('AG-2025-11-15', 5, 2600, '2025-11-01', '2025-11-09', '202511', 'Cuota mensual gimnasio', 'PAID', null)
  ('AG-2025-11-16', 6, 2200, '2025-11-01', '2025-11-10', '202511', 'Cuota mensual gimnasio', 'PAID', null),
  ('AG-2025-11-17', 7, 2700, '2025-11-01', '2025-11-11', '202511', 'Cuota mensual piscina', 'PAID', null),
  ('AG-2025-11-18', 8, 1600, '2025-11-01', '2025-11-12', '202511', 'Cuota mensual pilates', 'PAID', null),
  ('AG-2025-11-19', 9, 3100, '2025-11-01', '2025-11-13', '202511', 'Cuota mensual spa', 'CANCELED', null),
  ('AG-2025-11-20', 10, 2800, '2025-11-01', '2025-11-14', '202511', 'Cuota mensual gimnasio', 'PAID', null);
