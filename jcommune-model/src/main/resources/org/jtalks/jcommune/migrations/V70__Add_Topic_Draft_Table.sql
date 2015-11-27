create table TOPIC_DRAFT (
  ID bigint(20) not null auto_increment,
  UUID varchar(255) not null,
  TOPIC_STARTER_ID bigint(20) not null,
  TITLE varchar(255) default null,
  CONTENT longtext default null,
  POLL_TITLE varchar(255) default null,
  POLL_ITEMS_VALUE longtext default null,
  LAST_SAVED datetime not null,
  primary key(ID),
  unique key(UUID),
  unique key(TOPIC_STARTER_ID),
  constraint FK_USER_TOPIC_DRAFT foreign key (TOPIC_STARTER_ID) references USERS (ID)
) engine=InnoDb default charset='utf8' collate='utf8_bin';