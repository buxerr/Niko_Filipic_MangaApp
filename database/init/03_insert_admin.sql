-- Initial admin user.
-- For early testing this uses password_hash = 'admin'.
-- Later replace it with a real hash generated from Java.

INSERT INTO app_user (username, password_hash, role)
VALUES ('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'ADMIN')
ON CONFLICT (username) DO NOTHING;
