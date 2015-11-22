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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.Section;
import org.jtalks.common.model.entity.User;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Max Malakhov
 * @author Eugeny Batov
 */
public final class ObjectsFactory {

    public static final String EXTERNAL_LINK_URL = "http://jtalks.org";
    public static final String EXTERNAL_LINK_TITLE = "Open Source Java Forum";
    public static final String EXTERNAL_LINK_HINT = "Most powerful forum engine";

    private ObjectsFactory() {
    }

    public static JCUser getDefaultUser() {
        return getUser("username", "username@mail.com");
    }

    public static JCUser getRandomUser() {
        return getUser("username" + RandomUtils.nextInt(0, 10000), RandomUtils.nextInt(0, 10000) + "username@mail.com");
    }

    public static JCUser getUser(String username, String email) {
        JCUser newUser = new JCUser(username, email, "password");
        newUser.setFirstName("first name");
        newUser.setLastName("last name");
        return newUser;
    }

    public static JCUser getUserWithAllFieldsFilled() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        JCUser user = getDefaultUser();
        DateTime dateTime = new DateTime();
        user.setId(1);
        user.setLanguage(Language.RUSSIAN);
        user.setPageSize(1);
        user.setLocation("location");
        user.setSignature("signature");
        user.setRegistrationDate(dateTime);
        user.setEnabled(true);
        user.setAutosubscribe(true);
        user.setMentioningNotificationsEnabled(true);
        user.setSendPmNotification(true);
        user.getContacts().add(ObjectsFactory.getDefaultUserContact());
        user.setAvatarLastModificationTime(dateTime);
        user.setAllForumMarkedAsReadTime(dateTime);
        user.setAvatar(new byte[]{1});
        user.setVersion(1L);
        user.setBanReason("Ban Reason");
        user.setRole("Role");
        Method setLastLogin = User.class.getDeclaredMethod("setLastLogin", DateTime.class);
        Method setEncodedUsername = User.class.getDeclaredMethod("setEncodedUsername", String.class);
        setLastLogin.setAccessible(true);
        setEncodedUsername.setAccessible(true);
        setLastLogin.invoke(user, new DateTime());
        setEncodedUsername.invoke(user, "Encoded Username");
        return user;
    }

    public static Branch getDefaultBranch() {
        return new Branch("branch name", "branch description");
    }

    public static Branch getDefaultBranch(Long id) {
        Branch newBranch = new Branch("branch name" + id, "branch description" + id);
        newBranch.setId(id);
        return newBranch;
    }

    public static Branch getDefaultBranchWithTopic(Long id, Topic topic) {
        Branch branch = getDefaultBranch(id);
        branch.addTopic(topic);
        topic.setBranch(branch);
        return branch;
    }

    public static List<Branch> getDefaultBranchList() {
        List<Branch> branches = new ArrayList<>();
        for (Long i = 1L; i <= 3; i++) {
            branches.add(getDefaultBranch(i));
        }
        return branches;
    }

    public static Section getDefaultSection() {
        Section newSection = new Section("section name");
        newSection.setDescription("section description");
        newSection.setPosition(1);
        return newSection;
    }

    public static Section getDefaultSectionWithBranch(Branch branch) {
        Section section = getDefaultSection();
        branch.setSection(section);
        section.addOrUpdateBranch(branch);
        return section;
    }

    public static Section getDefaultSectionWithBranches() {
        Section section = getDefaultSection();
        section.getBranches().addAll(getDefaultBranchList());
        return section;
    }

    public static List<Section> getDefaultSectionListWithBranches() {
        List<Section> sections = new ArrayList<>();
        long branchId = 1;
        for (int i = 0; i < 3; i++) {
            sections.add(getDefaultSectionWithBranches());
            for (org.jtalks.common.model.entity.Branch branch : sections.get(i).getBranches()) {
                branch.setId(branchId++);
            }
        }
        return sections;
    }

    public static List<Topic> topics(JCUser author, int topicCount) {
        List<Topic> topics = new ArrayList<>();
        for (int i = 0; i < topicCount; i++) {
            Topic topic = new Topic(author, "title", "Discussion");
            topic.setBranch(getDefaultBranch());
            topic.addPost(new Post(author, "post-content"));
            topics.add(topic);
        }
        return topics;
    }

    public static Topic getDefaultTopic() {
        return new Topic(getDefaultUser(), "title", "Discussion");
    }

    public static TopicDraft getDefaultTopicDraft() {
        TopicDraft draft = new TopicDraft(getDefaultUser(),
                RandomStringUtils.random(5),
                RandomStringUtils.random(15));

        draft.setPollTitle(RandomStringUtils.random(5));
        draft.setPollItemsValue(RandomStringUtils.random(5) + "\n" + RandomStringUtils.random(5));
        draft.setTopicType(TopicTypeName.DISCUSSION.getName());
        draft.setBranchId(1L);

        return draft;
    }

    public static Topic getTopic(JCUser author, int numberOfPosts) {
        Topic topic = new Topic(author, "some topic");
        for (int i = 0; i < numberOfPosts; i++) {
            topic.addPost(new Post(author, "post #" + (i + 1)));
        }
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
        return type;
    }

    public static ExternalLink getDefaultExternalLink() {
        ExternalLink result = new ExternalLink();
        result.setUrl(EXTERNAL_LINK_URL);
        result.setTitle(EXTERNAL_LINK_TITLE);
        result.setHint(EXTERNAL_LINK_HINT);
        return result;
    }

    public static List<ExternalLink> getExternalLinks(int size) {
        List<ExternalLink> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(getDefaultExternalLink());
        }
        return result;
    }

    public static UserContact getDefaultUserContact() {
        UserContactType type = new UserContactType();
        UserContact contact = new UserContact("value", type);
        contact.setOwner(ObjectsFactory.getDefaultUser());
        return contact;
    }

    public static List<Group> getDefaultGroupList() {
        List<Group> groups = new ArrayList<>();
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

    public static Banner getDefaultBanner() {
        Banner banner = new Banner();
        banner.setPositionOnPage(BannerPosition.TOP);
        banner.setContent("<html></html>");
        return banner;
    }

    public static List<Banner> getBanners() {
        int bannersCount = 3;
        List<Banner> banners = new ArrayList<>();
        for (int i = 0; i < bannersCount; i++) {
            Banner banner = new Banner();
            banner.setContent("<html></html>");
            banner.setPositionOnPage(BannerPosition.TOP);
        }
        return banners;
    }

    /**
     * @return group with random name and description
     */
    public static Group getRandomGroup() {
        return new Group("group" + RandomUtils.nextInt(0, 10000), "description" + RandomUtils.nextInt(0, 10000));
    }

    public static PostVote getDefaultPostVote() {
        return new PostVote(getDefaultUser());
    }

    public static Post getPostWithComments() {
        Post post = new Post(getDefaultUser(), "test");
        post.addComment(new PostComment());
        post.addComment(new PostComment());
        return post;
    }
}
