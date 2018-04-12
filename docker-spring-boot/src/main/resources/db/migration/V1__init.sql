CREATE TABLE users (
  nickname VARCHAR(20) primary key,
  email    VARCHAR(255),
  fullname VARCHAR(255),
  about TEXT,
  UNIQUE (email)
);

CREATE TABLE forum (
  slug VARCHAR(255) primary key,
  title VARCHAR(255),
  userRef VARCHAR(255) references users (nickname),
  posts INTEGER,
  threads INTEGER
);

CREATE TABLE thread (
  id SERIAL PRIMARY KEY,
  slug VARCHAR(255)  references forum (slug),
  author VARCHAR(255) references (nickname),
  forum VARCHAR(255),
  created TIMESTAMP,
  message TEXT,
  title VARCHAR(255),
  votes INTEGER
);


