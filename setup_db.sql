CREATE DATABASE IF NOT EXISTS trackon_db;
USE trackon_db;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS certificate_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    base_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS certificate_applications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    application_number VARCHAR(50) UNIQUE NOT NULL,
    user_id INT NOT NULL,
    certificate_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    description TEXT,
    amount DECIMAL(10,2) NOT NULL,
    payment_status ENUM('PENDING', 'PAID', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    payment_date TIMESTAMP NULL,
    application_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    admin_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (admin_id) REFERENCES users(id),
    FOREIGN KEY (certificate_type) REFERENCES certificate_types(name)
);

CREATE TABLE IF NOT EXISTS grievances (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    application_id INT NOT NULL,
    subject VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    admin_response TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    admin_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (application_id) REFERENCES certificate_applications(id),
    FOREIGN KEY (admin_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS activity_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    admin_id INT NOT NULL,
    application_id INT NOT NULL,
    action ENUM('ADD', 'UPDATE', 'DELETE', 'STATUS_CHANGE', 'PAYMENT_UPDATE') NOT NULL,
    description TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES users(id),
    FOREIGN KEY (application_id) REFERENCES certificate_applications(id)
);

-- Insert default admin user
INSERT INTO users (username, password, email, role) 
VALUES ('admin', 'admin123', 'admin@trackon.com', 'ADMIN')
ON DUPLICATE KEY UPDATE id=id;

-- Insert certificate types
INSERT INTO certificate_types (name, description, base_amount) VALUES
('CASTE', 'Caste certificate for verification of social status', 100.00),
('INCOME', 'Income certificate for proof of annual income', 150.00),
('MARRIAGE', 'Marriage registration certificate', 500.00),
('BIRTH', 'Birth registration certificate', 200.00),
('DEATH', 'Death registration certificate', 200.00),
('RESIDENCE', 'Proof of residence certificate', 150.00),
('NATIONALITY', 'Nationality/Citizenship certificate', 1000.00),
('EMPLOYMENT', 'Employment status certificate', 250.00),
('PROPERTY', 'Property ownership certificate', 500.00),
('DISABILITY', 'Disability certificate for benefits', 0.00)
ON DUPLICATE KEY UPDATE 
    description = VALUES(description),
    base_amount = VALUES(base_amount); 