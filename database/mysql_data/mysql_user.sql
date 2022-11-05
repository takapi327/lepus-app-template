CREATE USER IF NOT EXISTS lepus@'%' identified by 'docker';
GRANT ALL PRIVILEGES ON *.* TO lepus;
