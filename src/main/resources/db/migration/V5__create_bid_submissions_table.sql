CREATE TABLE bid_submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tender_id UUID NOT NULL REFERENCES tenders(id),
    contractor_id UUID NOT NULL REFERENCES users(id),
    bidding_doc_version VARCHAR(10),
    company_name VARCHAR(255),
    tax_code VARCHAR(50),
    experience TEXT,
    capability_description TEXT,
    proposed_price DECIMAL(20, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    submitted_at TIMESTAMP,
    withdrawn_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE bid_submission_files (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bid_submission_id UUID NOT NULL REFERENCES bid_submissions(id),
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    uploaded_at TIMESTAMP NOT NULL DEFAULT NOW()
);
