CREATE TABLE domain_event (
  id VARCHAR NOT NULL,
  type VARCHAR NOT NULL,
  payload TEXT NOT NULL,
  published_at TIMESTAMP NOT NULL,
  dispatched bit DEFAULT 0
);
