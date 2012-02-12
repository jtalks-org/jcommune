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
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    private BBCodeService bbCodeService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
    private static final String LOG_TEMPLATE = "Error occurred while sending updates of %s %d to %s";
    private static final String HTML_TEMPLATES_PATH = "org/jtalks/jcommune/service/templates/html/";
    private static final String PLAIN_TEXT_TEMPLATES_PATH = "org/jtalks/jcommune/service/templates/plaintext/";
    private static final String URL = "url";
    private static final String USER = "user";
    private static final String NAME = "name";
    private static final String MESSAGE_SOURCE = "messageSource";
    private static final String GREETING_RESOURCE_KEY = "greeting";
    private static final String CONTENT_RESOURCE_KEY = "content";
    private static final String LINK_RESOURCE_KEY = "link";
    private static final String WISH_RESOURCE_KEY = "wish";
    private static final String SIGNATURE_RESOURCE_KEY = "signature";
    private static final String RECIPIENT_LOCALE = "locale";
    private static final String NO_ARGS = "noArgs";
    private static final String PASSWORD_RECOVERY_TEMPLATE = "passwordRecovery.vm";
    private static final String SUBSCRIPTION_NOTIFICATION_TEMPLATE = "subscriptionNotification.vm";
    private static final String RECEIVED_PM_NOTIFICATION_TEMPLATE = "receivedPrivateMessageNotification.vm";
    private static final String ACCOUNT_ACTIVATION_TEMPLATE = "accountActivation.vm";


    /**
     * Creates a mailing service with a default template message autowired.
     * "From" property is essential. Please note, that this address should be valid email address
     * as most e-mail servers will reject e-mail if sender is not really correlated with
     * the letter's "from" value.
     *
     * @param sender        spring mailing tool
     * @param from          blank message with "from" filed preset
     * @param engine        engine for templating email notifications
     * @param source        for resolving internationalization messages
     * @param bbCodeService to transform BB-encoded text to HTML
     */
    public MailService(JavaMailSender sender, String from, VelocityEngine engine, MessageSource source,
                       BBCodeService bbCodeService) {
        this.mailSender = sender;
        this.from = from;
        this.velocityEngine = engine;
        this.messageSource = source;
        this.bbCodeService = bbCodeService;
    }

    /**
     * Sends a password recovery message for the user.
     * This method does not generate new password, just sends a message.
     *
     * @param user        a person we will send a mail
     * @param newPassword new user password to be placed in an email
     * @throws MailingFailedException when mailing failed
     */
    public void sendPasswordRecoveryMail(JCUser user, String newPassword) throws MailingFailedException {
        String url = this.getDeploymentRootUrl() + "/login";
        String name = user.getUsername();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put(NAME, name);
        model.put("newPassword", newPassword);
        model.put(URL, url);
        model.put(MESSAGE_SOURCE, messageSource);
        model.put(GREETING_RESOURCE_KEY, GREETING_RESOURCE_KEY);
        model.put("contentPart1", "passwordRecovery.content.part1");
        model.put("contentPart2", "passwordRecovery.content.part2");
        model.put(LINK_RESOURCE_KEY, "passwordRecovery.link");
        model.put(WISH_RESOURCE_KEY, WISH_RESOURCE_KEY);
        model.put(SIGNATURE_RESOURCE_KEY, SIGNATURE_RESOURCE_KEY);
        model.put(NO_ARGS, new Object[]{});
        model.put(RECIPIENT_LOCALE, user.getLanguage().getLocale());
        String plainText = this.mergePlainTextTemplate(PASSWORD_RECOVERY_TEMPLATE, model);
        String htmlText = this.mergeHtmlTemplate(PASSWORD_RECOVERY_TEMPLATE, model);
        this.sendEmail(user.getEmail(), "Password recovery", plainText, htmlText);
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
            model.put(MESSAGE_SOURCE, messageSource);
            model.put(GREETING_RESOURCE_KEY, GREETING_RESOURCE_KEY);
            model.put(CONTENT_RESOURCE_KEY, "subscriptionNotification.content");
            model.put(LINK_RESOURCE_KEY, "subscriptionNotification.link");
            model.put(WISH_RESOURCE_KEY, WISH_RESOURCE_KEY);
            model.put(SIGNATURE_RESOURCE_KEY, SIGNATURE_RESOURCE_KEY);
            model.put(NO_ARGS, new Object[]{});
            model.put(RECIPIENT_LOCALE, user.getLanguage().getLocale());
            String plainText = this.mergePlainTextTemplate(SUBSCRIPTION_NOTIFICATION_TEMPLATE, model);
            String htmlText = this.mergeHtmlTemplate(SUBSCRIPTION_NOTIFICATION_TEMPLATE, model);
            this.sendEmail(user.getEmail(), "Forum updates", plainText, htmlText);
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
            model.put(MESSAGE_SOURCE, messageSource);
            model.put(GREETING_RESOURCE_KEY, GREETING_RESOURCE_KEY);
            model.put(CONTENT_RESOURCE_KEY, "subscriptionNotification.content");
            model.put(LINK_RESOURCE_KEY, "subscriptionNotification.link");
            model.put(WISH_RESOURCE_KEY, WISH_RESOURCE_KEY);
            model.put(SIGNATURE_RESOURCE_KEY, SIGNATURE_RESOURCE_KEY);
            model.put(NO_ARGS, new Object[]{});
            model.put(RECIPIENT_LOCALE, user.getLanguage().getLocale());
            String plainText = this.mergePlainTextTemplate(SUBSCRIPTION_NOTIFICATION_TEMPLATE, model);
            String htmlText = this.mergeHtmlTemplate(SUBSCRIPTION_NOTIFICATION_TEMPLATE, model);
            this.sendEmail(user.getEmail(), "Forum updates", plainText, htmlText);
        } catch (MailingFailedException e) {
            LOGGER.error(String.format(LOG_TEMPLATE, "Branch", branch.getId(), user.getUsername()));
        }
    }

    /**
     * Sends notification to user about received private message.
     *
     * @param recipient a person to be notified about received private message by email
     * @param pm        private message itself
     */
    public void sendReceivedPrivateMessageNotification(JCUser recipient, PrivateMessage pm) {
        try {
            String url = this.getDeploymentRootUrl() + "/pm/" + pm.getId();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("recipient", recipient);
            model.put(URL, url);
            model.put(MESSAGE_SOURCE, messageSource);
            model.put(GREETING_RESOURCE_KEY, GREETING_RESOURCE_KEY);
            model.put(CONTENT_RESOURCE_KEY, "receivedPrivateMessageNotification.content");
            model.put(LINK_RESOURCE_KEY, "receivedPrivateMessageNotification.link");
            model.put(WISH_RESOURCE_KEY, WISH_RESOURCE_KEY);
            model.put(SIGNATURE_RESOURCE_KEY, SIGNATURE_RESOURCE_KEY);
            model.put(NO_ARGS, new Object[]{});
            model.put(RECIPIENT_LOCALE, recipient.getLanguage().getLocale());
            model.put("title", pm.getTitle());
            model.put("message", bbCodeService.removeBBCodes(pm.getBody()));
            String plainText = this.mergePlainTextTemplate(RECEIVED_PM_NOTIFICATION_TEMPLATE, model);
            String htmlText = this.mergeHtmlTemplate(RECEIVED_PM_NOTIFICATION_TEMPLATE, model);
            this.sendEmail(recipient.getEmail(), "Received private message", plainText, htmlText);
        } catch (MailingFailedException e) {
            LOGGER.error(String.format(LOG_TEMPLATE, "Private message", pm.getId(), recipient.getUsername()));
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
            model.put(NAME, recipient.getUsername());
            model.put(URL, url);
            model.put(MESSAGE_SOURCE, messageSource);
            model.put(GREETING_RESOURCE_KEY, GREETING_RESOURCE_KEY);
            model.put("contentPart1", "accountActivation.content.part1");
            model.put("contentPart2", "accountActivation.content.part2");
            model.put("contentPart3", "accountActivation.content.part3");
            model.put(LINK_RESOURCE_KEY, "accountActivation.link");
            model.put(WISH_RESOURCE_KEY, WISH_RESOURCE_KEY);
            model.put(SIGNATURE_RESOURCE_KEY, SIGNATURE_RESOURCE_KEY);
            model.put(NO_ARGS, new Object[]{});
            model.put(RECIPIENT_LOCALE, recipient.getLanguage().getLocale());
            String plainText = this.mergePlainTextTemplate(ACCOUNT_ACTIVATION_TEMPLATE, model);
            String htmlText = this.mergeHtmlTemplate(ACCOUNT_ACTIVATION_TEMPLATE, model);
            this.sendEmail(recipient.getEmail(), "JTalks account activation", plainText, htmlText);
        } catch (MailingFailedException e) {
            LOGGER.error("Failed to sent activation mail for user: " + recipient.getUsername());
        }
    }

    /**
     * Just a convenience method for message sending to encapsulate
     * boilerplate error handling code.
     *
     * @param to        destination email address
     * @param subject   message headline
     * @param plainText plaintext message body
     * @param htmlText  html message body
     * @throws MailingFailedException exception with error message specified ic case of some error
     */
    private void sendEmail(String to, String subject, String plainText, String htmlText) throws MailingFailedException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(plainText, htmlText);
            mailSender.send(message);
        } catch (Exception e) {
            LOGGER.error("Mail sending failed", e);
            throw new MailingFailedException(e);
        }
    }

    /**
     * Creates a html text message from templates and param given.
     * Template should be located in org/jtalks/jcommune/service/templates/html/
     *
     * @param templateName template file name, like "template.vm"
     * @param model        template params to be substituted in velocity template
     * @return html text message, ready to be sent
     */
    private String mergeHtmlTemplate(String templateName, Map<String, Object> model) {
        String path = HTML_TEMPLATES_PATH + templateName;
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, path, model);
    }

    /**
     * Creates a plain text message from templates and param given.
     * Template should be located in org/jtalks/jcommune/service/templates/plaintext/
     *
     * @param templateName template file name, like "template.vm"
     * @param model        template params to be substituted in velocity template
     * @return plain text message, ready to be sent
     */
    private String mergePlainTextTemplate(String templateName, Map<String, Object> model) {
        String path = PLAIN_TEXT_TEMPLATES_PATH + templateName;
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, path, model);
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
