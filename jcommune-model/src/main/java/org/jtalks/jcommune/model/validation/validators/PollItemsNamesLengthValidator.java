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
import org.jtalks.jcommune.model.validation.annotations.PollItemsNamesLength;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;


/**
 * It validates the list of poll items to find poll item that has
 * incorrect length of the name.
 *
 * @author Anuar_Nurmakanov
 */
public class PollItemsNamesLengthValidator implements ConstraintValidator<PollItemsNamesLength, List<PollItem>> {

    private int minLength;
    private int maxLenght;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(PollItemsNamesLength constraintAnnotation) {
        this.minLength = constraintAnnotation.min();
        this.maxLenght = constraintAnnotation.max();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(List<PollItem> pollItems, ConstraintValidatorContext context) {
        if (pollItems != null) {
            for (PollItem pollItem : pollItems) {
                if (!isPollItemValid(pollItem)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Validates the name of poll title.
     *
     * @param pollItem validated poll item
     * @return {@code true} if poll item name has correct length,
     *         otherwise {@code false}
     */
    private boolean isPollItemValid(PollItem pollItem) {
        String pollItemName = pollItem.getName();
        Range range = new IntRange(minLength, maxLenght);
        int pollItemLength = pollItemName.length();
        return range.containsInteger(pollItemLength);
    }
}
