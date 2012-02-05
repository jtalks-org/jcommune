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
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
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

    private JavaMailSender mailSender;
    private String from;
    private VelocityEngine velocityEngine;
    private MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
    private static final String LOG_TEMPLATE = "Error occurred while sending updates of %s %d to %s";
    private static final String TEMPLATES_PATH = "org/jtalks/jcommune/service/templates/";
    private static final String URL = "url";
    private static final String USER = "user";

    // todo: apply i18n settings here somehow and extract them as templates (velocity?)

    /**
     * Creates a mailing service with a default template message autowired.
     * "From" property is essential. Please note, that this address should be valid email address
     * as most e-mail servers will reject e-mail if sender is not really correlated with
     * the letter's "from" value.
     *
     * @param sender spring mailing tool
     * @param from   blank message with "from" filed preset
     * @param engine engine for templating email notifications
     * @param source for resolving internationalization messages
     */
    public MailService(JavaMailSender sender, String from, VelocityEngine engine, MessageSource source) {
        this.mailSender = sender;
        this.from = from;
        this.velocityEngine = engine;
        this.messageSource = source;
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
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", name);
        model.put("newPassword", newPassword);
        model.put(URL, url);
        String text = this.mergeTemplate("passwordRecovery.vm", model);
        this.sendEmail(email, "Password recovery", text);
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
        try {
            String url = this.getDeploymentRootUrl() + "/posts/" + topic.getLastPost().getId();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put(USER, user);
            model.put(URL, url);
            String text = this.mergeTemplate("subscriptionNotification.vm", model);
            this.sendEmail(user.getEmail(), "Forum updates", text);
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
        try {
            String url = this.getDeploymentRootUrl() + "/branches/" + branch.getId();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put(USER, user);
            model.put(URL, url);
            String text = this.mergeTemplate("subscriptionNotification.vm", model);
            this.sendEmail(user.getEmail(), "Forum updates", text);
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
        try {
            String url = this.getDeploymentRootUrl() + "/inbox/" + pmId;
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("recipient", recipient);
            model.put(URL, url);
            model.put("messageSource", messageSource);
            model.put("greeting", "greeting");
            model.put("content", "content");
            model.put("wish", "wish");
            model.put("signature", "signature");
            model.put("noArgs", new Object[]{});
            model.put("locale", recipient.getLanguage().getLocale());
            String text = this.mergeTemplate("receivedPrivateMessageNotification.vm", model);
            this.sendEmail(recipient.getEmail(), "Received private message", text);
        } catch (MailingFailedException e) {
            LOGGER.error(String.format(LOG_TEMPLATE, "Private message", pmId, recipient.getUsername()));
        }
    }

    /**
     * Sends email with a hyperlink to activate user account.
     *
     * @param recipient user to send activation mail to
     */
    public void sendAccountActivationMail(JCUser recipient) {
        try {
            String url = this.getDeploymentRootUrl() + "/user/activate/" + recipient.getUuid();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("name", recipient.getUsername());
            model.put("url", url);
            String text = this.mergeTemplate("accountActivation.vm", model);
            this.sendEmail(recipient.getEmail(), "JTalks account activation", text);
        } catch (MailingFailedException e) {
            LOGGER.error("Failed to sent activation mail for user: " + recipient.getUsername());
        }
    }

    /**
     * Just a convenience method for message sending to encapsulte
     * boilerplate error handling code.
     *
     * @param to      destination wmail address
     * @param subject message headline
     * @param text    message body, may contain html
     * @throws MailingFailedException exception with error message specified ic case of some error
     */
    private void sendEmail(String to, String subject, String text) throws MailingFailedException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(text, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Mail sending failed", e);
            throw new MailingFailedException(e);
        } catch (MailException e) {
            LOGGER.error("Mail sending failed", e);
            throw new MailingFailedException(e);
        }
    }

    /**
     * Creates a text message from templates and param given.
     * Template should be located in org/jtalks/jcommune/service/templates/
     *
     * @param templateName template file name, like "template.vm"
     * @param model        template params to be substituted in velocity template
     * @return text message, ready to be sent
     */
    private String mergeTemplate(String templateName, Map<String, Object> model) {
        String path = TEMPLATES_PATH + templateName;
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, path, model);
    }

    /**
     * @return current deployment root, e.g. "http://myhost.com:1234/mycoolforum"
     */
    private String getDeploymentRootUrl() {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        return request.getScheme() + "://" + request.getServerName() + request.getContextPath();
    }
}
