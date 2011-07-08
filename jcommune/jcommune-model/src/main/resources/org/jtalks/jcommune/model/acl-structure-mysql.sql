
SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `acl`
--
USE jtalks;
-- --------------------------------------------------------

--
-- Table structure for table `acl_sid`
--
CREATE TABLE IF NOT EXISTS `acl_sid` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `principal` tinyint(1) NOT NULL,
  `sid` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_acl_sid` (`sid`,`principal`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

-- --------------------------------------------------------

--
-- Table structure for table `acl_class`
--
CREATE TABLE IF NOT EXISTS `acl_class` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `class` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_acl_class` (`class`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

-- --------------------------------------------------------

--
-- Table structure for table `acl_entry`
--
CREATE TABLE IF NOT EXISTS `acl_entry` (
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
  KEY `fk_acl_entry_sid` (`sid`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=43 ;

-- --------------------------------------------------------

--
-- Table structure for table `acl_object_identity`
--
CREATE TABLE IF NOT EXISTS `acl_object_identity` (
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
  KEY `fk_acl_obj_owner` (`owner_sid`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;

-- --------------------------------------------------------

--
-- Constraints for dumped tables
--
 
--
-- Constraints for table `acl_entry`
--
ALTER TABLE `acl_entry`
  ADD CONSTRAINT `fk_acl_entry_obj_id` FOREIGN KEY (`acl_object_identity`) REFERENCES `acl_object_identity` (`id`),
  ADD CONSTRAINT `fk_acl_entry_sid` FOREIGN KEY (`sid`) REFERENCES `acl_sid` (`id`);

--
-- Constraints for table `acl_object_identity`
--
ALTER TABLE `acl_object_identity`
  ADD CONSTRAINT `fk_acl_obj_parent` FOREIGN KEY (`parent_object`) REFERENCES `acl_object_identity` (`id`),
  ADD CONSTRAINT `fk_acl_obj_class` FOREIGN KEY (`object_id_class`) REFERENCES `acl_class` (`id`),
  ADD CONSTRAINT `fk_acl_obj_owner` FOREIGN KEY (`owner_sid`) REFERENCES `acl_sid` (`id`);
