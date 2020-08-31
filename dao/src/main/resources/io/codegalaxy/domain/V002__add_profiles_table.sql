
CREATE TABLE profiles (
  id            integer PRIMARY KEY,
  username      text NOT NULL,
  email         text,
  first_name    text,
  last_name     text,
  full_name     text,
  city          text,
  avatar_url    text,
  UNIQUE (username)
);
