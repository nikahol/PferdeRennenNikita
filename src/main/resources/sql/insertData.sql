-- insert initial test data
-- the id is hardcode to enable references between further test data
INSERT INTO horse (NAME, BREED, MIN_SPEED, MAX_SPEED, CREATED, UPDATED, DELETED )
VALUES ('horse1', 'Cob', 40.1, 50.0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
  ('horse2', 'Arab', 40.5, 50.7, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
  ('horse3', 'Andalusian', 40.0, 60.0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

INSERT INTO jockey (NAME, SKILL, CREATED, UPDATED, DELETED )
VALUES ('jockey1',  40.1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(),0),
    ('jockey2', 40.5, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(),0),
    ('jockey3', 40.0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(),0);