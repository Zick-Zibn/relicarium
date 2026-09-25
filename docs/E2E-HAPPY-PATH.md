# E2E: продажа на торгах и выплата клиенту

Сквозной сценарий через **pledge** (оркестрация **ledger** + **auction**).

Краткий список endpoint’ов — в [README.md](../README.md).

---

## 0. Предусловия

1. `docker compose up -d` (Postgres **5433 / 5434 / 5435**).
2. Запущены приложения (порядок удобный):
   - **ledger** → `http://localhost:8082`
   - **auction** → `http://localhost:8083`
   - **pledge** → `http://localhost:8081`
3. Postman, IntelliJ HTTP Client или curl.
4. На pledge применена миграция **`V2__app_users`** (пользователи для login). Если pledge поднимался раньше без неё — пересоздай БД или накати миграцию.

**JWT:** все запросы к `/api/v1/**` (pledge, ledger, auction) — с заголовком `Authorization: Bearer <accessToken>`, кроме **`POST …/auth/login`**.

---
## 1. Переменные сценария

Запиши себе (меняй `operationId` на каждый новый прогон):

| Переменная | Пример | Откуда |
|------------|--------|--------|
| `accessToken` | JWT строка | шаг 0, поле `accessToken` |
| `pledgeId` | UUID | ответ шага 1 |
| `opDisburse` | `disburse-e2e-1` | придумать |
| `opAuctionSubmit` | `auction-reg-e2e-1` | придумать |
| `opSale` | `auction-sale-e2e-1` | придумать |
| `opPayout` | `client-payout-e2e-1` | придумать |
| `totalDue` | число | шаг 6, поле `totalDue` |
| `saleProceeds` | число | **≥ totalDue** (шаг 7) |
| `payoutAmount` | число | **≤** переплата (`saleProceeds - totalDue`) |

Для **ENTER_GRACE** нужно `due_date ≤ сегодня`. В accept ниже **`termDays: 0`** — срок «сегодня».

Dev-логин: **`cashier`** / **`password`** (роль `CASHIER`). Альтернатива: **`admin`** / **`password`**.

---

## 2. Шаги

На каждый запрос ниже (кроме шага 0) добавляй:

```http
Authorization: Bearer <accessToken>
```

### 0. Login (JWT)

**POST** `http://localhost:8081/api/v1/auth/login`

```json
{
  "username": "cashier",
  "password": "password"
}
```

**Ожидание:** **200**, в теле `accessToken`, `tokenType`: `Bearer`, `expiresInSeconds`.

**Сохранить:** `accessToken` для всех следующих шагов.

---

### 1. Принять залог

**POST** `http://localhost:8081/api/v1/pledges`

```json
{
  "fullName": "Иван Тестов",
  "phone": "+79001234567",
  "passportSeries": "1234",
  "passportNumber": "567890",
  "itemName": "Золотое кольцо",
  "description": "E2E сценарий",
  "category": "JEWELRY",
  "estimatedValue": 15000.00,
  "loanAmount": 10000.00,
  "interestRate": 0.1200,
  "termDays": 0
}
```

**Ожидание:** **201**, `"status": "ACCEPTED"`.

**Сохранить:** `id` → `pledgeId`.

---

### 2. Выдача денег (ledger + ACTIVE)

**POST** `http://localhost:8081/api/v1/pledges/{pledgeId}/disbursements`

```json
{
  "operationId": "disburse-e2e-1",
  "openedAt": "2026-09-25T10:00:00+03:00"
}
```

**Ожидание:** **200**, `"status": "ACTIVE"`.

---

### 3. Льготный период

**POST** `http://localhost:8081/api/v1/pledges/{pledgeId}/status-transitions`

```json
{
  "transition": "ENTER_GRACE"
}
```

**Ожидание:** **200**, `"status": "GRACE"`.

Если **409** про due date — в accept увеличь `termDays` или сдвинь дату в прошлое (логика: grace только **on or after due_date**).

---

### 4. К торгам (FOR_SALE)

**POST** `http://localhost:8081/api/v1/pledges/{pledgeId}/status-transitions`

```json
{
  "transition": "MARK_FOR_SALE"
}
```

**Ожидание:** **200**, `"status": "FOR_SALE"`.

---

### 5. Отправить на торги (auction + ON_AUCTION)

**POST** `http://localhost:8081/api/v1/pledges/{pledgeId}/auction-submissions`

```json
{
  "operationId": "auction-reg-e2e-1"
}
```

**Ожидание:** **200**, `"status": "ON_AUCTION"`, `itemStorageStatus`: `AT_AUCTION`.

---

### 6. Сколько долг на сегодня

**GET** `http://localhost:8081/api/v1/pledges/{pledgeId}/amount-due`

(без query или `?asOf=2026-09-25`)

**Ожидание:** **200**, `"closed": false`.

**Сохранить:** `totalDue` (тело + проценты к закрытию займа при продаже).

Пример: `"totalDue": 10100.00` — подставь **своё** значение.

---

### 7. Продажа на торгах (auction SOLD + ledger settlement + SOLD)

**POST** `http://localhost:8081/api/v1/pledges/{pledgeId}/auction-sales`

`saleProceeds` **не меньше** `totalDue` из шага 6. Переплата клиенту = `saleProceeds - totalDue`.

```json
{
  "operationId": "auction-sale-e2e-1",
  "saleProceeds": 12000.00
}
```

**Ожидание:** **200**, `"status": "SOLD"`, `itemStorageStatus`: `SOLD`.

Если **502** — проверь ledger (8082) и auction (8083), что они запущены, и что pledge получил Bearer (шаг 0). Также: `saleProceeds` ≥ `totalDue`.

---

### 8. Проверка договора

**GET** `http://localhost:8081/api/v1/pledges/{pledgeId}`

**Ожидание:** **200**, `status` **SOLD**, `itemStorageStatus` **SOLD**.

---

### 9. Выплата клиенту (переплата)

Только для **SOLD**. `amount` не больше остатка на **CLIENT_PAYABLE** в ledger (для простого прогона = переплата с шага 7).

**POST** `http://localhost:8081/api/v1/pledges/{pledgeId}/client-payouts`

```json
{
  "operationId": "client-payout-e2e-1",
  "amount": 1900.00,
  "paidAt": "2026-09-25T15:00:00+03:00"
}
```

Пример: при `totalDue = 10100` и `saleProceeds = 12000` переплата **1900** — подставь **свои** цифры.

**Ожидание:** **200**, статус pledge остаётся **SOLD**.

Повтор с тем же `operationId` → идемпотентно в ledger.

---

### 10. (Опционально) Проверка в БД ledger

На **5434**, БД `ledger`:

- `loans.closed_at` NOT NULL для `pledge_id`
- документы `AUCTION_SETTLEMENT`, `CLIENT_PAYOUT`
- строки по **CLIENT_PAYABLE** и **CASH**

---

## 3. Альтернатива: выкуп вместо торгов

После **шага 2** (ACTIVE) — не забывай Bearer на каждый запрос:

1. **GET** `.../amount-due` → сумма выкупа
2. **POST** `.../redemptions` с `operationId`, `amount`, `paidAt`
3. **GET** `.../{pledgeId}` → **REDEEMED**, `itemStorageStatus` **RETURNED**

Шаги 3–9 из этого файла не нужны.

---

## 4. Типичные ошибки

| HTTP | Причина |
|------|---------|
| **401** | нет Bearer, просрочен/битый JWT, неверный login |
| **403** | редко при текущих настройках (любой залогиненный пользователь) |
| **400** | GET `/pledges` без `phone` и `status`; битый JSON |
| **404** | неверный `pledgeId` |
| **409** | неверный статус (sale не из ON_AUCTION, payout не из SOLD, grace раньше due_date) |
| **502** | ledger/auction не запущен или **401** на внутреннем вызове (нет/не пробросили JWT); `saleProceeds < totalDue`; payout `amount` > balance |

---

## 5. Идемпотентность

Один и тот же `operationId` для **disburse**, **auction-submissions**, **auction-sales**, **client-payouts** при повторе не должен дублировать проводки в ledger (pledge может уже быть в финальном статусе → **409** на повтор sale).

---

## 6. IntelliJ HTTP Client

Готовая последовательность (login + E2E): [http/relicarium.http](../http/relicarium.http). Сначала выполни **0. Login**, затем шаги 1–9.
