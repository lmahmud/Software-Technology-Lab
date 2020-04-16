USE crowdfundswt;

CREATE TABLE projects (
	id INT AUTO_INCREMENT NOT NULL,
	isTemp BOOLEAN NOT NULL,
	status VARCHAR(12),
	title VARCHAR(300) NOT NULL,
	description VARCHAR(1500),
	endDate DATE NOT NULL,
	fundingLimit DECIMAL(30,2) NOT NULL,
	psemail VARCHAR(100) NOT NULL,
	psname VARCHAR(100),
	pspayinfo VARCHAR(200),
	hash VARCHAR(300),
	PRIMARY KEY (id)
);


CREATE TABLE rewards (
	id INT AUTO_INCREMENT NOT NULL,
	reward VARCHAR(300) NOT NULL,
	amount DECIMAL(30,2) NOT NULL,
	project_id INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);



CREATE TABLE donations (
	id INT AUTO_INCREMENT NOT NULL,
	amount DECIMAL(30,2) NOT NULL,
	isTemp BOOLEAN NOT NULL,
	project_id INT NOT NULL,
	semail VARCHAR(100) NOT NULL,
	sname VARCHAR(100),
	spayinfo VARCHAR(200),
	reward_id INT,
	hash VARCHAR(300),
	PRIMARY KEY (id),
	FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
	FOREIGN KEY (reward_id) REFERENCES rewards(id)
);



