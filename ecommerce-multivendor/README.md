# Amar Bazaar API

Spring Boot backend for the Amar Bazaar multi-vendor marketplace.

- Java 17
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- MySQL
- Razorpay
- SMTP/OTP authentication

See the root/frontend README for the complete application setup and deployment architecture.

## Configuration

All credentials are environment variables. Start from `.env.example`.

Important variables:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `RAZORPAY_KEY_ID`
- `RAZORPAY_KEY_SECRET`
- `APP_CORS_ALLOWED_ORIGINS`
