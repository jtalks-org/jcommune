DROP VIEW BRANCHES_VIEW_TOPICS;
CREATE VIEW BRANCHES_VIEW_TOPICS AS
    SELECT acl_entry.id AS ID, BRANCHES.BRANCH_ID, SUBSTRING(acl_sid.sid,INSTR(acl_sid.sid,":")+1) AS SID
        FROM  acl_entry, acl_object_identity, acl_class ,acl_sid, BRANCHES
        WHERE acl_entry.mask=6
            AND acl_entry.granting=1
            AND acl_object_identity.id=acl_entry.acl_object_identity
            AND acl_class.id=acl_object_identity.object_id_class
            AND acl_class.class='BRANCH'
            AND BRANCHES.BRANCH_ID=acl_object_identity.object_id_identity
            AND acl_sid.id=acl_entry.sid
            AND (acl_sid.sid LIKE 'usergroup:%' OR acl_sid.sid LIKE 'user:anonymousUser');