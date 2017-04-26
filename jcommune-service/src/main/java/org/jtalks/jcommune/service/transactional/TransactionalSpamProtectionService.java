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

package org.jtalks.jcommune.service.transactional;

import com.google.common.collect.Sets;
import org.jtalks.common.service.exceptions.NotFoundException;
import org.jtalks.common.validation.ValidationError;
import org.jtalks.common.validation.ValidationException;
import org.jtalks.jcommune.model.dao.SpamRuleDao;
import org.jtalks.jcommune.model.entity.SpamRule;
import org.jtalks.jcommune.service.SpamProtectionService;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.util.regex.Pattern.matches;

/**
 * @author Oleg Tkachenko
 */
public class TransactionalSpamProtectionService extends AbstractTransactionalEntityService<SpamRule, SpamRuleDao> implements SpamProtectionService {

    public TransactionalSpamProtectionService(SpamRuleDao dao) {
        super(dao);
    }

    @Override
    public boolean isEmailInBlackList(String email) {
        if (email == null) return false;
        List<SpamRule> enabledRules = getDao().getEnabledRules();
        for (SpamRule enabledRule : enabledRules) {
            if (matches(enabledRule.getRegex(), email)) return true;
        }
        return false;
    }

    @Override
    public void saveOrUpdate(SpamRule rule) throws NotFoundException {
        try {
            Pattern.compile(rule.getRegex());
        } catch (PatternSyntaxException ex){
            throw new ValidationException(Sets.newHashSet(new ValidationError("regex", ex.getDescription() + " near index " + ex.getIndex())));
        }
        getDao().saveOrUpdate(rule);
    }

    @Override
    public void deleteRule(long id) {
        getDao().delete(id);
    }

    @Override
    public List<SpamRule> getAllRules() {
        return getDao().getAllRules();
    }

    @Override
    public SpamRule get(Long id) throws org.jtalks.jcommune.plugin.api.exceptions.NotFoundException {
        SpamRule spamRule = getDao().get(id);
        if (spamRule == null) throw new org.jtalks.jcommune.plugin.api.exceptions.NotFoundException("Spam rule with id = " + id + " is not found.");
        return spamRule;
    }
}
