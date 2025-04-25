USE trackon_db;

-- Add admin_id column to certificate_applications if it doesn't exist
ALTER TABLE certificate_applications 
ADD COLUMN IF NOT EXISTS admin_id INT,
ADD FOREIGN KEY IF NOT EXISTS (admin_id) REFERENCES users(id); 