INSERT INTO GROUPS (`UUID`, `NAME`, `DESCRIPTION`)
  SELECT (SELECT UUID() FROM dual),'Administrators', 'Administrators group.' FROM dual
    WHERE NOT EXISTS (SELECT gr.GROUP_ID FROM GROUPS gr WHERE gr.NAME='Administrators');

INSERT INTO GROUPS (`UUID`, `NAME`, `DESCRIPTION`)
  SELECT (SELECT UUID() FROM dual),'Registered Users', 'The group for all registered users.' FROM dual
    WHERE NOT EXISTS (SELECT gr.GROUP_ID FROM GROUPS gr WHERE gr.NAME='Registered Users');

INSERT INTO GROUPS (`UUID`, `NAME`, `DESCRIPTION`)
  SELECT (SELECT UUID() FROM dual),'Banned Users', 'The group for banned users.' FROM dual
    WHERE NOT EXISTS (SELECT gr.GROUP_ID FROM GROUPS gr WHERE gr.NAME='Banned Users');

SET @admin_group_sid := concat('usergroup:',(select GROUP_ID from GROUPS where `NAME`='Administrators'));
SET @registered_group_sid := concat('usergroup:',(select GROUP_ID from GROUPS where `NAME`='Registered Users'));
SET @banned_group_sid := concat('usergroup:',(select GROUP_ID from GROUPS where `NAME`='Banned Users'));

INSERT INTO `acl_sid`(`principal`,`sid`) SELECT 1, 'user:anonymousUser' FROM dual
  WHERE NOT EXISTS (SELECT * FROM `acl_sid` WHERE sid='user:anonymousUser');
INSERT INTO `acl_sid`(`principal`,`sid`) SELECT 0, @admin_group_sid FROM dual
  WHERE NOT EXISTS (SELECT * FROM `acl_sid` WHERE `sid`=@admin_group_sid);
INSERT INTO `acl_sid`(`principal`,`sid`) SELECT 0, @registered_group_sid FROM dual
  WHERE NOT EXISTS (SELECT * FROM `acl_sid` WHERE `sid`=@registered_group_sid);
INSERT INTO `acl_sid`(`principal`,`sid`) SELECT 0, @banned_group_sid FROM dual
  WHERE NOT EXISTS (SELECT * FROM `acl_sid` WHERE `sid`=@banned_group_sid);