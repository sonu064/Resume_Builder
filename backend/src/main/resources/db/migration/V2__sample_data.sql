-- Demo data for quick portfolio testing.
-- Login: demo@resume.com / demo1234

INSERT INTO users (id, email, password_hash, full_name, profile_image_url, created_at, updated_at)
VALUES (
  1,
  'demo@resume.com',
  '$2b$10$4hF09kBEUTV3MOnOyzvYWei/mMZtnEZHuXT0fTCKeb3OXQZcEywLe',
  'Demo User',
  NULL,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
)
ON DUPLICATE KEY UPDATE
  email = email;

INSERT INTO resumes (
  id,
  user_id,
  template_key,
  full_name,
  headline,
  email,
  phone,
  website,
  location,
  summary,
  profile_image_url,
  share_token,
  share_token_created_at,
  created_at,
  updated_at
)
VALUES (
  1,
  1,
  'classic',
  'Demo User',
  'Software Engineer | Java | Spring Boot | React',
  'demo@resume.com',
  '+1 555 123 4567',
  'https://example.com',
  'New York, NY',
  'Experienced full-stack developer focused on building reliable, secure web applications. Strong background in Spring Boot, REST APIs, and modern UI development.',
  NULL,
  'demoSharedToken1234567890abcdef1234567890abcd',
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
)
ON DUPLICATE KEY UPDATE
  full_name = full_name;

INSERT INTO education (id, resume_id, position, degree, school, location, start_date, end_date, description, created_at, updated_at)
VALUES
  (1, 1, 0, 'B.Sc. Computer Science', 'Example University', 'Boston, MA', '2016-09-01', '2020-05-31', 'Graduated with honors. Focus on software engineering and data structures.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 1, 1, 'M.Sc. Software Engineering', 'Tech Institute', 'Remote', '2020-09-01', NULL, 'Coursework in distributed systems, databases, and secure application design.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
  degree = degree;

INSERT INTO experience (id, resume_id, position, role, company, description, start_date, end_date, created_at, updated_at)
VALUES
  (1, 1, 0, 'Senior Software Engineer', 'Acme Corp', 'Led backend and frontend initiatives; improved API performance and delivered production features.', '2022-01-01', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 1, 1, 'Software Engineer', 'Globex', 'Built REST APIs and UI components; collaborated across teams to ship features.', '2020-06-01', '2021-12-31', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
  role = role;

INSERT INTO skills (id, resume_id, position, name, level, created_at, updated_at)
VALUES
  (1, 1, 0, 'Java', 'Advanced', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 1, 1, 'Spring Boot', 'Advanced', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, 1, 2, 'REST APIs', 'Advanced', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (4, 1, 3, 'MySQL', 'Advanced', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (5, 1, 4, 'JWT Authentication', 'Advanced', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (6, 1, 5, 'HTML/CSS/JS', 'Advanced', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (7, 1, 6, 'Security Best Practices', 'Advanced', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (8, 1, 7, 'Database Design', 'Advanced', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
  name = name;

INSERT INTO projects (id, resume_id, position, name, url, description, tech_stack, created_at, updated_at)
VALUES
  (1, 1, 0, 'Resume Builder', 'https://example.com/resume-builder', 'Full-stack resume builder with live preview, templates, and JWT-secured APIs.', 'Java, Spring Boot, MySQL, HTML/CSS/JS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 1, 1, 'Analytics Dashboard', 'https://example.com/analytics', 'Real-time insights with optimized queries and pagination.', 'Java, Spring Boot, MySQL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
  name = name;

