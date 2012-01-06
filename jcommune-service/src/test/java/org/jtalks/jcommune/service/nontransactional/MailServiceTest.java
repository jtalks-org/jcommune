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
package org.jtalks.jcommune.service.nontransactional;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.MailService;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

/**
 * Test for {@link MailServiceImpl}.
 *
 * @author Evgeniy Naumenko
 */
public class MailServiceTest {

    private MailService service;
    private MailSender sender;
    private SimpleMailMessage message;
    private MockHttpServletRequest request;

    private static final String FROM = "lol@wut.zz";
    private static final String TO = "foo@bar.zz";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "new_password";

    private User user = new User(USERNAME, TO, PASSWORD);
    private Topic topic = new Topic(user, "title");
    private Branch branch = new Branch("title");
    private ArgumentCaptor<SimpleMailMessage> captor;

    @BeforeMethod
    public void setUp() {
        sender = mock(MailSender.class);
        message = new SimpleMailMessage();
        message.setFrom(FROM);
        service = new MailServiceImpl(sender, message);
        captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    }

    @BeforeMethod
    public void setUpRequestContext() {
        request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("coolsite.com");
        request.setServerPort(1234);
        request.setContextPath("/forum");
        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request));
    }

    @Test
    public void testSendPasswordRecoveryMail() throws MailingFailedException {
        service.sendPasswordRecoveryMail(USERNAME, TO, PASSWORD);

        this.checkMailCredentials();
        assertTrue(captor.getValue().getText().contains(USERNAME));
        assertTrue(captor.getValue().getText().contains(PASSWORD));
        assertTrue(captor.getValue().getText().contains("http://coolsite.com:1234/forum/login"));
    }

    @Test
    public void testSendTopicUpdatesEmail() throws MailingFailedException {
        Post post = new Post(user, "content");
        post.setId(1);
        topic.addPost(post);

        service.sendTopicUpdatesOnSubscription(user, topic);

        this.checkMailCredentials();
        assertTrue(captor.getValue().getText().contains("http://coolsite.com:1234/forum/posts/1"));
    }

    @Test
    public void testSendBranchUpdateEmail() throws MailingFailedException {
        branch.setId(1);

        service.sendBranchUpdatesOnSubscription(user, branch);

        this.checkMailCredentials();
        assertTrue(captor.getValue().getText().contains("http://coolsite.com:1234/forum/branches/1"));
    }

    @Test(expectedExceptions = MailingFailedException.class)
    public void testRestorePasswordFail() throws NotFoundException, MailingFailedException {
        Exception fail = new MailSendException("");
        doThrow(fail).when(sender).send(Matchers.<SimpleMailMessage>any());

        service.sendPasswordRecoveryMail(USERNAME, TO, PASSWORD);
    }

    @Test(expectedExceptions = MailingFailedException.class)
    public void testTopicUpdateNotiticationFail() throws NotFoundException, MailingFailedException {
        Exception fail = new MailSendException("");
        doThrow(fail).when(sender).send(Matchers.<SimpleMailMessage>any());

        service.sendTopicUpdatesOnSubscription(user, topic);
    }

    @Test(expectedExceptions = MailingFailedException.class)
    public void testbranchUpdateNotificationFail() throws NotFoundException, MailingFailedException {
        Exception fail = new MailSendException("");
        doThrow(fail).when(sender).send(Matchers.<SimpleMailMessage>any());

        service.sendBranchUpdatesOnSubscription(user, branch);
    }

    private void checkMailCredentials() {
        verify(sender).send(captor.capture());
        assertEquals(captor.getValue().getTo().length, 1);
        assertEquals(captor.getValue().getTo()[0], TO);
        assertEquals(captor.getValue().getFrom(), FROM);
    }
}
