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
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;

import static org.mockito.Mockito.doThrow;
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
    private VelocityEngine velocityEngine;
    private MockHttpServletRequest request;
    private ReloadableResourceBundleMessageSource messageSource;
    @Mock
    private BBCodeService bbCodeService;

    private static final String FROM = "lol@wut.zz";
    private static final String TO = "foo@bar.zz";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "new_password";

    private JCUser user = new JCUser(USERNAME, TO, PASSWORD);
    private Topic topic = new Topic(user, "title");
    private Branch branch = new Branch("title");
    private ArgumentCaptor<MimeMessage> captor;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.setProperty("runtime.log.logsystem.class","org.apache.velocity.runtime.log.NullLogSystem");
        messageSource=new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/org/jtalks/jcommune/service/bundle/TemplatesMessages");
        service = new MailService(sender, FROM, velocityEngine, messageSource, bbCodeService);
        MimeMessage message = new MimeMessage((Session) null);
        when(sender.createMimeMessage()).thenReturn(message);
        captor = ArgumentCaptor.forClass(MimeMessage.class);
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
    public void testSendPasswordRecoveryMail() throws MailingFailedException, IOException, MessagingException {
        service.sendPasswordRecoveryMail(user, PASSWORD);

        this.checkMailCredentials();
        assertTrue(this.getMimeMailBody().contains(USERNAME));
        assertTrue(this.getMimeMailBody().toString().contains(PASSWORD));
        assertTrue(this.getMimeMailBody().contains("http://coolsite.com:1234/forum/login"));
    }

    @Test
    public void testSendTopicUpdatesEmail() throws MailingFailedException, IOException, MessagingException {
        Post post = new Post(user, "content");
        post.setId(1);
        topic.addPost(post);

        service.sendTopicUpdatesOnSubscription(user, topic);

        this.checkMailCredentials();
        assertTrue(this.getMimeMailBody().toString().contains("http://coolsite.com:1234/forum/posts/1"));
    }

    @Test
    public void testSendBranchUpdateEmail() throws MailingFailedException, IOException, MessagingException {
        branch.setId(1);

        service.sendBranchUpdatesOnSubscription(user, branch);

        this.checkMailCredentials();
        assertTrue(this.getMimeMailBody().contains("http://coolsite.com:1234/forum/branches/1"));
    }

    @Test
    public void testSendReceivedPrivateMessageNotification() throws IOException, MessagingException {
        PrivateMessage message = new PrivateMessage(null, null, "title", "body");
        message.setId(1);
        when(bbCodeService.removeBBCodes("body")).thenReturn("plain body");

        service.sendReceivedPrivateMessageNotification(user, message);

        this.checkMailCredentials();
        System.out.println(this.getMimeMailBody());
        assertTrue(this.getMimeMailBody().contains("http://coolsite.com:1234/forum/pm/1"));
        assertTrue(this.getMimeMailBody().contains("title"));
        assertTrue(this.getMimeMailBody().contains("plain body"));
    }

    @Test
    public void testSendActivationMail() throws IOException, MessagingException {
        JCUser user = new JCUser(USERNAME, TO, PASSWORD);
        service.sendAccountActivationMail(user);
        this.checkMailCredentials();
        assertTrue(this.getMimeMailBody().contains("http://coolsite.com:1234/forum/user/activate/" + user.getUuid()));
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
        doThrow(fail).when(sender).send(Matchers.<MimeMessage>any());

        service.sendPasswordRecoveryMail(user, PASSWORD);
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

        service.sendReceivedPrivateMessageNotification(user, new PrivateMessage(null, null, null, null));
    }

    private String getMimeMailBody() throws IOException, MessagingException {
        return ((MimeMultipart) ((MimeMultipart) ((MimeMultipart) captor.getValue().getContent()).getBodyPart(0).
                getDataHandler().getContent()).getBodyPart(0).getDataHandler().getContent()).getBodyPart(0).
                getDataHandler().getContent().toString();//sorry
    }

    private void checkMailCredentials() throws MessagingException {
        verify(sender).send(captor.capture());
        assertEquals(captor.getValue().getRecipients(Message.RecipientType.TO).length, 1);
        InternetAddress actualTo = (InternetAddress) captor.getValue().getRecipients(Message.RecipientType.TO)[0];
        assertEquals(actualTo.getAddress(), TO);
        InternetAddress actualFrom = (InternetAddress) captor.getValue().getFrom()[0];
        assertEquals(actualFrom.getAddress(), FROM);
    }
}
