CREATE TABLE IF NOT EXISTS users (
    id LONG PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    birthday TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS friends (
    id LONG PRIMARY KEY,
    user_id LONG NOT NULL,
    friend_id LONG NOT NULL,
    status VARCHAR(255) NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users (id),
    FOREIGN KEY(friend_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS rating (
    id LONG PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    id LONG PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    release_date TIMESTAMP NOT NULL,
    duration LONG NOT NULL,
    rating_id LONG,
    FOREIGN KEY (rating_id) REFERENCES rating (id)
);

CREATE TABLE IF NOT EXISTS genre (
    id LONG PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre (
    id LONG PRIMARY KEY,
    film_id LONG NOT NULL,
    genre_id LONG NOT NULL,
    FOREIGN KEY (film_id) REFERENCES films (id),
    FOREIGN KEY(genre_id) REFERENCES genre (id)
);

CREATE TABLE IF NOT EXISTS likes (
    id LONG PRIMARY KEY,
    film_id LONG NOT NULL,
    user_id LONG NOT NULL,
    FOREIGN KEY(film_id) REFERENCES films (id),
    FOREIGN KEY(user_id) REFERENCES users (id)
);


MERGE INTO users KEY(id)
VALUES (1, 'sdfs@fd', 'rfe', 'bvx', '2000-01-01');

MERGE INTO users KEY(id)
VALUES (2, 'sdfs@fdq', 'rfeq', 'bvxq', '2001-01-01');

MERGE INTO users KEY(id)
VALUES (3, 'sdfs@fdw', 'rfee', 'bvxe', '2003-01-01');

MERGE INTO films KEY(id)
VALUES (1, 'filmName', 'filmDescription', '2003-01-01', 120, NULL);