CREATE TABLE billing (
    id SERIAL PRIMARY KEY,
    due_date DATE NOT NULL,
    payment_date DATE,
    amount DECIMAL(10, 2) NOT NULL,
    description TEXT,
    situation VARCHAR(20) NOT NULL
);

INSERT INTO billing (due_date, payment_date, amount, description, situation) VALUES
('2024-07-01', '2024-07-01', 150.00, 'Electricity bill', 'PAID'),
('2024-07-05', NULL, 200.00, 'Water bill', 'PENDING'),
('2024-07-10', '2024-07-11', 50.00, 'Internet bill', 'PAID'),
('2024-07-15', NULL, 300.00, 'Rent', 'PENDING'),
('2024-07-20', '2024-07-22', 120.00, 'Gas bill', 'PAID'),
('2024-07-25', NULL, 80.00, 'Mobile bill', 'PENDING'),
('2024-07-30', NULL, 100.00, 'Cable TV bill', 'PENDING'),
('2024-08-01', '2024-08-01', 150.00, 'Electricity bill', 'PAID'),
('2024-08-05', NULL, 200.00, 'Water bill', 'PENDING'),
('2024-08-10', '2024-08-12', 50.00, 'Internet bill', 'PAID'),
('2024-08-15', NULL, 300.00, 'Rent', 'PENDING'),
('2024-08-20', '2024-08-22', 120.00, 'Gas bill', 'PAID'),
('2024-08-25', NULL, 80.00, 'Mobile bill', 'PENDING'),
('2024-08-30', NULL, 100.00, 'Cable TV bill', 'PENDING'),
('2024-09-01', '2024-09-01', 150.00, 'Electricity bill', 'PAID'),
('2024-09-05', NULL, 200.00, 'Water bill', 'PENDING'),
('2024-09-10', '2024-09-11', 50.00, 'Internet bill', 'PAID'),
('2024-09-15', NULL, 300.00, 'Rent', 'PENDING'),
('2024-09-20', '2024-09-21', 120.00, 'Gas bill', 'PAID'),
('2024-09-25', NULL, 80.00, 'Mobile bill', 'PENDING');
