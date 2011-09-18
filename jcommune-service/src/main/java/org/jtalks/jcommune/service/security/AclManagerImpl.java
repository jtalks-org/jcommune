/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.service.security;

import org.jtalks.common.model.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import java.util.List;

/**
 * Implementetion of {@link AclManager} interface.
 * Manage ACLs using Spring Security facilities.
 *
 * @author Kirill Afonin
 */
public class AclManagerImpl implements AclManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private MutableAclService mutableAclService;

    /**
     * Constructor creates instance.
     *
     * @param mutableAclService spring security service for ACLs
     */
    public AclManagerImpl(MutableAclService mutableAclService) {
        this.mutableAclService = mutableAclService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void grant(List<Sid> sids, List<Permission> permissions, Entity target) {
        ObjectIdentity oid = createIdentityFor(target);
        MutableAcl acl = getAclFor(oid);
        grantPermissionsToSids(sids, permissions, target, acl);
        mutableAclService.updateAcl(acl);
    }

    /**
     * Grant every permission from list to every sid from list.
     *
     * @param sids        list of sids
     * @param permissions list of permissions
     * @param target      securable object
     * @param acl         ACL of this object
     */
    private void grantPermissionsToSids(List<Sid> sids, List<Permission> permissions, Entity target,
                                        MutableAcl acl) {
        int aclIndex = acl.getEntries().size();
        for (Sid recipient : sids) {
            for (Permission permission : permissions) {
                // add permission to acl for recipient
                acl.insertAce(aclIndex++, permission, recipient, true);
                logger.debug("Added permission mask {} for Sid {} securedObject {} id {}",
                        new Object[]{permission.getMask(), recipient, target.getClass().getSimpleName(),
                                target.getId()});
            }
        }
    }

    /**
     * Get existing ACL for identity.
     * If ACL does not exist it will be created.
     *
     * @param oid object identity
     * @return ACL for this object identity
     */
    private MutableAcl getAclFor(ObjectIdentity oid) {
        MutableAcl acl;
        try {
            acl = (MutableAcl) mutableAclService.readAclById(oid);
        } catch (NotFoundException nfe) {
            acl = mutableAclService.createAcl(oid);
        }
        return acl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(List<Sid> sids, List<Permission> permissions, Entity target) {
        ObjectIdentity oid = createIdentityFor(target);
        MutableAcl acl = (MutableAcl) mutableAclService.readAclById(oid);

        List<AccessControlEntry> entries = acl.getEntries(); // it's copy
        int i = 0;
        // search for sid-permission pair
        for (AccessControlEntry entry : entries) {
            for (Sid recipient : sids) {
                for (Permission permission : permissions) {
                    if (entry.getSid().equals(recipient) &&
                            entry.getPermission().equals(permission)) {
                        acl.deleteAce(i); // delete from original list
                        logger.debug("Deleted from object {} id {} ACL permission {} for recipient {}",
                                new Object[]{target.getClass().getSimpleName(), target.getId(),
                                        permission, recipient});
                        i--; // because list item deleted in original list
                    }
                }
            }
            i++;
        }

        mutableAclService.updateAcl(acl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteFromAcl(Class clazz, long id) {
        if (id <= 0) {
            throw new IllegalStateException("Object id must be greater then 0.");
        }
        ObjectIdentity oid = new ObjectIdentityImpl(clazz, id);
        mutableAclService.deleteAcl(oid, true);
        logger.debug("Deleted securedObject" + clazz.getSimpleName() + " with id:" + id);
    }

    /**
     * Creates {@code ObjectIdentity} for {@code securedObject}
     *
     * @param securedObject object
     * @return identity with {@code securedObject} class name and id
     */
    private ObjectIdentity createIdentityFor(Entity securedObject) {
        if (securedObject.getId() <= 0) {
            throw new IllegalStateException("Object id must be assigned before creating acl.");
        }
        return new ObjectIdentityImpl(securedObject.getClass(),
                securedObject.getId());
    }
}
