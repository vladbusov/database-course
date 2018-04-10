CREATE TABLE users (
  id SERIAL primary key,
  nickname VARCHAR(255),
  email    VARCHAR(255),
  password VARCHAR(255),
  avatar VARCHAR(255),
  games_number INTEGER,
  score INTEGER
);