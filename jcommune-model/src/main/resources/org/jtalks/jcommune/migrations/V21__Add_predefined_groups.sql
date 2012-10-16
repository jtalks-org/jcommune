INSERT INTO GROUPS (`UUID`, `NAME`, `DESCRIPTION`)
  SELECT (SELECT UUID() FROM dual),'Administrators', 'Administrators group.' FROM dual
    WHERE NOT EXISTS (SELECT gr.GROUP_ID FROM GROUPS gr WHERE gr.NAME='Administrators');

INSERT INTO GROUPS (`UUID`, `NAME`, `DESCRIPTION`)
  SELECT (SELECT UUID() FROM dual),'Registered Users', 'The group for all registered users.' FROM dual
    WHERE NOT EXISTS (SELECT gr.GROUP_ID FROM GROUPS gr WHERE gr.NAME='Registered Users');

INSERT INTO GROUPS (`UUID`, `NAME`, `DESCRIPTION`)
  SELECT (SELECT UUID() FROM dual),'Banned Users', 'The group for banned users.' FROM dual
    WHERE NOT EXISTS (SELECT gr.GROUP_ID FROM GROUPS gr WHERE gr.NAME='Banned Users');

INSERT INTO `acl_sid` VALUES (5,1,'user:anonymousUser');
INSERT INTO `acl_sid` VALUES (1,0,concat('usergroup:',(select GROUP_ID from GROUPS where `NAME`='Registered Users')));
INSERT INTO `acl_sid` VALUES (3,0,concat('usergroup:',(select GROUP_ID from GROUPS where `NAME`='Banned Users')));
INSERT INTO `acl_sid` VALUES (2,0,concat('usergroup:',(select GROUP_ID from GROUPS where `NAME`='Administrators')));
