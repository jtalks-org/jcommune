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
package org.jtalks.jcommune.service.security;

import org.jtalks.common.model.entity.Entity;
import org.jtalks.common.security.acl.AclUtil;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;

import javax.annotation.Nonnull;

/**
 * This implementation of {@link ObjectIdentityRetrievalStrategy}
 * is used by Spring security custom tags.
 *
 * @author Elena Lepaeva
 */
public class ObjectIdentityRetrievalStrategyImpl implements ObjectIdentityRetrievalStrategy {
    private final AclUtil aclUtil;

    /**
     * @param aclUtil utilities to work with Spring ACL
     */
    public ObjectIdentityRetrievalStrategyImpl(@Nonnull AclUtil aclUtil) {
        this.aclUtil = aclUtil;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectIdentity getObjectIdentity(Object domainObject) {
        return aclUtil.createIdentityFor((Entity) domainObject);
    }
}
