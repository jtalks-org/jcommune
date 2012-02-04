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

import org.apache.velocity.app.VelocityEngine;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test for {@link MailService}.
 *
 * @author Evgeniy Naumenko
 */
public class MailServiceTest {

    private MailService service;
    @Mock
    private JavaMailSender sender;
    private SimpleMailMessage message;
    private VelocityEngine velocityEngine;
    private MockHttpServletRequest request;

    private static final String FROM = "lol@wut.zz";
    private static final String TO = "foo@bar.zz";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "new_password";

    private JCUser user = new JCUser(USERNAME, TO, PASSWORD);
    private Topic topic = new Topic(user, "title");
    private Branch branch = new Branch("title");
    private ArgumentCaptor<SimpleMailMessage> captor;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        message = new SimpleMailMessage();
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        message.setFrom(FROM);
        service = new MailService(sender, message, velocityEngine);
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

    @Test
    public void testSendReceivedPrivateMessageNotification() {
        service.sendReceivedPrivateMessageNotification(user, 1);

        this.checkMailCredentials();
        assertTrue(captor.getValue().getText().contains("http://coolsite.com:1234/forum/inbox/1"));
    }

    @Test
    public void testSendActivationMail() {
        JCUser user =  new JCUser(USERNAME, TO, PASSWORD);
        service.sendAccountActivationMail(user);
        this.checkMailCredentials();
        assertTrue(captor.getValue().getText().contains(
                "http://coolsite.com:1234/forum/user/activate/" + user.getUuid()));
    }

    @Test
    public void testSendActivationMailFail() {
        Exception fail = new MailSendException("");
        doThrow(fail).when(sender).send(Matchers.<SimpleMailMessage>any());

        service.sendAccountActivationMail(new JCUser(USERNAME, TO, PASSWORD));
    }

    @Test(expectedExceptions = MailingFailedException.class)
    public void testRestorePasswordFail() throws NotFoundException, MailingFailedException {
        Exception fail = new MailSendException("");
        doThrow(fail).when(sender).send(Matchers.<SimpleMailMessage>any());

        service.sendPasswordRecoveryMail(USERNAME, TO, PASSWORD);
    }

    @Test
    public void testTopicUpdateNotificationFail() throws NotFoundException {
        Exception fail = new MailSendException("");
        doThrow(fail).when(sender).send(Matchers.<SimpleMailMessage>any());

        service.sendTopicUpdatesOnSubscription(user, topic);
    }

    @Test
    public void testBranchUpdateNotificationFail() throws NotFoundException {
        Exception fail = new MailSendException("");
        doThrow(fail).when(sender).send(Matchers.<SimpleMailMessage>any());

        service.sendBranchUpdatesOnSubscription(user, branch);
    }

    @Test
    public void testSendReceivedPrivateMessageNotificationFail() {
        Exception fail = new MailSendException("");
        doThrow(fail).when(sender).send(Matchers.<SimpleMailMessage>any());

        service.sendReceivedPrivateMessageNotification(user, 1);
    }

    private void checkMailCredentials() {
        verify(sender).send(captor.capture());
        assertEquals(captor.getValue().getTo().length, 1);
        assertEquals(captor.getValue().getTo()[0], TO);
        assertEquals(captor.getValue().getFrom(), FROM);
    }
}
