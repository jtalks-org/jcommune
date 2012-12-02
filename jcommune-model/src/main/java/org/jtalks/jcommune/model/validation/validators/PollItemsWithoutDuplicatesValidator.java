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

import java.util.HashSet;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.jtalks.jcommune.model.entity.PollItem;
import org.jtalks.jcommune.model.validation.annotations.PollItemsWithoutDuplicates;

/**
 * It validates the list of poll items to search for duplicate poll items.
 * We consider the elements of a poll the same if they have the same pollItem.
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class PollItemsWithoutDuplicatesValidator 
    implements ConstraintValidator<PollItemsWithoutDuplicates, List<PollItem>> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(PollItemsWithoutDuplicates constraintAnnotation) {
        //we don't have any parameters in this validation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(List<PollItem> pollItems, ConstraintValidatorContext context) {
        int actualSize = pollItems.size();
        int withoutDuplicatesSize = getUniqueItemsCount(pollItems);
        return actualSize == withoutDuplicatesSize;
    }
    
    /**
     * Get count of unique poll items.
     * 
     * @param pollItems the source list of poll items
     * @return count of unique poll items
     */
    private int getUniqueItemsCount(List<PollItem> pollItems) {
        HashSet<String> uniquePollItemsNames = new HashSet<String>();
        for (PollItem pollItem: pollItems) {
            String pollItemName = pollItem.getName();
            uniquePollItemsNames.add(pollItemName);
        }
        return uniquePollItemsNames.size();
    }
}
