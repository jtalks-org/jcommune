/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.migrations;

import com.googlecode.flyway.core.migration.java.JavaMigration;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

/**
 *
 */
public class V25__Add_permissions_to_user_groups implements JavaMigration {
    private Map<String, Long> aclClassesMap = new HashMap<String, Long>();

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        List<Class> aclClasses = new ArrayList<Class>();
        aclClasses.add(Branch.class);
        aclClasses.add(Topic.class);
        aclClasses.add(Post.class);
        aclClasses.add(JCUser.class);

        for (Class oClass : aclClasses) {
            String name = oClass.getCanonicalName();
            Long id = getAclClassId(jdbcTemplate, name);
            if (id == null) {
                Long aclClassId;
                try {
                    aclClassId = jdbcTemplate.queryForLong("select max(id) from acl_class") + 1;
                } catch (DataAccessException e) {
                    aclClassId = 0L;
                }
                jdbcTemplate.execute("insert into acl_class(id,class) values(" + aclClassId + ", '" + name + "')");
                id = getAclClassId(jdbcTemplate, name);
            }
            aclClassesMap.put(name, id);
        }

        List<Long> branchIdList = jdbcTemplate.queryForList("select branch_id from branches", Long.class);

        for (Long id : branchIdList) {
            //User group
            Long userGroupSidId = createUserGroupSidId(jdbcTemplate, "usergroup:11");

            List<JtalksPermission> permissions = new ArrayList<JtalksPermission>(
                    Arrays.asList(BranchPermission.CREATE_POSTS,
                            BranchPermission.VIEW_TOPICS,
                            BranchPermission.CREATE_TOPICS,
                            BranchPermission.DELETE_OWN_POSTS));
            setPermissionsForGroup(jdbcTemplate, id, userGroupSidId, permissions, 1);

            //Admins
            userGroupSidId = createUserGroupSidId(jdbcTemplate, "usergroup:13");
            permissions = new ArrayList<JtalksPermission>(
                    Arrays.asList(BranchPermission.CREATE_POSTS,
                            BranchPermission.VIEW_TOPICS,
                            BranchPermission.CREATE_TOPICS,
                            BranchPermission.DELETE_OWN_POSTS,
                            BranchPermission.CLOSE_TOPICS,
                            BranchPermission.DELETE_OTHERS_POSTS,
                            BranchPermission.DELETE_TOPICS,
                            BranchPermission.MOVE_TOPICS,
                            BranchPermission.SPLIT_TOPICS));
            setPermissionsForGroup(jdbcTemplate, id, userGroupSidId, permissions, 1);

            //Banned users
            userGroupSidId = createUserGroupSidId(jdbcTemplate, "usergroup:12");
            setPermissionsForGroup(jdbcTemplate, id, userGroupSidId, permissions, 0);
        }
    }

    private void setPermissionsForGroup(JdbcTemplate jdbcTemplate,
                                        Long branchId,
                                        Long userGroupSidId,
                                        List<JtalksPermission> permissions,
                                        int grant) {

        Long acClassId = aclClassesMap.get(Branch.class.getCanonicalName());

        Long aclObjectIdentityId;
        try {
            aclObjectIdentityId = jdbcTemplate.queryForLong("select id from acl_object_identity where object_id_identity=" + branchId);
        } catch (DataAccessException e) {
            try {
                aclObjectIdentityId = jdbcTemplate.queryForLong("select max(id) from acl_object_identity") + 1;
            } catch (DataAccessException e1) {
                aclObjectIdentityId = 0L;
            }
            jdbcTemplate.execute("insert into acl_object_identity(id, object_id_class, " +
                    "object_id_identity, owner_sid, entries_inheriting) " +
                    "values(" + aclObjectIdentityId + ", "
                    + acClassId + ", "
                    + branchId + ",  "
                    + userGroupSidId
                    + ", 1)");
        }

        Long aclEntryId;
        try {
            aclEntryId = jdbcTemplate.queryForLong("select max(id) from acl_entry") + 1;
        } catch (DataAccessException e) {
            aclEntryId = 0L;
        }

        //create permissions:
        Long aceOrder;
        try {
            aceOrder = jdbcTemplate.queryForLong("select max(ace_order) " +
                    "from acl_entry where acl_object_identity=" + aclObjectIdentityId) + 1;
        } catch (DataAccessException e) {
            aceOrder = 0L;
        }
        for (JtalksPermission permission : permissions) {
            String insertPermission = "insert into acl_entry(id, acl_object_identity, " +
                    "ace_order, sid, mask, granting, audit_success, audit_failure) " +
                    "values(" + aclEntryId + ", "
                    + aclObjectIdentityId + ", "
                    + aceOrder + ", "
                    + userGroupSidId + ", "
                    + permission.getMask() + ", "
                    + grant + ", 0, 0)";

            jdbcTemplate.execute(insertPermission);
            aceOrder++;
            aclEntryId++;
        }
    }

    private Long getAclClassId(JdbcTemplate jdbcTemplate, String name) {
        Long id;
        try {
            id = jdbcTemplate.queryForLong("select id from acl_class where class = ?", name);
        } catch (DataAccessException e) {
            id = null;
        }
        return id;
    }

    private Long createUserGroupSidId(JdbcTemplate jdbcTemplate, String sid) {
        Long userGroupSidId = getUserGroupSidId(jdbcTemplate, sid);
        if (userGroupSidId == null) {
            Long aclSidId;
            try {
                aclSidId = jdbcTemplate.queryForLong("select max(id) from acl_sid") + 1;
            } catch (DataAccessException e) {
                aclSidId = 0L;
            }
            jdbcTemplate.execute("insert into acl_sid(id,principal,sid) values(" + aclSidId + ", 0, '" + sid + "')");
            userGroupSidId = getUserGroupSidId(jdbcTemplate, sid);
        }
        return userGroupSidId;
    }

    private Long getUserGroupSidId(JdbcTemplate jdbcTemplate, String sid) {
        Long id;
        try {
            id = jdbcTemplate.queryForLong("select id from acl_sid where sid = ?", sid);
        } catch (DataAccessException e) {
            id = null;
        }
        return id;
    }
}
