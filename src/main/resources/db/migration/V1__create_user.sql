CREATE TABLE IF NOT EXISTS users
(
    id         UUID PRIMARY KEY,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    name       VARCHAR(255)        NOT NULL,
    birth_date DATE,
    phone      VARCHAR(20),
    role       INT                 NOT NULL,
    created_at TIMESTAMP           NOT NULL,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255),
    deleted    BOOLEAN             NOT NULL DEFAULT FALSE
);