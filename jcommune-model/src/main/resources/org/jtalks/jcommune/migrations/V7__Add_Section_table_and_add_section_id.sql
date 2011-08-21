--
-- Table structure for table `SECTION`
--

DROP TABLE IF EXISTS `SECTION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SECTION` (
  `SECTION_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `UUID` varchar(255) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(255) COLLATE utf8_bin NOT NULL,
  `POSITION` bigint(20) DEFAULT NULL,
  `DESCRIPTION` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`SECTION_ID`),
  UNIQUE KEY `UUID` (`UUID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

ALTER TABLE BRANCH ADD `BRANCHES_INDEX` int(11) DEFAULT NULL;
ALTER TABLE BRANCH ADD `SECTION_ID` bigint(20) DEFAULT NULL;
ALTER TABLE BRANCH ADD KEY `FK_SECTION` (`SECTION_ID`);
--ALTER TABLE BRANCH ADD CONSTRAINT `FK_SECTION` FOREIGN KEY (`SECTION_ID`) REFERENCES `SECTION` (`SECTION_ID`);

-- setup sections
insert into SECTION (name,uuid,position) values ('First Section','notexistinguuid',1); 
set @bi=-1;
update BRANCH 
set branches_index = (@bi:=@bi+1), 
    section_id = (select section_id from SECTION s where s.uuid='notexistinguuid') 
where branches_index is null 
   or section_id is null ;
