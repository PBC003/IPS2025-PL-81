
DROP TABLE IF EXISTS Incident;
DROP TABLE IF EXISTS Incident_type;
DROP TABLE IF EXISTS Location;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Receipt;
DROP TABLE IF EXISTS Receipt_batch;
DROP TABLE IF EXISTS Reservation;
DROP TABLE IF EXISTS Assembly;


CREATE TABLE IF NOT EXISTS Users (
  id      INTEGER PRIMARY KEY AUTOINCREMENT,
  name    TEXT NOT NULL,
  surname TEXT NOT NULL,
  email   TEXT UNIQUE,
  iban    TEXT UNIQUE,
  monthly_fee_cents INTEGER NOT NULL CHECK (monthly_fee_cents > 0),
  role TEXT NOT NULL CHECK (role IN ('USER','ADMIN'))
);

CREATE TABLE IF NOT EXISTS Incident_type (
  code INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS Location (
  id   INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL UNIQUE,
  outdoor BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS Incident (
  id          INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id     INTEGER NOT NULL,
  inc_code    INTEGER NOT NULL,
  description TEXT,
  created_at  TEXT NOT NULL,
  status      TEXT NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN','ASSIGNED','WAITING_REPLY','CLOSED')),
  location_id INTEGER NULL,
  FOREIGN KEY (user_id)  REFERENCES Users(id)          ON DELETE RESTRICT,
  FOREIGN KEY (inc_code) REFERENCES Incident_type(code) ON DELETE RESTRICT,
  FOREIGN KEY (location_id) REFERENCES Location(id)     ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS Receipt (
  id              INTEGER PRIMARY KEY AUTOINCREMENT,
  receipt_number  TEXT UNIQUE,
  user_id         INTEGER NOT NULL,
  amount_cents    INTEGER NOT NULL,
  issue_date      TEXT NOT NULL,
  value_date      TEXT NOT NULL,
  charge_month    TEXT NOT NULL,
  concept         TEXT NOT NULL,
  status          TEXT NOT NULL DEFAULT 'GENERATED' CHECK (status IN ('GENERATED','PAID','CANCELED','REISSUED')),
  batch_id        INTEGER,
  FOREIGN KEY(batch_id) REFERENCES Receipt_batch(id),
  FOREIGN KEY(user_id) REFERENCES Users(id)
);

CREATE TABLE IF NOT EXISTS Receipt_batch (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  charge_month  TEXT NOT NULL,
  bank_entity   TEXT NOT NULL,
  created_at    TEXT NOT NULL,
  status          TEXT NOT NULL DEFAULT 'GENERATED' CHECK (status IN ('GENERATED','EXPORTED','PROCESSED','CANCELED')),
  file_name     TEXT NOT NULL,
  total_amount  INTEGER NOT NULL DEFAULT 0,
  receipts_cnt  INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS Reservation (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id       INTEGER NOT NULL,
  location_id   INTEGER NOT NULL,
  start_time    TEXT    NOT NULL,
  end_time      TEXT    NOT NULL,
  minutes       INTEGER NOT NULL CHECK (minutes > 0),
  created_at    TEXT    NOT NULL,
  FOREIGN KEY(user_id)     REFERENCES Users(id),
  FOREIGN KEY(location_id) REFERENCES Location(id)
);

CREATE TABLE IF NOT EXISTS Assembly (
  id             INTEGER PRIMARY KEY AUTOINCREMENT,
  title          TEXT    NOT NULL,
  description    TEXT,
  scheduled_at   TEXT    NOT NULL,
  created_at     TEXT    NOT NULL,
  status        TEXT    NOT NULL DEFAULT 'NOT_HELD' CHECK (status IN ('NOT_HELD','HELD')),
  type           TEXT    NOT NULL CHECK (type IN ('ORDINARY','EXTRAORDINARY')),
  minutes_text   TEXT,
  minutes_status TEXT    NOT NULL DEFAULT 'PENDING_UPLOAD' CHECK (minutes_status IN ('PENDING_UPLOAD','UPLOADED','APPROVED'))
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_receipt_per_month
  ON Receipt(user_id, charge_month)
  WHERE status <> 'REISSUED';

CREATE INDEX IF NOT EXISTS idx_receipt_per_batch
  ON Receipt(batch_id);

CREATE INDEX IF NOT EXISTS idx_res_by_location_time
  ON Reservation(location_id, start_time, end_time);

CREATE INDEX IF NOT EXISTS idx_res_by_user_time
  ON Reservation(user_id,    start_time, end_time);

CREATE INDEX IF NOT EXISTS idx_assembly_by_status_time
  ON Assembly(status, scheduled_at);

CREATE INDEX IF NOT EXISTS idx_assembly_by_time
  ON Assembly(scheduled_at);

CREATE UNIQUE INDEX IF NOT EXISTS uq_assembly_ordinary_per_year
  ON Assembly(substr(scheduled_at,1,4))
  WHERE type='ORDINARY';