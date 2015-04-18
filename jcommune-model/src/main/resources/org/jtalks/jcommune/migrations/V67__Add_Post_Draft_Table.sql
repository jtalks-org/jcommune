create table POST_DRAFT (
  ID bigint(20) not null auto_increment,
  UUID varchar(255) not null,
  CONTENT longtext not null,
  MODIFICATION_DATE datetime not null,
  USER_ID bigint(20) not null,
  TOPIC_ID bigint(20) not null,
  primary key(ID),
  unique key (USER_ID, TOPIC_ID),
  constraint FK_USER_POST_DRAFT foreign key (USER_ID) references USERS (ID),
  constraint FK_TOPIC_POST_DRAFT foreign key (TOPIC_ID) references TOPIC (TOPIC_ID) on delete cascade
)engine=InnoDb default charset='utf8' collate='utf8_bin';