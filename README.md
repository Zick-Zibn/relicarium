# Relicarium

Pet-проект «ломбард»: три микросервиса на **Java 17**, **Spring Boot 4.x**, **Flyway**, **PostgreSQL**.

| Сервис   | Назначение                          | HTTP | Postgres (host) |
|----------|-------------------------------------|------|-----------------|
| **pledge**  | договоры, клиенты, вещи, статусы   | 8081 | 5433 / `pledge` |
| **ledger**  | займы, журнал, касса               | 8082 | 5434 / `ledger` |
| **auction** | лоты на торгах                     | 8083 | 5435 / `auction` |

Сервисы **не** делят БД. Связь только по **HTTP** (`pledge` → `ledger`, `pledge` → `auction`).

---

## Требования

- JDK 17+
- Maven 3.9+
- Docker (для Postgres)

---

## Инфраструктура

Из корня репозитория:

```bash
docker compose up -d
```

Поднимаются три контейнера Postgres (порты **5433**, **5434**, **5435**).  
Логин/пароль/БД — см. `docker-compose.yml` (по умолчанию `postgres` / `postgres`).

---

## Запуск приложений

Нужны **три процесса** (три терминала). Сначала **ledger** и **auction**, затем **pledge** (ему нужны URL соседей в `application.yml`).

```bash
# терминал 1
mvn -pl ledger spring-boot:run

# терминал 2
mvn -pl auction spring-boot:run

# терминал 3
mvn -pl pledge spring-boot:run
```

Сборка всего проекта:

```bash
mvn clean install
```

Flyway накатывает схему при старте каждого сервиса (`ddl-auto: validate`).

---

## Безопасность (JWT)

Все **`/api/v1/**`** на **pledge**, **ledger** и **auction** требуют заголовок:

```http
Authorization: Bearer <accessToken>
```

Исключение на pledge: **`POST /api/v1/auth/login`** (без токена).

**Логин** (выдача JWT, только pledge):

```http
POST http://localhost:8081/api/v1/auth/login
Content-Type: application/json

{
  "username": "cashier",
  "password": "password"
}
```

Ответ: `accessToken`, `expiresInSeconds`, `roles`. Токен **HS256**, один секрет во всех трёх `application.yml` (`relicarium.security.jwt.*`).

**Dev-пользователи** (Flyway `pledge` `V2__app_users.sql`):

| Логин | Пароль | Роль |
|-------|--------|------|
| `cashier` | `password` | `CASHIER` |
| `admin` | `password` | `ADMIN` |

Pledge при вызовах ledger/auction **пробрасывает** тот же Bearer из входящего запроса. Прямые запросы на 8082/8083 — тоже с JWT (удобно взять токен после login).

Готовый прогон: **`http/relicarium.http`** (сначала **0. Login**). Подробный сценарий — **`docs/E2E-HAPPY-PATH.md`**.

---
## Жизненный цикл (кратко)

1. **Принять залог** — `POST /api/v1/pledges` → `ACCEPTED`
2. **Выдать деньги** — disburse → `ACTIVE` + займ в ledger
3. *(опционально)* **Оплата %**, **выкуп** → `REDEEMED`
4. **Просрочка** — grace → for-sale
5. **Торги** — submit → `ON_AUCTION` → sale или return
6. **Продажа** — settlement в ledger → `SOLD`
7. **Переплата клиенту** — client-payout (ledger + pledge)

Перед шагами — **login** и Bearer-токен (см. раздел «Безопасность» выше и `docs/E2E-HAPPY-PATH.md`).

---
## API pledge (`http://localhost:8081`)

Префикс: **`/api/v1`**. Кроме login — нужен **`Authorization: Bearer …`**.

| Метод | Path | Смысл |
|-------|------|--------|
| POST | `/auth/login` | JWT (логин) |
| POST | `/pledges` | принять залог |
| GET | `/pledges` | список (`phone`, `status`, `page`, `size`) |
| GET | `/pledges/{pledgeId}` | один договор |
| GET | `/pledges/{pledgeId}/amount-due` | сколько к оплате (прокси ledger) |
| POST | `/pledges/{pledgeId}/disbursements` | выдача |
| POST | `/pledges/{pledgeId}/interest-payments` | оплата % |
| POST | `/pledges/{pledgeId}/redemptions` | выкуп |
| POST | `/pledges/{pledgeId}/status-transitions` | grace / for-sale |
| POST | `/pledges/{pledgeId}/auction-submissions` | на торги |
| POST | `/pledges/{pledgeId}/auction-sales` | продано |
| POST | `/pledges/{pledgeId}/auction-returns` | не продано |
| POST | `/pledges/{pledgeId}/client-payouts` | выплата клиенту (после SOLD) |

---

## API ledger (`http://localhost:8082`)

Префикс: **`/api/v1/loans`**. JWT обязателен (обычно вызывается через pledge; для отладки — тот же Bearer после login).

Выдача, погашение, %, расчёт `amount-due`, settlement после торгов, client-payout — см. `LoanController`.

---

## API auction (`http://localhost:8083`)

Префикс: **`/api/v1/lots`**. JWT обязателен.

Регистрация лота, закрытие (SOLD / UNSOLD) — см. модуль `auction`.

---

## Структура репозитория

```text
relicarium/
├── pom.xml
├── docker-compose.yml
├── docs/
│   └── E2E-HAPPY-PATH.md
├── http/
│   └── relicarium.http
├── pledge/
├── ledger/
└── auction/
```

---

## Лицензия / автор

Учебный pet-проект.
