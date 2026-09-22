CREATE TABLE lots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pledge_id UUID NOT NULL,
    operation_id VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_lots_pledge_id UNIQUE (pledge_id),
    CONSTRAINT uq_lots_pledge_operation UNIQUE (pledge_id, operation_id)
);
