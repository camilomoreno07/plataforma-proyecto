CREATE DATABASE universidad;
CREATE TABLE `universidad`.`user` (
  `id` INT auto_increment NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `lastname` VARCHAR(45) NULL,
  `firstname` VARCHAR(45) NULL,
  `password` VARCHAR(100) NULL,
    `role` ENUM ('ADMIN', 'TEACHER', 'STUDENT') NOT NULL,
	CONSTRAINT NewTable_PK PRIMARY KEY (id),
	CONSTRAINT NewTable_UNIQUE UNIQUE KEY (username)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;