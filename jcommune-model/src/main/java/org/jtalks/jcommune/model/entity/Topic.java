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

import org.apache.solr.analysis.*;
import org.hibernate.search.annotations.*;
import org.joda.time.DateTime;
import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.validation.annotations.NotBlankSized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents the topic of the forum.
 * Contains the list of related {@link Post}.
 * All Posts will be cascade deleted with the associated Topic.
 * The fields creationDate, topicStarter and Title are required and can't be <code>null</code>
 *
 * @author Pavel Vervenko
 * @author Vitaliy Kravchenko
 * @author Max Malakhov
 * @author Anuar Nurmakanov
 */
@AnalyzerDefs({
        /*
        * Describes the analyzer for Russian.
        */
        @AnalyzerDef(name = "russianJtalksAnalyzer",
                tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
                filters = {
                        /*
                        * All "terms" of the search text will be converted to lower case.
                        */
                        @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                        /*
                        * Several words in language doesn't have a significant value.
                        * These filters exclude those words from the index.
                        */
                        @TokenFilterDef(factory = StopFilterFactory.class,
                                params = {
                                        @Parameter(name = "words",
                                                value = "org/jtalks/jcommune/lucene/english_stop.txt"),
                                        @Parameter(name = "ignoreCase", value = "true")
                                }),
                        @TokenFilterDef(factory = StopFilterFactory.class,
                                params = {
                                        @Parameter(name = "words",
                                                value = "org/jtalks/jcommune/lucene/russian_stop.txt"),
                                        @Parameter(name = "ignoreCase", value = "true")
                                }),
                        /*
                        * Provides the search by a root of a word.
                        * If two words have the same root, then they are equal in the terminology of search.
                        */
                        @TokenFilterDef(factory = SnowballPorterFilterFactory.class,
                                params = @Parameter(name = "language", value = "Russian"))
                }
        ),
        /*
        * Describes the analyzer for default language(English).
        */
        @AnalyzerDef(name = "defaultJtalksAnalyzer",
                tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
                filters = {
                        @TokenFilterDef(factory = StandardFilterFactory.class),
                        /*
                        * All "terms" of the search text will be converted to lower case.
                        */
                        @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                        /*
                        * Several words in language don't have a significant value.
                        * These filters exclude those words from the index.
                        */
                        @TokenFilterDef(factory = StopFilterFactory.class,
                                params = {
                                        @Parameter(name = "words",
                                                value = "org/jtalks/jcommune/lucene/english_stop.txt"),
                                        @Parameter(name = "ignoreCase", value = "true")
                                }),
                        @TokenFilterDef(factory = StopFilterFactory.class,
                                params = {
                                        @Parameter(name = "words",
                                                value = "org/jtalks/jcommune/lucene/russian_stop.txt"),
                                        @Parameter(name = "ignoreCase", value = "true")
                                }),
                        /*
                        * Provides the search by a root of a word.
                        * If two words have the same root, then they are equal in the terminology of search.
                        */
                        @TokenFilterDef(factory = SnowballPorterFilterFactory.class)
                }
        )
})
@Indexed
public class Topic extends Entity implements SubscriptionAwareEntity {

    private static final Logger LOGGER = LoggerFactory.getLogger(Topic.class);
    public static final String URL_SUFFIX = "/posts/";

    private DateTime creationDate;
    private DateTime modificationDate;
    private JCUser topicStarter;
    @NotBlankSized(min = MIN_NAME_SIZE, max = MAX_NAME_SIZE, message = "{length.constraint}")
    private String title;
    private boolean sticked;
    private boolean announcement;
    private boolean closed;
    private Branch branch;
    private int views;
    @Valid
    private Poll poll;
    private String type;
    private Map<String, String> attributes = new HashMap<>();
    private List<Post> posts = new ArrayList<>();
    private Set<JCUser> subscribers = new HashSet<>();

    // transient, makes sense for current user only if set explicitly
    private transient DateTime lastReadPostDate;

    public static final int MIN_NAME_SIZE = 1;
    public static final int MAX_NAME_SIZE = 120;

    /**
     * Name of the field in the index for Russian.
     */
    public static final String TOPIC_TITLE_FIELD_RU = "topicTitleRu";
    /**
     * Name of the field in the index for default language(English).
     */
    public static final String TOPIC_TITLE_FIELD_DEF = "topicTitle";
    /**
     * Name of the prefix for collection of posts.
     */
    public static final String TOPIC_POSTS_PREFIX = "topicPosts.";


    /**
     * Creates the Topic instance.
     */
    public Topic() {
    }

    /**
     * Creates the Topic instance with required fields.
     * Creation and modification date is set to now.
     *
     * @param topicStarter user who create the topic
     * @param title        topic title
     */
    public Topic(JCUser topicStarter, String title) {
        this.topicStarter = topicStarter;
        this.title = title;
        this.creationDate = new DateTime();
        this.modificationDate = new DateTime();
    }

    public Topic(JCUser topicStarter, String title, String topicType) {
        this.topicStarter = topicStarter;
        this.title = title;
        this.creationDate = new DateTime();
        this.modificationDate = new DateTime();
        this.type = topicType;
    }

    /**
     * Add new {@link Post} to the topic.
     * The method sets Posts.topic field to this Topic.
     *
     * @param post post to add
     */
    public void addPost(Post post) {
        setModificationDate(post.getCreationDate());
        post.setTopic(this);
        this.posts.add(post);
    }

    /**
     * Remove the post from the topic.
     *
     * @param postToRemove post to remove
     */
    public void removePost(Post postToRemove) {
        posts.remove(postToRemove);
        Topic topic = postToRemove.getTopic();
        if (postToRemove.getCreationDate().withMillis(0).equals(topic.getModificationDate().withMillis(0))) {
            topic.recalculateModificationDate();
        }
    }

    /**
     * Check subscribed user on topic or not.
     *
     * @param user checked user
     * @return true if user subscribed on topic
     *         false otherwise
     */
    public boolean userSubscribed(JCUser user) {
        return subscribers.contains(user);
    }

    /**
     * Get the post creation date.
     *
     * @return the creationDate
     */
    public DateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Set the post creation date.
     *
     * @param creationDate the creationDate to set
     */
    protected void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Get the user who created the post.
     *
     * @return the userCreated
     */
    public JCUser getTopicStarter() {
        return topicStarter;
    }

    /**
     * The the author of the post.
     *
     * @param userCreated the user who create the post
     */
    protected void setTopicStarter(JCUser userCreated) {
        this.topicStarter = userCreated;
    }

    /**
     * Gets the topic name.
     *
     * @return the topicName
     */
    @Fields({
            @Field(name = TOPIC_TITLE_FIELD_RU,
                    analyzer = @Analyzer(definition = "russianJtalksAnalyzer")),
            @Field(name = TOPIC_TITLE_FIELD_DEF,
                    analyzer = @Analyzer(definition = "defaultJtalksAnalyzer"))
    })
    public String getTitle() {
        return title;
    }

    /**
     * @param newTitle new title for this topic
     */
    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    /**
     * @return content of the first post of the topic
     */
    public String getBodyText() {
        Post firstPost = getFirstPost();
        return firstPost.getPostContent();
    }


    /**
     * @return the list of posts in the topic, always not null and not empty
     */
    @IndexedEmbedded(prefix = TOPIC_POSTS_PREFIX)
    public List<Post> getPosts() {
        return posts;
    }

    /**
     * @param posts the posts to set as topic contents, must not be empty or null
     */
    protected void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    /**
     * @return branch that contains the topic
     */
    public Branch getBranch() {
        return branch;
    }

    /**
     * @param branch branch to be set as topics branch
     */
    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    /**
     * @return the firstPost in the topic, topics are guaranteed to have at least the first post
     */
    public Post getFirstPost() {
        return posts.get(0);
    }
    
    /**
     * Get the last post in the topic. Topics are guaranteed to have at least the first post.
     * 
     * @return last post in the topic.
     */
    public Post getLastPost() {
        return posts.get(posts.size() - 1);
    }

    /**
     * Get next post to given post in topic. Following basic cases are possible:
     * <ol>
     * <li>In case of one post in topic it returns it back (not valid case from
     * end-user point of view).</li>
     * <li>In case if we pass post in the middle of the topic it returns next
     * post.</li>
     * <li>In case if we pass last post in the topic it returns previous one.</li>
     * </ol>
     * Used to find closest post which is good to be displayed after deletion of
     * post we pass as a parameter.
     * 
     * @param post
     * @return Neighbor post
     */
    public Post getNeighborPost(Post post) {
        for (int i = posts.size() - 1; i > 0; i--) {
            if (posts.get(i).equals(post)) {
                return (i == posts.size() - 1) ? posts.get(i - 1) : posts
                        .get(i + 1);
            }
        }
        return getFirstPost();
    }

    /**
     * @return date and time when theme was changed last time
     */
    public DateTime getModificationDate() {
        return modificationDate;
    }

    /**
     * @param modificationDate date and time when theme was changed last time
     */
    protected void setModificationDate(DateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    /**
     * Calculates modification date of topic taking it as last post in topic creation date.
     * Used after deletion of the post. It is necessary to save the sort order of topics in the future.
     */
    public void recalculateModificationDate() {
        DateTime newTopicModificationDate = getFirstPost().getCreationDate();
        for (Post post : posts) {
            if (post.getCreationDate().isAfter(newTopicModificationDate.toInstant())) {
                newTopicModificationDate = post.getCreationDate();
            }
        }
        modificationDate = newTopicModificationDate;
    }

    /**
     * Get the date of the last modification of posts in the current topic.
     */
    public DateTime getLastModificationPostDate() {
        DateTime newTopicModificationDate = getFirstPost().getLastTouchedDate();
        for (Post post : posts) {
            if (post.getLastTouchedDate().isAfter(newTopicModificationDate.toInstant())) {
                newTopicModificationDate = post.getLastTouchedDate();
            }
        }
        return newTopicModificationDate;
    }

    /**
     * @return flag og stickedness
     */
    public boolean isSticked() {
        return this.sticked;
    }

    /**
     * @param sticked a flag of stickedness for a topic
     */
    public void setSticked(boolean sticked) {
        this.sticked = sticked;
    }

    /**
     * @return flag og announcement
     */
    public boolean isAnnouncement() {
        return this.announcement;
    }

    /**
     * @param announcement a flag of announcement for a topic
     */
    public void setAnnouncement(boolean announcement) {
        this.announcement = announcement;
    }

    /**
     * Get count of post in topic.
     *
     * @return count of post
     */
    public int getPostCount() {
        return posts.size();
    }

    /**
     * @return topic page views
     */
    public int getViews() {
        return views;
    }

    /**
     * @param views topic page views
     */
    public void setViews(int views) {
        this.views = views;
    }

    /**
     * Get the poll for this topic.
     *
     * @return the poll for this topic
     */
    public Poll getPoll() {
        return poll;
    }

    /**
     * Set the poll for this topic.
     *
     * @param poll the poll for this topic
     */
    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    /**
     * @param lastReadPostDate last read post creation date
     */
    public void setLastReadPostDate(DateTime lastReadPostDate) {
        this.lastReadPostDate = lastReadPostDate;
    }

    /**
     * @return last read post creation date
     */
    public DateTime getLastReadPostDate() {
        return lastReadPostDate;
    }

    /**
     * Returns first unread post for current user. If no unread post
     * information has been set explicitly this method will return
     * first topic's post id, considering all topic as unread.
     *
     * @return returns first unread post id for the current user
     */
    public Long getFirstUnreadPostId() {
        if (isHasUpdates()) {
            return getFirstNewerPost(lastReadPostDate).getId();
        }

        return getFirstPost().getId();
    }

    /**
     * Returns first post that is newer then give time
     * @param time time to looking for newer post
     * @return first post that is newer then give time or first post if there is no post that is newer
     */
    private Post getFirstNewerPost(DateTime time) {
        for (Post post : getPosts()) {
            if (post.getCreationDate().isAfter(time)) {
                return post;
            }
        }

        return getFirstPost();
    }

    /**
     * This method will return true if there are unread posts in that topic
     * for the current user. This state is NOT persisted and must be
     * explicitly set by calling  Topic.setLastReadPostIndex().
     * <p/>
     * If setter has not been called this method will always return no updates
     *
     * @return if current topic has posts still unread by the current user
     */
    public boolean isHasUpdates() {
        return (lastReadPostDate == null) || (lastReadPostDate.isBefore(getLastPost().getCreationDate()));
    }

    /**
     * Determines a existence the poll in the topic.
     *
     * @return <tt>true</tt>  if the poll exists
     *         <tt>false</tt>  if the poll doesn't exist
     */
    public boolean isHasPoll() {
        return poll != null;
    }

    /**
     * {@inheritDoc}
     */
    @DocumentId
    @Override
    public long getId() {
        return super.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public Set<JCUser> getSubscribers() {
        return subscribers;
    }

    /**
     * {@inheritDoc}
     */
	public void setSubscribers(Set<JCUser> subscribers) {
        this.subscribers = subscribers;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The target URL has the next format http://{forum root}/posts/{id}
     */
    @Override
    public String getUrlSuffix() {
        return URL_SUFFIX + getLastPost().getId();
    }

    /**
     * @return True if topic is closed
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * @param closed If true then topic set to closed, else to open
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    /**
     * Gets type of the topic
     *
     * @return type of the topic
     */
    public String getType() {
        return type;
    }

    /**
     * Sets specified type to the topic
     *
     * @param type type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets attributes of the topic
     *
     * @return attributes of the topic
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Sets specified attributes to the topic
     * For hibernate usage. Use Topic#putAttribute
     *
     * @param attributes attributes to set
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * Adds new attribute or overrides existent attribute of the topic
     *
     * @param attributeName name of the attribute
     * @param attributeValue value of the attribute
     */
    public void addOrOverrideAttribute(String attributeName, String attributeValue) {
        this.attributes.put(attributeName, attributeValue);
    }

    /**
     * Determines if topic is code review
     *
     * @return true  if code review, otherwise false
     */
    public boolean isCodeReview() {
        return type != null && type.equals(TopicTypeName.CODE_REVIEW.getName());
    }

    /**
     * Determines if topic is provided by plugin.
     * NOTE: currently jcommune provides two topic types: "Code review" and "Discussion" all other
     * topic types are provided by plugins
     *
     * @return true if topic is provided by plugin otherwise false
     */
    public boolean isPlugable() {
        return type != null && !(this.isCodeReview() || type.equals(TopicTypeName.DISCUSSION.getName()));
    }
}
