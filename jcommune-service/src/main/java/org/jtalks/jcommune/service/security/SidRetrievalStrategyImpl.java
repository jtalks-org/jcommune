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

import org.jtalks.common.model.entity.Group;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.security.acl.sids.JtalksSidFactory;
import org.jtalks.jcommune.service.security.acl.sids.UserGroupSid;
import org.jtalks.jcommune.service.security.acl.sids.UserSid;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.Authentication;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * JCommune implementation of {@link SidRetrievalStrategy} that creates a {@link Sid}
 * for the principal by {@link JtalksSidFactory}. Created sids may be
 * {@link UserSid} or {@link UserGroupSid} type.
 *
 * @author Elena Lepaeva
 */
public class SidRetrievalStrategyImpl implements SidRetrievalStrategy {
    private JtalksSidFactory sidFactory;

    /**
     * @param sidFactory factory to work with principals
     */
    public SidRetrievalStrategyImpl(@Nonnull JtalksSidFactory sidFactory) {
        this.sidFactory = sidFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sid> getSids(Authentication authentication) {
        List<Sid> sids = new ArrayList<Sid>();
        sids.add(sidFactory.createPrincipal(authentication));

        if (!(authentication.getPrincipal() instanceof String)) {
            List<Group> groups = ((JCUser) authentication.getPrincipal()).getGroups();
            for (Group group : groups) {
                sids.add(sidFactory.create(group));
            }
        }
        return sids;
    }
}
