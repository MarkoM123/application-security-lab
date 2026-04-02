INSERT INTO app_user (id, username, password, role) VALUES
    (1, 'alice', 'alice123', 'USER'),
    (2, 'bob', 'bob123', 'USER'),
    (3, 'admin', 'admin123', 'ADMIN');

INSERT INTO customer (id, full_name, email, phone_number, account_id, owner_user_id) VALUES
    (1001, 'Alice Johnson', 'alice@example.local', '+1-555-0101', 5001, 1),
    (1002, 'Bob Smith', 'bob@example.local', '+1-555-0102', 5002, 2);

INSERT INTO billing_account (account_id, customer_id, balance, plan_name, owner_user_id) VALUES
    (5001, 1001, 49.99, 'Premium Voice', 1),
    (5002, 1002, 89.99, 'Unlimited Data', 2);

