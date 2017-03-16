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

import org.hibernate.validator.constraints.Length;
import org.jtalks.jcommune.model.entity.SpamRule;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleg Tkachenko
 */
public class SpamRuleDto {
    private long id;
    @NotNull
    @Length(min = 1, max = 255)
    private String regex;
    @Length(max = 255)
    private String description;
    private boolean enabled;

    protected SpamRuleDto() {
    }

    public SpamRuleDto(long id, String regex, String description, boolean enabled) {
        this.id = id;
        this.regex = regex;
        this.description = description;
        this.enabled = enabled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRegex() {
        return regex;
    }

    public SpamRuleDto setRegex(String regex) {
        this.regex = regex;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public SpamRuleDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static SpamRuleDto fromEntity(SpamRule entity) {
        if (entity == null) return null;
        return new SpamRuleDto(entity.getId(), entity.getRegex(), entity.getDescription(), entity.isEnabled());
    }

    public static List<SpamRuleDto> fromEntities(List<SpamRule> original) {
        List<SpamRuleDto> dtoList = new ArrayList<>(original.size());
        for (SpamRule spamRule : original) {
            dtoList.add(SpamRuleDto.fromEntity(spamRule));
        }
        return dtoList;
    }
}
