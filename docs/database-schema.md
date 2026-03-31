# Database Schema (MySQL)

All tables are created/managed by Flyway migrations in:
- `backend/src/main/resources/db/migration/`

## Tables

`users`
- `id` (BIGINT, PK, auto-increment)
- `email` (VARCHAR, unique)
- `password_hash` (VARCHAR)
- `full_name` (VARCHAR)
- `profile_image_url` (VARCHAR, nullable)
- `created_at`, `updated_at` (TIMESTAMP)

`resumes`
- `id` (BIGINT, PK, auto-increment)
- `user_id` (BIGINT, FK -> `users.id`, indexed, cascade delete)
- `template_key` (VARCHAR)
- Personal section fields:
  - `full_name`, `headline`, `email`, `phone`, `website`, `location`, `summary`
- `profile_image_url` (VARCHAR, nullable)
- Sharing:
  - `share_token` (VARCHAR, unique, nullable)
  - `share_token_created_at` (TIMESTAMP, nullable)
- `created_at`, `updated_at` (TIMESTAMP)

Section tables (all are ordered via `position` and have timestamps):
`education`, `experience`, `skills`, `projects`
- Each has:
  - `id` (BIGINT, PK, auto-increment)
  - `resume_id` (BIGINT, FK -> `resumes.id`, cascade delete, indexed)
  - `position` (INT, not null)
  - Section-specific fields
  - `created_at`, `updated_at` (TIMESTAMP)

## Relationships

- `users` -> `resumes`: One-to-Many
- `resumes` -> each section table: One-to-Many

