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

package org.jtalks.jcommune.model.dao.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jtalks.jcommune.model.dao.SpamRuleDao;
import org.jtalks.jcommune.model.entity.SpamRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.qala.datagen.RandomValue.length;
import static io.qala.datagen.StringModifier.Impls.suffix;
import static org.jtalks.jcommune.model.utils.SpamRuleUtils.listOfRandomSpamRules;
import static org.jtalks.jcommune.model.utils.SpamRuleUtils.randomSpamRule;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Oleg Tkachenko
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class SpamRuleHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    private @Autowired SpamRuleDao dao;
    private @Autowired SessionFactory sessionFactory;

    @Test
    public void shouldReturnAllSavedRules() {
        List<SpamRule> expectedList = persistObjects(listOfRandomSpamRules(10));
        List<SpamRule> actualList = dao.getAllRules();
        assertEquals(actualList.size(), expectedList.size());
        for (SpamRule expected : expectedList) {
            assertTrue(actualList.contains(expected));
        }
    }

    @Test
    public void shouldReturnAllEnabledRules(){
        List<SpamRule> randomSpamRules = persistObjects(listOfRandomSpamRules(10));
        List<SpamRule> expectedList = getEnabledRulesFrom(randomSpamRules);
        List<SpamRule> actualList = dao.getEnabledRules();
        assertEquals(actualList.size(), expectedList.size());
        for (SpamRule expected : expectedList) {
            assertTrue(actualList.contains(expected));
        }
    }

    @Test
    public void shouldNotBeAbleToInsertSqlInjection() {
        String sqlInjection = length(100).with(suffix("'\"")).alphanumeric();
        SpamRule expected = randomSpamRule().setRegex(sqlInjection).setDescription(sqlInjection);
        dao.saveOrUpdate(expected);
        flushAndClearCurrentSession();
        SpamRule actual = dao.get(expected.getId());
        assertReflectionEquals(expected, actual);
    }

    private List<SpamRule> getEnabledRulesFrom(List<SpamRule> rules) {
        List<SpamRule> enabledRules = new ArrayList<>(rules.size());
        for (SpamRule spamRule : rules) {
            if (spamRule.isEnabled()) enabledRules.add(spamRule);
        }
        return enabledRules;
    }

    private List<SpamRule> persistObjects(List<SpamRule> objects){
        Session session = sessionFactory.getCurrentSession();
        for (SpamRule object : objects) {
            session.save(object);
        }
        flushAndClearCurrentSession();
        return objects;
    }

    private void flushAndClearCurrentSession(){
        Session session = sessionFactory.getCurrentSession();
        session.flush();
        session.clear();
    }
}
