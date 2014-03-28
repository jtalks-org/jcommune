-- Creates users: default, admin, banned with respective permissions
-- Creates sections and branches to be able to see then and post something
set @forum_component_id := 2;
insert ignore into COMPONENTS (CMP_ID, COMPONENT_TYPE, UUID, `NAME`, DESCRIPTION) VALUES (2, 'FORUM', (SELECT UUID() FROM dual), 'JTalks Sample Forum', 'Available users: admin/admin registered/registered moderator/moderator banned/banned');

insert ignore into SECTIONS (SECTION_ID, UUID, `NAME`, DESCRIPTION, POSITION, COMPONENT_ID) VALUES
  (1,(SELECT UUID() FROM dual),'Physics', 'Physics related topics are discussed here', 1, @forum_component_id),
  (2,(SELECT UUID() FROM dual),'Chemistry', 'Chemistry related topics are discussed here', 2,  @forum_component_id);
-- GROUPS BEGIN
insert ignore into GROUPS (UUID, `NAME`, DESCRIPTION) VALUES ((SELECT UUID() FROM dual), 'Moderators', 'General group for all moderators');
SET @admin_group_id := (select GROUP_ID from GROUPS where `NAME`='Administrators');
SET @registered_group_id := (select GROUP_ID from GROUPS where `NAME`='Registered Users');
SET @banned_group_id := (select GROUP_ID from GROUPS where `NAME`='Banned Users');
SET @moderator_group_id := (select GROUP_ID from GROUPS where `NAME`='Moderators');

SET @admin_group_sid := concat('usergroup:',@admin_group_id);
SET @registered_group_sid := concat('usergroup:',@registered_group_id);
SET @banned_group_sid := concat('usergroup:',@banned_group_id);
SET @moderator_group_sid := concat('usergroup:',@moderator_group_id);
-- GROUPS END

insert ignore into BRANCHES (BRANCH_ID, UUID, `NAME`, DESCRIPTION, POSITION, SECTION_ID, MODERATORS_GROUP_ID) VALUES
  (1, UUID(), 'Classical Mechanics', 'Physical related to motion of bodies under the action of a system of forces', 0, 1, 1),
  (2, UUID(), 'Quantum Mechanics', 'Physical phenomena at microscopic scales', 1, 1 ,1),
  (3, UUID(), 'Acids and Bases', '', 0, 2 ,1),
  (4, UUID(), 'Micro level', 'Discussing atoms, electrons, nucleus', 1, 2 ,1);

-- ****USERS CREATION BEGIN****
-- Creates a default user with default/default, admin/admin, banned/banned credentials to be able to log in without manual registration
insert ignore into USERS (UUID, USERNAME, ENCODED_USERNAME, EMAIL, PASSWORD, ROLE, SALT, ENABLED) VALUES
  ((SELECT UUID() FROM dual), 'admin', 'admin', 'admin@jtalks.org', MD5('admin'), 'USER_ROLE', '',true),
  ((SELECT UUID() FROM dual), 'registered', 'registered', 'registered@jtalks.org', MD5('registered'), 'USER_ROLE', '',true),
  ((SELECT UUID() FROM dual), 'moderator', 'moderator', 'moderator@jtalks.org', MD5('moderator'), 'USER_ROLE', '', true),
  ((SELECT UUID() FROM dual), 'banned', 'banned', 'banned@jtalks.org', MD5('banned'), 'USER_ROLE', '', true);
insert ignore into JC_USER_DETAILS (USER_ID, REGISTRATION_DATE, POST_COUNT) values
  ((select ID from USERS where USERNAME = 'admin'), NOW(), 0),
  ((select ID from USERS where USERNAME = 'registered'), NOW(), 0),
  ((select ID from USERS where USERNAME = 'moderator'), NOW(), 0),
  ((select ID from USERS where USERNAME = 'banned'), NOW(), 0) ;
-- ****USERS CREATION END****

-- Add users to appropriate groups
insert ignore into GROUP_USER_REF select @registered_group_id, ID from USERS;
insert ignore into GROUP_USER_REF select @moderator_group_id, ID from USERS where USERNAME in ('moderator', 'admin');
insert ignore into GROUP_USER_REF select @admin_group_id, ID from USERS where USERNAME = 'admin';
insert ignore into GROUP_USER_REF select @banned_group_id, ID from USERS where USERNAME = 'banned';

set @component_acl_class=1;
set @group_acl_class=2;
set @branch_acl_class=3;
insert ignore into acl_class values (@branch_acl_class,'BRANCH'), (@group_acl_class,'GROUP'), (@component_acl_class,'COMPONENT');

insert into acl_sid(principal, sid) values (0, @moderator_group_sid);

SET @admin_group_sid_id := (select id from acl_sid where sid=@admin_group_sid);
SET @registered_group_sid_id := (select id from acl_sid where sid=@registered_group_sid);
SET @banned_group_sid_id := (select id from acl_sid where sid=@banned_group_sid);
SET @moderator_group_sid_id := (select id from acl_sid where sid=@moderator_group_sid);
SET @anonymous_sid_id := (select id from acl_sid where sid='user:anonymousUser');

SET @SEND_PRIVATE_MESSAGES_MASK := 14;
SET @CREATE_FORUM_FAQ_MASK := 20;
SET @EDIT_OWN_PROFILE_MASK := 15;
SET @EDIT_OTHERS_PROFILE_MASK := 23;

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

set @registered_group_object_identity=5;
set @admin_group_object_identity=6;
set @banned_group_object_identity=7;
insert ignore into acl_object_identity values
  (1, @branch_acl_class, 1, NULL, 1, 1),
  (2, @branch_acl_class, 2, NULL, 1, 1),
  (3, @branch_acl_class, 3, NULL, 1, 1),
  (4, @branch_acl_class, 4, NULL, 1, 1),
  (@registered_group_object_identity, @group_acl_class, @registered_group_id, NULL, 1, 1),
  (6, @group_acl_class, @admin_group_id, NULL, 1, 1),
  (@banned_group_object_identity, @group_acl_class, @banned_group_id, NULL, 1, 1),
  (8, @component_acl_class, @forum_component_id, NULL, 1, 1);

-- VIEW_TOPICS FOR anonymous users
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1000, @anonymous_sid_id, @VIEW_TOPICS_MASK, 1, 0, 0 from BRANCHES;
-- permissions for registered users on all branches
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1001, @registered_group_sid_id, @VIEW_TOPICS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1002, @registered_group_sid_id, @CREATE_POSTS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1003, @registered_group_sid_id, @EDIT_OWN_POSTS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1004, @registered_group_sid_id, @DELETE_OWN_POSTS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1006, @registered_group_sid_id, @LEAVE_COMMENTS_IN_CODE_REVIEW_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1007, @registered_group_sid_id, @CREATE_CODE_REVIEW_MASK, 1, 0, 0 from BRANCHES;

-- permissions for moderator users on all branches
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1008, @moderator_group_sid_id, @MOVE_TOPICS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1009, @moderator_group_sid_id, @CLOSE_TOPICS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1010, @moderator_group_sid_id, @DELETE_OTHERS_POSTS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1011, @moderator_group_sid_id, @EDIT_OTHERS_POSTS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1012, @moderator_group_sid_id, @CREATE_STICKED_TOPICS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1013, @moderator_group_sid_id, @CREATE_ANNOUNCEMENTS_MASK, 1, 0, 0 from BRANCHES;

-- setting permissions for banned users
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1015, @banned_group_sid_id, @CREATE_POSTS_MASK, 0, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1016, @banned_group_sid_id, @EDIT_OWN_POSTS_MASK, 0, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1017, @banned_group_sid_id, @DELETE_OWN_POSTS_MASK, 0, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1018, @banned_group_sid_id, @LEAVE_COMMENTS_IN_CODE_REVIEW_MASK, 0, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1019, @banned_group_sid_id, @CREATE_CODE_REVIEW_MASK, 0, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  select BRANCH_ID, 1020, @banned_group_sid_id, @VIEW_TOPICS_MASK, 0, 0, 0 from BRANCHES;

-- personal permissions
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  values (@registered_group_object_identity, 1000, @registered_group_sid_id, @SEND_PRIVATE_MESSAGES_MASK, 1, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  values (@registered_group_object_identity, 1001, @registered_group_sid_id, @EDIT_OWN_PROFILE_MASK, 1, 0, 0);
-- admin
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  values (@admin_group_object_identity, 1002, @admin_group_sid_id, @EDIT_OTHERS_PROFILE_MASK, 1, 0, 0);
-- banned
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  values (@banned_group_object_identity, 1003, @banned_group_sid_id, @SEND_PRIVATE_MESSAGES_MASK, 0, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  values (@banned_group_object_identity, 1004, @banned_group_sid_id, @EDIT_OWN_PROFILE_MASK, 0, 0, 0);

-- admin permissions for the component
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
  values (@admin_group_object_identity, 1000, @admin_group_sid_id, @ADMIN_MASK, 1, 0, 0);

