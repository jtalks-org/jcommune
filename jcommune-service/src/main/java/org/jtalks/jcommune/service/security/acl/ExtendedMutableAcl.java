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

import org.apache.commons.lang.Validate;
import org.springframework.security.acls.model.*;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;

/**
 * This implementations of {@link MutableAcl} adds additional handy methods like {@link #delete(AccessControlEntry)}.
 * It's actually a wrapper that delegates all the implemented methods to the internal {@link MutableAcl} that is
 * accepted in the constructor.
 *
 * @author stanislav bashkirtsev
 */
public class ExtendedMutableAcl implements MutableAcl {
    /**
     * This is a mock instance that is used usually just for testing purposes, most of its methods will throw {@link
     * NullPointerException} if you invoke them.
     */
    public final static ExtendedMutableAcl NULL_ACL = new ExtendedMutableAcl();

    private final MutableAcl acl;

    /**
     * Use factory methods like {@link #create(MutableAcl)} to instantiate the objects.
     *
     * @param acl the internal delegate-instance
     */
    private ExtendedMutableAcl(@Nonnull MutableAcl acl) {
        Validate.notNull(acl, "Acl can't be null while creating ExtendedMutableAcl!");
        this.acl = acl;
    }

    /**
     * An empty construct that shouldn't be ever used :) It's purpose is only to serve for {@link #NULL_ACL}.
     */
    private ExtendedMutableAcl() {
        acl = null;
    }

    /**
     * Finds the specified {@link AccessControlEntry} and removes it from the entry list of the {@link Acl}.
     *
     * @param entryToDelete the entry to remove from the {@link Acl}
     * @return the id of the removed entry or {@code -1} if no such entry was found
     */
    public int delete(@Nonnull AccessControlEntry entryToDelete) {
        List<AccessControlEntry> aclEntries = acl.getEntries();
        for (int i = 0; i < aclEntries.size(); i++) {
            if (aclEntries.get(i).equals(entryToDelete)) {
                acl.deleteAce(i);
                return i;
            }
        }
        return -1;
    }

    /**
     * Deletes all the specified entries from the {@link Acl#getEntries()} list. If some or all entries were not found
     * in the list, those elements are not removed and nothing happens.
     *
     * @param entriesToDelete the list of entries to remove from the {@link Acl#getEntries()}
     */
    public void delete(@Nonnull List<AccessControlEntry> entriesToDelete) {
        for (AccessControlEntry next : entriesToDelete) {
            delete(next);
        }
    }

    /**
     * Adds all the permissions to the specified sid. Note, that it doesn't check whether there is already such Sid with
     * such Permission in the ACL, so you should ensures this on your own.
     *
     * @param sid         sid to grant the permissions to
     * @param permissions the list of permissions that should be granted to the Sid
     * @param granting    specify {@code true} if you want to grant the permission or {@code false} if you want to
     *                    restrict all the permissions
     * @see #addPermissions(List, List, boolean)
     */
    public void addPermissions(@Nonnull Sid sid, @Nonnull List<Permission> permissions, boolean granting) {
        int entriesAmount = acl.getEntries().size();
        for (Permission permission : permissions) {
            acl.insertAce(entriesAmount++, permission, sid, granting);
        }
    }

    /**
     * Adds all the permissions to all the specified sids. Note, that it doesn't check whether there are already such
     * Sids with such Permission in the ACL, so you should check it on your own.
     *
     * @param sids        the sids to grant the permissions to
     * @param permissions the list of permissions that should be granted to the Sid
     * @param granting    specify {@code true} if you want to grant the permission or {@code false} if you want to
     *                    restrict all the permissions
     * @see #addPermissions(Sid, List, boolean)
     */
    public void addPermissions(@Nonnull List<? extends Sid> sids, @Nonnull List<Permission> permissions, boolean granting) {
        for (Sid recipient : sids) {
            addPermissions(recipient, permissions, granting);
        }
    }

    /**
     * Wraps the specified {@link MutableAcl} with the instance of {@link ExtendedMutableAcl} and returns the latter.
     *
     * @param acl the acl to be wrapped with the {@link ExtendedMutableAcl}
     * @return a new instance of {@link ExtendedMutableAcl} that wraps the specified {@code acl}
     */
    public static ExtendedMutableAcl create(@Nonnull MutableAcl acl) {
        return new ExtendedMutableAcl(acl);
    }

    /**
     * Wraps the specified {@link MutableAcl} with the instance of {@link ExtendedMutableAcl} and returns the latter.
     * Throws exception if the specified parameter is not an instance of {@link MutableAcl}.
     *
     * @param acl the acl to be wrapped with the {@link ExtendedMutableAcl}
     * @return a new instance of {@link ExtendedMutableAcl} that wraps the specified {@code acl}
     * @throws ClassCastException if the specified parameter is not an instance of {@link MutableAcl}
     */
    public static ExtendedMutableAcl castAndCreate(@Nonnull Acl acl) {
        return new ExtendedMutableAcl((MutableAcl) acl);
    }

    /**
     * Gets the wrapped instance that was specified into the factory methods.
     *
     * @return the wrapped instance that was specified into the factory methods
     */
    public MutableAcl getAcl() {
        return acl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAce(int aceIndex) throws NotFoundException {
        acl.deleteAce(aceIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertAce(
            int atIndexLocation, Permission permission, Sid sid, boolean granting) throws NotFoundException {
        acl.insertAce(atIndexLocation, permission, sid, granting);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AccessControlEntry> getEntries() {
        return acl.getEntries();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable getId() {
        return acl.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectIdentity getObjectIdentity() {
        return acl.getObjectIdentity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEntriesInheriting() {
        return acl.isEntriesInheriting();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGranted(List<Permission> permission, List<Sid> sids,
                             boolean administrativeMode) throws NotFoundException, UnloadedSidException {
        return acl.isGranted(permission, sids, administrativeMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSidLoaded(List<Sid> sids) {
        return acl.isSidLoaded(sids);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEntriesInheriting(boolean entriesInheriting) {
        acl.setEntriesInheriting(entriesInheriting);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOwner(Sid newOwner) {
        acl.setOwner(newOwner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sid getOwner() {
        return acl.getOwner();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParent(Acl newParent) {
        acl.setParent(newParent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Acl getParentAcl() {
        return acl.getParentAcl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAce(int aceIndex, Permission permission) throws NotFoundException {
        acl.updateAce(aceIndex, permission);
    }

}
