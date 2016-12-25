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

package org.jtalks.jcommune.service.security.acl;

import org.jtalks.common.model.dao.GroupDao;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.service.security.acl.sids.UserGroupSid;
import org.springframework.security.acls.model.AccessControlEntry;

/**
 * @author stanislav bashkirtsev
 */
public class GroupAce {
    private final AccessControlEntry ace;

    public GroupAce(AccessControlEntry ace) {
        if (!(ace.getSid() instanceof UserGroupSid)) {
            throw new IllegalArgumentException("The specified ACE has sid which is not of type: " + UserGroupSid.class);
        }
        this.ace = ace;
    }

    public Group getGroup(GroupDao groupDao) {
        long groupId = getGroupId();
        Group group = groupDao.get(groupId);
        throwIfNull(groupId, group);
        return group;
    }

    /**
     * @return id of associated {@link UserGroupSid} and its {@link Group}
     */
    public long getGroupId() {
        String groupIdString = ((UserGroupSid) ace.getSid()).getGroupId();
        return Long.parseLong(groupIdString);
    }

    public JtalksPermission getPermission() {
        JtalksPermission permission = BranchPermission.findByMask(getPermissionMask());
        if (permission == null) {
            permission = GeneralPermission.findByMask(getPermissionMask());
        }
        return permission;
    }

    public int getPermissionMask() {
        return ace.getPermission().getMask();
    }

    public boolean isGranting() {
        return ace.isGranting();
    }

    /**
     * Defines whether the ACE is restricting and SID is not allowed to perform action.
     *
     * @return true if the permission is restricted or false if it's granted
     */
    public boolean isRestricting() {
        return !isGranting();
    }

    public AccessControlEntry getOriginalAce() {
        return ace;
    }

    private void throwIfNull(long groupId, Group group) {
        if (group == null) {
            throw new ObsoleteAclException(groupId);
        }
    }

    @SuppressWarnings("serial")
    public static class ObsoleteAclException extends RuntimeException {

        public ObsoleteAclException(long groupId) {
            super(new StringBuilder("A group with ID [").append(groupId).append("] was removed")
                    .append("but this ID is still registered as a Permission owner (SID) in ACL tables. ")
                    .append("To resolve this issue you should manually remove records from ACL tables ")
                    .append("Note, that this is a bug and this issue should be reported to be corrected in ")
                    .append("future versions.").toString());
        }
    }
}
