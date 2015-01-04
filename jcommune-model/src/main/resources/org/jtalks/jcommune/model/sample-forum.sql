insert ignore into COMPONENTS (CMP_ID, COMPONENT_TYPE, UUID, `NAME`, DESCRIPTION) VALUES (2, 'FORUM', (SELECT UUID() FROM dual), 'JTalks Sample Forum', 'Available users: admin/admin registered/registered moderator/moderator banned/banned');

insert ignore into SECTIONS (SECTION_ID, UUID, `NAME`, DESCRIPTION, POSITION, COMPONENT_ID) VALUES
  (1,(SELECT UUID() FROM dual),'Sport', 'All about sport', 1, @forum_component_id),
  (2,(SELECT UUID() FROM dual),'Transport', 'How move from A to B', 2, @forum_component_id),
  (3,(SELECT UUID() FROM dual),'Travels', 'Leave your home to see the world', 3, @forum_component_id),
  (4,(SELECT UUID() FROM dual),'Music','Heaven for ears',4,@forum_component_id),
  (5,(SELECT UUID() FROM dual),'History', 'Past, present and future', 5, @forum_component_id),
  (6,(SELECT UUID() FROM dual),'Literature', 'Progress for your brain', 6, @forum_component_id),
  (7,(SELECT UUID() FROM dual),'TV', 'Has this zombobox something interesting?', 7, @forum_component_id),
  (8,(SELECT UUID() FROM dual),'Hi-tech', 'Technologies', 8, @forum_component_id),
  (9,(SELECT UUID() FROM dual),'People', 'All about mankind', 9, @forum_component_id),
  (10,(SELECT UUID() FROM dual),'Leisure', 'Have free time?', 9, @forum_component_id);
  
-- GROUPS BEGIN
insert ignore into GROUPS (UUID, `NAME`, DESCRIPTION) VALUES ((SELECT UUID() FROM dual), 'Moderators', 'General group for all moderators');
insert ignore into GROUPS (UUID, `NAME`) VALUES 
	((SELECT UUID() FROM dual), 'createPosts'),
	((SELECT UUID() FROM dual), 'createStickedPosts'),
	((SELECT UUID() FROM dual), 'createAnnouncements'),
	((SELECT UUID() FROM dual), 'editOwnPosts'),
	((SELECT UUID() FROM dual), 'editOtherPosts'),
	((SELECT UUID() FROM dual), 'deleteOwnPosts'),
	((SELECT UUID() FROM dual), 'deleteOthersPosts'),
	((SELECT UUID() FROM dual), 'moveTopics'),
	((SELECT UUID() FROM dual), 'closeTopics'),
	((SELECT UUID() FROM dual), 'createCodeReview'),
	((SELECT UUID() FROM dual), 'leaveCommentsInCodeReview');

SET @admin_group_id := (select GROUP_ID from GROUPS where `NAME`='Administrators');
SET @registered_group_id := (select GROUP_ID from GROUPS where `NAME`='Registered Users');
SET @banned_group_id := (select GROUP_ID from GROUPS where `NAME`='Banned Users');
SET @moderator_group_id := (select GROUP_ID from GROUPS where `NAME`='Moderators');
SET @createPosts_group_id = (select GROUP_ID from GROUPS where `NAME`='createPosts');
SET @createStickedPosts_group_id = (select GROUP_ID from GROUPS where `NAME`='createStickedPosts');
SET @createAnnouncements_group_id = (select GROUP_ID from GROUPS where `NAME`='createAnnouncements');
SET @editOwnPosts_group_id = (select GROUP_ID from GROUPS where `NAME`='editOwnPosts');
SET @editOtherPosts_group_id = (select GROUP_ID from GROUPS where `NAME`='editOtherPosts');
SET @deleteOwnPosts_group_id = (select GROUP_ID from GROUPS where `NAME`='deleteOwnPosts');
SET @deleteOthersPosts_group_id = (select GROUP_ID from GROUPS where `NAME`='deleteOthersPosts');
SET @moveTopics_group_id = (select GROUP_ID from GROUPS where `NAME`='moveTopics');
SET @closeTopics_group_id = (select GROUP_ID from GROUPS where `NAME`='closeTopics');
SET @createCodeReview_group_id = (select GROUP_ID from GROUPS where `NAME`='createCodeReview');
SET @leaveCommentsInCodeReview_group_id = (select GROUP_ID from GROUPS where `NAME`='leaveCommentsInCodeReview');

SET @admin_group_sid := concat('usergroup:',@admin_group_id);
SET @registered_group_sid := concat('usergroup:',@registered_group_id);
SET @banned_group_sid := concat('usergroup:',@banned_group_id);
SET @moderator_group_sid := concat('usergroup:',@moderator_group_id);
SET @createPosts_group_sid = concat('usergroup:',@createPosts_group_id);
SET @createStickedPosts_group_sid = concat('usergroup:',@createStickedPosts_group_id);
SET @createAnnouncements_group_sid = concat('usergroup:',@createAnnouncements_group_id);
SET @editOwnPosts_group_sid = concat('usergroup:',@editOwnPosts_group_id);
SET @editOtherPosts_group_sid = concat('usergroup:',@editOtherPosts_group_id);
SET @deleteOwnPosts_group_sid = concat('usergroup:',@deleteOwnPosts_group_id);
SET @deleteOthersPosts_group_sid = concat('usergroup:',@deleteOthersPosts_group_id);
SET @moveTopics_group_sid = concat('usergroup:',@moveTopics_group_id);
SET @closeTopics_group_sid = concat('usergroup:',@closeTopics_group_id);
SET @createCodeReview_group_sid = concat('usergroup:',@createCodeReview_group_id);
SET @leaveCommentsInCodeReview_group_sid = concat('usergroup:',@leaveCommentsInCodeReview_group_id);
-- GROUPS END

-- BRANCHES BEGIN
insert ignore into BRANCHES (BRANCH_ID, UUID, `NAME`, DESCRIPTION, POSITION, SECTION_ID, MODERATORS_GROUP_ID) VALUES
  (1, UUID(), 'Curling', 'Brooms and stones', 0, 1, 1),
  (2, UUID(), 'Cricet', 'Balls and bats', 1, 1 ,1),
  (3, UUID(), 'Field hockey', 'Sticks and balls on the grass', 2, 1 ,1),
  (4, UUID(), 'Korfball', 'It is NOT basketball!!', 3, 1 ,1),
  (5, UUID(), 'Sepak takraw', 'Rattan balls,', 4, 1 ,1),
  
  (6, UUID(), 'Bicycle', 'Two wheels', 0, 2, 1),
  (7, UUID(), 'Rickshaw', '', 1, 2 ,1),
  (8, UUID(), 'Horse', 'Still actual', 2, 2 ,1),
  (9, UUID(), 'On foot', 'Maybe some classic?', 3, 2 ,1),
  (10, UUID(), 'UFO', 'What about some extreme', 4, 2 ,1),

  (11, UUID(), 'Europe', 'Classic trips', 0, 3, 1),
  (12, UUID(), 'Asia', 'Most populated', 1, 3 ,1),
  (13, UUID(), 'America', 'Other hemisphere', 2, 3 ,1),
  (14, UUID(), 'Africa', 'Too hot?', 3, 3 ,1),
  (15, UUID(), 'Australia', 'Something very interesting', 4, 3 ,1),
  
  (16, UUID(), 'Classic', 'Checked by ages', 0, 4, 1),
  (17, UUID(), 'Rock', 'Something heavier?', 1, 4, 1),
  (18, UUID(), 'Electronic', '', 2, 4 ,1),
  (19, UUID(), 'Pop', 'La-la', 3, 4 ,1),
  (20, UUID(), 'Rap', 'Yo!', 4, 4 ,1),
  
  (21, UUID(), 'Ancient', 'Rome, Greece', 0, 5, 1),
  (22, UUID(), 'Middle Ages', 'Europe', 1, 5 ,1),
  (23, UUID(), 'Renaissance', 'Still Europe', 2, 5 ,1),
  (24, UUID(), 'Modern', 'All Earth', 3, 5 ,1),
  (25, UUID(), 'Future', 'Go ahead all over the world', 4, 5 ,1),
  
  (26, UUID(), 'Sci-fi', 'What about something unbelievable?', 0, 6, 1),
  (27, UUID(), 'Adventures', 'Breathtaking books', 1, 6 ,1),
  (28, UUID(), 'Fairytales', 'Not only for kids', 2, 6 ,1),
  (29, UUID(), 'Realism', 'All about our life', 3, 6 ,1),
  (30, UUID(), 'Comedy', 'LOL', 4, 6 ,1),
  
  (31, UUID(), 'TV shows', 'Funny and useful', 0, 7, 1),
  (32, UUID(), 'News', 'From north to south', 1, 7 ,1),
  (33, UUID(), 'Cartoon', 'Kids time', 2, 7 ,1),
  (34, UUID(), 'Discovery', 'All Earth', 3, 7 ,1),
  (35, UUID(), 'Advertisement', 'Part of the TV', 4, 7 ,1),
  
  (36, UUID(), 'Hard', 'Components', 0, 8, 1),
  (37, UUID(), 'Soft', 'Applications', 1, 8 ,1),
  (38, UUID(), 'Programming', 'Development', 2, 8 ,1),
  (39, UUID(), 'Network', 'World wide web', 3, 8 ,1),
  (40, UUID(), 'Electronics', 'Devices', 4, 8 ,1),
  
  (41, UUID(), 'Philosophy', 'Wisdom', 0, 9, 1),
  (42, UUID(), 'Sociology', 'Human behaviour', 1, 9 ,1),
  (43, UUID(), 'Psychology', 'Human motives', 2, 9 ,1),
  (44, UUID(), 'Education', 'Evolution', 3, 9 ,1),
  (45, UUID(), 'Religion', 'Human faith', 4, 9 ,1),
  
  (46, UUID(), 'Theatre', 'Performances', 0, 10, 1),
  (47, UUID(), 'Cinema', 'New blockbusters', 1, 10 ,1),
  (48, UUID(), 'Exhibitions', 'Art', 2, 10 ,1),
  (49, UUID(), 'Competitions', 'Some sport', 3, 10 ,1);
  -- BRANCHES END
  
  -- ****USERS CREATION BEGIN****
-- Creates a default users with admin/admin, registered/registered, moderator/moderator, banned/banned credentials, individual permissions to be able to log in without manual registration
insert ignore into USERS (UUID, USERNAME, ENCODED_USERNAME, EMAIL, PASSWORD, ROLE, SALT, ENABLED) VALUES
	((SELECT UUID() FROM dual), 'admin', 'admin', 'admin@jtalks.org', MD5('admin'), 'USER_ROLE', '',true),
	((SELECT UUID() FROM dual), 'registered', 'registered', 'registered@jtalks.org', MD5('registered'), 'USER_ROLE', '',true),
	((SELECT UUID() FROM dual), 'moderator', 'moderator', 'moderator@jtalks.org', MD5('moderator'), 'USER_ROLE', '', true),
	((SELECT UUID() FROM dual), 'banned', 'banned', 'banned@jtalks.org', MD5('banned'), 'USER_ROLE', '', true),
	((SELECT UUID() FROM dual), 'post_creator', 'post_creator', 'post_creator@jtalks.org', MD5('qwerty'), 'USER_ROLE', '',true),
	((SELECT UUID() FROM dual), 'sticked_post_creator', 'sticked_post_creator', 'sticked_post_creator@jtalks.org', MD5('qwerty'), 'USER_ROLE', '',true),
	((SELECT UUID() FROM dual), 'announcement_creator', 'announcement_creator', 'announcement_creator@jtalks.org', MD5('qwerty'), 'USER_ROLE', '',true),
	((SELECT UUID() FROM dual), 'ownpost_editor', 'ownpost_editor', 'ownpost_editor@jtalks.org', MD5('qwerty'), 'USER_ROLE', '',true),
	((SELECT UUID() FROM dual), 'otherpost_editor', 'otherpost_editor', 'otherpost_editor@jtalks.org', MD5('qwerty'), 'USER_ROLE', '',true),
	((SELECT UUID() FROM dual), 'ownpost_remover', 'ownpost_remover', 'ownpost_remover@jtalks.org', MD5('qwerty'), 'USER_ROLE', '',true),
	((SELECT UUID() FROM dual), 'otherpost_remover', 'otherpost_remover', 'otherpost_remover@jtalks.org', MD5('qwerty'), 'USER_ROLE', '',true),
	((SELECT UUID() FROM dual), 'topic_mover', 'topic_mover', 'topic_mover@jtalks.org', MD5('qwerty'), 'USER_ROLE', '',true),
	((SELECT UUID() FROM dual), 'topic_closer', 'topic_closer', 'topic_closer@jtalks.org', MD5('qwerty'), 'USER_ROLE', '',true),
	((SELECT UUID() FROM dual), 'codeReview_creator', 'codeReview_creator', 'codeReview_creator@jtalks.org', MD5('qwerty'), 'USER_ROLE', '',true),
	((SELECT UUID() FROM dual), 'codeReview_commentator', 'codeReview_commentator', 'codeReview_commentator@jtalks.org', MD5('qwerty'), 'USER_ROLE', '',true);
insert ignore into JC_USER_DETAILS (USER_ID, REGISTRATION_DATE, POST_COUNT) values
	((select ID from USERS where USERNAME = 'admin'), NOW(), 0),
	((select ID from USERS where USERNAME = 'registered'), NOW(), 0),
	((select ID from USERS where USERNAME = 'moderator'), NOW(), 0),
	((select ID from USERS where USERNAME = 'banned'), NOW(), 0),
	((select ID from USERS where USERNAME = 'post_creator'), NOW(), 0),
	((select ID from USERS where USERNAME = 'sticked_post_creator'), NOW(), 0),
	((select ID from USERS where USERNAME = 'announcement_creator'), NOW(), 0),
	((select ID from USERS where USERNAME = 'ownpost_editor'), NOW(), 0),
	((select ID from USERS where USERNAME = 'otherpost_editor'), NOW(), 0),
	((select ID from USERS where USERNAME = 'ownpost_remover'), NOW(), 0),
	((select ID from USERS where USERNAME = 'otherpost_remover'), NOW(), 0),
	((select ID from USERS where USERNAME = 'topic_mover'), NOW(), 0),
	((select ID from USERS where USERNAME = 'topic_closer'), NOW(), 0),
	((select ID from USERS where USERNAME = 'codeReview_creator'), NOW(), 0),
	((select ID from USERS where USERNAME = 'codeReview_commentator'), NOW(), 0);
  
-- ****USERS CREATION END****

-- Add users to appropriate groups
insert ignore into GROUP_USER_REF select @registered_group_id, ID from USERS where USERNAME in ('registered, moderator', 'admin', 'banned');
insert ignore into GROUP_USER_REF select @moderator_group_id, ID from USERS where USERNAME in ('moderator', 'admin');
insert ignore into GROUP_USER_REF select @admin_group_id, ID from USERS where USERNAME = 'admin';
insert ignore into GROUP_USER_REF select @banned_group_id, ID from USERS where USERNAME = 'banned';
insert ignore into GROUP_USER_REF select @createPosts_group_id, ID from USERS where USERNAME = 'post_creator';
insert ignore into GROUP_USER_REF select @createStickedPosts_group_id, ID from USERS where USERNAME = 'sticked_post_creator';
insert ignore into GROUP_USER_REF select @createAnnouncements_group_id, ID from USERS where USERNAME = 'announcement_creator';
insert ignore into GROUP_USER_REF select @editOwnPosts_group_id, ID from USERS where USERNAME = 'ownpost_editor';
insert ignore into GROUP_USER_REF select @editOtherPosts_group_id, ID from USERS where USERNAME = 'otherpost_editor';
insert ignore into GROUP_USER_REF select @deleteOwnPosts_group_id, ID from USERS where USERNAME = 'ownpost_remover';
insert ignore into GROUP_USER_REF select @deleteOthersPosts_group_id, ID from USERS where USERNAME = 'otherpost_remover';
insert ignore into GROUP_USER_REF select @moveTopics_group_id, ID from USERS where USERNAME = 'topic_mover';
insert ignore into GROUP_USER_REF select @closeTopics_group_id, ID from USERS where USERNAME = 'topic_closer';
insert ignore into GROUP_USER_REF select @createCodeReview_group_id, ID from USERS where USERNAME = 'codeReview_creator';
insert ignore into GROUP_USER_REF select @leaveCommentsInCodeReview_group_id, ID from USERS where USERNAME = 'codeReview_commentator';

set @component_acl_class=1;
set @group_acl_class=2;
set @branch_acl_class=3;
insert ignore into acl_class values (@branch_acl_class,'BRANCH'), (@group_acl_class,'GROUP'), (@component_acl_class,'COMPONENT');

insert into acl_sid(principal, sid) values (0, @moderator_group_sid);
insert into acl_sid(principal, sid) values (0, @createPosts_group_sid);
insert into acl_sid(principal, sid) values (0, @createStickedPosts_group_sid);
insert into acl_sid(principal, sid) values (0, @createAnnouncements_group_sid);
insert into acl_sid(principal, sid) values (0, @editOwnPosts_group_sid);
insert into acl_sid(principal, sid) values (0, @editOtherPosts_group_sid);
insert into acl_sid(principal, sid) values (0, @deleteOwnPosts_group_sid);
insert into acl_sid(principal, sid) values (0, @deleteOthersPosts_group_sid);
insert into acl_sid(principal, sid) values (0, @moveTopics_group_sid);
insert into acl_sid(principal, sid) values (0, @closeTopics_group_sid);
insert into acl_sid(principal, sid) values (0, @createCodeReview_group_sid);
insert into acl_sid(principal, sid) values (0, @leaveCommentsInCodeReview_group_sid);

SET @admin_group_sid_id := (select id from acl_sid where sid=@admin_group_sid);
SET @registered_group_sid_id := (select id from acl_sid where sid=@registered_group_sid);
SET @banned_group_sid_id := (select id from acl_sid where sid=@banned_group_sid);
SET @moderator_group_sid_id := (select id from acl_sid where sid=@moderator_group_sid);
SET @anonymous_sid_id := (select id from acl_sid where sid='user:anonymousUser');
SET @createPosts_group_sid_id = (select id from acl_sid where sid=@createPosts_group_sid);
SET @createStickedPosts_group_sid_id = (select id from acl_sid where sid=@createStickedPosts_group_sid);
SET @createAnnouncements_group_sid_id = (select id from acl_sid where sid=@createAnnouncements_group_sid);
SET @editOwnPosts_group_sid_id = (select id from acl_sid where sid=@editOwnPosts_group_sid);
SET @editOtherPosts_group_sid_id = (select id from acl_sid where sid=@editOtherPosts_group_sid);
SET @deleteOwnPosts_group_sid_id = (select id from acl_sid where sid=@deleteOwnPosts_group_sid);
SET @deleteOthersPosts_group_sid_id = (select id from acl_sid where sid=@deleteOthersPosts_group_sid);
SET @moveTopics_group_sid_id = (select id from acl_sid where sid=@moveTopics_group_sid);
SET @closeTopics_group_sid_id = (select id from acl_sid where sid=@closeTopics_group_sid);
SET @createCodeReview_group_sid_id = (select id from acl_sid where sid=@createCodeReview_group_sid);
SET @leaveCommentsInCodeReview_group_sid_id = (select id from acl_sid where sid=@leaveCommentsInCodeReview_group_sid);

-- PERMISSIONS BEGIN
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
-- PERMISSIONS END

insert ignore into acl_object_identity
  SELECT BranchTable.BRANCH_ID, @branch_acl_class, BranchTable.BRANCH_ID, NULL, 1, 1
  FROM (SELECT BRANCH_ID FROM BRANCHES) BranchTable;

set @branches_count = (SELECT COUNT(*) FROM BRANCHES);
set @registered_group_object_identity=@branches_count + 1;
set @admin_group_object_identity=@branches_count + 2;
set @banned_group_object_identity=@branches_count + 3;
set @createPosts_group_object_identity=@branches_count + 5;
set @createStickedPosts_group_object_identity=@branches_count + 6;
set @createAnnouncements_group_object_identity=@branches_count + 7;
set @editOwnPosts_group_object_identity=@branches_count + 8;
set @editOtherPosts_group_object_identity=@branches_count + 9;
set @deleteOwnPosts_group_object_identity=@branches_count + 10;
set @deleteOthersPosts_group_object_identity=@branches_count + 11;
set @moveTopics_group_object_identity=@branches_count + 12;
set @closeTopics_group_object_identity=@branches_count + 13;
set @createCodeReview_group_object_identity=@branches_count + 14;
set @leaveCommentsInCodeReview_group_object_identity=@branches_count + 15;

insert ignore into acl_object_identity values
  (@registered_group_object_identity, @group_acl_class, @registered_group_id, NULL, 1, 1),
  (@admin_group_object_identity, @group_acl_class, @admin_group_id, NULL, 1, 1),
  (@banned_group_object_identity, @group_acl_class, @banned_group_id, NULL, 1, 1),
  (53, @component_acl_class, @forum_component_id, NULL, 1, 1);

-- VIEW_TOPICS for anonymous users
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

-- setting permissions for createPosts users on all branches
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1022, @createPosts_group_sid_id, @VIEW_TOPICS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1023, @createPosts_group_sid_id, @CREATE_POSTS_MASK, 1, 0, 0 from BRANCHES;

 -- setting permissions for createStickedPosts users on all branches
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1024, @createStickedPosts_group_sid_id, @VIEW_TOPICS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1025, @createStickedPosts_group_sid_id, @CREATE_POSTS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1026, @createStickedPosts_group_sid_id, @CREATE_STICKED_TOPICS_MASK, 1, 0, 0 from BRANCHES;

 -- setting permissions for createAnnouncements users on all branches
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1027, @createAnnouncements_group_sid_id, @VIEW_TOPICS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1028, @createAnnouncements_group_sid_id, @CREATE_POSTS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1029, @createAnnouncements_group_sid_id, @CREATE_ANNOUNCEMENTS_MASK, 1, 0, 0 from BRANCHES;

-- setting permissions for editOwnPosts users on all branches
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1030, @editOwnPosts_group_sid_id, @VIEW_TOPICS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1031, @editOwnPosts_group_sid_id, @CREATE_POSTS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1032, @editOwnPosts_group_sid_id, @EDIT_OWN_POSTS_MASK, 1, 0, 0 from BRANCHES;

-- setting permissions for editOtherPosts users on all branches
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1033, @editOtherPosts_group_sid_id, @VIEW_TOPICS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1034, @editOtherPosts_group_sid_id, @EDIT_OTHERS_POSTS_MASK, 1, 0, 0 from BRANCHES;

-- setting permissions for deleteOwnPosts users on all branches
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1035, @deleteOwnPosts_group_sid_id, @VIEW_TOPICS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1036, @deleteOwnPosts_group_sid_id, @CREATE_POSTS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1037, @deleteOwnPosts_group_sid_id, @DELETE_OWN_POSTS_MASK, 1, 0, 0 from BRANCHES;

-- setting permissions for deleteOtherPosts users on all branches
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1038, @deleteOthersPosts_group_sid_id, @VIEW_TOPICS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1039, @deleteOthersPosts_group_sid_id, @CREATE_POSTS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1040, @deleteOthersPosts_group_sid_id, @DELETE_OTHERS_POSTS_MASK, 1, 0, 0 from BRANCHES;

-- setting permissions for moveTopics users on all branches
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1041, @moveTopics_group_sid_id, @VIEW_TOPICS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1042, @moveTopics_group_sid_id, @MOVE_TOPICS_MASK, 1, 0, 0 from BRANCHES;

-- setting permissions for closeTopics users on all branches
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1043, @closeTopics_group_sid_id, @VIEW_TOPICS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1044, @closeTopics_group_sid_id, @CLOSE_TOPICS_MASK, 1, 0, 0 from BRANCHES;

-- setting permissions for createCodeReview users on all branches
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1045, @createCodeReview_group_sid_id, @VIEW_TOPICS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1046, @createCodeReview_group_sid_id, @CREATE_CODE_REVIEW_MASK, 1, 0, 0 from BRANCHES;

-- setting permissions for leaveCommentsInCodeReview users on all branches
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1047, @leaveCommentsInCodeReview_group_sid_id, @VIEW_TOPICS_MASK, 1, 0, 0 from BRANCHES;
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) 
  select BRANCH_ID, 1048, @leaveCommentsInCodeReview_group_sid_id, @LEAVE_COMMENTS_IN_CODE_REVIEW_MASK, 1, 0, 0 from BRANCHES;
  
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
