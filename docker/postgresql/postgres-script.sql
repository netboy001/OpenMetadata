CREATE DATABASE umetadata_db;
CREATE DATABASE airflow_db;
CREATE USER umetadata_user WITH PASSWORD 'umetadata_password';
CREATE USER airflow_user WITH PASSWORD 'airflow_pass';
ALTER DATABASE umetadata_db OWNER TO umetadata_user;
ALTER DATABASE airflow_db OWNER TO airflow_user;
ALTER USER airflow_user SET search_path = public;
commit;