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
package org.jtalks.jcommune.plugin.api.service.nontransactional;

import org.jtalks.jcommune.model.entity.JCommuneProperty;

/**
 * Class that holds properties and allows access to these properties from plugins
 *
 * This class is  singleton because we can't use spring dependency injection mechanism in plugins due plugins can be
 * added or removed in runtime.
 *
 *
 * @author Mikhail Stryzhonok
 */
public class PropertiesHolder {

    private static final PropertiesHolder INSTANCE = new PropertiesHolder();

    private JCommuneProperty allPagesTitlePrefixProperty;

    /**
     * Use {@link #getInstance()}, this class is singleton
     */
    private PropertiesHolder() {

    }

    /**
     * Gets instance of this class
     *
     * @return instance of {@link PropertiesHolder}
     */
    public static PropertiesHolder getInstance() {
        return INSTANCE;
    }

    /**
     * Gets prefix for titles of all pages
     *
     * @return prefix for titles of all pages
     */
    public String getAllPagesTitlePrefix() {
        return allPagesTitlePrefixProperty.getValue();
    }

    public void setAllPagesTitlePrefixProperty(JCommuneProperty allPagesTitlePrefixProperty) {
        this.allPagesTitlePrefixProperty = allPagesTitlePrefixProperty;
    }
}
