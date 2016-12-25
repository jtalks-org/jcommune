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
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.Sid;

import javax.sql.DataSource;

/**
 * Gives possibility to implement custom Sid
 * @author Mikhail Stryzhonok
 * @see Sid
 */
public class JtalksLookupStrategy extends BasicLookupStrategy {

    private SidFactory sidFactory;

    public JtalksLookupStrategy(DataSource dataSource, AclCache aclCache,
                                AclAuthorizationStrategy aclAuthorizationStrategy, AuditLogger auditLogger) {
        super(dataSource, aclCache, aclAuthorizationStrategy, auditLogger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Sid createSid(boolean isPrincipal, String sid) {
        return sidFactory.create(sid, isPrincipal);
    }

    public SidFactory getSidFactory() {
        return sidFactory;
    }

    public void setSidFactory(SidFactory sidFactory) {
        this.sidFactory = sidFactory;
    }
}
