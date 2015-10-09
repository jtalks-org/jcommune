package org.jtalks.jcommune.plugin.api.service.transactional;

import org.jtalks.jcommune.model.entity.TopicDraft;
import org.jtalks.jcommune.plugin.api.service.PluginTopicDraftService;

/**
 * @author Dmitry S. Dolzhenko
 */
public class TransactionalPluginTopicDraftService implements PluginTopicDraftService {
    private static final PluginTopicDraftService INSTANCE =
            new TransactionalPluginTopicDraftService();

    private PluginTopicDraftService topicDraftService;

    public TransactionalPluginTopicDraftService() {
    }

    public static PluginTopicDraftService getInstance() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TopicDraft getDraft() {
        return topicDraftService.getDraft();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TopicDraft saveOrUpdateDraft(TopicDraft draft) {
        return topicDraftService.saveOrUpdateDraft(draft);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDraft() {
        topicDraftService.deleteDraft();
    }

    public void setTopicDraftService(PluginTopicDraftService topicDraftService) {
        this.topicDraftService = topicDraftService;
    }
}
