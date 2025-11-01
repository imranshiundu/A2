-- Insert Genres with explicit IDs
INSERT INTO genres (id, name) VALUES 
(1, 'Action'),
(2, 'Comedy'),
(3, 'Drama'),
(4, 'Sci-Fi'),
(5, 'Horror'),
(6, 'Romance'),
(7, 'Thriller'),
(8, 'Adventure'),
(9, 'Fantasy');

-- Insert Actors with explicit IDs
INSERT INTO actors (id, name, birth_date) VALUES 
(1, 'Tom Hanks', '1956-07-09'),
(2, 'Meryl Streep', '1949-06-22'),
(3, 'Leonardo DiCaprio', '1974-11-11'),
(4, 'Jennifer Lawrence', '1990-08-15'),
(5, 'Denzel Washington', '1954-12-28'),
(6, 'Scarlett Johansson', '1984-11-22'),
(7, 'Robert Downey Jr.', '1965-04-04'),
(8, 'Emma Watson', '1990-04-15'),
(9, 'Brad Pitt', '1963-12-18'),
(10, 'Angelina Jolie', '1975-06-04'),
(11, 'Johnny Depp', '1963-06-09'),
(12, 'Morgan Freeman', '1937-06-01'),
(13, 'Samuel L. Jackson', '1948-12-21'),
(14, 'Chris Hemsworth', '1983-08-11'),
(15, 'Natalie Portman', '1981-06-09'),
(16, 'Keanu Reeves', '1964-09-02'),
(17, 'Anne Hathaway', '1982-11-12'),
(18, 'Will Smith', '1968-09-25'),
(19, 'Matt Damon', '1970-10-08'),
(20, 'Cate Blanchett', '1969-05-14');

-- Insert Movies with explicit IDs
INSERT INTO movies (id, title, release_year, duration) VALUES 
-- 1990s Movies
(1, 'Forrest Gump', 1994, 142),
(2, 'The Shawshank Redemption', 1994, 142),
(3, 'Pulp Fiction', 1994, 154),
(4, 'The Matrix', 1999, 136),

-- 2000s Movies
(5, 'Gladiator', 2000, 155),
(6, 'The Lord of the Rings: The Fellowship of the Ring', 2001, 178),
(7, 'The Dark Knight', 2008, 152),
(8, 'Inception', 2010, 148),

-- 2010s Movies
(9, 'The Social Network', 2010, 120),
(10, 'The Avengers', 2012, 143),
(11, 'Gravity', 2013, 91),
(12, 'The Wolf of Wall Street', 2013, 180),
(13, 'Interstellar', 2014, 169),
(14, 'Mad Max: Fury Road', 2015, 120),
(15, 'La La Land', 2016, 128),
(16, 'Get Out', 2017, 104),
(17, 'Black Panther', 2018, 134),
(18, 'Parasite', 2019, 132),
(19, 'Joker', 2019, 122),
(20, 'Once Upon a Time in Hollywood', 2019, 161),

-- Additional movies to reach 20+
(21, 'The Silence of the Lambs', 1991, 118),
(22, 'Goodfellas', 1990, 146),
(23, 'Fight Club', 1999, 139),
(24, 'The Departed', 2006, 151),
(25, 'No Country for Old Men', 2007, 122);

-- Associate Movies with Genres (Many-to-Many relationships)
INSERT INTO movie_genres (movie_id, genre_id) VALUES
-- Forrest Gump
(1, 3),
-- The Matrix
(4, 1), (4, 4),
-- Gladiator
(5, 1), (5, 3),
-- The Dark Knight
(7, 1), (7, 7),
-- The Avengers
(10, 1), (10, 8),
-- Mad Max: Fury Road
(14, 1), (14, 8),
-- Black Panther
(17, 1), (17, 8),
-- La La Land
(15, 2), (15, 6),
-- Once Upon a Time in Hollywood
(20, 2), (20, 3),
-- The Shawshank Redemption
(2, 3),
-- Pulp Fiction
(3, 3), (3, 7),
-- Inception
(8, 1), (8, 4), (8, 7),
-- The Social Network
(9, 3),
-- The Wolf of Wall Street
(12, 3),
-- Interstellar
(13, 3), (13, 4), (13, 8),
-- Get Out
(16, 3), (16, 5), (16, 7),
-- Parasite
(18, 3), (18, 7),
-- Joker
(19, 3),
-- The Silence of the Lambs
(21, 3), (21, 5), (21, 7),
-- Goodfellas
(22, 3),
-- Fight Club
(23, 3),
-- The Departed
(24, 3), (24, 7),
-- No Country for Old Men
(25, 3), (25, 7),
-- Gravity
(11, 4), (11, 8),
-- The Lord of the Rings: The Fellowship of the Ring
(6, 8), (6, 9);

-- Associate Actors with Movies (Many-to-Many relationships) - REMOVED DUPLICATES
INSERT INTO movie_actors (movie_id, actor_id) VALUES
-- Forrest Gump
(1, 1),
-- The Shawshank Redemption
(2, 2),
-- The Wolf of Wall Street & Joker
(12, 3), (19, 3),
-- Inception
(8, 4),
-- Gladiator
(5, 5),
-- The Avengers
(10, 6), (10, 7), (10, 13), (10, 14),
-- The Lord of the Rings: The Fellowship of the Ring
(6, 8), (6, 20),
-- Once Upon a Time in Hollywood & Fight Club
(20, 9), (23, 9),
-- Gladiator
(5, 10),
-- The Matrix
(4, 11),
-- The Shawshank Redemption & The Dark Knight
(2, 12), (7, 12),
-- Pulp Fiction
(3, 13),
-- The Dark Knight
(7, 15),
-- The Matrix
(4, 16),
-- Interstellar
(13, 17),
-- Inception
(8, 18),
-- Interstellar & The Departed
(13, 19), (24, 19),
-- Parasite
(18, 20);