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

import org.joda.time.DateTime;
import org.jtalks.common.model.entity.Entity;

/**
 * Represents one comment to one line in code review.
 * 
 * @author Vyacheslav Mishcheryakov
 */
public class CodeReviewComment extends Entity {

    /** Number of commented line of code */
    private int lineNumber;
    
    private JCUser author;
    
    private DateTime creationDate;
    
    private String body;
    
    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return the author
     */
    public JCUser getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(JCUser author) {
        this.author = author;
    }
    
    /**
     * The time when comment was added
     * @return the creationDate
     */
    public DateTime getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate the creation date of this comment
     */
    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return the comment body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the comment body to set
     */
    public void setBody(String body) {
        this.body = body;
    }
    
}
