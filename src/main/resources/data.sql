MERGE INTO Genres KEY (genre_id) VALUES (1, 'Комедия');
MERGE INTO Genres KEY (genre_id) VALUES (2, 'Драма');
MERGE INTO Genres KEY (genre_id) VALUES (3, 'Мультфильм');
MERGE INTO Genres KEY (genre_id) VALUES (4, 'Триллер');
MERGE INTO Genres KEY (genre_id) VALUES (5, 'Документальный');
MERGE INTO Genres KEY (genre_id) VALUES (6, 'Боевик');
MERGE INTO Mpa KEY (mpa_id) VALUES (1, 'G');
MERGE INTO Mpa KEY (mpa_id) VALUES (2, 'PG');
MERGE INTO Mpa KEY (mpa_id) VALUES (3, 'PG-13');
MERGE INTO Mpa KEY (mpa_id) VALUES (4, 'R');
MERGE INTO Mpa KEY (mpa_id) VALUES (5, 'NC-17');

/*INSERT INTO genres (genre_name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

INSERT INTO mpa (mpa_name)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');*/