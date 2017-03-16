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

import org.jtalks.jcommune.model.dao.SpamRuleDao;
import org.jtalks.jcommune.model.entity.SpamRule;
import org.jtalks.jcommune.service.SpamProtectionService;
import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static io.qala.datagen.RandomShortApi.alphanumeric;
import static io.qala.datagen.RandomValue.between;
import static io.qala.datagen.StringModifier.Impls.prefix;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Oleg Tkachenko
 */
public class TransactionalSpamProtectionServiceTest {
    private @Mock SpamRuleDao dao;
    private SpamProtectionService spamProtectionService;

    @BeforeMethod
    public void setUp(){
        initMocks(this);
        spamProtectionService = new TransactionalSpamProtectionService(dao);
    }

    @Test
    public void shouldReturnTrueIfEmailInBlackList(){
        String domain = randomDomain();
        String address = alphanumeric(10) + domain;
        List<SpamRule> spamRules = Collections.singletonList(spamRuleToBlock(domain));
        when(dao.getEnabledRules()).thenReturn(spamRules);
        boolean inBlackList = spamProtectionService.isEmailInBlackList(address);
        Assert.assertTrue(inBlackList);
    }

    @Test
    public void shouldReturnFalseIfEmailNotInBlackList(){
        String domain = randomDomain();
        String address = alphanumeric(10) + domain;
        List<SpamRule> spamRules = Collections.singletonList(spamRuleToBlock(randomDomain()));
        when(dao.getEnabledRules()).thenReturn(spamRules);
        boolean inBlackList = spamProtectionService.isEmailInBlackList(address);
        Assert.assertFalse(inBlackList);
    }

    private String randomDomain() {
        return between(3, 15).with(prefix("@")).alphanumeric();
    }

    private SpamRule spamRuleToBlock(String domain){
        return new SpamRule(".*" + domain, "testRule", true);
    }
}
