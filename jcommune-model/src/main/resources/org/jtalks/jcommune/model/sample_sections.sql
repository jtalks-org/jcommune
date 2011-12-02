insert into section (section_id, uuid, name, position, description) values (1,'1','Sample section', 1, 'Some description here');
insert into section (section_id, uuid, name, position, description) values (2,'2','Another section', 2, 'Whatever else');
insert into branch (uuid, name, description, branches_index, section_id) values('3', 'A cool branch', 'More information', 0, 1);
insert into branch (uuid, name, description, branches_index, section_id) values('4', 'The second branch', 'More information', 1, 1);
insert into branch (uuid, name, description, branches_index, section_id) values('5', 'One more branch', 'More information', 0, 2);
insert into branch (uuid, name, description, branches_index, section_id) values('6', 'The last, but not least', 'More information', 1, 2);