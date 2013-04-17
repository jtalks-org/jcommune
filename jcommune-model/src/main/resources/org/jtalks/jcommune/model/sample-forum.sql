-- Creates users: default, admin, banned with respective permissions
-- Creates sections and branches to be able to see then and post something

INSERT INTO COMPONENTS (CMP_ID, COMPONENT_TYPE, UUID, `NAME`, DESCRIPTION) VALUES (1, 'FORUM', (SELECT UUID() FROM dual), 'JTalks Sample Forum', 'You can quickly experiment on this instance');

INSERT INTO SECTIONS (SECTION_ID, UUID, `NAME`, DESCRIPTION, POSITION, COMPONENT_ID) VALUES
  (1,(SELECT UUID() FROM dual),'Physics', 'Physics related topics are discussed here', 1, 1),
  (2,(SELECT UUID() FROM dual),'Chemistry', 'Chemistry related topics are discussed here', 2,  1);
-- GROUPS BEGIN
INSERT INTO GROUPS (UUID, `NAME`, DESCRIPTION) VALUES ((SELECT UUID() FROM dual), 'Moderators', 'General group for all moderators');
SET @admin_group_id := (select GROUP_ID from GROUPS where `NAME`='Administrators');
SET @registered_group_id := (select GROUP_ID from GROUPS where `NAME`='Registered Users');
SET @banned_group_id := (select GROUP_ID from GROUPS where `NAME`='Banned Users');
SET @moderator_group_id := (select GROUP_ID from GROUPS where `NAME`='Moderators');

SET @admin_group_sid := concat('usergroup:',@admin_group_id);
SET @registered_group_sid := concat('usergroup:',@registered_group_id);
SET @banned_group_sid := concat('usergroup:',@banned_group_id);
SET @moderator_group_sid := concat('usergroup:',@moderator_group_id);
-- GROUPS END

INSERT INTO BRANCHES (BRANCH_ID, UUID, `NAME`, DESCRIPTION, POSITION, SECTION_ID, MODERATORS_GROUP_ID) VALUES
  (1, UUID(), 'Classical Mechanics', 'Physical related to motion of bodies under the action of a system of forces', 0, 1, 1),
  (2, UUID(), 'Quantum Mechanics', 'Physical phenomena at microscopic scales', 1, 1 ,1),
  (3, UUID(), 'Acids and Bases', '', 0, 2 ,1),
  (4, UUID(), 'Micro level', 'Discussing atoms, electrons, nucleus', 1, 2 ,1);

-- ****USERS CREATION BEGIN****
-- Creates a default user with default/default, admin/admin, banned/banned credentials to be able to log in without manual registration
INSERT IGNORE INTO USERS (UUID, USERNAME, ENCODED_USERNAME, EMAIL, PASSWORD, ROLE, SALT, REGISTRATION_DATE, ENABLED) VALUES
  ((SELECT UUID() FROM dual), 'admin', 'admin', 'admin@jtalks.org', MD5('admin'), 'USER_ROLE', '', NOW(), true),
  ((SELECT UUID() FROM dual), 'default', 'default', 'default@jtalks.org', MD5('default'), 'USER_ROLE', '', NOW(), true),
  ((SELECT UUID() FROM dual), 'banned', 'banned', 'banned@jtalks.org', MD5('banned'), 'USER_ROLE', '', NOW(), true);

INSERT IGNORE INTO JC_USER_DETAILS (USER_ID, REGISTRATION_DATE, POST_COUNT) values
  ((select ID from USERS where USERNAME = 'admin'), NOW(), 0),
  ((select ID from USERS where USERNAME = 'default'), NOW(), 0),
  ((select ID from USERS where USERNAME = 'banned'), NOW(), 0) ;
-- ****USERS CREATION END****

-- Default user is then being added to Registered Users group and to the Moderators group
INSERT IGNORE INTO GROUP_USER_REF select @moderator_group_id, ID from USERS where USERNAME = 'default';
INSERT IGNORE INTO GROUP_USER_REF select @registered_group_id, ID from USERS where USERNAME = 'default';
INSERT IGNORE INTO GROUP_USER_REF select @admin_group_id, ID from USERS where USERNAME = 'admin';
INSERT IGNORE INTO GROUP_USER_REF select @registered_group_id, ID from USERS where USERNAME = 'admin';
INSERT IGNORE INTO GROUP_USER_REF select @banned_group_id, ID from USERS where USERNAME = 'banned';
INSERT IGNORE INTO GROUP_USER_REF select @registered_group_id, ID from USERS where USERNAME = 'banned';

INSERT INTO acl_class VALUES (1,'BRANCH'), (2,'GROUP'), (3,'COMPONENT');

INSERT INTO acl_sid VALUES (5, 0, @moderator_group_sid);

SET @admin_group_sid_id := (select id from acl_sid where sid=@admin_group_sid);
SET @registered_group_sid_id := (select id from acl_sid where sid=@registered_group_sid);
SET @banned_group_sid_id := (select id from acl_sid where sid=@banned_group_sid);
SET @moderator_group_sid_id := (select id from acl_sid where sid=@moderator_group_sid);
SET @anonymous_sid_id := (select id from acl_sid where sid='user:anonymousUser');

SET @SEND_PRIVATE_MESSAGES_MASK := 14;
SET @CREATE_FORUM_FAQ_MASK := 20;
SET @EDIT_OWN_PROFILE_MASK := 15;

SET @VIEW_TOPICS_MASK := 6;
SET @MOVE_TOPICS_MASK := 8;
SET @CLOSE_TOPICS_MASK := 11;
SET @CREATE_POSTS_MASK := 12;
SET @DELETE_OWN_POSTS_MASK := 7;
SET @DELETE_OTHERS_POSTS_MASK := 13;
SET @EDIT_OWN_POSTS_MASK := 133;
SET @EDIT_OTHERS_POSTS_MASK := 17;
SET @CREATE_ANNOUNCEMENTS_MASK := 18;
SET @CREATE_STICKED_TOPICS_MASK := 19;
SET @CREATE_CODE_REVIEW_MASK := 21;
SET @LEAVE_COMMENTS_IN_CODE_REVIEW_MASK := 22;

SET @ADMIN_MASK := 16;

INSERT INTO acl_object_identity VALUES
  (1, 1, 1, NULL, 1, 1),
  (2, 1, 2, NULL, 1, 1),
  (3, 1, 3, NULL, 1, 1),
  (4, 1, 4, NULL, 1, 1),
  (5, 2, @registered_group_id, NULL, 1, 1),
  (6, 2, @admin_group_id, NULL, 1, 1),
  (7, 3, 1, NULL, 1, 1);

-- VIEW_TOPICS FOR registered users on all branches
INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1, @registered_group_sid_id, @VIEW_TOPICS_MASK, 1, 0, 0 from BRANCHES;

-- VIEW_TOPICS FOR anonymous users
INSERT INTO acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 2, @anonymous_sid_id, @VIEW_TOPICS_MASK, 1, 0, 0 from BRANCHES;
