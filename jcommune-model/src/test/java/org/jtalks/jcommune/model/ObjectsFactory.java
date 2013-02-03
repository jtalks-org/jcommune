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

import org.apache.commons.lang.math.RandomUtils;
import org.joda.time.DateTime;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Max Malakhov
 * @author Eugeny Batov
 */
public final class ObjectsFactory {

    public static final String EXTERNAL_LINK_URL = "jtalks.org";
    public static final String EXTERNAL_LINK_TITLE = "Open Source Java Forum";
    public static final String EXTERNAL_LINK_HINT = "Most powerful forum engine";

    private ObjectsFactory() {
    }

    public static JCUser getDefaultUser() {
        return getUser("username", "username@mail.com");
    }

    public static JCUser getRandomUser() {
        return getUser("username" + RandomUtils.nextInt(10000), RandomUtils.nextInt(10000) + "username@mail.com");
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

    public static Branch getDefaultBranch(Long id) {
        Branch newBranch = new Branch("branch name" + id, "branch description" + id);
        newBranch.setId(id);
        return newBranch;
    }

    public static List<Branch> getDefaultBranchList() {
        List<Branch> branches = new ArrayList<Branch>();
        for (Long i = 1L; i <= 3; i++) {
            branches.add(getDefaultBranch(i));
        }
        return branches;
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

    public static ExternalLink getDefaultExternalLink() {
        ExternalLink result = new ExternalLink();
        result.setUrl(EXTERNAL_LINK_URL);
        result.setTitle(EXTERNAL_LINK_TITLE);
        result.setHint(EXTERNAL_LINK_HINT);
        return result;
    }

    public static UserContact getDefaultUserContact() {
        UserContactType type = new UserContactType();
        UserContact contact = new UserContact("value", type);
        contact.setOwner(ObjectsFactory.getDefaultUser());
        return contact;
    }

    public static List<Group> getDefaultGroupList() {
        List<Group> groups = new ArrayList<Group>();
        Group group = new Group("Administrators");
        group.setId(13L);
        groups.add(group);
        group = new Group("Banned Users");
        group.setId(12L);
        groups.add(group);
        group = new Group("Registered Users");
        group.setId(11L);
        groups.add(group);
        return groups;
    }

    public static SimplePage getDefaultSimplePage() {
        return new SimplePage("name", "content", "pathName");
    }

    public static CodeReview getDefaultCodeReview() {
        CodeReview review = new CodeReview();
        review.setId(1L);

        List<CodeReviewComment> comments = new ArrayList<CodeReviewComment>();
        CodeReviewComment comment1 = new CodeReviewComment();
        comment1.setId(1L);
        comment1.setAuthor(getDefaultUser());
        comment1.setBody("Comment1 body");
        comment1.setLineNumber(1);
        comment1.setCreationDate(new DateTime(1));
        comments.add(comment1);

        CodeReviewComment comment2 = new CodeReviewComment();
        comment2.setId(2L);
        comment2.setAuthor(getDefaultUser());
        comment2.setBody("Comment2 body");
        comment2.setLineNumber(2);
        comment2.setCreationDate(new DateTime(2));
        comments.add(comment2);

        review.setComments(comments);

        return review;
    }
}
