INSERT INTO SECTIONS (SECTION_ID, UUID, `NAME`, DESCRIPTION, POSITION, COMPONENT_ID) VALUES (1,(SELECT UUID() FROM dual),'Sample section', 'Some description here', 1, 1);
INSERT INTO SECTIONS (SECTION_ID, UUID, `NAME`, DESCRIPTION, POSITION, COMPONENT_ID) VALUES (2,(SELECT UUID() FROM dual),'Another section', 'Whatever else', 2,  1);

INSERT INTO GROUPS (GROUP_ID, UUID, `NAME`, DESCRIPTION) VALUES(5, (SELECT UUID() FROM dual), 'Moderator group', 'Moderator group');

INSERT INTO BRANCHES (BRANCH_ID, UUID, `NAME`, DESCRIPTION, POSITION, SECTION_ID, MODERATORS_GROUP_ID) VALUES(1, (SELECT UUID() FROM dual), 'A cool branch', 'More information', 0, 1, 1);
INSERT INTO BRANCHES (BRANCH_ID, UUID, `NAME`, DESCRIPTION, POSITION, SECTION_ID, MODERATORS_GROUP_ID) VALUES(2, (SELECT UUID() FROM dual), 'The second branch', 'More information', 1, 1 ,1);
INSERT INTO BRANCHES (BRANCH_ID, UUID, `NAME`, DESCRIPTION, POSITION, SECTION_ID, MODERATORS_GROUP_ID) VALUES(3, (SELECT UUID() FROM dual), 'One more branch', 'More information', 0, 2 ,1);
INSERT INTO BRANCHES (BRANCH_ID, UUID, `NAME`, DESCRIPTION, POSITION, SECTION_ID, MODERATORS_GROUP_ID) VALUES(4, (SELECT UUID() FROM dual), 'The last, but not least', 'More information', 1, 2 ,1);

-- Creates a default user with default/default credentials to be able to log in without manual registration
INSERT IGNORE INTO USERS (UUID, FIRST_NAME, LAST_NAME, USERNAME, ENCODED_USERNAME, EMAIL, PASSWORD, ROLE, SALT, REGISTRATION_DATE)
  VALUES((SELECT UUID() FROM dual), 'default', 'default', 'default', 'default', 'default@jtalks.org', MD5('default'), 'USER_ROLE', '', NOW());
INSERT IGNORE INTO JC_USER_DETAILS (USER_ID, REGISTRATION_DATE, POST_COUNT, ENABLED)
  select ID, NOW(), 0, true from USERS where USERNAME = 'default';
-- Default user is then being added to Registered Users group and to the Moderator group
INSERT IGNORE INTO GROUP_USER_REF select (select GROUP_ID from GROUPS where `NAME`='Moderator group'), ID from USERS where USERNAME = 'default';
INSERT IGNORE INTO GROUP_USER_REF select (select GROUP_ID from GROUPS where `NAME`='Administrators'), ID from USERS where USERNAME = 'default';
INSERT IGNORE INTO GROUP_USER_REF select (select GROUP_ID from GROUPS where `NAME`='Registered Users'), ID from USERS where USERNAME = 'default';

INSERT INTO `acl_class` VALUES (1,'BRANCH');
INSERT INTO `acl_class` VALUES (2,'GROUP');

INSERT INTO `acl_sid` VALUES (1,0,concat('usergroup:',(select GROUP_ID from GROUPS where `NAME`='Registered Users')));
INSERT INTO `acl_sid` VALUES (3,0,concat('usergroup:',(select GROUP_ID from GROUPS where `NAME`='Banned Users')));
INSERT INTO `acl_sid` VALUES (2,0,concat('usergroup:',(select GROUP_ID from GROUPS where `NAME`='Administrators')));
INSERT INTO `acl_sid` VALUES (4,0,concat('usergroup:',(select GROUP_ID from GROUPS where `NAME`='Moderator group')));
INSERT INTO `acl_sid` VALUES (5,1,'user:anonymousUser');

INSERT INTO `acl_object_identity` VALUES (1,1,1,NULL,1,1);
INSERT INTO `acl_object_identity` VALUES (2,1,2,NULL,1,1);
INSERT INTO `acl_object_identity` VALUES (3,1,3,NULL,1,1);
INSERT INTO `acl_object_identity` VALUES (4,1,4,NULL,1,1);
INSERT INTO `acl_object_identity` VALUES (5,2,(select GROUP_ID from GROUPS where `NAME`='Registered Users'),NULL,1,1);

INSERT INTO `acl_entry` VALUES (1,1,1,1,12,1,0,0);
INSERT INTO `acl_entry` VALUES (2,1,2,1,6,1,0,0);
INSERT INTO `acl_entry` VALUES (4,1,4,1,7,1,0,0);
INSERT INTO `acl_entry` VALUES (5,1,5,2,12,1,0,0);
INSERT INTO `acl_entry` VALUES (6,1,6,2,6,1,0,0);
INSERT INTO `acl_entry` VALUES (8,1,8,2,7,1,0,0);
INSERT INTO `acl_entry` VALUES (9,1,9,2,11,1,0,0);
INSERT INTO `acl_entry` VALUES (10,1,10,2,13,1,0,0);
INSERT INTO `acl_entry` VALUES (12,1,12,2,8,1,0,0);
INSERT INTO `acl_entry` VALUES (14,1,14,3,12,0,0,0);
INSERT INTO `acl_entry` VALUES (15,1,15,3,6,0,0,0);
INSERT INTO `acl_entry` VALUES (17,1,17,3,7,0,0,0);
INSERT INTO `acl_entry` VALUES (18,1,18,3,11,0,0,0);
INSERT INTO `acl_entry` VALUES (19,1,19,3,13,0,0,0);
INSERT INTO `acl_entry` VALUES (21,1,21,3,8,0,0,0);
INSERT INTO `acl_entry` VALUES (23,2,1,1,12,1,0,0);
INSERT INTO `acl_entry` VALUES (24,2,2,1,6,1,0,0);
INSERT INTO `acl_entry` VALUES (26,2,4,1,7,1,0,0);
INSERT INTO `acl_entry` VALUES (27,2,5,2,12,1,0,0);
INSERT INTO `acl_entry` VALUES (28,2,6,2,6,1,0,0);
INSERT INTO `acl_entry` VALUES (30,2,8,2,7,1,0,0);
INSERT INTO `acl_entry` VALUES (31,2,9,2,11,1,0,0);
INSERT INTO `acl_entry` VALUES (32,2,10,2,13,1,0,0);
INSERT INTO `acl_entry` VALUES (34,2,12,2,8,1,0,0);
INSERT INTO `acl_entry` VALUES (36,2,14,3,12,0,0,0);
INSERT INTO `acl_entry` VALUES (37,2,15,3,6,0,0,0);
INSERT INTO `acl_entry` VALUES (39,2,17,3,7,0,0,0);
INSERT INTO `acl_entry` VALUES (40,2,18,3,11,0,0,0);
INSERT INTO `acl_entry` VALUES (41,2,19,3,13,0,0,0);
INSERT INTO `acl_entry` VALUES (43,2,21,3,8,0,0,0);
INSERT INTO `acl_entry` VALUES (45,3,1,1,12,1,0,0);
INSERT INTO `acl_entry` VALUES (46,3,2,1,6,1,0,0);
INSERT INTO `acl_entry` VALUES (48,3,4,1,7,1,0,0);
INSERT INTO `acl_entry` VALUES (49,3,5,2,12,1,0,0);
INSERT INTO `acl_entry` VALUES (50,3,6,2,6,1,0,0);
INSERT INTO `acl_entry` VALUES (52,3,8,2,7,1,0,0);
INSERT INTO `acl_entry` VALUES (53,3,9,2,11,1,0,0);
INSERT INTO `acl_entry` VALUES (54,3,10,2,13,1,0,0);
INSERT INTO `acl_entry` VALUES (56,3,12,2,8,1,0,0);
INSERT INTO `acl_entry` VALUES (58,3,14,3,12,0,0,0);
INSERT INTO `acl_entry` VALUES (59,3,15,3,6,0,0,0);
INSERT INTO `acl_entry` VALUES (61,3,17,3,7,0,0,0);
INSERT INTO `acl_entry` VALUES (62,3,18,3,11,0,0,0);
INSERT INTO `acl_entry` VALUES (63,3,19,3,13,0,0,0);
INSERT INTO `acl_entry` VALUES (65,3,21,3,8,0,0,0);
INSERT INTO `acl_entry` VALUES (67,4,1,1,12,1,0,0);
INSERT INTO `acl_entry` VALUES (68,4,2,1,6,1,0,0);
INSERT INTO `acl_entry` VALUES (70,4,4,1,7,1,0,0);
INSERT INTO `acl_entry` VALUES (71,4,5,2,12,1,0,0);
INSERT INTO `acl_entry` VALUES (72,4,6,2,6,1,0,0);
INSERT INTO `acl_entry` VALUES (74,4,8,2,7,1,0,0);
INSERT INTO `acl_entry` VALUES (75,4,9,2,11,1,0,0);
INSERT INTO `acl_entry` VALUES (76,4,10,2,13,1,0,0);
INSERT INTO `acl_entry` VALUES (78,4,12,2,8,1,0,0);
INSERT INTO `acl_entry` VALUES (80,4,14,3,12,0,0,0);
INSERT INTO `acl_entry` VALUES (81,4,15,3,6,0,0,0);
INSERT INTO `acl_entry` VALUES (83,4,17,3,7,0,0,0);
INSERT INTO `acl_entry` VALUES (84,4,18,3,11,0,0,0);
INSERT INTO `acl_entry` VALUES (85,4,19,3,13,0,0,0);
INSERT INTO `acl_entry` VALUES (87,4,21,3,8,0,0,0);
INSERT INTO `acl_entry` VALUES (89,1,23,4,17,1,0,0);
INSERT INTO `acl_entry` VALUES (90,2,23,4,17,1,0,0);
INSERT INTO `acl_entry` VALUES (91,3,23,4,17,1,0,0);
INSERT INTO `acl_entry` VALUES (92,4,23,4,17,1,0,0);
INSERT INTO `acl_entry` VALUES (93,1,24,2,17,1,0,0);
INSERT INTO `acl_entry` VALUES (94,2,24,2,17,1,0,0);
INSERT INTO `acl_entry` VALUES (95,3,24,2,17,1,0,0);
INSERT INTO `acl_entry` VALUES (96,4,24,2,17,1,0,0);

/* VIEW_TOPICS FOR registered users */
INSERT INTO `acl_entry` VALUES (97,1,25,1,6,1,0,0);
INSERT INTO `acl_entry` VALUES (98,2,25,1,6,1,0,0);
INSERT INTO `acl_entry` VALUES (99,3,25,1,6,1,0,0);
INSERT INTO `acl_entry` VALUES (100,4,25,1,6,1,0,0);

/* VIEW_TOPICS FOR anonymous users */
INSERT INTO `acl_entry` VALUES (101,1,26,5,6,1,0,0);
INSERT INTO `acl_entry` VALUES (102,2,26,5,6,1,0,0);
INSERT INTO `acl_entry` VALUES (103,3,26,5,6,1,0,0);
INSERT INTO `acl_entry` VALUES (104,4,26,5,6,1,0,0);

/* SEND_PRIVATE_MESSAGES for registered users */
INSERT INTO `acl_entry` VALUES (105,5,1,1,14,1,0,0);

/* EDIT_PROFILE for registered users */
INSERT INTO `acl_entry` VALUES (106,5,2,1,15,1,0,0);