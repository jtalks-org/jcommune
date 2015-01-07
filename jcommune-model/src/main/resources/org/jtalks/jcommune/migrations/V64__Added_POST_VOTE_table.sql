create table POST_VOTE (
  ID bigint(20) not null auto_increment,
  UUID varchar(255) not null,
  USER_ID bigint(20) not null,
  POST_ID bigint(20) not null,
  VOTE_DATE datetime not null,
  IS_VOTED_UP tinyint(1) not null,
  primary key(ID),
  unique key (USER_ID, POST_ID),
  constraint FK_VOTED_USER foreign key (USER_ID) references USERS (ID),
  constraint FK_VOTE_POST foreign key (POST_ID) references POST (POST_ID) on delete cascade
)engine=InnoDb default charset='utf8' collate='utf8_bin';