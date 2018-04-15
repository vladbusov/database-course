CREATE TABLE users (
  nickname VARCHAR(255) primary key,
  email    VARCHAR(255),
  fullname VARCHAR(255),
  about TEXT,
  UNIQUE (email)
);

CREATE TABLE forum (
  slug VARCHAR(255) primary key,
  title VARCHAR(255),
  userRef VARCHAR(255) references users (nickname) ON DELETE CASCADE NOT NULL,
  posts INTEGER,
  threads INTEGER
);

CREATE TABLE thread (
  id SERIAL PRIMARY KEY,
  slug VARCHAR(255),
  forum VARCHAR(255) references forum (slug) ON DELETE CASCADE NOT NULL,
  created TIMESTAMPTZ DEFAULT NOW(),
  message TEXT,
  votes INTEGER DEFAULT 0,
  title VARCHAR(255),
  author VARCHAR(255) references users (nickname) ON DELETE CASCADE NOT NULL
);

CREATE TABLE posts (
  id SERIAL PRIMARY KEY,
  author VARCHAR(255),
  message TEXT,
  parent INTEGER,
  created TIMESTAMP,
  isEdited BOOLEAN,
  thread INTEGER  REFERENCES thread (id) ON DELETE CASCADE NOT NULL,
  forum VARCHAR(255) REFERENCES forum (slug) ON DELETE CASCADE NOT NULL
);

CREATE TABLE votes (
  id Serial PRIMARY KEY ,
  nickname VARCHAR(255) REFERENCES users (nickname) ON DELETE CASCADE NOT NULL,
  voice INTEGER,
  threadId INTEGER REFERENCES thread(id) ON DELETE CASCADE NOT NULL
);
