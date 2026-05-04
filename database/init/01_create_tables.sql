DROP TABLE IF EXISTS manga_character CASCADE;
DROP TABLE IF EXISTS manga_author CASCADE;
DROP TABLE IF EXISTS manga_genre CASCADE;
DROP TABLE IF EXISTS manga CASCADE;
DROP TABLE IF EXISTS story_character CASCADE;
DROP TABLE IF EXISTS author CASCADE;
DROP TABLE IF EXISTS genre CASCADE;
DROP TABLE IF EXISTS publisher CASCADE;
DROP TABLE IF EXISTS app_user CASCADE;

CREATE TABLE publisher (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE genre (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL UNIQUE,
                       description TEXT
);

CREATE TABLE author (
                        id BIGSERIAL PRIMARY KEY,
                        first_name VARCHAR(100) NOT NULL,
                        last_name VARCHAR(100) NOT NULL,
                        orientation VARCHAR(30) NOT NULL CHECK (orientation IN ('MANGAKA', 'ARTIST', 'WRITER'))
);

CREATE TABLE story_character (
                                 id BIGSERIAL PRIMARY KEY,
                                 first_name VARCHAR(100) NOT NULL,
                                 last_name VARCHAR(100)
);

CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(100) NOT NULL UNIQUE,
                          password_hash VARCHAR(255) NOT NULL,
                          role VARCHAR(20) NOT NULL CHECK (role IN ('USER', 'ADMIN'))
);

CREATE TABLE manga (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(200) NOT NULL,
                       description TEXT,
                       release_year INT,
                       volumes INT,
                       status VARCHAR(20) NOT NULL CHECK (status IN ('ONGOING', 'COMPLETED', 'HIATUS', 'CANCELLED')),
                       image_path VARCHAR(255),
                       publisher_id BIGINT REFERENCES publisher(id) ON DELETE SET NULL
);

CREATE TABLE manga_genre (
                             manga_id BIGINT NOT NULL REFERENCES manga(id) ON DELETE CASCADE,
                             genre_id BIGINT NOT NULL REFERENCES genre(id) ON DELETE CASCADE,
                             PRIMARY KEY (manga_id, genre_id)
);

CREATE TABLE manga_author (
                              manga_id BIGINT NOT NULL REFERENCES manga(id) ON DELETE CASCADE,
                              author_id BIGINT NOT NULL REFERENCES author(id) ON DELETE CASCADE,
                              PRIMARY KEY (manga_id, author_id)
);

CREATE TABLE manga_character (
                                 manga_id BIGINT NOT NULL REFERENCES manga(id) ON DELETE CASCADE,
                                 character_id BIGINT NOT NULL REFERENCES story_character(id) ON DELETE CASCADE,
                                 PRIMARY KEY (manga_id, character_id)
);

CREATE INDEX idx_manga_title ON manga(title);
CREATE INDEX idx_manga_release_year ON manga(release_year);
CREATE INDEX idx_manga_publisher_id ON manga(publisher_id);

CREATE INDEX idx_genre_name ON genre(name);
CREATE INDEX idx_author_name ON author(last_name, first_name);
CREATE INDEX idx_character_name ON story_character(last_name, first_name);
CREATE INDEX idx_user_username ON app_user(username);