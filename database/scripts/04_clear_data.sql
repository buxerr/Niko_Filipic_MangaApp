-- Clears all application data and resets identity counters.
-- Use this for the administrator reset functionality.

TRUNCATE TABLE 
    manga_character,
    manga_author,
    manga_genre,
    manga,
    story_character,
    author,
    genre,
    publisher,
    app_user
RESTART IDENTITY CASCADE;

INSERT INTO app_user (username, password_hash, role)
VALUES ('admin', 'admin', 'ADMIN')
ON CONFLICT (username) DO NOTHING;
