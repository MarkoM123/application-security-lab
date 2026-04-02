CREATE TABLE app_user (
    id BIGINT PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    password VARCHAR(128) NOT NULL,
    role VARCHAR(32) NOT NULL
);

CREATE TABLE customer (
    id BIGINT PRIMARY KEY,
    full_name VARCHAR(128) NOT NULL,
    email VARCHAR(128) NOT NULL,
    phone_number VARCHAR(32) NOT NULL,
    account_id BIGINT NOT NULL,
    owner_user_id BIGINT NOT NULL
);

CREATE TABLE billing_account (
    account_id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    balance DECIMAL(12, 2) NOT NULL,
    plan_name VARCHAR(64) NOT NULL,
    owner_user_id BIGINT NOT NULL
);

