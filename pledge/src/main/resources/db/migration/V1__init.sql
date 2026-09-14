CREATE TABLE clients (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name       VARCHAR(255)  NOT NULL,
    phone           VARCHAR(20)   NOT NULL,
    passport_series VARCHAR(10)   NULL,
    passport_number VARCHAR(20)   NULL,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_clients_phone UNIQUE (phone)
    );

CREATE TABLE items (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id       UUID          NOT NULL REFERENCES clients (id) ON DELETE RESTRICT,
    name            VARCHAR(255)  NOT NULL,
    description     TEXT          NULL,
    category        VARCHAR(50)   NOT NULL,
    estimated_value numeric(15,2) NOT NULL CHECK(estimated_value > 0),
    storage_status  VARCHAR(50)   NOT NULL DEFAULT 'IN_VAULT',
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
    );

CREATE INDEX idx_items_client_id ON items (client_id);

CREATE TABLE pledges
(
id            UUID               PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id       UUID           NOT NULL UNIQUE REFERENCES items (id) ON DELETE RESTRICT,
    client_id     UUID           NOT NULL REFERENCES clients (id) ON DELETE RESTRICT,
    loan_amount   NUMERIC(15, 2) NOT NULL CHECK (loan_amount > 0),
    interest_rate NUMERIC(5, 4)  NOT NULL CHECK (interest_rate >= 0),
    term_days     INTEGER        NOT NULL CHECK (term_days > 0),
    status        VARCHAR(30)    NOT NULL DEFAULT 'ACCEPTED',
    accepted_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    due_date      DATE           NOT NULL,
    redeemed_at   TIMESTAMPTZ    NULL,
    created_at    TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ    NOT NULL DEFAULT NOW()
    );

CREATE INDEX idx_pledges_client_id ON pledges (client_id);
CREATE INDEX idx_pledges_status ON pledges (status);
CREATE INDEX idx_pledges_due_date ON pledges (due_date);