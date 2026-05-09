-- Optional test data for development.
-- Run after 01_create_tables.sql, 02_create_functions.sql and 03_insert_admin.sql.

INSERT INTO publisher (name)
VALUES 
    ('Hakusensha'),
    ('Shueisha'),
    ('Kodansha')
ON CONFLICT DO NOTHING;

INSERT INTO genre (name, description)
VALUES
    ('Dark Fantasy', 'Dark fantasy stories'),
    ('Action', 'Action-focused stories'),
    ('Adventure', 'Adventure stories')
ON CONFLICT DO NOTHING;

INSERT INTO author (first_name, last_name, orientation)
VALUES
    ('Kentaro', 'Miura', 'MANGAKA'),
    ('Eiichiro', 'Oda', 'MANGAKA'),
    ('Masashi', 'Kishimoto', 'MANGAKA');

INSERT INTO story_character (first_name, last_name, role)
VALUES
    ('Guts', NULL, 'MAIN'),
    ('Griffith', NULL, 'MAIN'),
    ('Casca', NULL, 'SUPPORTING');

INSERT INTO manga (
    title,
    description,
    release_year,
    volumes,
    status,
    image_path,
    publisher_id
)
VALUES (
           'Berserk',
           'Dark fantasy manga about Guts and his journey.',
           1989,
           42,
           'ONGOING',
           'assets/covers/berserk.jpg',
           (SELECT id FROM publisher WHERE name = 'Hakusensha')
       );

DO $$
DECLARE
v_berserk_id BIGINT;
    v_dark_fantasy_id BIGINT;
    v_action_id BIGINT;
    v_kentaro_miura_id BIGINT;
    v_guts_id BIGINT;
    v_griffith_id BIGINT;
BEGIN
SELECT id INTO v_berserk_id
FROM manga
WHERE title = 'Berserk';

SELECT id INTO v_dark_fantasy_id
FROM genre
WHERE name = 'Dark Fantasy';

SELECT id INTO v_action_id
FROM genre
WHERE name = 'Action';

SELECT id INTO v_kentaro_miura_id
FROM author
WHERE first_name = 'Kentaro'
  AND last_name = 'Miura';

SELECT id INTO v_guts_id
FROM story_character
WHERE first_name = 'Guts';

SELECT id INTO v_griffith_id
FROM story_character
WHERE first_name = 'Griffith';

CALL sp_add_manga_genre(v_berserk_id, v_dark_fantasy_id);
CALL sp_add_manga_genre(v_berserk_id, v_action_id);

CALL sp_add_manga_author(v_berserk_id, v_kentaro_miura_id);

CALL sp_add_manga_character(v_berserk_id, v_guts_id);
CALL sp_add_manga_character(v_berserk_id, v_griffith_id);
END;
$$;