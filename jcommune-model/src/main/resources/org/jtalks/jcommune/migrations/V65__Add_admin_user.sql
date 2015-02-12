set @adminUserName := 'admin';
set @adminPassword := 'admin';
set @adminGroupName := 'Administrators';
set @adminGroupDescription := 'Administrators group.';
set @adminRoleName := 'ADMIN_ROLE';
set @adminEMail := 'admin@jtalks.org';
set @moderatorsGroupName := 'Moderators';
set @moderatorsGroupDescription := 'General group for all moderators';
set @forumComponentName := 'JTalks Sample Forum';
set @forumComponentType := 'FORUM';
set @forumComponentAclClass :='COMPONENT';
set @forumComponentId := 2;
set @availableUsersText := 'Available users: admin/admin';
set @isPrincipal := true;
set @notPricipal := false;
set @adminMask := 16;

insert into COMPONENTS (CMP_ID, COMPONENT_TYPE, UUID, `NAME`, DESCRIPTION)
  select @forumComponentId, @forumComponentType, UUID(), @forumComponentName, @availableUsersText
  from dual
  where not exists (select 1 from COMPONENTS where COMPONENT_TYPE = @forumComponentType);

-- 'FROM COMPONENTS' are not used, but query mast contain 'FROM dual' clause
--  @see <a href="http://dev.mysql.com">http://dev.mysql.com/doc/refman/5.0/en/select.html/a>.
insert into GROUPS (UUID, `NAME`, DESCRIPTION)
  select UUID(), @moderatorsGroupName, @moderatorsGroupDescription
  from dual
  where not exists (select GROUP_ID from GROUPS where `NAME` = @moderatorsGroupName);

insert into GROUPS (UUID, `NAME`, DESCRIPTION)
  select UUID(), @adminGroupName, @adminGroupDescription
  from dual
  where not exists (select gr.GROUP_ID from GROUPS gr where gr.NAME = @adminGroupName);

insert into USERS (UUID, FIRST_NAME, LAST_NAME, USERNAME, ENCODED_USERNAME, EMAIL, PASSWORD, ROLE, SALT, ENABLED)
  select UUID(), @adminUserName, @adminUserName, @adminUserName, @adminUserName, @adminEMail, MD5(@adminPassword), @adminRoleName, '', true
  from dual
  where not exists (select 1 from USERS where USERNAME = @adminUserName);

alter table JC_USER_DETAILS add unique (USER_ID);
insert into JC_USER_DETAILS (USER_ID, REGISTRATION_DATE, POST_COUNT)
  select ID, NOW(), 0
  from USERS
  where USERNAME = @adminUserName and not exists (select 1 from JC_USER_DETAILS where USER_ID = USERS.ID);

-- Adding created Admin to Administrators group(created at this migration or common migration) ).
set @admin_group_id := (select GROUP_ID from GROUPS where `NAME` = @adminGroupName);
insert into GROUP_USER_REF (GROUP_ID, USER_ID)
  select @admin_group_id, ID
  from USERS
  where USERNAME = @adminUserName and not exists (select *
                                           from GROUP_USER_REF
                                           where GROUP_ID = @admin_group_id and USER_ID = USERS.ID);

-- Adding record with added component class.
set @component_acl_class := 1;
set @group_acl_class := 2;
set @branch_acl_class := 3;

insert ignore into acl_class (ID, CLASS)
  values (@branch_acl_class, 'BRANCH'), (@group_acl_class, 'GROUP'), (@component_acl_class, 'COMPONENT');

set @acl_sid_admin_group := (select GROUP_CONCAT('usergroup:', CONVERT(GROUP_ID, char(19)))
                       from GROUPS g
                       where g.NAME = @adminGroupName);
set @acl_sid_admin_user := (select GROUP_CONCAT('user:', CONVERT(ID, char(19)))
                      from USERS u
                      where u.USERNAME = @adminUserName);
set @forum_object_id_identity := (select component.CMP_ID
                            from COMPONENTS component
                            where component.COMPONENT_TYPE = @forumComponentType);

-- Adding record to acl_sid table, this record wires sid and user id.
insert into acl_sid (principal, sid)
  select @isPrincipal, @acl_sid_admin_user
  from dual
  where not exists (select acl_sid.sid from acl_sid where sid = @acl_sid_admin_user);

set @acl_sid_admin_user_id := (select sid.id from acl_sid sid where sid.sid = @acl_sid_admin_user);

-- Adding record to acl_sid table, this record wires sid and group id.
insert ignore into acl_sid (principal, sid)
  values(@notPrincipal, @acl_sid_admin_group);

set @acl_sid_admin_group_id := (select sid.id from acl_sid sid where sid.sid = @acl_sid_admin_group);
set @forum_component_acl_class_id := (select class.id from acl_class class where class.class = @forumComponentAclClass);

insert ignore into acl_object_identity (object_id_class, object_id_identity, owner_sid, entries_inheriting)
  select @forum_component_acl_class_id, @forum_object_id_identity, @acl_sid_admin_user_id, 1
  from dual;

set @forum_acl_object_identity_id := (select aoi.id from acl_object_identity aoi
                                where aoi.object_id_class = @forum_component_acl_class_id
                                      and aoi.object_id_identity = @forum_object_id_identity);

set @ace_order_max := (select MAX(ae.ace_order) from acl_entry ae);
set @ace_order := (case when  @ace_order_max is null then 0 else @ace_order_max + 1 end);

insert ignore into acl_entry (acl_object_identity, sid, ace_order, mask, granting, audit_success, audit_failure)
  select @forum_acl_object_identity_id, @acl_sid_admin_group_id, @ace_order, @adminMask, 1, 0 , 0
  from dual;