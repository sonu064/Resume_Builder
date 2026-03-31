# Frontend (Vanilla HTML/CSS/JS)

## What this frontend does
- Provides pages for `Login/Register`, `Dashboard`, `Resume Builder`, and `Resume Preview`
- Talks to the backend REST API using `fetch()`
- Stores JWT in `localStorage`
- Exports resumes to PDF (client-side)

## Run the frontend
This project is plain static files (no npm build step).

1. Start the backend first (defaults to `http://localhost:8080`).
2. Serve this `frontend/` folder as static content.

### Option A: Python static server (recommended)
From `frontend/`:
```bash
cd frontend
python -m http.server 5500
```
Open:
- `http://localhost:5500/`

### Option B: VS Code Live Server
- Right-click `frontend/index.html` -> “Open with Live Server”

## Backend URL (API base)
By default the frontend calls:
- `http://localhost:8080`

If you need to override it, set in DevTools console:
```js
localStorage.setItem("resumeBuilderApiBase", "http://localhost:8080");
```

## Swagger verification checklist
Swagger UI is on the backend:
- `http://localhost:8080/swagger-ui.html`

Verify in this order:

1. **Auth**
   - `POST /api/auth/register` (create a new user)
   - `POST /api/auth/login` (get JWT)

2. **Resumes**
   - `POST /api/resumes` (create a resume)
   - `GET /api/resumes?page=0&size=10` (paginated list)
   - `GET /api/resumes/{resumeId}` (details)
   - `PUT /api/resumes/{resumeId}` (bulk update: personal + all sections)
   - `DELETE /api/resumes/{resumeId}`

3. **Section CRUD (education/experience/skills/projects)**
   - `GET /api/resumes/{resumeId}/education`
   - `PUT /api/resumes/{resumeId}/education`
   - `POST /api/resumes/{resumeId}/education`
   - `DELETE /api/resumes/{resumeId}/education`
   - Repeat the same pattern for:
     - experience (`/experience`)
     - skills (`/skills`)
     - projects (`/projects`)

4. **Scoring + Share**
   - `POST /api/resumes/{resumeId}/score`
   - `POST /api/resumes/{resumeId}/share` (returns `shareUrl`)
   - `GET /api/resumes/shared/{shareToken}` (public shared preview)

5. **Profile image upload (bonus)**
   - `POST /api/users/me/profile-image`
   - Confirm the returned `profileImageUrl` is accessible

## Run order (full application)
1. MySQL: create `resume_builder` database
2. Backend: `mvn spring-boot:run` (port `8080`)
3. Frontend: serve `frontend/` as static files (e.g., port `5500`)
4. Login with seeded demo user (Flyway):
   - `demo@resume.com` / `demo1234`

