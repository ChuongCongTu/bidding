CREATE TABLE bid_evaluations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tender_id UUID NOT NULL REFERENCES tenders(id),
    bid_submission_id UUID NOT NULL REFERENCES bid_submissions(id),
    evaluator_id UUID NOT NULL REFERENCES users(id),
    technical_score DECIMAL(5, 2),
    notes TEXT,
    result VARCHAR(10),
    evaluated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
