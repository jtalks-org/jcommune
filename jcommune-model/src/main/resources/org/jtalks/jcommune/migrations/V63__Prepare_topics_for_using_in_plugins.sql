
CREATE TABLE `POST_COMMENT` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `UUID` VARCHAR(255) NOT NULL,
  `BODY` LONGTEXT NOT NULL,
  `CREATION_DATE` DATETIME NOT NULL,
  `AUTHOR_ID` BIGINT(20) NOT NULL,
  `POST_ID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY (`UUID`),
  CONSTRAINT `FC_COMMENT_POST` FOREIGN KEY (`POST_ID`) REFERENCES `POST` (`POST_ID`) ON DELETE CASCADE,
  CONSTRAINT `FC_COMMENT_AUTHOR` FOREIGN KEY (`AUTHOR_ID`) REFERENCES `USERS` (`ID`)
)ENGINE=InnoDb DEFAULT CHARSET='utf8' COLLATE='utf8_bin';

INSERT INTO `POST_COMMENT` (`UUID`, `BODY`, `CREATION_DATE`, `AUTHOR_ID`, `POST_ID`)
  SELECT `UUID`, `BODY`, `CREATION_DATE`, `AUTHOR_ID`,
    (SELECT `POST_ID` FROM `POST` p JOIN `TOPIC` t ON t.TOPIC_ID = p.TOPIC_ID WHERE t.CODE_REVIEW_ID=CRC.CODE_REVIEW_ID)
  FROM CODE_REVIEW_COMMENTS CRC;

CREATE TABLE `CUSTOM_TOPIC_PARAMETER` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `UUID` VARCHAR(255) NOT NULL,
  `NAME` VARCHAR(255) NOT NULL,
  `TYPE` VARCHAR(255) NOT NULL,
  `VALUE` VARCHAR(255) NOT NULL,
  `TOPIC_ID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY (`UUID`),
  UNIQUE KEY (`NAME`, `TOPIC_ID`),
  CONSTRAINT `FC_TOPIC_PARAM` FOREIGN KEY (`TOPIC_ID`) REFERENCES `TOPIC` (`TOPIC_ID`) ON DELETE CASCADE
)ENGINE=InnoDb DEFAULT CHARSET='utf8' COLLATE='utf8_bin';

CREATE TABLE `CUSTOM_COMMENT_PARAMETER` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `UUID` VARCHAR(255) NOT NULL,
  `NAME` VARCHAR(255) NOT NULL,
  `TYPE` VARCHAR(255) NOT NULL,
  `VALUE` VARCHAR(255) NOT NULL,
  `COMMENT_ID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY (`UUID`),
  UNIQUE KEY (`NAME`, `COMMENT_ID`),
  CONSTRAINT `FC_COMMENT_PARAM` FOREIGN KEY (`COMMENT_ID`) REFERENCES `POST_COMMENT` (`ID`) ON DELETE CASCADE
)ENGINE=InnoDb DEFAULT CHARSET='utf8' COLLATE='utf8_bin';

ALTER TABLE `POST` ADD `RATING` INT DEFAULT 0;

CREATE TABLE `TOPIC_TYPE` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `UUID` VARCHAR(255) NOT NULL,
  `NAME` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY (`UUID`),
  UNIQUE KEY (`NAME`)
)ENGINE=InnoDb DEFAULT CHARSET='utf8' COLLATE='utf8_bin';

INSERT INTO `TOPIC_TYPE` (`ID`, `UUID`, `NAME`) VALUES (1, (SELECT UUID() FROM dual), 'Discussion');
INSERT INTO `TOPIC_TYPE` (`ID`, `UUID`, `NAME`) VALUES (2, (SELECT UUID() FROM dual), 'Code review');

ALTER TABLE `TOPIC`
ADD `TYPE_ID` BIGINT(20),
ADD CONSTRAINT `FK_TOPIC_TOPIC_TYPE` FOREIGN KEY (`TYPE_ID`) REFERENCES `TOPIC_TYPE` (`ID`);


UPDATE `TOPIC` SET `TYPE_ID` = 1 WHERE CODE_REVIEW_ID IS NULL;
UPDATE `TOPIC` SET `TYPE_ID` = 2 WHERE CODE_REVIEW_ID IS NOT NULL;

ALTER TABLE `TOPIC`
MODIFY `TYPE_ID` BIGINT(20) NOT NULL;

INSERT INTO `CUSTOM_COMMENT_PARAMETER` (`UUID`, `NAME`, `TYPE`, `VALUE`, `COMMENT_ID`)
  SELECT (SELECT UUID() FROM dual), 'line_number', 'INT ', CAST(`LINE_NUMBER` AS CHAR(255)),
    (SELECT `POST_ID` FROM `POST` p JOIN `TOPIC` t ON t.TOPIC_ID = p.TOPIC_ID WHERE t.CODE_REVIEW_ID=CRC.CODE_REVIEW_ID)
  FROM `CODE_REVIEW_COMMENTS` CRC;