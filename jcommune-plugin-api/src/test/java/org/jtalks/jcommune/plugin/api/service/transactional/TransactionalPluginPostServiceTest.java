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
package org.jtalks.jcommune.plugin.api.service.transactional;

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.PostVote;
import org.jtalks.jcommune.plugin.api.service.PluginPostService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 *
 * @author Mikhail Stryzhonok
 */
public class TransactionalPluginPostServiceTest {
    @Mock
    private PluginPostService postService;

    @BeforeMethod
    public void init() {
        initMocks(this);
        TransactionalPluginPostService pluginPostService = (TransactionalPluginPostService)
                TransactionalPluginPostService.getInstance();
        pluginPostService.setPostService(postService);
    }

    @Test
    public void testVote() {
        Post post = new Post(null, null);
        PostVote vote = new PostVote();

        when(postService.vote(post, vote)).thenReturn(post);

        Post result = TransactionalPluginPostService.getInstance().vote(post, vote);

        assertEquals(result, post);
        verify(postService).vote(post, vote);
    }
}
