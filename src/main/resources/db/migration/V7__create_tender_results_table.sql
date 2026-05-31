CREATE TABLE tender_results (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tender_id UUID NOT NULL UNIQUE REFERENCES tenders(id),
    winning_contractor_id UUID REFERENCES users(id),
    winning_submission_id UUID REFERENCES bid_submissions(id),
    contract_price DECIMAL(20, 2),
    notes TEXT,
    announced_at TIMESTAMP NOT NULL DEFAULT NOW()
);
