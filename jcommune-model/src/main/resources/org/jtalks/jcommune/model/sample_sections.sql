insert into SECTIONS (SECTION_ID, UUID, NAME, POSITION_, DESCRIPTION) values (1,'1','Sample section', 1, 'Some description here');
insert into SECTIONS (SECTION_ID, UUID, NAME, POSITION_, DESCRIPTION) values (2,'2','Another section', 2, 'Whatever else');
insert into BRANCHES (UUID, NAME, DESCRIPTION, POSITION, SECTION_ID) values('3', 'A cool branch', 'More information', 0, 1);
insert into BRANCHES (UUID, NAME, DESCRIPTION, POSITION, SECTION_ID) values('4', 'The second branch', 'More information', 1, 1);
insert into BRANCHES (UUID, NAME, DESCRIPTION, POSITION, SECTION_ID) values('5', 'One more branch', 'More information', 0, 2);
insert into BRANCHES (UUID, NAME, DESCRIPTION, POSITION, SECTION_ID) values('6', 'The last, but not least', 'More information', 1, 2);