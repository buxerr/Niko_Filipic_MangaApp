-- Initial admin user.
-- Password is SHA-256 hash for: admin

CALL sp_create_user(
        'admin',
        '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918',
        'ADMIN'
     );
