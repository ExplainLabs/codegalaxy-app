
CREATE TABLE chapters (
  id            integer PRIMARY KEY,
  topic         text NOT NULL,
  alias         text NOT NULL,
  name          text NOT NULL,
  num_questions integer NOT NULL,
  num_paid      integer NOT NULL,
  num_learners  integer NOT NULL,
  num_chapters  integer NOT NULL,
  progress      integer NOT NULL,
  UNIQUE (topic, alias)
);

CREATE INDEX idx_chapters_topic ON chapters (topic);
