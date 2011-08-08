alter table TOPIC drop foreign key FK_LAST_POST;
alter table TOPIC drop column LAST_POST;
alter table TOPIC add MODIFICATION_DATE datetime;
update TOPIC set MODIFICATION_DATE=CREATION_DATE;