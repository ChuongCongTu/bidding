CREATE TABLE bidding_docs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tender_id UUID NOT NULL REFERENCES tenders(id),
    version VARCHAR(10) NOT NULL DEFAULT '1.0',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    description TEXT,
    technical_requirements TEXT,
    evaluation_criteria TEXT,
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE bidding_doc_files (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bidding_doc_id UUID NOT NULL REFERENCES bidding_docs(id),
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    uploaded_at TIMESTAMP NOT NULL DEFAULT NOW()
);
