-- Initial admin user.
-- For early testing this uses password_hash = 'admin'.
-- Later replace it with a real hash generated from Java.

INSERT INTO app_user (username, password_hash, role)
VALUES ('admin', 'admin', 'ADMIN')
ON CONFLICT (username) DO NOTHING;
