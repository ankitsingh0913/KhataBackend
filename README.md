# 🚀 Khata Backend API

> A highly scalable, production-ready **Spring Boot backend** for the Khata application. Built with secure authentication, Google OAuth integration, email services, Redis caching, and a fully Dockerized deployment architecture following **12-Factor App** principles.

---

## ✨ Features

* 🔐 JWT-based Authentication & Authorization
* 🌐 Google OAuth 2.0 Login
* 📧 Email Services Integration
* ⚡ Redis Caching for Performance
* 🐘 PostgreSQL Database
* 🐳 Docker & Docker Compose Ready
* 📘 Swagger / OpenAPI Documentation
* 🏗️ Environment-based Profiles (`dev` / `prod`)
* 📈 Scalable & Production Ready Architecture

---

## 🛠️ Tech Stack

| Category   | Technology             |
| ---------- | ---------------------- |
| Language   | Java 17                |
| Framework  | Spring Boot 3+         |
| Security   | Spring Security, JWT   |
| Database   | PostgreSQL 16          |
| Cache      | Redis 7                |
| Auth       | Google OAuth 2.0       |
| Build Tool | Maven                  |
| Docs       | Swagger / OpenAPI 3    |
| Deployment | Docker, Docker Compose |

---

## 🏗️ Project Architecture & Profiles

The application uses separate configuration files for security and maintainability:

```yaml
application.yaml       # Shared configurations
application-dev.yaml   # Local development setup
application-prod.yaml  # Production config via env variables
```

### Profiles Explained

* **dev** → Local database + verbose SQL logs
* **prod** → Secure environment variables + hardened validation

---

# 💻 Local Development Setup

## ✅ Prerequisites

Make sure you have installed:

* Java 17+
* Maven
* PostgreSQL (running on `5432`)
* Redis (running on `6379`)

---

## 🚀 Run Locally

### 1️⃣ Clone Repository

```bash
git clone https://github.com/ankitsingh0913/KhataBackend.git
cd KhataBackend
```

### 2️⃣ Create Database

Create a PostgreSQL database named:

```sql
khata_db
```

### 3️⃣ Start Application

```bash
mvn spring-boot:run
```

> By default, it runs using the **dev** profile.

### 4️⃣ Swagger Docs

```text
http://localhost:8082/swagger-ui.html
```

---

# 🐳 Docker Deployment (Production Ready)

The project includes a **multi-stage Docker build** for clean, lightweight, and production-grade deployments.

## 🚀 Start Full Infrastructure

This starts:

* Backend API
* PostgreSQL
* Redis

```bash
docker-compose up -d --build
```

## 📜 View Backend Logs

```bash
docker-compose logs -f backend
```

## 🛑 Stop Containers Safely

```bash
docker-compose down
```

> PostgreSQL data remains persisted using Docker Volume: `postgres_data`

---

# 🔐 Environment Variables

For production, credentials are injected securely via `docker-compose.yml`.

| Variable                     | Description                 |
| ---------------------------- | --------------------------- |
| `SPRING_PROFILES_ACTIVE`     | Activate production profile |
| `SPRING_DATASOURCE_URL`      | PostgreSQL JDBC URL         |
| `SPRING_DATASOURCE_USERNAME` | Database username           |
| `SPRING_DATASOURCE_PASSWORD` | Database password           |
| `SPRING_DATA_REDIS_HOST`     | Redis hostname              |
| `SPRING_DATA_REDIS_PORT`     | Redis port                  |

---

# 📂 Suggested Project Structure

```bash
src/
 ┣ main/
 ┃ ┣ java/
 ┃ ┣ resources/
 ┃ ┃ ┣ application.yaml
 ┃ ┃ ┣ application-dev.yaml
 ┃ ┃ ┗ application-prod.yaml
 ┗ test/
```

---

# 🤝 Contributing

Contributions are welcome.

```bash
1. Fork the repository
2. Create feature branch   git checkout -b feature/amazing-feature
3. Commit changes          git commit -m "Add amazing feature"
4. Push branch             git push origin feature/amazing-feature
5. Open Pull Request
```

---

# 🌟 Author

**Ankit Singh**
Backend Developer | Spring Boot | Flutter | Scalable Systems

---

# 📜 License

This project is open-source and available under the MIT License.
