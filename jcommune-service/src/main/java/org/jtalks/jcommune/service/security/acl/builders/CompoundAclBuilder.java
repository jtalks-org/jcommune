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

package org.jtalks.jcommune.service.security.acl.builders;

import org.jtalks.common.model.entity.Entity;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.jcommune.service.security.acl.AclManager;
import org.jtalks.jcommune.service.security.acl.sids.JtalksSidFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The class that implements all the AclBuilder related interfaces like {@link AclTo} or {@link AclFlush}, so it
 * actually is a combination of ACL operations. Don't use it directly, use {@link AclBuilders} instead to upcast it to
 * the respective interfaces. The only operation that actually pushes the changes to the data store is {@link
 * #flush()}.
 *
 * @author stanislav bashkirtsev
 * @see AclBuilders
 */
public class CompoundAclBuilder<T extends Entity> implements AclAction<T>, AclTo<T>, AclFrom<T>, AclOn, AclFlush {
    private final List<Permission> permissions = new ArrayList<Permission>();
    private final List<Sid> sids = new ArrayList<Sid>();
    private final AclManager aclManager;
    private JtalksSidFactory sidFactory = new JtalksSidFactory();
    private Entity objectIdentity;
    private Actions action;

    /**
     * Constructs the full blown acl builder, usually you shouldn't use this constructor and instead work with {@link
     * AclBuilders}, but if you're writing tests or creating your own builders API, this might be useful for you.
     *
     * @param aclManager acl builder works with the ACL Manager in order to access the data store and actually work with
     *                   permissions
     */
    public CompoundAclBuilder(@Nonnull AclManager aclManager) {
        this.aclManager = aclManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclTo<T> grant(@Nonnull JtalksPermission... permissions) {
        addPermissions(Actions.GRANT, permissions);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclTo<T> restrict(@Nonnull JtalksPermission... permissions) {
        addPermissions(Actions.RESTRICT, permissions);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclFrom<T> delete(@Nonnull JtalksPermission... permissions) {
        addPermissions(Actions.DELETE, permissions);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclOn from(@Nonnull T... sids) {
        this.sids.addAll(sidFactory.create(Arrays.asList(sids)));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclOn to(@Nonnull T... sids) {
        this.sids.addAll(sidFactory.create(Arrays.asList(sids)));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AclFlush on(@Nonnull Entity objectIdentity) {
        this.objectIdentity = objectIdentity;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() {
        if (action == Actions.GRANT) {
            aclManager.grant(clone(sids), clone(permissions), objectIdentity);
        } else if (action == Actions.RESTRICT) {
            aclManager.restrict(clone(sids), clone(permissions), objectIdentity);
        } else {
            aclManager.delete(clone(sids), clone(permissions), objectIdentity);
        }
    }

    /**
     * {@inheritDoc}
     */
    private void addPermissions(Actions action, JtalksPermission... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
        this.action = action;
    }

    /**
     * {@inheritDoc}
     */
    private <T> List<T> clone(List<T> list) {
        return new ArrayList<T>(list);
    }
}
