DELETE FROM movie_actors;
DELETE FROM movie_genres;
DELETE FROM actors;
DELETE FROM genres;
DELETE FROM movies;

-- Insert Genres WITHOUT explicit IDs
INSERT INTO genres (name) VALUES ('Action');
INSERT INTO genres (name) VALUES ('Drama');
INSERT INTO genres (name) VALUES ('Comedy');
INSERT INTO genres (name) VALUES ('Sci-Fi');
INSERT INTO genres (name) VALUES ('Thriller');
INSERT INTO genres (name) VALUES ('Horror');
INSERT INTO genres (name) VALUES ('Romance');
INSERT INTO genres (name) VALUES ('Adventure');

-- Insert Actors WITHOUT explicit IDs - FIXED: Use exact column names
INSERT INTO actors (name, birth_date) VALUES ('Tom Hanks', '1956-07-09');
INSERT INTO actors (name, birth_date) VALUES ('Meryl Streep', '1949-06-22');
INSERT INTO actors (name, birth_date) VALUES ('Leonardo DiCaprio', '1974-11-11');
INSERT INTO actors (name, birth_date) VALUES ('Jennifer Lawrence', '1990-08-15');
INSERT INTO actors (name, birth_date) VALUES ('Denzel Washington', '1954-12-28');
INSERT INTO actors (name, birth_date) VALUES ('Emma Watson', '1990-04-15');
INSERT INTO actors (name, birth_date) VALUES ('Robert Downey Jr.', '1965-04-04');
INSERT INTO actors (name, birth_date) VALUES ('Scarlett Johansson', '1984-11-22');
INSERT INTO actors (name, birth_date) VALUES ('Brad Pitt', '1963-12-18');
INSERT INTO actors (name, birth_date) VALUES ('Angelina Jolie', '1975-06-04');
INSERT INTO actors (name, birth_date) VALUES ('Morgan Freeman', '1937-06-01');
INSERT INTO actors (name, birth_date) VALUES ('Samuel L. Jackson', '1948-12-21');
INSERT INTO actors (name, birth_date) VALUES ('Natalie Portman', '1981-06-09');
INSERT INTO actors (name, birth_date) VALUES ('Chris Hemsworth', '1983-08-11');
INSERT INTO actors (name, birth_date) VALUES ('Chris Evans', '1981-06-13');

-- Insert Movies WITHOUT explicit IDs - FIXED: Use exact column names
INSERT INTO movies (title, release_year, duration) VALUES ('The Shawshank Redemption', 1994, 142);
INSERT INTO movies (title, release_year, duration) VALUES ('The Godfather', 1972, 175);
INSERT INTO movies (title, release_year, duration) VALUES ('The Dark Knight', 2008, 152);
INSERT INTO movies (title, release_year, duration) VALUES ('Pulp Fiction', 1994, 154);
INSERT INTO movies (title, release_year, duration) VALUES ('Forrest Gump', 1994, 142);
INSERT INTO movies (title, release_year, duration) VALUES ('Inception', 2010, 148);
INSERT INTO movies (title, release_year, duration) VALUES ('The Matrix', 1999, 136);
INSERT INTO movies (title, release_year, duration) VALUES ('Goodfellas', 1990, 146);
INSERT INTO movies (title, release_year, duration) VALUES ('The Silence of the Lambs', 1991, 118);
INSERT INTO movies (title, release_year, duration) VALUES ('Saving Private Ryan', 1998, 169);
INSERT INTO movies (title, release_year, duration) VALUES ('The Avengers', 2012, 143);
INSERT INTO movies (title, release_year, duration) VALUES ('Black Panther', 2018, 134);
INSERT INTO movies (title, release_year, duration) VALUES ('Jurassic Park', 1993, 127);
INSERT INTO movies (title, release_year, duration) VALUES ('Titanic', 1997, 195);
INSERT INTO movies (title, release_year, duration) VALUES ('The Social Network', 2010, 120);
INSERT INTO movies (title, release_year, duration) VALUES ('La La Land', 2016, 128);
INSERT INTO movies (title, release_year, duration) VALUES ('Get Out', 2017, 104);
INSERT INTO movies (title, release_year, duration) VALUES ('Mad Max: Fury Road', 2015, 120);
INSERT INTO movies (title, release_year, duration) VALUES ('The Lion King', 1994, 88);
INSERT INTO movies (title, release_year, duration) VALUES ('Spirited Away', 2001, 125);

-- KEEP ALL relationship inserts EXACTLY THE SAME
-- Insert Movie-Genre relationships
INSERT INTO movie_genres (movie_id, genre_id) VALUES (1, 1);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (2, 2);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (3, 1);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (3, 5);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (4, 1);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (4, 5);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (5, 2);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (5, 3);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (6, 1);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (6, 4);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (7, 1);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (7, 4);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (8, 2);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (9, 5);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (10, 1);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (10, 2);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (11, 1);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (11, 4);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (12, 1);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (12, 4);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (13, 1);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (13, 4);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (14, 2);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (14, 7);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (15, 2);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (16, 2);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (16, 7);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (17, 5);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (17, 6);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (18, 1);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (18, 4);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (19, 3);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (19, 8);
INSERT INTO movie_genres (movie_id, genre_id) VALUES (20, 8);

-- Insert Movie-Actor relationships
INSERT INTO movie_actors (movie_id, actor_id) VALUES (1, 1);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (1, 11);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (5, 1);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (10, 1);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (6, 3);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (3, 7);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (7, 8);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (4, 9);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (9, 2);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (11, 7);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (11, 8);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (11, 14);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (11, 15);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (12, 5);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (14, 3);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (14, 8);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (15, 3);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (16, 4);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (17, 5);
INSERT INTO movie_actors (movie_id, actor_id) VALUES (18, 9);