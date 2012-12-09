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
package org.jtalks.jcommune.model.validation.validators;

import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.Range;
import org.jtalks.jcommune.model.entity.PollItem;
import org.jtalks.jcommune.model.validation.annotations.PollItemsSize;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * It validates the size of the list of poll items.
 * 
 * @author Anuar_Nurmakanov
 * 
 */
public class PollItemsSizeValidator implements ConstraintValidator<PollItemsSize, List<PollItem>> {
    private int minSize;
    private int maxSize;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(PollItemsSize constraintAnnotation) {
        this.minSize = constraintAnnotation.min();
        this.maxSize = constraintAnnotation.max();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(List<PollItem> pollItems, ConstraintValidatorContext context) {
        if (!CollectionUtils.isEmpty(pollItems)) {
            Range range = new IntRange(minSize, maxSize);
            return range.containsInteger(pollItems.size());
        }
        return true;
    }
}