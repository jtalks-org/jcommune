-- MySQL dump 10.13  Distrib 5.1.54, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: jtalks
-- ------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `BRANCH`
--

DROP TABLE IF EXISTS `BRANCH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `BRANCH` (
  `BRANCH_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `UUID` varchar(255) COLLATE utf8_bin NOT NULL,
  `NAME` varchar(255) COLLATE utf8_bin NOT NULL,
  `DESCRIPTION` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`BRANCH_ID`),
  UNIQUE KEY `UUID` (`UUID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `POST`
--

DROP TABLE IF EXISTS `POST`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `POST` (
  `POST_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `UUID` varchar(255) COLLATE utf8_bin NOT NULL,
  `POST_DATE` datetime NOT NULL,
  `POST_CONTENT` varchar(255) COLLATE utf8_bin NOT NULL,
  `USER_CREATED` bigint(20) NOT NULL,
  `TOPIC_ID` bigint(20) DEFAULT NULL,
  `LIST_INDEX` int(11) DEFAULT NULL,
  PRIMARY KEY (`POST_ID`),
  UNIQUE KEY `UUID` (`UUID`),
  KEY `FK_USER` (`USER_CREATED`),
  KEY `FK_TOPIC` (`TOPIC_ID`),
  CONSTRAINT `FK_TOPIC` FOREIGN KEY (`TOPIC_ID`) REFERENCES `TOPIC` (`TOPIC_ID`),
  CONSTRAINT `FK_USER` FOREIGN KEY (`USER_CREATED`) REFERENCES `USER` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PRIVATE_MESSAGE`
--

DROP TABLE IF EXISTS `PRIVATE_MESSAGE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PRIVATE_MESSAGE` (
  `PM_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `UUID` varchar(255) COLLATE utf8_bin NOT NULL,
  `USER_FROM` bigint(20) NOT NULL,
  `USER_TO` bigint(20) NOT NULL,
  `CREATION_DATE` datetime NOT NULL,
  `TITLE` varchar(255) COLLATE utf8_bin NOT NULL,
  `BODY` varchar(255) COLLATE utf8_bin NOT NULL,
  `STATUS` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`PM_ID`),
  UNIQUE KEY `UUID` (`UUID`),
  KEY `FK_USER_TO` (`USER_TO`),
  KEY `FK_USER_FROM` (`USER_FROM`),
  CONSTRAINT `FK_USER_FROM` FOREIGN KEY (`USER_FROM`) REFERENCES `USER` (`ID`),
  CONSTRAINT `FK_USER_TO` FOREIGN KEY (`USER_TO`) REFERENCES `USER` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TOPIC`
--

DROP TABLE IF EXISTS `TOPIC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TOPIC` (
  `TOPIC_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `UUID` varchar(255) COLLATE utf8_bin NOT NULL,
  `TITLE` varchar(255) COLLATE utf8_bin NOT NULL,
  `CREATION_DATE` datetime NOT NULL,
  `FIRST_POST` bigint(20) DEFAULT NULL,
  `LAST_POST` bigint(20) DEFAULT NULL,
  `TOPIC_STARTER` bigint(20) NOT NULL,
  `BRANCH_ID` bigint(20) DEFAULT NULL,
  `TOPICS_INDEX` int(11) DEFAULT NULL,
  PRIMARY KEY (`TOPIC_ID`),
  UNIQUE KEY `UUID` (`UUID`),
  UNIQUE KEY `FIRST_POST` (`FIRST_POST`),
  UNIQUE KEY `LAST_POST` (`LAST_POST`),
  KEY `FK_LAST_POST` (`LAST_POST`),
  KEY `FK_TOPIC_STARTER` (`TOPIC_STARTER`),
  KEY `FK_FIRST_POST` (`FIRST_POST`),
  KEY `FK_BRANCH` (`BRANCH_ID`),
  CONSTRAINT `FK_BRANCH` FOREIGN KEY (`BRANCH_ID`) REFERENCES `BRANCH` (`BRANCH_ID`),
  CONSTRAINT `FK_FIRST_POST` FOREIGN KEY (`FIRST_POST`) REFERENCES `POST` (`POST_ID`),
  CONSTRAINT `FK_LAST_POST` FOREIGN KEY (`LAST_POST`) REFERENCES `POST` (`POST_ID`),
  CONSTRAINT `FK_TOPIC_STARTER` FOREIGN KEY (`TOPIC_STARTER`) REFERENCES `USER` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `USER`
--

DROP TABLE IF EXISTS `USER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `USER` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `UUID` varchar(255) COLLATE utf8_bin NOT NULL,
  `FIRST_NAME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `LAST_NAME` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `USERNAME` varchar(255) COLLATE utf8_bin NOT NULL,
  `ENCODED_USERNAME` varchar(255) COLLATE utf8_bin NOT NULL,
  `EMAIL` varchar(255) COLLATE utf8_bin NOT NULL,
  `PASSWORD` varchar(255) COLLATE utf8_bin NOT NULL,
  `ROLE` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `LAST_LOGIN` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UUID` (`UUID`),
  UNIQUE KEY `USERNAME` (`USERNAME`),
  UNIQUE KEY `EMAIL` (`EMAIL`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `acl_class`
--

DROP TABLE IF EXISTS `acl_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `acl_class` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `class` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_acl_class` (`class`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `acl_entry`
--

DROP TABLE IF EXISTS `acl_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `acl_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `acl_object_identity` bigint(20) NOT NULL,
  `ace_order` int(11) NOT NULL,
  `sid` bigint(20) NOT NULL,
  `mask` int(11) NOT NULL,
  `granting` tinyint(1) NOT NULL,
  `audit_success` tinyint(1) NOT NULL,
  `audit_failure` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_acl_entry` (`acl_object_identity`,`ace_order`),
  KEY `fk_acl_entry_obj_id` (`acl_object_identity`),
  KEY `fk_acl_entry_sid` (`sid`),
  CONSTRAINT `fk_acl_entry_obj_id` FOREIGN KEY (`acl_object_identity`) REFERENCES `acl_object_identity` (`id`),
  CONSTRAINT `fk_acl_entry_sid` FOREIGN KEY (`sid`) REFERENCES `acl_sid` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `acl_object_identity`
--

DROP TABLE IF EXISTS `acl_object_identity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `acl_object_identity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `object_id_class` bigint(20) NOT NULL,
  `object_id_identity` bigint(20) NOT NULL,
  `parent_object` bigint(20) DEFAULT NULL,
  `owner_sid` bigint(20) DEFAULT NULL,
  `entries_inheriting` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_acl_objid` (`object_id_class`,`object_id_identity`),
  KEY `fk_acl_obj_parent` (`parent_object`),
  KEY `fk_acl_obj_class` (`object_id_class`),
  KEY `fk_acl_obj_owner` (`owner_sid`),
  CONSTRAINT `fk_acl_obj_parent` FOREIGN KEY (`parent_object`) REFERENCES `acl_object_identity` (`id`),
  CONSTRAINT `fk_acl_obj_class` FOREIGN KEY (`object_id_class`) REFERENCES `acl_class` (`id`),
  CONSTRAINT `fk_acl_obj_owner` FOREIGN KEY (`owner_sid`) REFERENCES `acl_sid` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `acl_sid`
--

DROP TABLE IF EXISTS `acl_sid`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `acl_sid` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `principal` tinyint(1) NOT NULL,
  `sid` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_acl_sid` (`sid`,`principal`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-07-20 22:44:08