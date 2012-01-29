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
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * This service is focused on sending e-mail to forum users.
 * Notifications, confirmations or e-mail based subscriptions of a various
 * kind should use this service to perform e-mail sending.
 *
 * @author Evgeniy Naumenko
 * @author Eugeny Batov
 */
public class MailService {

    private MailSender mailSender;
    private SimpleMailMessage templateMessage;
    private VelocityEngine velocityEngine;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
    private static final String LOG_TEMPLATE = "Error occurred while sending updates of %s %d to %s";

    // todo: apply i18n settings here somehow and extract them as templates (velocity?)

    /**
     * Creates a mailing service with a default template message autowired.
     * Template message contains sender address and can be configured via spring
     * xml configuration. Please note, that this address should be valid email address
     * as most e-mail servers will reject e-mail if sender is not really correlated with
     * the letters "from" value.
     *
     * @param mailSender      spring mailing tool
     * @param templateMessage blank message with "from" filed preset
     * @param velocityEngine  engine for templating email notifications
     */
    public MailService(MailSender mailSender, SimpleMailMessage templateMessage, VelocityEngine velocityEngine) {
        this.mailSender = mailSender;
        this.templateMessage = templateMessage;
        this.velocityEngine = velocityEngine;
    }

    /**
     * Sends a password recovery message for the user with a given email.
     * This method does not generate new password, just sends a message.
     *
     * @param name        username to be used in a mail
     * @param email       address to mail to
     * @param newPassword new user password to be placed in an email
     * @throws MailingFailedException when mailing failed
     */
    public void sendPasswordRecoveryMail(String name, String email, String newPassword) throws MailingFailedException {
        String url = this.getDeploymentRootUrl() + "/login/";
        Map model = new HashMap();
        model.put("name", name);
        model.put("newPassword", newPassword);
        model.put("url", url);
        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                "org/jtalks/jcommune/service/templates/passwordRecoveryTemplate.vm", model);
        this.sendEmail(
                email,
                "Password recovery",
                text,
                "Password recovery email sending failed");
        LOGGER.info("Password recovery email sent for {}", name);
    }

    /**
     * Sends update notification to user specified, e.g. when some new
     * posts were added to the topic. This method won't check if user
     * is subscribed to the particular notification or not.
     *
     * @param user  a person to be notified about updates by email
     * @param topic topic changed (to include more detailes in email)
     */
    public void sendTopicUpdatesOnSubscription(JCUser user, Topic topic) {
        String url = this.getDeploymentRootUrl() + "/posts/" + topic.getLastPost().getId();
        try {
            Map model = new HashMap();
            model.put("user", user);
            model.put("url", url);
            String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                    "org/jtalks/jcommune/service/templates/subscriptionNotificationTemplate.vm", model);
            this.sendEmail(
                    user.getEmail(),
                    "Forum updates",
                    text,
                    "Subscription update sending failed");
        } catch (MailingFailedException e) {
            LOGGER.error(String.format(LOG_TEMPLATE, "Topic", topic.getId(), user.getUsername()));
        }
    }

    /**
     * Sends update notification to user specified, e.g. when some new
     * posts were added to the topic. This method won't check if user
     * is subscribed to the particular notification or not.
     *
     * @param user   a person to be notified about updates by email
     * @param branch branch changed (to include more detailes in email)
     */
    public void sendBranchUpdatesOnSubscription(JCUser user, Branch branch) {
        String url = this.getDeploymentRootUrl() + "/branches/" + branch.getId();
        try {
            Map model = new HashMap();
            model.put("user", user);
            model.put("url", url);
            String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                    "org/jtalks/jcommune/service/templates/subscriptionNotificationTemplate.vm", model);
            this.sendEmail(
                    user.getEmail(),
                    "Forum updates",
                    text,
                    "Subscription update sending failed");
        } catch (MailingFailedException e) {
            LOGGER.error(String.format(LOG_TEMPLATE, "Branch", branch.getId(), user.getUsername()));
        }
    }

    /**
     * Sends notification to user about received private message.
     *
     * @param recipient a person to be notified about received private message by email
     * @param pmId      id of received private message
     */
    public void sendReceivedPrivateMessageNotification(JCUser recipient, long pmId) {
        String url = this.getDeploymentRootUrl() + "/inbox/" + pmId;
        try {
            Map model = new HashMap();
            model.put("recipient", recipient);
            model.put("url", url);
            String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                    "org/jtalks/jcommune/service/templates/receivedPrivateMessageNotificationTemplate.vm", model);
            this.sendEmail(recipient.getEmail(),
                    "Received private message",
                    text,
                    "Received private message notification sending failed");
        } catch (MailingFailedException e) {
            LOGGER.error(String.format(LOG_TEMPLATE, "Private message", pmId, recipient.getUsername()));
        }
    }

    /**
     * Just a convenience method for message sending to encapsulte
     * boilerplate error handling code.
     *
     * @param to           destination wmail address
     * @param subject      message headline
     * @param text         message body, may contain html
     * @param errorMessage to be logged and thrown if some error occurs
     * @throws MailingFailedException exception with error message specified ic case of some error
     */
    private void sendEmail(String to, String subject, String text, String errorMessage) throws MailingFailedException {
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        try {
            this.mailSender.send(msg);
        } catch (MailException e) {
            LOGGER.error(errorMessage, e);
            throw new MailingFailedException(errorMessage, e);
        }
    }

    /**
     * @return current deployment root, e.g. "http://myhost.com:1234/mycoolforum"
     */
    private String getDeploymentRootUrl() {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        return request.getScheme()
                + "://" + request.getServerName()
                + ":" + request.getServerPort()
                + request.getContextPath();
    }
}
