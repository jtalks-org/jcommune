-- COLLATE DEFAULT means that we want to set TABLE collation the same as database.
ALTER TABLE `acl_sid` CHAR SET 'UTF8' COLLATE DEFAULT;
ALTER TABLE `acl_sid` MODIFY `sid` VARCHAR(100) not null;
ALTER TABLE `acl_class` CHAR SET 'UTF8' COLLATE DEFAULT;
ALTER TABLE `acl_class` MODIFY `class` VARCHAR(255)
