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

import org.jtalks.jcommune.model.entity.SpamRule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

import static io.qala.datagen.RandomShortApi.alphanumeric;
import static io.qala.datagen.RandomShortApi.nullOrEmpty;
import static org.jtalks.jcommune.model.utils.SpamRuleUtils.*;
import static org.testng.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Oleg Tkachenko
 */
public class SpamRuleDtoTest {
    private Validator validator;

    @BeforeClass
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void convertFromEntityShouldCopyRequiredFields(){
        SpamRule expected = randomSpamRule();
        SpamRuleDto dto = SpamRuleDto.fromEntity(expected);
        SpamRule actual = dto.toEntity();
        actual.setUuid(expected.getUuid());
        assertReflectionEquals(expected, actual);
    }

    @Test
    public void convertFromEntitiesShouldConvertAllEntries(){
        List<SpamRule> entities = listOfRandomSpamRules(5);
        List<SpamRuleDto> dtoList = SpamRuleDto.fromEntities(entities);
        for (int i = 0; i < entities.size(); i++) {
            SpamRule expected = entities.get(i);
            SpamRule actual = dtoList.get(i).toEntity();
            actual.setUuid(expected.getUuid());
            assertReflectionEquals(expected, actual);
        }
    }

    @Test
    public void validationOfValidSpamRulePass(){
        SpamRuleDto spamRuleDto = randomSpamRuleDto();
        Set<ConstraintViolation<SpamRuleDto>> constraintViolations = validator.validate(spamRuleDto);
        assertEquals(constraintViolations.size(), 0);
    }

    @Test
    public void emptyRegexValidationFails(){
        SpamRuleDto spamRuleDto = randomSpamRuleDto().setRegex(nullOrEmpty());
        Set<ConstraintViolation<SpamRuleDto>> constraintViolations = validator.validate(spamRuleDto);
        assertEquals(constraintViolations.size(), 1);
    }

    @Test
    public void regexValidationFailsIfLengthMoreThan255(){
        SpamRuleDto spamRuleDto = randomSpamRuleDto().setRegex(alphanumeric(256));
        Set<ConstraintViolation<SpamRuleDto>> constraintViolations = validator.validate(spamRuleDto);
        assertEquals(constraintViolations.size(), 1);
    }

    @Test
    public void descriptionValidationFailsIfLengthMoreThan255(){
        SpamRuleDto spamRuleDto = randomSpamRuleDto().setDescription(alphanumeric(256));
        Set<ConstraintViolation<SpamRuleDto>> constraintViolations = validator.validate(spamRuleDto);
        assertEquals(constraintViolations.size(), 1);
    }
}
