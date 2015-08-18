create table POST_DRAFT (
  ID bigint(20) not null auto_increment,
  UUID varchar(255) not null,
  TOPIC_ID bigint(20) not null,
  USER_ID bigint(20) not null,
  CONTENT longtext not null,
  LAST_SAVED datetime not null,
  primary key(ID),
  unique key(TOPIC_ID, USER_ID),
  constraint FK_TOPIC_POST_DRAFT foreign key (TOPIC_ID) references TOPIC (TOPIC_ID) on delete cascade,
  constraint FK_USER_POST_DRAFT foreign key (USER_ID) references USERS (ID)
)engine=InnoDb default charset='utf8' collate='utf8_bin';

insert into POST_DRAFT (UUID, TOPIC_ID, USER_ID, CONTENT, LAST_SAVED)
  SELECT (SELECT UUID() FROM dual), TOPIC_ID, USER_CREATED, POST_CONTENT, POST_DATE from POST where STATE='DRAFT';

alter table POST drop column STATE;