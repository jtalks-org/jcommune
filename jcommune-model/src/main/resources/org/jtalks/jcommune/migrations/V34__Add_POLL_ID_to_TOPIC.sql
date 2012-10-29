alter table TOPIC add(POLL_ID bigint(10));
alter table TOPIC add constraint TOPIC_POLL_FK foreign key(POLL_ID) references POLLS(POLL_ID);
update TOPIC, POLLS set TOPIC.POLL_ID=POLLS.POLL_ID where TOPIC.TOPIC_ID=POLLS.POLL_ID;