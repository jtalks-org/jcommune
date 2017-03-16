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

package org.jtalks.jcommune.model.utils;

import org.jtalks.jcommune.model.dto.SpamRuleDto;
import org.jtalks.jcommune.model.entity.SpamRule;

import java.util.ArrayList;
import java.util.List;

import static io.qala.datagen.RandomShortApi.*;

/**
 * @author Oleg Tkachenko
 */
public class SpamRuleUtils {

    public static SpamRule randomSpamRule() {
        return new SpamRule(unicode(1, 255), blankOr(unicode(1, 255)), bool());
    }

    public static SpamRuleDto randomSpamRuleDto() {
        return new SpamRuleDto(0, unicode(1, 255), blankOr(unicode(1, 255)), bool());
    }

    public static List<SpamRule> listOfRandomSpamRules(int size) {
        List<SpamRule> ruleList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ruleList.add(randomSpamRule());
        }
        return ruleList;
    }
}
