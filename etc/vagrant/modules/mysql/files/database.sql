#####################################################################################
# Create Permissions

GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON play.* TO 'josrom'@'localhost' IDENTIFIED BY 'josevte1';
FLUSH PRIVILEGES;

#####################################################################################
# Create Database

DROP DATABASE IF EXISTS play;
CREATE DATABASE play;
USE play;

#####################################################################################

