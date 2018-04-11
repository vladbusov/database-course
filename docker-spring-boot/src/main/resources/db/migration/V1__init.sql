CREATE TABLE users (
  id SERIAL primary key,
  nickname VARCHAR(255),
  email    VARCHAR(255),
  fullname VARCHAR(255),
  about TEXT
);