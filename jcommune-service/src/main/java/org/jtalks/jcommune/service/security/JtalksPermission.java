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

import org.springframework.security.acls.domain.AbstractPermission;
import org.springframework.security.acls.model.Permission;

/**
 *
 */
public class JtalksPermission extends AbstractPermission {
    public static final Permission EDIT_TOPIC = new JtalksPermission(16, 'T'); // 1
    public static final Permission CREATE_TOPIC = new JtalksPermission(2, 'T'); // 2
    public static final Permission DELETE_TOPIC = new JtalksPermission(3, 'T'); // 2

    protected JtalksPermission(int mask) {
        super(mask);
    }

    protected JtalksPermission(int mask, char code) {
        super(mask, code);
    }
}
