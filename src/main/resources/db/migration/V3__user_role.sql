-- Add basic user role for RBAC.
ALTER TABLE users
  ADD COLUMN role VARCHAR(32) NOT NULL DEFAULT 'USER';

-- Backfill safety (in case some rows were created before the column existed).
UPDATE users
SET role = 'USER'
WHERE role IS NULL;

