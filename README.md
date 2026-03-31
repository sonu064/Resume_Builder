# Resume Builder (Spring Boot + MySQL + HTML/CSS/JS)

Production-ready full-stack resume builder with:
- Spring Boot (MVC) + Spring Data JPA
- MySQL schema managed by Flyway
- JWT authentication (BCrypt password hashing)
- Clean, responsive frontend with multi-step resume builder, live preview, PDF export, templates, dark mode

## 1. Prerequisites
- JDK 17+
- Maven
- MySQL 8+

## 2. Create Database
Create a database named `resume_builder`:
```sql
CREATE DATABASE resume_builder;
```

## 3. Configure Backend
Backend config is in `backend/src/main/resources/application.yml`.

Set environment variables (recommended) before running:
- `DB_URL` (default: `jdbc:mysql://localhost:3306/resume_builder?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`)
- `DB_USER` (default: `root`)
- `DB_PASSWORD` (required)
- `JWT_SECRET` (required; set to a strong random string)
- `CORS_ORIGINS` (default allows local static servers)
- `UPLOAD_DIR` (default: `uploads`)

The server runs at `http://localhost:8080`.

## 4. Run Backend
From the `backend/` folder:
```bash
mvn spring-boot:run
```

Swagger UI:
- `http://localhost:8080/swagger-ui.html`

## 5. Run Frontend
This project uses vanilla HTML/CSS/JS (no npm build).

Serve the `frontend/` folder as static files (example):
```bash
python -m http.server 5500
```
Then open:
- `http://localhost:5500/`

## 6. Demo Credentials
Flyway seeds a demo account:
- Email: `demo@resume.com`
- Password: `demo1234`

## 7. Notes
- Profile image upload is stored on disk under `backend/uploads/profile-images/{userId}/...` and served by Spring as static resources (`/profile-images/...`).
- Resume preview can be shared via share tokens:
  - Generate link: `POST /api/resumes/{resumeId}/share`
  - Public preview: `GET /api/resumes/shared/{shareToken}`

