CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    name       VARCHAR(255)        NOT NULL,
    birth_date DATE                NOT NULL,
    phone      VARCHAR(20)         NOT NULL,
    role       INT                 NOT NULL,
    created_at DATETIME            NOT NULL,
    created_by BINARY(16)          NOT NULL,
    updated_at DATETIME,
    updated_by BINARY(16),
    deleted    BOOLEAN             NOT NULL DEFAULT FALSE
);