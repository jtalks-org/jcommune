INSERT INTO `simple_pages` (`UUID`, `NAME`, `PATH_NAME`, `CONTENT`) VALUES ('unique', 'Title', 'for_newbies', 'Nothing here');
INSERT INTO `acl_class` (`class`) VALUES ('SIMPLE_PAGE');

INSERT INTO `acl_object_identity` (`object_id_class`, `object_id_identity`, `owner_sid`, `entries_inheriting`)
VALUES ((SELECT MAX(`id`) FROM `acl_class`), (SELECT MAX(`PAGE_ID`) FROM `simple_pages`), 2, 1);

INSERT INTO `acl_entry` (`acl_object_identity`, `ace_order`, `sid`, `mask`, `granting`, `audit_success`, `audit_failure`)
VALUES ((SELECT MAX(`id`) FROM `acl_object_identity`), 0, 2, 4, 1, 0, 0);