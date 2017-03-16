create table `SPAM_RULES` (
  `ID` bigint(20) not null auto_increment,
  `UUID` varchar(255) not null,
  `REGEX` varchar(255) not null,
  `DESCRIPTION` varchar(255) null,
  `ENABLED` bit not null default 0,
  primary key (`ID`));
