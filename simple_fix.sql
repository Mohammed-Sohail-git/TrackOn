USE trackon_db;

-- Add the admin_id column
ALTER TABLE certificate_applications 
ADD COLUMN admin_id INT;

-- Add the foreign key constraint
ALTER TABLE certificate_applications 
ADD FOREIGN KEY (admin_id) REFERENCES users(id); 