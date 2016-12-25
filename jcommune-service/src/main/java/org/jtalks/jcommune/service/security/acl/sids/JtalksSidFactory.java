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

package org.jtalks.jcommune.service.security.acl.sids;

import org.jtalks.common.model.entity.Entity;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.User;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Decides what implementation of {@link Sid} should be created by the string representation of the sid name (or sid id)
 * or its class or whatever. There are might be either standard {@link Sid}s or custom sids like {@link UserGroupSid}.
 * If you want to add another possible implementation, take a look at the methods {@link #create(Entity)} and {@link
 * #parseCustomSid(String)}.
 *
 * @author stanislav bashkirtsev
 * @see Sid
 * @see UniversalSid
 */
public class JtalksSidFactory implements SidFactory {
    /**
     * This is a static factory, it shouldn't be instantiated.
     */
    public JtalksSidFactory() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sid createPrincipal(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return new UserSid((User) principal);
        } else if (UserSid.isAnonymous(principal.toString())) {
            return UserSid.createAnonymous();
        } else {
            return new UserSid(principal.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends Sid> createGrantedAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities) {
        List<Sid> sids = new ArrayList<Sid>();
        for (GrantedAuthority authority : grantedAuthorities) {
            sids.add(new GrantedAuthoritySid(authority));
        }
        return sids;
    }

    /**
     * Creates a list of sids by the underlying classes that were specified.
     *
     * @param receivers the list of receivers to be wrapped with the respective implementations of {@link Sid}s
     * @return the list of sids that wrap the specified receivers. The list might contain {@code null}s if some of
     *         specified receivers don't have the matching {@link Sid} implementation.
     * @see #create(Entity)
     */
    public List<Sid> create(List<? extends Entity> receivers) {
        List<Sid> sids = new ArrayList<Sid>(receivers.size());
        for (Entity next : receivers) {
            sids.add(create(next));
        }
        return sids;
    }

    /**
     * Creates the instance of custom sid that works with specified {@code receiver}. E.g. if the {@link User} or one of
     * its children was specified, then a {@link UserSid} instance will be returned.
     *
     * @param receiver the object to be wrapped with the Sid to become a real receiver from the Spring Security
     *                 perspective
     * @return the instance of custom sid that works with specified {@code receiver} or {@code null} if no respective
     *         sid class was found
     */
    public Sid create(Entity receiver) {
        if (User.class.isAssignableFrom(receiver.getClass())) {
            return new UserSid((User) receiver);
        } else if (Group.class.isAssignableFrom(receiver.getClass())) {
            return new UserGroupSid((Group) receiver);
        } else {
            return null;
        }
    }

    /**
     * Looks at the format of the {@code sidName} and finds out what sid implementation should be created. If the
     * specified name doesn't comply with the format of custom sids (prefix + {@link UniversalSid#SID_NAME_SEPARATOR} +
     * entity id), then ordinary Spring Security implementations are used (either {@link PrincipalSid} or {@link
     * GrantedAuthoritySid} which is defined by the second parameter {@code principal}.
     *
     * @param sidName   the name of the sid (its id) to look at its format and decide what implementation of sid should
     *                  be created
     * @param principal pass {@code true} if it's some kind of entity ID (like user or group), or {@code false} if it's
     *                  some standard role ({@link GrantedAuthoritySid}
     * @return created instance of sid that has the {@code sidName} as the sid id inside
     */
    @Override
    public Sid create(@Nonnull String sidName, boolean principal) {
        Sid toReturn = parseCustomSid(sidName);
        if (toReturn == null) {
            if (principal) {
                toReturn = new PrincipalSid(sidName);
            } else {
                toReturn = new GrantedAuthoritySid(sidName);
            }
        }
        return toReturn;
    }

    /**
     * Iterates through all the available sid prefixes and finds out what of them suites more to the specified sid
     * name.
     *
     * @param sidName the name of the sid to find the respective sid implementation
     * @return the instantiated sid implementation that complies with the pattern of specified sid name or {@code null}
     *         if no mapping for that name was found and there are no appropriate custom implementations of sid
     */
    private static Sid parseCustomSid(String sidName) {
        if (sidName.startsWith(UserGroupSid.SID_PREFIX)) {
            return new UserGroupSid(sidName);
        } else if (sidName.startsWith(UserSid.SID_PREFIX)) {
            return new UserSid(sidName);
        } else {
            return null;
        }
    }
}
