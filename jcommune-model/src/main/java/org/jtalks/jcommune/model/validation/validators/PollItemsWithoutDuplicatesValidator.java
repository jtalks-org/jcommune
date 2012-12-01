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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
        int withoutDuplicatesSize = getCountUniqueItems(pollItems);
        return actualSize == withoutDuplicatesSize;
    }
    
    /**
     * Get count of unique poll items.
     * 
     * @param pollItems the source list of poll items
     * @return count of unique poll items
     */
    private int getCountUniqueItems(List<PollItem> pollItems) {
        HashSet<PollItemNameEqualWrapper> containerWithoutDuplicates = 
                new HashSet<PollItemNameEqualWrapper>();
        for (PollItem pollItem: pollItems) {
            String pollItemName = pollItem.getName();
            containerWithoutDuplicates.add(new PollItemNameEqualWrapper(pollItemName));
        }
        return containerWithoutDuplicates.size();
    }
    
    /**
     * Help wrapper that gives an ability to remove duplicates by using
     * {@link HashSet}.
     * We can't use {@link PollItem} for this purpose, because in this
     * case we need to fully override equals and hashcode methods.
     * 
     * @author Anuar_Nurmakanov
     *
     */
    private static class PollItemNameEqualWrapper {
        private String pollItem;

        /**
         * Constructs an instance with required fields.
         * 
         * @param pollItem the name of the poll
         */
        public PollItemNameEqualWrapper(String pollItem) {
            this.pollItem = pollItem;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                .append(pollItem)
                .hashCode();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PollItemNameEqualWrapper)) {
                return false;
            }
            PollItemNameEqualWrapper equaledObject = (PollItemNameEqualWrapper) obj;
            return new EqualsBuilder()
                .append(pollItem, equaledObject.pollItem)
                .isEquals();
        }
    }
}
