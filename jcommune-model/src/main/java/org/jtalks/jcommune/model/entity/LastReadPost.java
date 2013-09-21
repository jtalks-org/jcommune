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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * The only reason why this class is serializable is that is required by
 * Hibernate 'cause we're using composite id here.
 *
 * @author Evgeniy Naumenko
 */
public class LastReadPost extends Entity {
    private Topic topic;
    private JCUser user;
    private DateTime postDate;

    /**
     * For hibernate use only
     */
    protected LastReadPost() {
        this.postDate = new DateTime();
    }

    /**
     * @param user user to track last read post for
     * @param topic topic we're marking last read post in
     * @param postDate post date
     */
    public LastReadPost(JCUser user, Topic topic, DateTime postDate) {
        this.topic = topic;
        this.postDate = postDate;
        this.user = user;
    }
    
    /**
     * @return topic we're tracking last read post for
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * @param topic topic we're tracking last read post for
     */
    protected void setTopic(Topic topic) {
        this.topic = topic;
    }

    /**
     *
     * @return last read post date
     */
    public DateTime getPostDate() {
        return postDate;
    }

    /**
     *
     * @param postDate new value for the last read post date
     */
    public void setPostDate(DateTime postDate) {
        this.postDate = postDate;
    }

    /**
     * @return user we're tracking last read post for
     */
    public JCUser getUser() {
        return user;
    }

    /**
     * @param user user to track last read post for
     */
    protected void setUser(JCUser user) {
        this.user = user;
    }

    /**
     * Return stack trace with message,To format stack trace for logging, For easy viewing of text log
     * @param message stack trace's message
     * @return stack trace with message
     */
    private String getStackTrace(String message){
        StringWriter sw = new StringWriter();
        new Throwable(message).printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
