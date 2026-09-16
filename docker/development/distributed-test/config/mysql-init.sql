-- MySQL initialization script for distributed test environment

-- Create the UMetadata database
CREATE DATABASE IF NOT EXISTS umetadata_db;

-- Create the UMetadata user
CREATE USER IF NOT EXISTS 'umetadata_user'@'%' IDENTIFIED BY 'umetadata_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON umetadata_db.* TO 'umetadata_user'@'%';
GRANT ALL PRIVILEGES ON *.* TO 'umetadata_user'@'%';

FLUSH PRIVILEGES;
