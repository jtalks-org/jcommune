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

import org.jtalks.jcommune.service.security.acl.sids.SidFactory;
import org.jtalks.jcommune.service.security.acl.sids.UniversalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import javax.sql.DataSource;

/**
 * Gives possibility to implement custom Sid
 * @author Mikhail Stryzhonok
 * @see Sid
 * @see UniversalSid
 */
public class JtalksMutableAcService extends JdbcMutableAclService {

    private SidFactory sidFactory;

    public JtalksMutableAcService(DataSource dataSource, LookupStrategy lookupStrategy, AclCache aclCache) {
        super(dataSource, lookupStrategy, aclCache);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MutableAcl createAcl(ObjectIdentity objectIdentity) throws AlreadyExistsException {
        Assert.notNull(objectIdentity, "Object Identity required");

        // Check this object identity hasn't already been persisted
        if (retrieveObjectIdentityPrimaryKey(objectIdentity) != null) {
            throw new AlreadyExistsException("Object identity '" + objectIdentity + "' already exists");
        }

        // Need to retrieve the current principal, in order to know who "owns" this ACL (can be changed later on)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Sid sid = sidFactory.createPrincipal(auth);
        createObjectIdentity(objectIdentity, sid);

        // Retrieve the ACL via superclass (ensures cache registration, proper retrieval etc)
        Acl acl = readAclById(objectIdentity);
        Assert.isInstanceOf(MutableAcl.class, acl, "MutableAcl should be been returned");

        return (MutableAcl) acl;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected Long createOrRetrieveSidPrimaryKey(Sid sid, boolean allowCreate) {
        Assert.notNull(sid, "Sid required");
        Assert.isInstanceOf(UniversalSid.class, sid, "Unsupported sid implementation");

        String sidId = ((UniversalSid) sid).getSidId();
        boolean isPrinciple = ((UniversalSid) sid).isPrincipal();
        return createOrRetrieveSidPrimaryKey(sidId, isPrinciple, allowCreate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSidId(Sid sid) {
        Assert.notNull(sid, "Sid required");
        Assert.isInstanceOf(UniversalSid.class, sid, "Unsupported sid implementation");
        return ((UniversalSid) sid).getSidId();
    }

    public SidFactory getSidFactory() {
        return sidFactory;
    }

    public void setSidFactory(SidFactory sidFactory) {
        this.sidFactory = sidFactory;
    }
}
