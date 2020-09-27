
DROP TABLE topics;
DROP TABLE chapters;

CREATE TABLE topics (
  id            integer PRIMARY KEY,
  alias         text NOT NULL,
  name          text NOT NULL,
  lang          text NOT NULL,
  num_questions integer NOT NULL,
  num_paid      integer NOT NULL,
  num_learners  integer NOT NULL,
  num_chapters  integer NOT NULL,
  svg_icon      text,
  UNIQUE (alias)
);

CREATE TABLE chapters (
  id            integer PRIMARY KEY,
  topic         text NOT NULL,
  alias         text NOT NULL,
  name          text NOT NULL,
  num_questions integer NOT NULL,
  num_paid      integer NOT NULL,
  num_learners  integer NOT NULL,
  num_chapters  integer NOT NULL,
  UNIQUE (topic, alias)
);

CREATE INDEX idx_chapters_topic ON chapters (topic);

CREATE TABLE topics_stats (
  id            integer PRIMARY KEY,
  progress      integer NOT NULL,
  progress_once integer NOT NULL,
  progress_all  integer NOT NULL,
  free_percent  integer NOT NULL,
  paid          integer NOT NULL
);

CREATE TABLE chapters_stats (
  id            integer PRIMARY KEY,
  progress      integer NOT NULL,
  progress_once integer NOT NULL,
  progress_all  integer NOT NULL,
  free_percent  integer NOT NULL,
  paid          integer NOT NULL
);
