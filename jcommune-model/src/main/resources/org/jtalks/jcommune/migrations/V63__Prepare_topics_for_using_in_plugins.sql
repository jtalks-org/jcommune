
create table POST_COMMENT (
  ID bigint (20) not null auto_increment,
  UUID varchar(255) not null,
  BODY longtext not null,
  CREATION_DATE datetime not null,
  AUTHOR_ID bigint(20) not null,
  POST_ID bigint(20) not null,
  primary key (ID),
  constraint FK_COMMENT_POST foreign key (POST_ID) references POST (POST_ID) on delete cascade,
  constraint FK_COMMENT_AUTHOR foreign key (AUTHOR_ID) references USERS (ID)
)engine=InnoDb default charset='utf8' collate='utf8_bin';

insert into POST_COMMENT (UUID, BODY, CREATION_DATE, AUTHOR_ID, POST_ID)
  select UUID, BODY, CREATION_DATE, AUTHOR_ID,
    (select POST_ID from POST p join TOPIC t on t.TOPIC_ID = p.TOPIC_ID where t.CODE_REVIEW_ID=crc.CODE_REVIEW_ID)
  from CODE_REVIEW_COMMENTS crc;

create table TOPIC_ATTRIBUTE (
  ID bigint(20) not null auto_increment,
  UUID varchar(255) not null,
  NAME varchar(255) not null,
  TYPE varchar(255) not null,
  VALUE longtext not null,
  TOPIC_ID bigint(20) not null,
  primary key (ID),
  unique key (NAME, TOPIC_ID),
  constraint FK_TOPIC_PARAM foreign key (TOPIC_ID) references TOPIC (TOPIC_ID) on delete cascade
)engine=InnoDb default charset='utf8' collate='utf8_bin';

create table COMMENT_ATTRIBUTE (
  ID bigint(20) not null auto_increment,
  UUID varchar(255) not null,
  NAME varchar(255) not null,
  TYPE varchar(255) not null,
  VALUE longtext not null,
  COMMENT_ID bigint(20) not null,
  primary key (ID),
  unique key (NAME, COMMENT_ID),
  constraint FK_COMMENT_PARAM foreign key (COMMENT_ID) references POST_COMMENT (ID) on delete cascade
)engine=InnoDb default charset='utf8' collate='utf8_bin';

alter table POST add RATING int default 0;

alter table TOPIC add TYPE varchar(255);

update TOPIC set TYPE = 'Discussion' where CODE_REVIEW_ID is null;
update TOPIC set TYPE = 'Code review' where CODE_REVIEW_ID is not null;

alter table TOPIC
    modify TYPE varchar(255) not null;

insert into COMMENT_ATTRIBUTE (UUID, NAME, TYPE, VALUE, COMMENT_ID)
  select (select UUID() from dual), 'line_number', 'INT', CAST(LINE_NUMBER as char(255)),
    (select ID from POST_COMMENT pc join POST p on pc.POST_ID = p.POST_ID
      join TOPIC t on p.TOPIC_ID = t.TOPIC_ID where t.CODE_REVIEW_ID = crc.CODE_REVIEW_ID and crc.UUID = pc.UUID)
  from CODE_REVIEW_COMMENTS crc;