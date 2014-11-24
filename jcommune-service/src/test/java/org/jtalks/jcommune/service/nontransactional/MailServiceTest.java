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
import org.jtalks.common.model.entity.Property;
import org.jtalks.jcommune.model.dao.PropertyDao;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
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

import static org.jtalks.jcommune.model.entity.JCommuneProperty.SENDING_NOTIFICATIONS_ENABLED;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test for {@link MailService}.
 *
 * @author Evgeniy Naumenko
 */
public class MailServiceTest {
    private static final String PROPERTY_NAME = "property";
    private static final String TRUE_STRING = Boolean.TRUE.toString();
    private static final String FALSE_STRING = Boolean.FALSE.toString();
    private static final String FROM = "lol@wut.zz";
    private static final String TO = "foo@bar.zz";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "new_password";
    @Mock
    private PropertyDao propertyDao;
    @Mock
    private MailSender sender;
    private JCommuneProperty notificationsEnabledProperty = SENDING_NOTIFICATIONS_ENABLED;
    //
    private MailService service;

    private JCUser user = new JCUser(USERNAME, TO, PASSWORD);
    private Topic topic = new Topic(user, "title Topic");
    private Branch branch = new Branch("title Branch", "description");
    private ArgumentCaptor<MimeMessage> captor;
    private ReloadableResourceBundleMessageSource messageSource;
    private long topicId = 777;
    private long branchId = 7;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        //
        notificationsEnabledProperty.setPropertyDao(propertyDao);
        notificationsEnabledProperty.setName(PROPERTY_NAME);
        //
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/org/jtalks/jcommune/service/bundle/TemplatesMessages");
        service = new MailService(sender, FROM, velocityEngine, messageSource, notificationsEnabledProperty);
        MimeMessage message = new MimeMessage((Session) null);
        when(sender.createMimeMessage()).thenReturn(message);
        captor = ArgumentCaptor.forClass(MimeMessage.class);
        topic.setId(topicId);
        branch.setId(branchId);
        topic.setBranch(branch);
        topic.setType(TopicTypeName.DISCUSSION.getName());
    }

    @BeforeMethod
    public void setUpRequestContext() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("coolsite.com");
        request.setServerPort(1234);
        request.setContextPath("/forum");
        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request));
    }

    @Test
    public void testSendPasswordRecoveryMail() throws Exception {
        enableEmailNotifications();
        service.sendPasswordRecoveryMail(user, PASSWORD);

        this.checkMailCredentials();
        assertTrue(this.getMimeMailBody().contains(USERNAME));
        assertTrue(this.getMimeMailBody().contains(PASSWORD));
        assertTrue(this.getMimeMailBody().contains("http://coolsite.com:1234/forum/login"));
    }

    @Test
    public void passwordRecoveryMailShouldNotBeSentIfNotificationsAreDisabled() throws Exception {
        disableEmailNotifications();
        service.sendPasswordRecoveryMail(user, PASSWORD);
        verify(sender, never()).send(any(MimeMessage.class));
    }

    @Test
    public void testSendTopicUpdatesEmail() throws Exception {
        enableEmailNotifications();
        Post post = new Post(user, "content");
        post.setId(1);
        topic.addPost(post);

        service.sendUpdatesOnSubscription(user, topic);

        this.checkMailCredentials();
        assertTrue(this.getMimeMailBody().contains("http://coolsite.com:1234/forum/posts/1"));
    }

    @Test
    public void topicUpdatesEmailShouldNotBeSentIfNotificationsAreDisabled() throws Exception {
        disableEmailNotifications();
        Post post = new Post(user, "content");
        post.setId(1);
        topic.addPost(post);

        service.sendUpdatesOnSubscription(user, topic);
        verify(sender, never()).send(any(MimeMessage.class));
    }

    @Test
    public void testSendTopicUpdateEmailCheckTitleInSubject() throws Exception {
        enableEmailNotifications();
        Post post = new Post(user, "content");
        post.setId(1);
        topic.addPost(post);

        service.sendUpdatesOnSubscription(user, topic);

        this.checkMailCredentials();
        assertTrue(this.getMimeMailSubject().contains("title Topic"));
    }

    @Test
    public void testSendBranchUpdateEmail() throws Exception {
        enableEmailNotifications();
        service.sendUpdatesOnSubscription(user, branch);

        this.checkMailCredentials();
        assertTrue(this.getMimeMailBody().contains("http://coolsite.com:1234/forum/branches/" + branchId));
    }

    @Test
    public void branchUpdateEmailShouldNotBeSentIfNotificationsAreDisabled() throws Exception {
        disableEmailNotifications();
        service.sendUpdatesOnSubscription(user, branch);
        verify(sender, never()).send(any(MimeMessage.class));
    }

    @Test
    public void testSendBranchUpdateEmailCheckTitleInSubject() throws Exception {
        enableEmailNotifications();
        service.sendUpdatesOnSubscription(user, branch);
        this.checkMailCredentials();
        assertTrue(this.getMimeMailSubject().contains("title Branch"));
    }

    @Test
    public void testSendReceivedPrivateMessageNotification() throws Exception {
        enableEmailNotifications();
        PrivateMessage message = new PrivateMessage(null, null, "title", "body");
        message.setId(1);

        service.sendReceivedPrivateMessageNotification(user, message);

        this.checkMailCredentials();
        System.out.println(this.getMimeMailBody());
        assertTrue(this.getMimeMailBody().contains("http://coolsite.com:1234/forum/pm/inbox/1"));
    }

    @Test
    public void receivedPrivateMessageNotificationShouldNotBeSentIfNotificationsAreDisabled() throws Exception {
        disableEmailNotifications();
        PrivateMessage message = new PrivateMessage(null, null, "title", "body");
        message.setId(1);

        service.sendReceivedPrivateMessageNotification(user, message);
        verify(sender, never()).send(any(MimeMessage.class));
    }

    @Test
    public void testSendActivationMail() throws Exception {
        enableEmailNotifications();
        JCUser user = new JCUser(USERNAME, TO, PASSWORD);
        service.sendAccountActivationMail(user);
        this.checkMailCredentials();
        assertTrue(this.getMimeMailBody().contains("http://coolsite.com:1234/forum/user/activate/" + user.getUuid()));
    }

    @Test
    public void activationMailShouldNotBeSentIfNotificationsAreDisabled() throws Exception {
        disableEmailNotifications();
        JCUser user = new JCUser(USERNAME, TO, PASSWORD);
        service.sendAccountActivationMail(user);
        verify(sender, never()).send(any(MimeMessage.class));
    }

    @Test
    public void testSendActivationMailFail() {
        enableEmailNotifications();
        Exception fail = new MailSendException("");
        doThrow(fail).when(sender).send(Matchers.<SimpleMailMessage>any());

        service.sendAccountActivationMail(new JCUser(USERNAME, TO, PASSWORD));
    }

    @Test(expectedExceptions = MailingFailedException.class)
    public void testRestorePasswordFail() throws NotFoundException, MailingFailedException {
        enableEmailNotifications();
        Exception fail = new MailSendException("");
        doThrow(fail).when(sender).send(Matchers.<MimeMessage>any());

        service.sendPasswordRecoveryMail(user, PASSWORD);
    }

    @Test
    public void testTopicUpdateNotificationFail() throws NotFoundException {
        enableEmailNotifications();
        Exception fail = new MailSendException("");
        doThrow(fail).when(sender).send(Matchers.<SimpleMailMessage>any());

        service.sendUpdatesOnSubscription(user, topic);
    }

    @Test
    public void testBranchUpdateNotificationFail() throws NotFoundException {
        enableEmailNotifications();
        Exception fail = new MailSendException("");
        doThrow(fail).when(sender).send(Matchers.<SimpleMailMessage>any());

        service.sendUpdatesOnSubscription(user, branch);
    }

    @Test
    public void testSendReceivedPrivateMessageNotificationFail() {
        enableEmailNotifications();
        Exception fail = new MailSendException("");
        doThrow(fail).when(sender).send(Matchers.<SimpleMailMessage>any());

        service.sendReceivedPrivateMessageNotification(user, new PrivateMessage(null, null, null, null));
    }

    @Test
    public void testSendTopicMovedMail() throws Exception {
        enableEmailNotifications();
        service.sendTopicMovedMail(user, topic);

        this.checkMailCredentials();
        assertTrue(this.getMimeMailBody().contains(USERNAME));
        assertTrue(this.getMimeMailBody().contains("http://coolsite.com:1234/forum/topics/" + topicId));
        assertTrue(this.getMimeMailBody().contains("http://coolsite.com:1234/forum/branches/" + branchId
                + "/unsubscribe"));
    }

    @Test
    public void topicMovedMailShouldNotBeSentIfNotificationsAreDisabled() throws Exception {
        disableEmailNotifications();
        service.sendTopicMovedMail(user, topic);
        verify(sender, never()).send(any(MimeMessage.class));
    }

    @Test
    public void testSendTopicMovedMailFailed() throws Exception {
        enableEmailNotifications();
        Exception fail = new MailSendException("");
        doThrow(fail).when(sender).send(Matchers.<SimpleMailMessage>any());

        service.sendTopicMovedMail(user, topic);
        
        this.checkMailCredentials();
        assertTrue(this.getMimeMailBody().contains("http://coolsite.com:1234/forum/topics/" + topicId));
        assertTrue(this.getMimeMailBody().contains("http://coolsite.com:1234/forum/branches/" + branchId
                + "/unsubscribe"));
    }
    
    @Test
    public void sendUserMentionedNotificationShouldSentIt() throws Exception {
        enableEmailNotifications();
        long postId = 25l;
        
        service.sendUserMentionedNotification(user, postId);
        
        this.checkMailCredentials();
        assertTrue(this.getMimeMailBody().contains(USERNAME));
        assertTrue(this.getMimeMailBody().contains("http://coolsite.com:1234/forum/posts/" + postId));
    }
    
    @Test
    public void sendUserMentionedNotificationShouldNotSentWhenForumNotificationsAreDisabled() 
                        throws Exception {
        disableEmailNotifications();
        long postId = 25l;
        
        service.sendUserMentionedNotification(user, postId);
        
        verify(sender, never()).send(any(MimeMessage.class));
    }

    private String getMimeMailBody() throws IOException, MessagingException {
        return ((MimeMultipart) ((MimeMultipart) ((MimeMultipart) captor.getValue().getContent()).getBodyPart(0).
                getDataHandler().getContent()).getBodyPart(0).getDataHandler().getContent()).getBodyPart(0).
                getDataHandler().getContent().toString();//sorry
    }

    private String getMimeMailSubject() throws IOException, MessagingException {
        return captor.getValue().getSubject();
    }

    private void checkMailCredentials() throws MessagingException {
        verify(sender).send(captor.capture());
        assertEquals(captor.getValue().getRecipients(Message.RecipientType.TO).length, 1);
        InternetAddress actualTo = (InternetAddress) captor.getValue().getRecipients(Message.RecipientType.TO)[0];
        assertEquals(actualTo.getAddress(), TO);
        InternetAddress actualFrom = (InternetAddress) captor.getValue().getFrom()[0];
        assertEquals(actualFrom.getAddress(), FROM);
    }
    
    
    private void disableEmailNotifications() {
        Property disabledProperty = new Property(PROPERTY_NAME, FALSE_STRING);
        when(propertyDao.getByName(PROPERTY_NAME)).thenReturn(disabledProperty);
    }
    
    private void enableEmailNotifications() {
        Property enabledProperty = new Property(PROPERTY_NAME, TRUE_STRING);
        when(propertyDao.getByName(PROPERTY_NAME)).thenReturn(enabledProperty);
    }

    @Test
    public void testSendRemovingTopicMail() throws Exception {
        enableEmailNotifications();

        service.sendRemovingTopicMail(user, topic);

        this.checkMailCredentials();

        String subjectTemplate =
                messageSource.getMessage("removeTopic.subject",  new Object[]{}, user.getLanguage().getLocale());

        String bodyTemplate =
                messageSource.getMessage("removeTopic.content",  new Object[]{}, user.getLanguage().getLocale());

        assertEquals(this.getMimeMailSubject(), subjectTemplate);
        assertTrue(this.getMimeMailBody().contains(bodyTemplate));
    }

    @Test
    public void testSendRemovingTopicMail_ForMethodWith3Params() throws Exception{
        enableEmailNotifications();

        service.sendRemovingTopicMail(user, topic, "admin");
        this.checkMailCredentials();

        String subjectTemplate =
                messageSource.getMessage("removeTopic.subject",  new Object[]{}, user.getLanguage().getLocale());

        String bodyTemplate =
                messageSource.getMessage("removeTopic.content",  new Object[]{}, user.getLanguage().getLocale());

        assertEquals(this.getMimeMailSubject(), subjectTemplate);
        assertTrue(this.getMimeMailBody().contains(bodyTemplate));
    }

    @Test
    public void removingTopicMailWhenShouldNotSentWhenForumNotificationsAreDisabled() throws Exception {
        disableEmailNotifications();

        service.sendRemovingTopicMail(user, topic);

        verify(sender, never()).send(any(MimeMessage.class));
    }

    @Test
    public void removingTopicMailWhenShouldNotSentWhenForumNotificationsAreDisabled_ForMethodWith3Params()
            throws Exception {
        disableEmailNotifications();

        service.sendRemovingTopicMail(user, topic, "admin");

        verify(sender, never()).send(any(MimeMessage.class));
    }

    @Test
    public void testSendRemovingTopicWitCodeReviewMail() throws Exception {
        enableEmailNotifications();
        topic.setType(TopicTypeName.CODE_REVIEW.getName());
        service.sendRemovingTopicMail(user, topic);

        this.checkMailCredentials();

        String subjectTemplate =
                messageSource.getMessage("removeCodeReview.subject",  new Object[]{}, user.getLanguage().getLocale());

        String bodyTemplate =
                messageSource.getMessage("removeCodeReview.content",  new Object[]{}, user.getLanguage().getLocale());

        assertEquals(this.getMimeMailSubject(), subjectTemplate);
        assertTrue(this.getMimeMailBody().contains(bodyTemplate));
    }

    @Test
    public void testSendRemovingTopicWitCodeReviewMail_ForMethodWith3Params() throws Exception {
        enableEmailNotifications();
        topic.setType(TopicTypeName.CODE_REVIEW.getName());
        service.sendRemovingTopicMail(user, topic, "admin");

        this.checkMailCredentials();

        String subjectTemplate =
                messageSource.getMessage("removeCodeReview.subject",  new Object[]{}, user.getLanguage().getLocale());

        String bodyTemplate =
                messageSource.getMessage("removeCodeReview.content",  new Object[]{}, user.getLanguage().getLocale());

        assertEquals(this.getMimeMailSubject(), subjectTemplate);
        assertTrue(this.getMimeMailBody().contains(bodyTemplate));
    }
    
    @Test
    public void testSendTopicCreationMail() throws Exception {
        enableEmailNotifications();
        branch.addTopic(topic);
        service.sendTopicCreationMail(user, topic);
        this.checkMailCredentials();

        String subjectTemplate = messageSource.getMessage("subscriptionNotification.subject",  
                                                          new Object[]{}, user.getLanguage().getLocale());

        String bodyTemplate = messageSource.getMessage("branchSubscriptionNotification.content",  
                                                       new Object[]{}, user.getLanguage().getLocale());

        assertEquals(this.getMimeMailSubject(), subjectTemplate  + ": " + branch.getName());
        assertTrue(this.getMimeMailBody().contains(bodyTemplate));      
        assertTrue(this.getMimeMailBody().contains("http://coolsite.com:1234/forum/topics/" + topic.getId()));
        assertTrue(this.getMimeMailBody().contains("http://coolsite.com:1234/forum/branches/" + branchId
                + "/unsubscribe"));
    }

    @Test
    public void topicCreationMailShouldNotSentWhenForumNotificationsAreDisabled() throws Exception {
        disableEmailNotifications();
        branch.addTopic(topic);
        service.sendTopicCreationMail(user, topic);
        verify(sender, never()).send(any(MimeMessage.class));
    }
}
