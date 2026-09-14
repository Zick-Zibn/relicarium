CREATE TABLE accounts (
    code        VARCHAR(30)     NOT NULL PRIMARY KEY,
    name        VARCHAR(255)    NOT NULL,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

INSERT INTO accounts (code, name) VALUES
      ('CASH',              'Касса'),
      ('LOANS',             'Выданные займы'),
      ('INTEREST_INCOME',   'Доход по процентам'),
      ('CLIENT_PAYABLE',    'Расчёты с клиентом (переплата / долг клиенту после торгов)');

CREATE TABLE loans (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),         -- ID займа в ledger
    pledge_id       UUID NOT NULL UNIQUE,                               -- Ссылка на договор в pledge
    client_id       UUID NOT NULL,                                      -- Ссылка на клиента (из pledge)
    principal       NUMERIC(15, 2) NOT NULL CHECK (principal > 0),      -- Сумма выдачи (тело)
    interest_rate   NUMERIC(5, 4) NOT NULL CHECK (interest_rate >= 0),  -- Годовая доля: 0.1200 = 12%
    opened_at       TIMESTAMPTZ NOT NULL,                               -- Когда выдали деньги
    due_date        DATE NOT NULL,                                      -- Срок из договора на момент открытия
    closed_at       TIMESTAMPTZ NULL,                                   -- Когда закрыли тело (выкуп/торги); пока займ открыт — NULL
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_loans_client_id ON loans (client_id);

CREATE TABLE journal_documents(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),         -- ID документа
    document_type   VARCHAR(30) NOT NULL,                               -- Тип операции
    pledge_id       UUID NOT NULL,                                      -- К какому залогу
    loan_id         UUID NULL REFERENCES loans (id) ON DELETE RESTRICT, -- Займ в ledger
    operation_id    VARCHAR(100) NOT NULL,                              -- Внешний ключ идемпотентности
    posted_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),                 -- Когда провели
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_journal_documents_operation
        UNIQUE (document_type, pledge_id, operation_id)
);
CREATE INDEX idx_journal_documents_pledge_loan_posted ON journal_documents (pledge_id, loan_id, posted_at);

CREATE TABLE journal_lines(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),         -- ID строки
    document_id     UUID NOT NULL REFERENCES journal_documents (id)
                                                ON DELETE RESTRICT,     --К какому документу
    account_code    VARCHAR(30) NOT NULL REFERENCES accounts (code)
                                                ON DELETE RESTRICT,     -- К какому счету
    debit           NUMERIC(15, 2) NOT NULL CHECK (debit >= 0),         -- Дебет
    credit          NUMERIC(15, 2) NOT NULL CHECK (credit >= 0),        -- Кредит
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CHECK (NOT (debit > 0 AND credit > 0)),
    CHECK (debit > 0 OR credit > 0)
);
CREATE INDEX idx_journal_lines_doc_id_account_code ON journal_lines (document_id, account_code);