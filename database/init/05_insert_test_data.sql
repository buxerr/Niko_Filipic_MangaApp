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

INSERT INTO story_character (first_name, last_name)
VALUES
    ('Guts', NULL),
    ('Griffith', NULL),
    ('Monkey D.', 'Luffy'),
    ('Naruto', 'Uzumaki');

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
           'assets/images/berserk.jpg',
           (SELECT id FROM publisher WHERE name = 'Hakusensha')
       );

CALL sp_add_manga_genre(
    (SELECT id FROM manga WHERE title = 'Berserk'),
    (SELECT id FROM genre WHERE name = 'Dark Fantasy')
);

CALL sp_add_manga_genre(
    (SELECT id FROM manga WHERE title = 'Berserk'),
    (SELECT id FROM genre WHERE name = 'Action')
);

CALL sp_add_manga_author(
    (SELECT id FROM manga WHERE title = 'Berserk'),
    (SELECT id FROM author WHERE first_name = 'Kentaro' AND last_name = 'Miura')
);

CALL sp_add_manga_character(
    (SELECT id FROM manga WHERE title = 'Berserk'),
    (SELECT id FROM story_character WHERE first_name = 'Guts')
);

CALL sp_add_manga_character(
    (SELECT id FROM manga WHERE title = 'Berserk'),
    (SELECT id FROM story_character WHERE first_name = 'Griffith')
);
