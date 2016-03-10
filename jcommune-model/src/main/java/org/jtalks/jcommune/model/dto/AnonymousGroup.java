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
package org.jtalks.jcommune.model.dto;

import org.jtalks.common.model.entity.Group;

/**
 * Represents Anonymous Group for permissions granting by UI. This class contains only {@code ANONYMOUS_GROUP} field.
 * Application doesn't store {@code ANONYMOUS_GROUP} in data base.
 */
public final class AnonymousGroup extends Group {
    public static final Group ANONYMOUS_GROUP = new AnonymousGroup("Anonymous Users");

    /**
     * @param name group name
     */
    private AnonymousGroup(String name) {
        super(name);
    }
}
