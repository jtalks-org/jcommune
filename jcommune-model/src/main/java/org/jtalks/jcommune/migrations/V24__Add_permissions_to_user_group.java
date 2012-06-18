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
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 *
 */
public class V24__Add_permissions_to_user_group implements JavaMigration {
    private Map<String, Long> aclClassesMap;

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        List<Class> aclClasses = new ArrayList<Class>();
        aclClasses.add(Branch.class);
        aclClasses.add(Topic.class);
        aclClasses.add(Post.class);
        aclClasses.add(JCUser.class);

        aclClassesMap = new HashMap<String, Long>();

        for (Class oClass : aclClasses) {
            String name = oClass.getCanonicalName();
            Long id = getAclClassId(jdbcTemplate, name);
            if (id == null) {
                Long maxId = jdbcTemplate.query("select max(id) from acl_classes",
                        new Object[]{name}, new ResultSetExtractor<Long>() {
                    @Override
                    public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                        if (rs.next()) {
                            return rs.getLong(1);
                        }
                        return null;
                    }
                });
                jdbcTemplate.update("insert into acl_classes(id,class) values(?, ?)", maxId + 1, name);
                id = getAclClassId(jdbcTemplate, name);
            }
            aclClassesMap.put(name, id);
        }


        List<Long> branchIdList = jdbcTemplate.query("select branch_id from branches", new ResultSetExtractor<List<Long>>() {
            @Override
            public List<Long> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Long> branchIdList = new ArrayList<Long>();
                while (rs.next()) {
                    branchIdList.add(rs.getLong(1));
                }
                return branchIdList;
            }
        });

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
        Long maxId = jdbcTemplate.query("select max(id) from acl_object_identity", new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return null;
            }
        });
        jdbcTemplate.update("insert into acl_object_identity(id, object_id_class, " +
                "object_id_identity, parent_object, owner_id, entries_inheriting) " +
                "values(?, ?, ?, ?, ?, ?)", maxId + 1, acClassId, branchId, null, userGroupSidId, 1);

        Long maxIdAclEntry = jdbcTemplate.query("select max(id) from acl_entry", new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return null;
            }
        });

        //create permissions:
        int aceOrder = 0;
        String insertPermission = "insert into acl_entry(id, acl_object_identity, " +
                "ace_order, sid, mask, granting, audit_success, audit_failure) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?)";
        for (JtalksPermission permission : permissions) {
            jdbcTemplate.update(insertPermission, maxIdAclEntry + 1, maxId + 1, aceOrder,
                    userGroupSidId, permission.getMask(), grant, 0, 0);
            aceOrder++;
        }
    }

    private Long getAclClassId(JdbcTemplate jdbcTemplate, String name) {
        return jdbcTemplate.query("select id from acl_classes where class = ?",
                new Object[]{name}, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return null;
            }
        });
    }

    private Long createUserGroupSidId(JdbcTemplate jdbcTemplate, String sid) {
        Long userGroupSidId = getUserGroupSidId(jdbcTemplate, sid);
        if (userGroupSidId == null) {
            Long maxId = jdbcTemplate.query("select max(id) from acl_sid", new ResultSetExtractor<Long>() {
                @Override
                public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                    return null;
                }
            });
            jdbcTemplate.update("insert into acl_sid(id,principal,sid) values(?, ?, ?)", maxId + 1, 0, sid);
            userGroupSidId = getUserGroupSidId(jdbcTemplate, sid);
        }
        return userGroupSidId;
    }

    private Long getUserGroupSidId(JdbcTemplate jdbcTemplate, String sid) {

        return jdbcTemplate.query("select id from acl_sid where sid = ?",
                new Object[]{sid}, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return null;
            }
        });
    }
}
