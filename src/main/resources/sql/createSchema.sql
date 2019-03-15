-- create table horse if not exists

CREATE TABLE IF NOT EXISTS horse (
  -- use auto incrementing IDs as primary key
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(255) NOT NULL,
  breed     TEXT,
  min_speed DOUBLE       NOT NULL,
  max_speed DOUBLE       NOT NULL,
  created   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted   BIT          NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS jockey (

  -- use auto incrementing IDs as primary key
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(255) NOT NULL,
  skill     DOUBLE       NOT NULL,
  created   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted   BIT          NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS horseVersions (
  -- use auto incrementing IDs as primary key
  id        BIGINT       NOT NULL,
  name      VARCHAR(255) NOT NULL,
  breed     TEXT,
  min_speed DOUBLE       NOT NULL,
  max_speed DOUBLE       NOT NULL,
  created   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  primary key(id, updated)
);

CREATE TABLE IF NOT EXISTS jockeyVersions (

  -- use auto incrementing IDs as primary key
  id        BIGINT       NOT NULL,
  name      VARCHAR(255) NOT NULL,
  skill     DOUBLE       NOT NULL,
  created   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  primary key(id, updated)
);

CREATE TABLE IF NOT EXISTS simulationRelation(
  simID     BIGINT       NOT NULL,
  horseID   BIGINT       NOT NULL,
  horesUp   DATETIME     NOT NULL,
  jockeyID  BIGINT       NOT NULL,
  jockeyUp  BIGINT       NOT NULL,
  FOREIGN KEY(horseID, horseUp) REFERENCES horseVersions (id, updated),
  FOREIGN KEY(jockeyID, jockeyUp) REFERENCES jockeyVersions (id, updated),
  FOREIGN KEY(simID) REFERENCES simulation (simID)
);

CREATE TABLE IF NOT EXISTS simulation {
  simID     BIGINT       AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(255) NOT NULL
};




