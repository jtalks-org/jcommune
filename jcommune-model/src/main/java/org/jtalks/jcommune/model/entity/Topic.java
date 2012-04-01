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

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.SnowballPorterFilterFactory;
import org.apache.solr.analysis.StandardFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.apache.solr.analysis.StopFilterFactory;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.AnalyzerDefs;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import org.joda.time.DateTime;

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
                params =  @Parameter(name="language", value="Russian"))
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
            @TokenFilterDef(factory = SnowballPorterFilterFactory.class)
        }
    )
})
@Indexed
public class Topic extends SubscriptionAwareEntity {
    private DateTime creationDate;
    private DateTime modificationDate;
    private JCUser topicStarter;
    private String title;
    private int topicWeight;
    private boolean sticked;
    private boolean announcement;
    private List<Post> posts = new ArrayList<Post>();
    private Branch branch;
    private int views;

    // transient, makes sense for current user only if set explicitly
    private Integer lastReadPostIndex;

    public static final int MIN_NAME_SIZE = 5;
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
     * Used only by hibernate.
     */
    protected Topic() {
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
        this.topicWeight = 0;
        this.sticked = false;
        this.announcement = false;
    }

    /**
     * Add new {@link Post} to the topic.
     * The method sets Posts.topic field to this Topic.
     *
     * @param post post to add
     */
    public void addPost(Post post) {
        post.setTopic(this);
        updateModificationDate();
        this.posts.add(post);
    }

    /**
     * Remove the post from the topic.
     *
     * @param postToRemove post to remove
     */
    public void removePost(Post postToRemove) {
        posts.remove(postToRemove);
        updateModificationDate();
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
     * Sets the topic title.
     *
     * @param newTitle the title to set
     */
    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    /**
     * Get the list of the posts.
     *
     * @return the list of posts
     */
    @IndexedEmbedded(prefix = TOPIC_POSTS_PREFIX)
    public List<Post> getPosts() {
        return posts;
    }

    /**
     * Set the list of posts
     *
     * @param posts the posts to set
     */
    protected void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    /**
     * Get branch that contains topic
     *
     * @return branch that contains the topic
     */
    public Branch getBranch() {
        return branch;
    }

    /**
     * Set branch that contains topic
     *
     * @param branch branch that contains the topic
     */
    void setBranch(Branch branch) {
        this.branch = branch;
    }

    /**
     * Get the topic first post.
     *
     * @return the firstPost
     */
    public Post getFirstPost() {
        return posts.get(0);
    }

    /**
     * Get the topic last post.
     *
     * @return last post
     */
    public Post getLastPost() {
        return posts.get(posts.size() - 1);
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
     * Set modification date to now.
     *
     * @return new modification date
     */
    public DateTime updateModificationDate() {
        this.modificationDate = new DateTime();
        return this.modificationDate;
    }

    /**
     * @return priority of a sticked topic
     */
    public int getTopicWeight() {
        return this.topicWeight;
    }

    /**
     * @param topicWeight a priority for a sticked topic
     */
    public void setTopicWeight(int topicWeight) {
        this.topicWeight = topicWeight;
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
        if (!sticked) {
            topicWeight = 0;
        }
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
     * @param index last read post index in this topic for current user
     *              (0 means first post is the last read one)
     */
    public void setLastReadPostIndex(int index) {
        if (index >= posts.size()) {
            throw new IllegalArgumentException("Last read post index is bigger than post count in the topic");
        }
        lastReadPostIndex = index;
    }

    /**
     * Returns first unread post for current user. If no unread post
     * information has been set explicitly this method will return
     * first topic's post id, considering all topic as unread.
     *
     * @return returns first unread post id for the current user
     */
    public Long getFirstUnreadPostId() {
        if (lastReadPostIndex == null) {
            return posts.get(0).getId();
        } else {
            return posts.get(lastReadPostIndex + 1).getId();
        }
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
        return (lastReadPostIndex == null) || (lastReadPostIndex + 1 < posts.size());
    }

    /**
     * {@inheritDoc}
     */
    @DocumentId
    @Override
    public long getId() {
        return super.getId();
    }
}
