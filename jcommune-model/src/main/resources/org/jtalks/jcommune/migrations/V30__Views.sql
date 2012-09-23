CREATE VIEW BRANCHES_VIEW_TOPICS AS
 SELECT
     acl_entry.id, BRANCHES.BRANCH_ID, GROUPS.GROUP_ID
 FROM
     acl_entry, acl_object_identity, acl_class ,acl_sid, BRANCHES, GROUPS
 WHERE
         acl_entry.mask=6
     AND acl_entry.granting=1
     AND acl_object_identity.id=acl_entry.acl_object_identity
     AND acl_class.id=acl_object_identity.object_id_class
     AND acl_class.class='BRANCH'
     AND BRANCHES.BRANCH_ID=acl_object_identity.object_id_identity
     AND acl_entry.sid=acl_sid.id
     AND acl_sid.sid LIKE 'usergroup:%'
     AND GROUPS.GROUP_ID=SUBSTRING(acl_sid.sid,LENGTH('usergroup:')+1);