package org.jtalks.jcommune.plugin.api.service;

import org.jtalks.jcommune.model.entity.TopicDraft;

/**
 * @author Dmitry S. Dolzhenko
 */
public interface PluginTopicDraftService {
    /**
     * Returns the draft topic for current user.
     *
     * @return the draft topic or null
     */
    TopicDraft getDraft();

    /**
     * Save or update the draft topic.
     *
     * @param draft the draft topic
     */
    TopicDraft saveOrUpdateDraft(TopicDraft draft);

    /**
     * Delete the draft topic.
     */
    void deleteDraft();
}
