CREATE TABLE universidad.NewTable (
	id INT auto_increment NOT NULL,
	username varchar(100) NOT NULL,
	lastname varchar(100) NULL,
	firstname varchar(100) NULL,
	country varchar(100) NULL,
	password varchar(100) NULL,
	`role` ENUM NULL,
	CONSTRAINT NewTable_PK PRIMARY KEY (id),
	CONSTRAINT NewTable_UNIQUE UNIQUE KEY (username)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;
