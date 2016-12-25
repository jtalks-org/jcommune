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
import org.jtalks.common.model.entity.Branch;
import org.jtalks.common.model.entity.Entity;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.service.security.acl.sids.UserGroupSid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains coarse-grained operations with Spring ACL to manage the permissions of Groups & Users for the actions on
 * entities like Branch or Topic.
 *
 * @author Kirill Afonin
 */
public class AclManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final MutableAclService mutableAclService;
    private GroupDao groupDao;
    private AclUtil aclUtil;

    public AclManager(@Nonnull MutableAclService mutableAclService) {
        this.mutableAclService = mutableAclService;
        aclUtil = new AclUtil(mutableAclService);
    }

    /**
     * Gets only group permissions (where sid is {@link UserGroupSid}) and returns them for the specified entity (object
     * identity). Note, that if there are other records with sids different than {@link UserGroupSid}, they will be
     * filtered out.
     *
     * @param entity an object for which the permissions were given
     * @return permissions assigned on {@link Group}s without any other permissions. Returns empty collection if there
     *         are no group permissions given on the specified object identity
     */
    public List<GroupAce> getGroupPermissionsOn(@Nonnull Entity entity) {
        MutableAcl branchAcl = aclUtil.getAclFor(entity);
        return getGroupPermissions(branchAcl);
    }

    /**
     * Gets only group permissions (where sid is {@link UserGroupSid}) and returns them for the specified entity (object
     * identity). Note, that if there are other records with sids different than {@link UserGroupSid}, they will be
     * filtered out.
     *
     * @param entity an object identity for which the permissions were given
     * @return permissions assigned on {@link Group}s without any other permissions. Returns empty collection if there
     *         are no group permissions given on the specified object identity
     */
    public List<GroupAce> getGroupPermissionsOn(@Nonnull ObjectIdentity entity) {
        MutableAcl branchAcl = aclUtil.getAclFor(entity);
        return getGroupPermissions(branchAcl);
    }

    public List<GroupAce> getGroupPermissionsFilteredByPermissionOn(@Nonnull ObjectIdentity entity, JtalksPermission permission) {
        MutableAcl branchAcl = aclUtil.getAclFor(entity);
        return getGroupPermissionsFilteredByPermission(branchAcl, permission);
    }

    private List<GroupAce> getGroupPermissions(MutableAcl branchAcl) {
        List<AccessControlEntry> originalAces = branchAcl.getEntries();
        List<GroupAce> resultingAces = new ArrayList<GroupAce>(originalAces.size());
        for (AccessControlEntry originalAce : originalAces) {
            if (originalAce.getSid() instanceof UserGroupSid) {
                resultingAces.add(new GroupAce(originalAce));
            }
        }
        return resultingAces;
    }

    private List<GroupAce> getGroupPermissionsFilteredByPermission(MutableAcl branchAcl, JtalksPermission permission) {
        List<AccessControlEntry> originalAces = branchAcl.getEntries();
        List<GroupAce> resultingAces = new ArrayList<GroupAce>(originalAces.size());
        int permissionMask = permission.getMask();
        for (AccessControlEntry originalAce : originalAces) {
            if (originalAce.getSid() instanceof UserGroupSid
                    && originalAce.getPermission().getMask() == permissionMask) {
                resultingAces.add(new GroupAce(originalAce));
            }
        }
        return resultingAces;
    }

    /**
     * @deprecated use {@link #getGroupPermissionsOn}
     */
    @Deprecated()
    public List<GroupAce> getBranchPermissions(Branch branch) {
        MutableAcl branchAcl = aclUtil.getAclFor(branch);
        List<AccessControlEntry> originalAces = branchAcl.getEntries();
        List<GroupAce> resultingAces = new ArrayList<GroupAce>(originalAces.size());
        for (AccessControlEntry entry : originalAces) {
            resultingAces.add(new GroupAce(entry));
        }
        return resultingAces;
    }

    /**
     * TODO: NOT FINISHED! TO BE IMPLEMENTED
     *
     * @param user
     * @param branch
     * @return
     */
    public List<Permission> getPermissions(User user, Branch branch) {
        throw new UnsupportedOperationException();
//        List<Permission> permissions = new ArrayList<Permission>();
//
//        List<Group> groups = groupDao.getGroupsOfUser(user);
//
//        MutableAcl branchAcl = aclUtil.getAclFor(branch);
//        List<AccessControlEntry> originalAces = branchAcl.getEntries();
//
//        for (AccessControlEntry entry : originalAces) {
//            GroupAce groupAce = new GroupAce(entry);
//            if (groups.contains(groupAce.getGroup(groupDao))) {
//                permissions.add(groupAce.getBranchPermission());
//            }
//        }
//
//        return permissions;
    }

    /**
     * Grant permissions from list to every sid in list on {@code target} object.
     *
     * @param sids        list of sids
     * @param permissions list of permissions
     * @param target      secured object
     */
    public void grant(List<? extends Sid> sids, List<Permission> permissions, Entity target) {
        MutableAcl acl = aclUtil.grant(sids, permissions, target);
        mutableAclService.updateAcl(acl);
    }

    /**
     * Revoke permissions from lists for every sid in list on {@code target} entity
     *
     * @param sids        list of sids
     * @param permissions list of permissions
     * @param target      secured object
     */
    public void restrict(List<? extends Sid> sids, List<Permission> permissions, Entity target) {
        MutableAcl acl = aclUtil.restrict(sids, permissions, target);
        mutableAclService.updateAcl(acl);
    }

    /**
     * Delete permissions from list for every sid in list on {@code target} object.
     *
     * @param sids        list of sids
     * @param permissions list of permissions
     * @param target      secured object
     */
    public void delete(List<? extends Sid> sids, List<Permission> permissions, Entity target) {
        MutableAcl acl = aclUtil.delete(sids, permissions, target);
        mutableAclService.updateAcl(acl);
    }

   /**
     * Deletes all ACEs defined in the acl_entry table, wired with the presented SID, also wires owner_sid of OID
     * belongs to SID to another SID, deletes given SID defined in acl_sid.
     *
     * @param sid     to ACL delete
     * @param sidHeir will became the owner of ObjectIdentities belongs to sid
     */
    public void deleteSid(Sid sid, Sid sidHeir){
       mutableAclService.deleteEntriesForSid(sid, sidHeir);
    }

    /**
     * Delete object from acl. All permissions will be removed.
     *
     * @param clazz object {@code Class}
     * @param id    object id
     */
    public void deleteFromAcl(Class clazz, long id) {
        if (id <= 0) {
            throw new IllegalStateException("Object id must be greater then 0.");
        }
        ObjectIdentity oid = new ObjectIdentityImpl(clazz, id);
        mutableAclService.deleteAcl(oid, true);
        logger.debug("Deleted securedObject" + clazz.getSimpleName() + " with id:" + id);
    }


    public void setAclUtil(AclUtil aclUtil) {
        this.aclUtil = aclUtil;
    }
}
