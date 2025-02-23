CREATE TABLE IF NOT EXISTS users (
    id LONG GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    birthday TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS friends (
    id LONG GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id LONG NOT NULL,
    friend_id LONG NOT NULL,
    status VARCHAR(255),
    FOREIGN KEY(user_id) REFERENCES users (id),
    FOREIGN KEY(friend_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS rating (
    id LONG GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    id LONG GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    release_date TIMESTAMP NOT NULL,
    duration LONG NOT NULL,
    rating_id LONG,
    FOREIGN KEY (rating_id) REFERENCES rating (id)
);

CREATE TABLE IF NOT EXISTS genre (
    id LONG GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS film_genre (
    id LONG GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    film_id LONG NOT NULL,
    genre_id LONG NOT NULL,
    FOREIGN KEY (film_id) REFERENCES films (id),
    FOREIGN KEY(genre_id) REFERENCES genre (id)
);

CREATE TABLE IF NOT EXISTS likes (
    id LONG GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    film_id LONG NOT NULL,
    user_id LONG NOT NULL,
    FOREIGN KEY(film_id) REFERENCES films (id),
    FOREIGN KEY(user_id) REFERENCES users (id)
);


INSERT INTO users (email, login, name, birthday)
VALUES ('email1@mail.ru', 'userLogin1', 'userName1', '2004-10-08');

INSERT INTO users (email, login, name, birthday)
VALUES ('email2@mail.ru', 'userLogin2', 'userName2', '2001-01-01');

INSERT INTO users (email, login, name, birthday)
VALUES ('email3@mail.ru', 'userLogin3', 'userName3', '2003-01-01');

INSERT INTO rating (name)
VALUES ('G');

INSERT INTO rating (name)
VALUES ('PG');

INSERT INTO rating (name)
VALUES ('PG-13');

INSERT INTO rating (name)
VALUES ('R');

INSERT INTO rating (name)
VALUES ('NC-17');

INSERT INTO films (name, description, release_date, duration, rating_id)
VALUES ('filmName1', 'filmDescription1', '2003-01-01', 120,
(SELECT id FROM RATING WHERE id = 1));

INSERT INTO films (name, description, release_date, duration, rating_id)
VALUES ('filmName2', 'filmDescription2', '2013-06-01', 115,
(SELECT id FROM RATING WHERE id = 2));

INSERT INTO genre (name)
VALUES ('Комедия');

INSERT INTO genre (name)
VALUES ('Драма');

INSERT INTO genre (name)
VALUES ('Мультфильм');

INSERT INTO genre (name)
VALUES ('Триллер');

INSERT INTO genre (name)
VALUES ('Документальный');

INSERT INTO genre (name)
VALUES ('Боевик');

INSERT INTO film_genre (film_id, genre_id)
VALUES (
    (SELECT id FROM films WHERE name = 'filmName1'),
    (SELECT id FROM genre WHERE name = 'Комедия')
);


INSERT INTO film_genre (film_id, genre_id)
VALUES (
    (SELECT id FROM films WHERE name = 'filmName2'),
    (SELECT id FROM genre WHERE name = 'Боевик')
);