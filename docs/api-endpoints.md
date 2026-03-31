# API Endpoints (Summary)

Swagger/OpenAPI:
- `GET http://localhost:8080/swagger-ui.html`

## Auth
- `POST /api/auth/register`
- `POST /api/auth/login`

Auth header for protected routes:
- `Authorization: Bearer <jwt>`

## Resumes (protected)
- `POST /api/resumes` (create)
- `GET /api/resumes?page=&size=` (list, paginated)
- `GET /api/resumes/{resumeId}` (details)
- `PUT /api/resumes/{resumeId}` (bulk update: personal + sections)
- `DELETE /api/resumes/{resumeId}`

### Sections (bulk replace + delete)
- `GET /api/resumes/{resumeId}/education`
- `PUT /api/resumes/{resumeId}/education` (replace all education)
- `DELETE /api/resumes/{resumeId}/education`

Same pattern for:
- experience (`/experience`)
- skills (`/skills`)
- projects (`/projects`)

## Scoring + Share
- `POST /api/resumes/{resumeId}/score` -> resume score + suggestions
- `POST /api/resumes/{resumeId}/share` -> returns `{ "shareUrl": "..." }`

## Public shared preview (no auth)
- `GET /api/resumes/shared/{shareToken}` -> resume preview payload

## Profile image (bonus)
- `POST /api/users/me/profile-image` (multipart form field: `image`)

