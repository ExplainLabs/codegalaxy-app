
-- force data refresh
DELETE FROM topics;

ALTER TABLE topics
  ADD COLUMN progress integer;
