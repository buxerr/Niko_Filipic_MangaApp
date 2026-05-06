-- =========================================================
-- MangaApp PostgreSQL functions/procedures
-- Run after: 01_create_tables.sql
-- =========================================================

-- =========================================================
-- Publisher
-- =========================================================

CREATE OR REPLACE FUNCTION fn_create_publisher(
    p_name VARCHAR
)
RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
new_id BIGINT;
BEGIN
INSERT INTO publisher (name)
VALUES (p_name)
    RETURNING publisher.id INTO new_id;

RETURN new_id;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_publisher_by_id(
    p_id BIGINT
)
RETURNS TABLE (
    id BIGINT,
    name VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT p.id, p.name
FROM publisher p
WHERE p.id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_all_publishers()
RETURNS TABLE (
    id BIGINT,
    name VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT p.id, p.name
FROM publisher p
ORDER BY p.name;
END;
$$;

CREATE OR REPLACE FUNCTION fn_search_publishers(
    p_query TEXT
)
RETURNS TABLE (
    id BIGINT,
    name VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT p.id, p.name
FROM publisher p
WHERE p_query IS NULL
   OR BTRIM(p_query) = ''
   OR LOWER(p.name) LIKE '%' || LOWER(p_query) || '%'
ORDER BY p.name;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_update_publisher(
    p_id BIGINT,
    p_name VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
UPDATE publisher p
SET name = p_name
WHERE p.id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_delete_publisher(
    p_id BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
DELETE FROM publisher p
WHERE p.id = p_id;
END;
$$;


-- =========================================================
-- Genre
-- =========================================================

CREATE OR REPLACE FUNCTION fn_genre_exists_by_name(
    p_name VARCHAR
)
RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
BEGIN
RETURN EXISTS (
    SELECT 1
    FROM genre g
    WHERE LOWER(g.name) = LOWER(BTRIM(p_name))
);
END;
$$;

CREATE OR REPLACE FUNCTION fn_create_genre(
    p_name VARCHAR,
    p_description TEXT
)
RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
new_id BIGINT;
BEGIN
INSERT INTO genre (name, description)
VALUES (p_name, p_description)
    RETURNING genre.id INTO new_id;

RETURN new_id;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_genre_by_id(
    p_id BIGINT
)
RETURNS TABLE (
    id BIGINT,
    name VARCHAR,
    description TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT g.id, g.name, g.description
FROM genre g
WHERE g.id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_all_genres()
RETURNS TABLE (
    id BIGINT,
    name VARCHAR,
    description TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT g.id, g.name, g.description
FROM genre g
ORDER BY g.name;
END;
$$;

CREATE OR REPLACE FUNCTION fn_search_genres(
    p_query TEXT
)
RETURNS TABLE (
    id BIGINT,
    name VARCHAR,
    description TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT g.id, g.name, g.description
FROM genre g
WHERE p_query IS NULL
   OR BTRIM(p_query) = ''
   OR LOWER(g.name) LIKE '%' || LOWER(p_query) || '%'
   OR LOWER(COALESCE(g.description, '')) LIKE '%' || LOWER(p_query) || '%'
ORDER BY g.name;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_update_genre(
    p_id BIGINT,
    p_name VARCHAR,
    p_description TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
UPDATE genre g
SET name = p_name,
    description = p_description
WHERE g.id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_delete_genre(
    p_id BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
DELETE FROM genre g
WHERE g.id = p_id;
END;
$$;


-- =========================================================
-- Author
-- =========================================================

CREATE OR REPLACE FUNCTION fn_create_author(
    p_first_name VARCHAR,
    p_last_name VARCHAR,
    p_orientation VARCHAR
)
RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
new_id BIGINT;
BEGIN
INSERT INTO author (first_name, last_name, orientation)
VALUES (p_first_name, p_last_name, p_orientation)
    RETURNING author.id INTO new_id;

RETURN new_id;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_author_by_id(
    p_id BIGINT
)
RETURNS TABLE (
    id BIGINT,
    first_name VARCHAR,
    last_name VARCHAR,
    orientation VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT a.id, a.first_name, a.last_name, a.orientation
FROM author a
WHERE a.id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_all_authors()
RETURNS TABLE (
    id BIGINT,
    first_name VARCHAR,
    last_name VARCHAR,
    orientation VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT a.id, a.first_name, a.last_name, a.orientation
FROM author a
ORDER BY a.last_name, a.first_name;
END;
$$;

CREATE OR REPLACE FUNCTION fn_search_authors(
    p_query TEXT
)
RETURNS TABLE (
    id BIGINT,
    first_name VARCHAR,
    last_name VARCHAR,
    orientation VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT a.id, a.first_name, a.last_name, a.orientation
FROM author a
WHERE p_query IS NULL
   OR BTRIM(p_query) = ''
   OR LOWER(a.first_name) LIKE '%' || LOWER(p_query) || '%'
   OR LOWER(a.last_name) LIKE '%' || LOWER(p_query) || '%'
   OR LOWER(a.first_name || ' ' || a.last_name) LIKE '%' || LOWER(p_query) || '%'
   OR LOWER(a.orientation) LIKE '%' || LOWER(p_query) || '%'
ORDER BY a.last_name, a.first_name;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_update_author(
    p_id BIGINT,
    p_first_name VARCHAR,
    p_last_name VARCHAR,
    p_orientation VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
UPDATE author a
SET first_name = p_first_name,
    last_name = p_last_name,
    orientation = p_orientation
WHERE a.id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_delete_author(
    p_id BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
DELETE FROM author a
WHERE a.id = p_id;
END;
$$;


-- =========================================================
-- Story Character
-- =========================================================

CREATE OR REPLACE FUNCTION fn_create_story_character(
    p_first_name VARCHAR,
    p_last_name VARCHAR,
    p_role VARCHAR
)
RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
new_id BIGINT;
BEGIN
INSERT INTO story_character (first_name, last_name, role)
VALUES (p_first_name, p_last_name, p_role)
    RETURNING story_character.id INTO new_id;

RETURN new_id;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_story_character_by_id(
    p_id BIGINT
)
RETURNS TABLE (
    id BIGINT,
    first_name VARCHAR,
    last_name VARCHAR,
    role VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT sc.id, sc.first_name, sc.last_name, sc.role
FROM story_character sc
WHERE sc.id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_all_story_characters()
RETURNS TABLE (
    id BIGINT,
    first_name VARCHAR,
    last_name VARCHAR,
    role VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT sc.id, sc.first_name, sc.last_name, sc.role
FROM story_character sc
ORDER BY sc.last_name, sc.first_name;
END;
$$;

CREATE OR REPLACE FUNCTION fn_search_story_characters(
    p_query TEXT
)
RETURNS TABLE (
    id BIGINT,
    first_name VARCHAR,
    last_name VARCHAR,
    role VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT sc.id, sc.first_name, sc.last_name, sc.role
FROM story_character sc
WHERE p_query IS NULL
   OR BTRIM(p_query) = ''
   OR LOWER(sc.first_name) LIKE '%' || LOWER(p_query) || '%'
   OR LOWER(COALESCE(sc.last_name, '')) LIKE '%' || LOWER(p_query) || '%'
   OR LOWER(sc.first_name || ' ' || COALESCE(sc.last_name, '')) LIKE '%' || LOWER(p_query) || '%'
   OR LOWER(sc.role) LIKE '%' || LOWER(p_query) || '%'
ORDER BY sc.last_name, sc.first_name;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_update_story_character(
    p_id BIGINT,
    p_first_name VARCHAR,
    p_last_name VARCHAR,
    p_role VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
UPDATE story_character sc
SET first_name = p_first_name,
    last_name = p_last_name,
    role = p_role
WHERE sc.id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_delete_story_character(
    p_id BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
DELETE FROM story_character sc
WHERE sc.id = p_id;
END;
$$;


-- =========================================================
-- App User
-- =========================================================

CREATE OR REPLACE FUNCTION fn_create_user(
    p_username VARCHAR,
    p_password_hash VARCHAR,
    p_role VARCHAR
)
RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
new_id BIGINT;
BEGIN
INSERT INTO app_user (username, password_hash, role)
VALUES (p_username, p_password_hash, p_role)
    RETURNING app_user.id INTO new_id;

RETURN new_id;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_user_by_id(
    p_id BIGINT
)
RETURNS TABLE (
    id BIGINT,
    username VARCHAR,
    password_hash VARCHAR,
    role VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT u.id, u.username, u.password_hash, u.role
FROM app_user u
WHERE u.id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_user_by_username(
    p_username VARCHAR
)
RETURNS TABLE (
    id BIGINT,
    username VARCHAR,
    password_hash VARCHAR,
    role VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT u.id, u.username, u.password_hash, u.role
FROM app_user u
WHERE LOWER(u.username) = LOWER(p_username);
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_all_users()
RETURNS TABLE (
    id BIGINT,
    username VARCHAR,
    password_hash VARCHAR,
    role VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT u.id, u.username, u.password_hash, u.role
FROM app_user u
ORDER BY u.username;
END;
$$;

CREATE OR REPLACE FUNCTION fn_username_exists(
    p_username VARCHAR
)
RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
BEGIN
RETURN EXISTS (
    SELECT 1
    FROM app_user u
    WHERE LOWER(u.username) = LOWER(p_username)
);
END;
$$;

CREATE OR REPLACE PROCEDURE sp_update_user(
    p_id BIGINT,
    p_username VARCHAR,
    p_password_hash VARCHAR,
    p_role VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
UPDATE app_user u
SET username = p_username,
    password_hash = p_password_hash,
    role = p_role
WHERE u.id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_delete_user(
    p_id BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
DELETE FROM app_user u
WHERE u.id = p_id;
END;
$$;


-- =========================================================
-- Manga
-- =========================================================

CREATE OR REPLACE FUNCTION fn_create_manga(
    p_title VARCHAR,
    p_description TEXT,
    p_release_year INT,
    p_volumes INT,
    p_status VARCHAR,
    p_image_path VARCHAR,
    p_publisher_id BIGINT
)
RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
new_id BIGINT;
BEGIN
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
           p_title,
           p_description,
           p_release_year,
           p_volumes,
           p_status,
           p_image_path,
           p_publisher_id
       )
    RETURNING manga.id INTO new_id;

RETURN new_id;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_manga_by_id(
    p_id BIGINT
)
RETURNS TABLE (
    id BIGINT,
    title VARCHAR,
    description TEXT,
    release_year INT,
    volumes INT,
    status VARCHAR,
    image_path VARCHAR,
    publisher_id BIGINT,
    publisher_name VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
    m.id,
    m.title,
    m.description,
    m.release_year,
    m.volumes,
    m.status,
    m.image_path,
    p.id AS publisher_id,
    p.name AS publisher_name
FROM manga m
         LEFT JOIN publisher p ON p.id = m.publisher_id
WHERE m.id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_all_mangas()
RETURNS TABLE (
    id BIGINT,
    title VARCHAR,
    description TEXT,
    release_year INT,
    volumes INT,
    status VARCHAR,
    image_path VARCHAR,
    publisher_id BIGINT,
    publisher_name VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
    m.id,
    m.title,
    m.description,
    m.release_year,
    m.volumes,
    m.status,
    m.image_path,
    p.id AS publisher_id,
    p.name AS publisher_name
FROM manga m
         LEFT JOIN publisher p ON p.id = m.publisher_id
ORDER BY m.title;
END;
$$;

CREATE OR REPLACE FUNCTION fn_search_mangas(
    p_title TEXT DEFAULT NULL,
    p_genre_id BIGINT DEFAULT NULL,
    p_author_id BIGINT DEFAULT NULL,
    p_publisher_id BIGINT DEFAULT NULL,
    p_status VARCHAR DEFAULT NULL,
    p_release_year_from INT DEFAULT NULL,
    p_release_year_to INT DEFAULT NULL
)
RETURNS TABLE (
    id BIGINT,
    title VARCHAR,
    description TEXT,
    release_year INT,
    volumes INT,
    status VARCHAR,
    image_path VARCHAR,
    publisher_id BIGINT,
    publisher_name VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT DISTINCT
    m.id,
    m.title,
    m.description,
    m.release_year,
    m.volumes,
    m.status,
    m.image_path,
    p.id AS publisher_id,
    p.name AS publisher_name
FROM manga m
         LEFT JOIN publisher p ON p.id = m.publisher_id
WHERE
    (
        p_title IS NULL
            OR BTRIM(p_title) = ''
            OR LOWER(m.title) LIKE '%' || LOWER(p_title) || '%'
        )
  AND (
    p_genre_id IS NULL
        OR EXISTS (
        SELECT 1
        FROM manga_genre mg
        WHERE mg.manga_id = m.id
          AND mg.genre_id = p_genre_id
    )
    )
  AND (
    p_author_id IS NULL
        OR EXISTS (
        SELECT 1
        FROM manga_author ma
        WHERE ma.manga_id = m.id
          AND ma.author_id = p_author_id
    )
    )
  AND (
    p_publisher_id IS NULL
        OR m.publisher_id = p_publisher_id
    )
  AND (
    p_status IS NULL
        OR BTRIM(p_status) = ''
        OR m.status = p_status
    )
  AND (
    p_release_year_from IS NULL
        OR m.release_year >= p_release_year_from
    )
  AND (
    p_release_year_to IS NULL
        OR m.release_year <= p_release_year_to
    )
ORDER BY m.title;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_update_manga(
    p_id BIGINT,
    p_title VARCHAR,
    p_description TEXT,
    p_release_year INT,
    p_volumes INT,
    p_status VARCHAR,
    p_image_path VARCHAR,
    p_publisher_id BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
UPDATE manga m
SET title = p_title,
    description = p_description,
    release_year = p_release_year,
    volumes = p_volumes,
    status = p_status,
    image_path = p_image_path,
    publisher_id = p_publisher_id
WHERE m.id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_delete_manga(
    p_id BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
DELETE FROM manga m
WHERE m.id = p_id;
END;
$$;


-- =========================================================
-- Manga relations
-- =========================================================

CREATE OR REPLACE PROCEDURE sp_add_manga_genre(
    p_manga_id BIGINT,
    p_genre_id BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
INSERT INTO manga_genre (manga_id, genre_id)
VALUES (p_manga_id, p_genre_id)
    ON CONFLICT DO NOTHING;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_remove_manga_genre(
    p_manga_id BIGINT,
    p_genre_id BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
DELETE FROM manga_genre mg
WHERE mg.manga_id = p_manga_id
  AND mg.genre_id = p_genre_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_add_manga_author(
    p_manga_id BIGINT,
    p_author_id BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
INSERT INTO manga_author (manga_id, author_id)
VALUES (p_manga_id, p_author_id)
    ON CONFLICT DO NOTHING;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_remove_manga_author(
    p_manga_id BIGINT,
    p_author_id BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
DELETE FROM manga_author ma
WHERE ma.manga_id = p_manga_id
  AND ma.author_id = p_author_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_add_manga_character(
    p_manga_id BIGINT,
    p_character_id BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
INSERT INTO manga_character (manga_id, character_id)
VALUES (p_manga_id, p_character_id)
    ON CONFLICT DO NOTHING;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_remove_manga_character(
    p_manga_id BIGINT,
    p_character_id BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
DELETE FROM manga_character mc
WHERE mc.manga_id = p_manga_id
  AND mc.character_id = p_character_id;
END;
$$;


-- =========================================================
-- Load manga relations
-- =========================================================

CREATE OR REPLACE FUNCTION fn_find_genres_by_manga_id(
    p_manga_id BIGINT
)
RETURNS TABLE (
    id BIGINT,
    name VARCHAR,
    description TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT g.id, g.name, g.description
FROM genre g
         INNER JOIN manga_genre mg ON mg.genre_id = g.id
WHERE mg.manga_id = p_manga_id
ORDER BY g.name;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_authors_by_manga_id(
    p_manga_id BIGINT
)
RETURNS TABLE (
    id BIGINT,
    first_name VARCHAR,
    last_name VARCHAR,
    orientation VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT a.id, a.first_name, a.last_name, a.orientation
FROM author a
         INNER JOIN manga_author ma ON ma.author_id = a.id
WHERE ma.manga_id = p_manga_id
ORDER BY a.last_name, a.first_name;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_characters_by_manga_id(
    p_manga_id BIGINT
)
RETURNS TABLE (
    id BIGINT,
    first_name VARCHAR,
    last_name VARCHAR,
    role VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT sc.id, sc.first_name, sc.last_name, sc.role
FROM story_character sc
         INNER JOIN manga_character mc ON mc.character_id = sc.id
WHERE mc.manga_id = p_manga_id
ORDER BY sc.last_name, sc.first_name;
END;
$$;

CREATE OR REPLACE FUNCTION fn_find_story_characters_by_manga_id(
    p_manga_id BIGINT
)
RETURNS TABLE (
    id BIGINT,
    first_name VARCHAR,
    last_name VARCHAR,
    role VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT sc.id, sc.first_name, sc.last_name, sc.role
FROM story_character sc
         INNER JOIN manga_character mc ON mc.character_id = sc.id
WHERE mc.manga_id = p_manga_id
ORDER BY sc.last_name, sc.first_name;
END;
$$;


-- =========================================================
-- Admin clear/reset
-- =========================================================

CREATE OR REPLACE PROCEDURE sp_clear_all_data()
LANGUAGE plpgsql
AS $$
BEGIN
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
VALUES (
           'admin',
           '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918',
           'ADMIN'
       );
END;
$$;