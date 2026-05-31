CREATE TABLE plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    investor_id UUID NOT NULL REFERENCES users(id),
    name VARCHAR(500) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    total_budget DECIMAL(20, 2),
    fiscal_year INTEGER,
    description TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
