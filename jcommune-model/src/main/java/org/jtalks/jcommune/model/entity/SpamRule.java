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

import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.dto.SpamRuleDto;

/**
 * @author Oleg Tkachenko
 */
public class SpamRule extends Entity {
    private String regex;
    private String description;
    private boolean enabled;

    protected SpamRule() {
    }

    public SpamRule(String regex, String description, boolean enabled) {
        this.regex = regex;
        this.description = description;
        this.enabled = enabled;
    }

    public String getRegex() {
        return regex;
    }

    public SpamRule setRegex(String regex) {
        this.regex = regex;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public SpamRule setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static SpamRule toEntity(SpamRuleDto dto) {
        if (dto == null) return null;
        return new SpamRule(dto.getRegex(), dto.getDescription(), dto.isEnabled());
    }
}
