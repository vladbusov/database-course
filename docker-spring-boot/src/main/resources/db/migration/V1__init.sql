CREATE TABLE users (
  id SERIAL primary key,
  nickname VARCHAR(255),
  email    VARCHAR(255),
  fullname VARCHAR(255),
  about TEXT,
  UNIQUE (nickname, email)
);

CREATE TABLE thread (
  id SERIAL PRIMARY KEY ,
  slug VARCHAR(255),
  author VARCHAR(255) REFERENCES users(nickname),
  forum VARCHAR(255) REFERENCES forum(slug),
  created TIMESTAMP,
  message TEXT,
  title VARCHAR(255),
  votes INTEGER,
  UNIQUE (slug)
);

CREATE TABLE forum (
  id SERIAL PRIMARY KEY,
  slug VARCHAR(255) REFERENCES threadModel(slug),
  title VARCHAR(255),
  user VARCHAR(255) REFERENCES users(nickname),
  posts INTEGER,
  threads INTEGER,
  UNIQUE (title)
);
