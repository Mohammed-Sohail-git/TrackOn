USE trackon_db;

-- Check if admin_id column exists in certificate_applications table
SET @dbname = 'trackon_db';
SET @tablename = 'certificate_applications';
SET @columnname = 'admin_id';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      TABLE_SCHEMA = @dbname
      AND TABLE_NAME = @tablename
      AND COLUMN_NAME = @columnname
  ) > 0,
  "SELECT 1",
  "ALTER TABLE certificate_applications ADD COLUMN admin_id INT, ADD FOREIGN KEY (admin_id) REFERENCES users(id)"
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists; 