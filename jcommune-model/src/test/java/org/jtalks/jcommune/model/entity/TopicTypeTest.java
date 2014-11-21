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
package org.jtalks.jcommune.model.entity;

import org.testng.annotations.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author Mikhail Stryzhonok
 */
public class TopicTypeTest {

    @Test
    public void isCodeReviewShouldReturnTrueIfTypeIsCodeReview() {
        TopicType type = new TopicType(TopicTypeName.CODE_REVIEW.getName());

        assertTrue(type.isCodeReview());
    }

    @Test
    public void isCodeReviewShouldReturnFalseIfTypeIsNotCodeReview() {
        TopicType type = new TopicType(TopicTypeName.DISCUSSION.getName());

        assertFalse(type.isCodeReview());
    }

    @Test
    public void isCodeReviewShouldReturnFalseIfTypeNameIsNull() {
        TopicType type = new TopicType();

        assertFalse(type.isCodeReview());
    }
}
