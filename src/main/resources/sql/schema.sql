drop table Users;
CREATE TABLE Users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  email TEXT UNIQUE
);

drop table Incident_type;
CREATE TABLE Incident_type (
    code INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    type TEXT NOT NULL CHECK (type IN ('location','description'))
);

drop table Incident;
CREATE TABLE IF NOT EXISTS Incident (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    inc_code INTEGER NOT NULL,
    description TEXT,
    created_at TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE RESTRICT,
    FOREIGN KEY (inc_code) REFERENCES Incident_type(code) ON DELETE RESTRICT
);