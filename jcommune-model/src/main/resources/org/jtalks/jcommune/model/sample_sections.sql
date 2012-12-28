INSERT INTO SECTIONS (SECTION_ID, UUID, `NAME`, DESCRIPTION, POSITION, COMPONENT_ID) VALUES (1,(SELECT UUID() FROM dual),'Sample section', 'Some description here', 1, 1);
INSERT INTO SECTIONS (SECTION_ID, UUID, `NAME`, DESCRIPTION, POSITION, COMPONENT_ID) VALUES (2,(SELECT UUID() FROM dual),'Another section', 'Whatever else', 2,  1);

INSERT INTO GROUPS (UUID, `NAME`, DESCRIPTION) VALUES((SELECT UUID() FROM dual), 'Moderator group', 'Moderator group');

INSERT INTO BRANCHES (BRANCH_ID, UUID, `NAME`, DESCRIPTION, POSITION, SECTION_ID, MODERATORS_GROUP_ID) VALUES(1, (SELECT UUID() FROM dual), 'A cool branch', 'More information', 0, 1, 1);
INSERT INTO BRANCHES (BRANCH_ID, UUID, `NAME`, DESCRIPTION, POSITION, SECTION_ID, MODERATORS_GROUP_ID) VALUES(2, (SELECT UUID() FROM dual), 'The second branch', 'More information', 1, 1 ,1);
INSERT INTO BRANCHES (BRANCH_ID, UUID, `NAME`, DESCRIPTION, POSITION, SECTION_ID, MODERATORS_GROUP_ID) VALUES(3, (SELECT UUID() FROM dual), 'One more branch', 'More information', 0, 2 ,1);
INSERT INTO BRANCHES (BRANCH_ID, UUID, `NAME`, DESCRIPTION, POSITION, SECTION_ID, MODERATORS_GROUP_ID) VALUES(4, (SELECT UUID() FROM dual), 'The last, but not least', 'More information', 1, 2 ,1);

-- Creates a default user with default/default credentials to be able to log in without manual registration
INSERT IGNORE INTO USERS (UUID, FIRST_NAME, LAST_NAME, USERNAME, ENCODED_USERNAME, EMAIL, PASSWORD, ROLE, SALT, REGISTRATION_DATE)
  VALUES((SELECT UUID() FROM dual), 'default', 'default', 'default', 'default', 'default@jtalks.org', MD5('default'), 'USER_ROLE', '', NOW());
INSERT IGNORE INTO JC_USER_DETAILS (USER_ID, REGISTRATION_DATE, POST_COUNT, ENABLED)
  select ID, NOW(), 0, true from USERS where USERNAME = 'default';

SET @admin_group_id := (select GROUP_ID from GROUPS where `NAME`='Administrators');
SET @registered_group_id := (select GROUP_ID from GROUPS where `NAME`='Registered Users');
SET @banned_group_id := (select GROUP_ID from GROUPS where `NAME`='Banned Users');
SET @moderator_group_id := (select GROUP_ID from GROUPS where `NAME`='Moderator group');

SET @admin_group_sid := concat('usergroup:',@admin_group_id);
SET @registered_group_sid := concat('usergroup:',@registered_group_id);
SET @banned_group_sid := concat('usergroup:',@banned_group_id);
SET @moderator_group_sid := concat('usergroup:',@moderator_group_id);

-- Default user is then being added to Registered Users group and to the Moderator group
INSERT IGNORE INTO GROUP_USER_REF select @moderator_group_id, ID from USERS where USERNAME = 'default';
INSERT IGNORE INTO GROUP_USER_REF select @registered_group_id, ID from USERS where USERNAME = 'default';
INSERT IGNORE INTO GROUP_USER_REF select @admin_group_id, ID from USERS where USERNAME = 'default';

INSERT INTO `acl_class` VALUES (1,'BRANCH');
INSERT INTO `acl_class` VALUES (2,'GROUP');

INSERT INTO `acl_sid` VALUES (5,0,@moderator_group_sid);

SET @admin_group_sid_id := (select id from `acl_sid` where `sid`=@admin_group_sid);
SET @registered_group_sid_id := (select id from `acl_sid` where `sid`=@registered_group_sid);
SET @banned_group_sid_id := (select id from `acl_sid` where `sid`=@banned_group_sid);
SET @moderator_group_sid_id := (select id from `acl_sid` where `sid`=@moderator_group_sid);
SET @anonymous_sid_id := (select id from `acl_sid` where `sid`='user:anonymousUser');

INSERT INTO `acl_object_identity` VALUES (1,1,1,NULL,1,1);
INSERT INTO `acl_object_identity` VALUES (2,1,2,NULL,1,1);
INSERT INTO `acl_object_identity` VALUES (3,1,3,NULL,1,1);
INSERT INTO `acl_object_identity` VALUES (4,1,4,NULL,1,1);
INSERT INTO `acl_object_identity` VALUES (5,2,@registered_group_id,NULL,1,1);
INSERT INTO `acl_object_identity` VALUES (6,2,@admin_group_id,NULL,1,1);

SET @SEND_PRIVATE_MESSAGES_MASK := 14;
SET @CREATE_FORUM_FAQ_MASK := 20;
SET @EDIT_PROFILE_MASK := 15;

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

/* VIEW_TOPICS FOR registered users */
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,1,@registered_group_sid_id,@VIEW_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,1,@registered_group_sid_id,@VIEW_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,1,@registered_group_sid_id,@VIEW_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,1,@registered_group_sid_id,@VIEW_TOPICS_MASK,1,0,0);

/* VIEW_TOPICS FOR anonymous users */
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,2,@anonymous_sid_id,@VIEW_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,2,@anonymous_sid_id,@VIEW_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,2,@anonymous_sid_id,@VIEW_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,2,@anonymous_sid_id,@VIEW_TOPICS_MASK,1,0,0);

INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,3,@admin_group_sid_id,@MOVE_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,4,@moderator_group_sid_id,@MOVE_TOPICS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,3,@admin_group_sid_id,@MOVE_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,4,@moderator_group_sid_id,@MOVE_TOPICS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,3,@admin_group_sid_id,@MOVE_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,4,@moderator_group_sid_id,@MOVE_TOPICS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,3,@admin_group_sid_id,@MOVE_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,4,@moderator_group_sid_id,@MOVE_TOPICS_MASK,0,0,0);

INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,5,@admin_group_sid_id,@DELETE_OTHERS_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,6,@moderator_group_sid_id,@DELETE_OTHERS_POSTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,5,@admin_group_sid_id,@DELETE_OTHERS_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,6,@moderator_group_sid_id,@DELETE_OTHERS_POSTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,5,@admin_group_sid_id,@DELETE_OTHERS_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,6,@moderator_group_sid_id,@DELETE_OTHERS_POSTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,5,@admin_group_sid_id,@DELETE_OTHERS_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,6,@moderator_group_sid_id,@DELETE_OTHERS_POSTS_MASK,0,0,0);

INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,7,@admin_group_sid_id,@CLOSE_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,8,@moderator_group_sid_id,@CLOSE_TOPICS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,7,@admin_group_sid_id,@CLOSE_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,8,@moderator_group_sid_id,@CLOSE_TOPICS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,7,@admin_group_sid_id,@CLOSE_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,8,@moderator_group_sid_id,@CLOSE_TOPICS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,7,@admin_group_sid_id,@CLOSE_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,8,@moderator_group_sid_id,@CLOSE_TOPICS_MASK,0,0,0);

INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,9,@registered_group_sid_id,@CREATE_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,9,@registered_group_sid_id,@CREATE_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,9,@registered_group_sid_id,@CREATE_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,9,@registered_group_sid_id,@CREATE_POSTS_MASK,1,0,0);

INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,10,@anonymous_sid_id,@CREATE_POSTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,10,@anonymous_sid_id,@CREATE_POSTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,10,@anonymous_sid_id,@CREATE_POSTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,10,@anonymous_sid_id,@CREATE_POSTS_MASK,0,0,0);

INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,11,@registered_group_sid_id,@DELETE_OWN_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,11,@registered_group_sid_id,@DELETE_OWN_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,11,@registered_group_sid_id,@DELETE_OWN_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,11,@registered_group_sid_id,@DELETE_OWN_POSTS_MASK,1,0,0);

INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,12,@anonymous_sid_id,@DELETE_OWN_POSTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,12,@anonymous_sid_id,@DELETE_OWN_POSTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,12,@anonymous_sid_id,@DELETE_OWN_POSTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,12,@anonymous_sid_id,@DELETE_OWN_POSTS_MASK,0,0,0);

INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,13,@registered_group_sid_id,@EDIT_OWN_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,13,@registered_group_sid_id,@EDIT_OWN_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,13,@registered_group_sid_id,@EDIT_OWN_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,13,@registered_group_sid_id,@EDIT_OWN_POSTS_MASK,1,0,0);

INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,14,@anonymous_sid_id,@EDIT_OWN_POSTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,14,@anonymous_sid_id,@EDIT_OWN_POSTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,14,@anonymous_sid_id,@EDIT_OWN_POSTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,14,@anonymous_sid_id,@EDIT_OWN_POSTS_MASK,0,0,0);

INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,15,@admin_group_sid_id,@EDIT_OTHERS_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,16,@moderator_group_sid_id,@EDIT_OTHERS_POSTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,15,@admin_group_sid_id,@EDIT_OTHERS_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,16,@moderator_group_sid_id,@EDIT_OTHERS_POSTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,15,@admin_group_sid_id,@EDIT_OTHERS_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,16,@moderator_group_sid_id,@EDIT_OTHERS_POSTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,15,@admin_group_sid_id,@EDIT_OTHERS_POSTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,16,@moderator_group_sid_id,@EDIT_OTHERS_POSTS_MASK,0,0,0);

INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,17,@admin_group_sid_id,@CREATE_ANNOUNCEMENTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,18,@moderator_group_sid_id,@CREATE_ANNOUNCEMENTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,17,@admin_group_sid_id,@CREATE_ANNOUNCEMENTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,18,@moderator_group_sid_id,@CREATE_ANNOUNCEMENTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,17,@admin_group_sid_id,@CREATE_ANNOUNCEMENTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,18,@moderator_group_sid_id,@CREATE_ANNOUNCEMENTS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,17,@admin_group_sid_id,@CREATE_ANNOUNCEMENTS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,18,@moderator_group_sid_id,@CREATE_ANNOUNCEMENTS_MASK,0,0,0);

INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,19,@admin_group_sid_id,@CREATE_STICKED_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,20,@moderator_group_sid_id,@CREATE_STICKED_TOPICS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,19,@admin_group_sid_id,@CREATE_STICKED_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,20,@moderator_group_sid_id,@CREATE_STICKED_TOPICS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,19,@admin_group_sid_id,@CREATE_STICKED_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,20,@moderator_group_sid_id,@CREATE_STICKED_TOPICS_MASK,0,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,19,@admin_group_sid_id,@CREATE_STICKED_TOPICS_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,20,@moderator_group_sid_id,@CREATE_STICKED_TOPICS_MASK,0,0,0);

/* SEND_PRIVATE_MESSAGES for registered users */
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (5,1,@registered_group_sid_id,@SEND_PRIVATE_MESSAGES_MASK,1,0,0);

/* EDIT_PROFILE for registered users */
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (5,2,@registered_group_sid_id,@EDIT_PROFILE_MASK,1,0,0);

/* CREATE_FORUM_FAQ for admins */
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (6,1,@admin_group_sid_id,@CREATE_FORUM_FAQ_MASK,1,0,0);
                
/* Create code review and add review comments for registered users */
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,21,@registered_group_sid_id,@CREATE_CODE_REVIEW_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,21,@registered_group_sid_id,@CREATE_CODE_REVIEW_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,21,@registered_group_sid_id,@CREATE_CODE_REVIEW_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,21,@registered_group_sid_id,@CREATE_CODE_REVIEW_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (1,22,@registered_group_sid_id,@LEAVE_COMMENTS_IN_CODE_REVIEW_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (2,22,@registered_group_sid_id,@LEAVE_COMMENTS_IN_CODE_REVIEW_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (3,22,@registered_group_sid_id,@LEAVE_COMMENTS_IN_CODE_REVIEW_MASK,1,0,0);
INSERT INTO `acl_entry`(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
                VALUES (4,22,@registered_group_sid_id,@LEAVE_COMMENTS_IN_CODE_REVIEW_MASK,1,0,0);
                
/* Delete permissions for registered users */                