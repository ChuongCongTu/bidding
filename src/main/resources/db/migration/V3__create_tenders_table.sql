CREATE TABLE tenders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plan_id UUID NOT NULL REFERENCES plans(id),
    name VARCHAR(500) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    method VARCHAR(50) NOT NULL,
    estimated_value DECIMAL(20, 2),
    hsmt_issue_date TIMESTAMP,
    bid_open_date TIMESTAMP,
    bid_deadline TIMESTAMP,
    contract_duration INTEGER,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
