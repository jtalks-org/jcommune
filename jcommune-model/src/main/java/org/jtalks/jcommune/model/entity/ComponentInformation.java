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

package org.jtalks.jcommune.model.entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Class keeping administrative information about the component
 */
public class ComponentInformation {
    private static final int NAME_MAX_SIZE = 255;

    @NotNull(message = "{validation.not_null}")
    @Size(max = NAME_MAX_SIZE, message = "{validation.links.title.length}")
    private String name;
    private String description;
    private String logoTooltip;
    private byte[] logo;

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoTooltip() {
        return logoTooltip;
    }

    public void setLogoTooltip(String logoTooltip) {
        this.logoTooltip = logoTooltip;
    }
}
