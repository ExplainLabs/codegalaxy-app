
-- non-transactional
PRAGMA foreign_keys = ON;

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
