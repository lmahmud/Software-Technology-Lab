-- projects
INSERT INTO projects(isTemp, status, title, description, endDate, fundingLimit,
	psemail, psname, pspayinfo)
VALUES (0, 'Successful', 'Ubuntu Touch', 'Phone that runs ubuntu linux distribution', 
	'2020-01-10', 10000.0, 'alan@ubuntu.org', 'Alan Turing', '12345678')
;

INSERT INTO projects(isTemp, status, title, description, endDate, fundingLimit,
	psemail, psname, pspayinfo)
VALUES (0, 'Open', 'Ubuntu Touch Pro', 'Next gen version of Ubuntu touch', 
	'2020-03-15', 20000.0, 'alan@ubuntu.org', 'Alan Turing', '12345678')
;

INSERT INTO projects(isTemp, status, title, description, endDate, fundingLimit,
	psemail, psname, pspayinfo)
VALUES (0, 'Open', 'Cooking Robot', 'Robot that can cook for you', 
	'2020-01-27', 2000.0, 'james@cook.org', 'James Randy', '12345678')
;

INSERT INTO projects(isTemp, status, title, description, endDate, fundingLimit,
	psemail, psname, pspayinfo)
VALUES (0, 'Failed', 'Art Exhibition', 'Art exhibition', 
	'2020-01-02', 5000.0, 'judy@art.org', 'Judy Smith', '12345678')
;

INSERT INTO projects(isTemp, status, title, description, endDate, fundingLimit,
	psemail, psname, pspayinfo)
VALUES (1, 'Open', 'Educational Book', 'Educational book for kids', 
	'2020-03-01', 1000.50, 'writer@art.org', 'Ian Writer', '12345678')
;

-- donations
INSERT INTO donations(amount, isTemp, project_id, semail, sname, spayinfo)
VALUES (11500.0, 0, 1, 'richguy@email.com', 'Rich Guy', '12345678');

INSERT INTO donations(amount, isTemp, project_id, semail, sname, spayinfo)
VALUES (500.45, 0, 1, 'otherguy@email.com', 'Other Guy', '12345678');

INSERT INTO donations(amount, isTemp, project_id, semail, sname, spayinfo)
VALUES (60.0, 0, 3, 'alex@email.com', 'Alex Key', '12345678');


