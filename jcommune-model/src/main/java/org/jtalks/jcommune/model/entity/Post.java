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
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import org.joda.time.DateTime;
import org.jtalks.common.model.entity.Entity;

/**
 * Represents the simple version of post of the forum
 * with String content.<br/>
 * Always included in the {@link Topic}. Topic itself should contain at lest one Post <br/>
 * All fields of this object are required and can't be null.<br/>
 * The topic field will be updated automatically when called Topic.addPost(Post). <br/>
 * The Post deletes automatically if the parent Topic deleted.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Anuar Nurmakanov
 */
@AnalyzerDefs({
    @AnalyzerDef(name = "russianJtalksAnalyzer",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
            @TokenFilterDef(factory = LowerCaseFilterFactory.class),
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
            @TokenFilterDef(factory = SnowballPorterFilterFactory.class, 
                params =  @Parameter(name="language", value="Russian"))
        }
    ),
    @AnalyzerDef(name = "defaultJtalksAnalyzer",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
            @TokenFilterDef(factory = StandardFilterFactory.class),
            @TokenFilterDef(factory = LowerCaseFilterFactory.class),
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
            @TokenFilterDef(factory = SnowballPorterFilterFactory.class)
        }
    )
})
@Indexed
public class Post extends Entity implements IndexedEntity {

    private DateTime creationDate;
    private DateTime modificationDate;
    private JCUser userCreated;
    private String postContent;
    private Topic topic;

    public static final int MAX_LENGTH = 20000;
    public static final int MIN_LENGTH = 2;
    
    public static final String POST_CONTENT_FIELD_RU = "postContentRu";
    public static final String POST_CONTENT_FIELD_DEF = "postContent";

    /**
     * For Hibernate use only
     */
    protected Post() {
    }

    /**
     * Creates the Post instance with required fields.
     * Creation date is set to now.
     *
     * @param userCreated user who create the post
     * @param postContent content of the post
     */
    public Post(JCUser userCreated, String postContent) {
        this.creationDate = new DateTime();
        this.userCreated = userCreated;
        this.postContent = postContent;
    }

    /**
     * Used to find out the current post index on JSP page.
     * We can't invoke a method there so use an explicit getter.
     *
     * @return index of this post ina  topic, starting from 0
     */
    public int getPostIndexInTopic(){
        return topic.getPosts().indexOf(this);
    }
    
    /**
     * @return the postDate
     */
    public DateTime getCreationDate() {
        return creationDate;
    }

    /**
     * @return date and time when the post was changed last time
     */
    public DateTime getModificationDate() {
        return modificationDate;
    }

    /**
     * @param postDate the postDate to set
     */
    protected void setCreationDate(DateTime postDate) {
        this.creationDate = postDate;
    }

    /**
     * @param modificationDate date and time when the post was changed last time
     */
    protected void setModificationDate(DateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    /**
     * Set modification date to now. The post's topic's
     * modification date will be also set to now
     *
     * @return new modification date
     */
    public DateTime updateModificationDate() {
        this.modificationDate = new DateTime();
        this.topic.updateModificationDate();
        return this.modificationDate;
    }

    /**
     * @return the userCreated
     */
    public JCUser getUserCreated() {
        return userCreated;
    }

    /**
     * Set the User who create this post.
     *
     * @param userCreated the userCreated to set
     */
    protected void setUserCreated(JCUser userCreated) {
        this.userCreated = userCreated;
    }

    /**
     * @return the postContent
     */
    @Fields({
        @Field(name = POST_CONTENT_FIELD_RU,
            analyzer = @Analyzer(definition = "russianJtalksAnalyzer")),
        @Field(name = POST_CONTENT_FIELD_DEF,
            analyzer = @Analyzer(definition = "defaultJtalksAnalyzer"))
    })
    public String getPostContent() {
        return postContent;
    }

    /**
     * @param postContent the postContent to set
     */
    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    /**
     * @return the topic
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * @param topic the Topic to set
     */
    protected void setTopic(Topic topic) {
        this.topic = topic;
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
