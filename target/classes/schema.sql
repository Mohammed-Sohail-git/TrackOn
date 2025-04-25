-- Create database
CREATE DATABASE IF NOT EXISTS trackon_db;
USE trackon_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('ADMIN', 'USER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Certificate Applications table
CREATE TABLE IF NOT EXISTS certificate_applications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    application_number VARCHAR(20) UNIQUE NOT NULL,
    user_id INT NOT NULL,
    certificate_type ENUM('CASTE', 'INCOME', 'MARRIAGE') NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    description TEXT,
    application_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    admin_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (admin_id) REFERENCES users(id)
);

-- Grievances table
CREATE TABLE IF NOT EXISTS grievances (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    application_id INT NOT NULL,
    subject VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED') NOT NULL DEFAULT 'OPEN',
    admin_response TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    admin_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (application_id) REFERENCES certificate_applications(id),
    FOREIGN KEY (admin_id) REFERENCES users(id)
);

-- Activity Logs table
CREATE TABLE IF NOT EXISTS activity_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    admin_id INT NOT NULL,
    application_id INT NOT NULL,
    action ENUM('ADD', 'UPDATE', 'DELETE', 'STATUS_CHANGE') NOT NULL,
    description TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES users(id),
    FOREIGN KEY (application_id) REFERENCES certificate_applications(id)
);

-- Insert default admin user
INSERT INTO users (username, password, email, role) 
VALUES ('admin', 'admin123', 'admin@trackon.com', 'ADMIN')
ON DUPLICATE KEY UPDATE id=id; 