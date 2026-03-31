-- Sample data is loaded by Flyway migration:
-- backend/src/main/resources/db/migration/V2__sample_data.sql
--
-- Demo login:
--   demo@resume.com / demo1234
--
-- The seeded demo resume includes:
--   - Education (2 entries)
--   - Experience (2 entries)
--   - Skills (8 entries)
--   - Projects (2 entries)
--   - A pre-generated share token for public preview

-- You can optionally verify with:
-- SELECT id, email, full_name FROM users;
-- SELECT id, user_id, template_key, share_token FROM resumes;

