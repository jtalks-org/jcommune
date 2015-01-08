package org.jtalks.jcommune.plugin.api.service.transactional;

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.plugin.api.service.PluginPostService;

/**
 * @author Andrei Alikov
 */
public class TransactionalPluginPostService implements PluginPostService {

    private static final TransactionalPluginPostService INSTANCE = new TransactionalPluginPostService();

    private PluginPostService postService;

    /** Use {@link #getInstance()}, this class is singleton. */
    private TransactionalPluginPostService() {
    }

    /**
     * Gets instance of {@link TransactionalPluginPostService}
     *
     * @return instance of {@link TransactionalPluginPostService}
     */
    public static TransactionalPluginPostService getInstance() {
        return INSTANCE;
    }

    @Override
    public Post get(Long id) throws NotFoundException {
        return postService.get(id);
    }

    @Override
    public void deletePost(Post post) {
        postService.deletePost(post);
    }

    /**
     * Sets post service. Should be used once, during initialization
     *
     * @param postService
     */
    public void setPostService(PluginPostService postService) {
        this.postService = postService;
    }
}
