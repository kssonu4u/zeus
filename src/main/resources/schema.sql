CREATE TABLE if not exists build_details
(
id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
name varchar(255) NOT NULL,
path varchar(255) NOT NULL,
environment varchar(255) NOT NULL,
email varchar(255) NOT NULL,
comments text DEFAULT NULL,
`created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
`updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);