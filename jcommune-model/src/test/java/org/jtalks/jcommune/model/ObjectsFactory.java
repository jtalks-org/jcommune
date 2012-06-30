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
package org.jtalks.jcommune.model;

import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.UserContact;
import org.jtalks.jcommune.model.entity.UserContactType;

/**
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Max Malakhov
 * @author Eugeny Batov
 */
public final class ObjectsFactory {

    private ObjectsFactory() {
    }

    public static JCUser getDefaultUser() {
        return getUser("username", "username@mail.com");
    }

    public static JCUser getUser(String username, String email) {
        JCUser newUser = new JCUser(username, email, "password");
        newUser.setFirstName("first name");
        newUser.setLastName("last name");
        return newUser;
    }

    public static Branch getDefaultBranch() {
        Branch newBranch = new Branch("branch name", "branch description");
        return newBranch;
    }

    public static Section getDefaultSection() {
        Section newSection = new Section("section name");
        newSection.setDescription("branch description");
        newSection.setPosition(1);
        return newSection;
    }

    public static Topic getDefaultTopic() {
        Topic topic = new Topic(getDefaultUser(), "title");
        topic.setId(1);
        return topic;
    }

    public static PrivateMessage getPrivateMessage(JCUser userTo, JCUser userFrom) {
        return new PrivateMessage(userTo, userFrom,
                "Message title", "Private message body");
    }

    public static UserContactType getDefaultUserContactType() {
        UserContactType type = new UserContactType();
        type.setTypeName("Some type");
        type.setIcon("/some/icon");
        type.setMask("12345");
        type.setDisplayPattern("protocol://" + UserContactType.CONTACT_MASK_PLACEHOLDER);
        type.setValidationPattern("\\d+");
        return type;
    }

    public static UserContact getDefaultUserContact() {
        UserContactType type = new UserContactType();
        UserContact contact = new UserContact("value", type);
        contact.setOwner(ObjectsFactory.getDefaultUser());
        return contact;
    }

}
