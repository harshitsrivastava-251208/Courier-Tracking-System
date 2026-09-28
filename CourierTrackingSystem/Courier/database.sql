-- Run this in MySQL Workbench before running the project

CREATE DATABASE IF NOT EXISTS courierdb;

USE courierdb;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100),
    phone VARCHAR(15),
    password VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS couriers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tracking_no VARCHAR(30) UNIQUE NOT NULL,
    sender_name VARCHAR(100),
    sender_phone VARCHAR(15),
    sender_address VARCHAR(200),
    receiver_name VARCHAR(100),
    receiver_phone VARCHAR(15),
    receiver_address VARCHAR(200),
    weight DOUBLE DEFAULT 0,
    type VARCHAR(50),
    priority VARCHAR(20) DEFAULT 'Standard',
    description VARCHAR(200),
    price DOUBLE DEFAULT 0,
    status VARCHAR(30) DEFAULT 'Booked',
    booked_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booked_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS tracking_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tracking_no VARCHAR(30),
    status VARCHAR(30),
    location VARCHAR(100),
    note VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Default admin user
INSERT INTO users (username, email, phone, password)
VALUES ('admin', 'admin@swiftcourier.com', '9999999999', 'admin123');

-- Sample couriers
INSERT INTO couriers (tracking_no, sender_name, sender_phone, sender_address, receiver_name, receiver_phone, receiver_address, weight, type, priority, price, status, booked_by)
VALUES
('SWC123456', 'Himanshu Pal', '9876543210', 'Lucknow, UP', 'Rahul Kumar', '9876543211', 'Delhi, DL', 2.5, 'Parcel', 'Express', 187.50, 'In Transit', 1),
('SWC789012', 'Amit Singh', '9876543212', 'Mumbai, MH', 'Priya Sharma', '9876543213', 'Pune, MH', 0.5, 'Document', 'Standard', 25.00, 'Delivered', 1);

-- Sample tracking logs
INSERT INTO tracking_logs (tracking_no, status, location, note) VALUES
('SWC123456', 'Booked', 'Lucknow Center', 'Courier booked'),
('SWC123456', 'Picked Up', 'Lucknow HUB', 'Picked up from sender'),
('SWC123456', 'In Transit', 'Agra Sorting Center', 'In transit to Delhi');
