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
import org.apache.velocity.tools.generic.EscapeTool;
import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.service.dto.EntityToDtoConverter;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
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

    public static final String REMOVE_TOPIC_SUBJECT_TEMPLATE = "removeTopic.subject";
    public static final String REMOVE_CODE_REVIEW_SUBJECT_TEMPLATE = "removeCodeReview.subject";
    public static final String REMOVE_TOPIC_MESSAGE_BODY_TEMPLATE = "removeTopic.vm";
    public static final String REMOVE_CODE_REVIEW_MESSAGE_BODY_TEMPLATE = "removeCodeReview.vm";

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
    private static final String LOG_TEMPLATE = "Error occurred while sending updates of %s %d to %s";
    private static final String HTML_TEMPLATES_PATH = "org/jtalks/jcommune/service/templates/html/";
    private static final String PLAIN_TEXT_TEMPLATES_PATH = "org/jtalks/jcommune/service/templates/plaintext/";
    private static final String LINK = "link";
    private static final String LINK_UNSUBSCRIBE = "link_unsubscribe";
    private static final String LINK_LABEL = "linkLabel";
    private static final String CUR_USER = "cur_user";
    private static final String USER = "user";
    private static final String NAME = "name";
    private static final String TOPIC = "topic";
    private static final String MESSAGE_SOURCE = "messageSource";
    private static final String RECIPIENT_LOCALE = "locale";
    private static final String NO_ARGS = "noArgs";
    private static final String ESCAPE_TOOL = "escape";
    private final JavaMailSender mailSender;
    private final String from;
    private final VelocityEngine velocityEngine;
    private final MessageSource messageSource;
    private final JCommuneProperty notificationsEnabledProperty;
    private final EscapeTool escapeTool;
    private final EntityToDtoConverter converter;

    /**
     * Creates a mailing service with a default template message autowired.
     * "From" property is essential. Please note, that this address should be valid email address
     * as most e-mail servers will reject e-mail if sender is not really correlated with
     * the letter's "from" value.
     *
     * @param sender                       spring mailing tool
     * @param from                         blank message with "from" filed preset
     * @param engine                       engine for templating email notifications
     * @param source                       for resolving internationalization messages
     * @param notificationsEnabledProperty to check whether email notifications are enabled
     * @param escapeTool                   velocity tool to perform html-escape
     */
    public MailService(JavaMailSender sender,
                       String from,
                       VelocityEngine engine,
                       MessageSource source,
                       JCommuneProperty notificationsEnabledProperty,
                       EscapeTool escapeTool,
                       EntityToDtoConverter converter) {
        this.mailSender = sender;
        this.from = from;
        this.velocityEngine = engine;
        this.messageSource = source;
        this.notificationsEnabledProperty = notificationsEnabledProperty;
        this.escapeTool = escapeTool;
        this.converter = converter;
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
        String urlSuffix = "/login";
        String url = this.getDeploymentRootUrl() + urlSuffix;
        String name = user.getUsername();
        Locale locale = user.getLanguage().getLocale();
        Map<String, Object> model = new HashMap<>();
        model.put(NAME, name);
        model.put("newPassword", newPassword);
        model.put(LINK, url);
        model.put(LINK_LABEL, getDeploymentRootUrlWithoutPort() + urlSuffix);
        model.put(RECIPIENT_LOCALE, locale);
        this.sendEmail(user.getEmail(), messageSource.getMessage("passwordRecovery.subject", new Object[]{}, locale),
                model, "passwordRecovery.vm");
        LOGGER.info("Password recovery email sent for {}", name);
    }

    /**
     * Sends update notification to user specified if
     * {@link SubscriptionAwareEntity} was updated, e.g. when some new
     * information were added to the subscribed entity. This method won't check if user
     * is subscribed to the particular notification or not.
     *
     * @param recipient a person to be notified about updates by email
     * @param entity    changed subscribed entity.
     */
    public void sendUpdatesOnSubscription(JCUser recipient, SubscriptionAwareEntity entity) {
        try {
            String urlSuffix = entity.prepareUrlSuffix();
            String url = this.getDeploymentRootUrl() + urlSuffix;
            Locale locale = recipient.getLanguage().getLocale();
            Map<String, Object> model = new HashMap<>();
            model.put(LINK, url);
            model.put(LINK_LABEL, getDeploymentRootUrlWithoutPort() + urlSuffix);
            if (entity instanceof Branch) {
                model.put(LINK_UNSUBSCRIBE, this.getDeploymentRootUrl() + getUnsubscribeBranchLink(entity));
            }
            sendEmailOnForumUpdates(recipient, model, locale, (Entity) entity,
                    "subscriptionNotification.subject", "subscriptionNotification.vm");
        } catch (MailingFailedException e) {
            LOGGER.error(String.format(LOG_TEMPLATE,
                    entity.getClass().getCanonicalName(),
                    ((Entity) entity).getId(),
                    recipient.getUsername()));
        }
    }

    /**
     * Sends email on forum updates.
     *
     * @param recipient a person to be notified about updates by email
     * @param model     template params to be substituted in velocity template
     * @param locale    recipient locale
     * @throws MailingFailedException when mailing failed
     */
    private void sendEmailOnForumUpdates(JCUser recipient, Map<String, Object> model, Locale locale,
                                         Entity entity, String subject, String nameTemplate) throws MailingFailedException {
        model.put(USER, recipient);
        model.put(RECIPIENT_LOCALE, locale);
        String titleEntity = this.getTitleName(entity);
        this.sendEmail(recipient.getEmail(), messageSource.getMessage(subject,
                new Object[]{}, locale) + titleEntity, model, nameTemplate);
    }

    /**
     * Sends notification to user about received private message.
     *
     * @param recipient a person to be notified about received private message by email
     * @param pm        private message itself
     */
    public void sendReceivedPrivateMessageNotification(JCUser recipient, PrivateMessage pm) {
        try {
            String urlSuffix = "/pm/inbox/" + pm.getId();
            String url = this.getDeploymentRootUrl() + urlSuffix;
            Locale locale = recipient.getLanguage().getLocale();
            Map<String, Object> model = new HashMap<>();
            model.put("recipient", recipient);
            model.put(LINK, url);
            model.put(LINK_LABEL, getDeploymentRootUrlWithoutPort() + urlSuffix);
            model.put(RECIPIENT_LOCALE, locale);
            this.sendEmail(recipient.getEmail(),
                    messageSource.getMessage("receivedPrivateMessageNotification.subject", new Object[]{}, locale),
                    model, "receivedPrivateMessageNotification.vm");
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
            String urlSuffix = "/user/activate/" + recipient.getUuid();
            String url = this.getDeploymentRootUrl() + urlSuffix;
            Locale locale = recipient.getLanguage().getLocale();
            Map<String, Object> model = new HashMap<>();
            model.put(NAME, recipient.getUsername());
            model.put(LINK, url);
            model.put(LINK_LABEL, getDeploymentRootUrlWithoutPort() + urlSuffix);
            model.put(RECIPIENT_LOCALE, locale);
            this.sendEmail(recipient.getEmail(), messageSource.getMessage("accountActivation.subject",
                    new Object[]{}, locale), model, "accountActivation.vm");
        } catch (MailingFailedException e) {
            LOGGER.error("Failed to sent activation mail for user: " + recipient.getUsername());
        }
    }

    /**
     * Sends email to topic starter that his or her topic was moved
     *
     * @param recipient user to send notification
     * @param topic     relocated topic
     */
    public void sendTopicMovedMail(JCUser recipient, Topic topic) {
        String urlSuffix = getTopicUrlSuffix(topic);
        String url = this.getDeploymentRootUrl() + urlSuffix;
        Locale locale = recipient.getLanguage().getLocale();
        Map<String, Object> model = new HashMap<>();
        model.put(NAME, recipient.getUsername());
        model.put(LINK, url);
        model.put(LINK_UNSUBSCRIBE, this.getDeploymentRootUrl() + getUnsubscribeBranchLink(topic.getBranch()));
        model.put(LINK_LABEL, getDeploymentRootUrlWithoutPort() + urlSuffix);
        model.put(RECIPIENT_LOCALE, locale);
        try {
            this.sendEmail(recipient.getEmail(), messageSource.getMessage("moveTopic.subject",
                    new Object[]{}, locale), model, "moveTopic.vm");
        } catch (MailingFailedException e) {
            LOGGER.error("Failed to sent activation mail for user: " + recipient.getUsername());
        }
    }

    /**
     * Sends email to topic starter that his or her topic was moved
     *
     * @param recipient user to send notification
     * @param topic     relocated topic
     * @param curUser   User that moved topic
     */
    public void sendTopicMovedMail(JCUser recipient, Topic topic, String curUser) {
        String urlSuffix = getTopicUrlSuffix(topic);
        String url = this.getDeploymentRootUrl() + urlSuffix;
        Locale locale = recipient.getLanguage().getLocale();
        Map<String, Object> model = new HashMap<>();
        model.put(NAME, recipient.getUsername());
        model.put(CUR_USER, curUser);
        model.put(LINK, url);
        model.put(LINK_UNSUBSCRIBE, this.getDeploymentRootUrl() + getUnsubscribeBranchLink(topic.getBranch()));
        model.put(LINK_LABEL, getDeploymentRootUrlWithoutPort() + urlSuffix);
        model.put(RECIPIENT_LOCALE, locale);
        try {
            this.sendEmail(recipient.getEmail(), messageSource.getMessage("moveTopic.subject",
                    new Object[]{}, locale), model, "moveTopic.vm");
        } catch (MailingFailedException e) {
            LOGGER.error("Failed to sent activation mail for user: " + recipient.getUsername());
        }
    }

    /**
     * Send email notification to user when he was mentioned in forum.
     * Email notification will be sent only when notifications are enabled
     * in forum, otherwise nothing will happen.
     *
     * @param recipient mentioned user who will receive notification
     * @param postId    id of post where user was mentioned
     */
    public void sendUserMentionedNotification(JCUser recipient, long postId) {
        String urlSuffix = "/posts/" + postId;
        String url = this.getDeploymentRootUrl() + urlSuffix;
        Locale locale = recipient.getLanguage().getLocale();
        Map<String, Object> model = new HashMap<>();
        model.put(NAME, recipient.getUsername());
        model.put(LINK, url);
        model.put(LINK_LABEL, getDeploymentRootUrlWithoutPort() + urlSuffix);
        model.put(RECIPIENT_LOCALE, locale);
        try {
            this.sendEmail(recipient.getEmail(), messageSource.getMessage("userMentioning.subject",
                    new Object[]{}, locale), model, "userMentioning.vm");
        } catch (MailingFailedException e) {
            LOGGER.error("Failed to sent activation mail for user: " + recipient.getUsername());
        }
    }

    /**
     * Just a convenience method for message sending to encapsulate
     * boilerplate error handling code.
     *
     * @param to           destination email address
     * @param subject      message headline
     * @param model        template params to be substituted in velocity template
     * @param templateName template file name, like "template.vm"
     * @throws MailingFailedException exception with error message specified ic case of some error
     */
    private void sendEmail(String to, String subject, Map<String, Object> model,
                           String templateName) throws MailingFailedException {
        if (!notificationsEnabledProperty.booleanValue()) {
            LOGGER.debug("Email notifications are turned off in Forum Settings, skip sending to [{}]" +
                    " mail with subject [{}]. User with Admin Permissions can enter Poulpe (that should be changed" +
                    " soon) and change the setting.", to, subject);
            return;
        }
        LOGGER.debug("Sending email to [{}] with subject [{}]", to, subject);
        try {
            model.put(MESSAGE_SOURCE, messageSource);
            model.put(ESCAPE_TOOL, escapeTool);
            model.put(NO_ARGS, new Object[]{});
            String plainText = this.mergePlainTextTemplate(templateName, model);
            String htmlText = this.mergeHtmlTemplate(templateName, model);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(plainText, htmlText);
            mailSender.send(message);
        } catch (MailException | MessagingException e) {
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
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, path, "UTF-8", model);
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
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, path, "UTF-8", model);
    }

    /**
     * @return current deployment root, e.g. "http://myhost.com:1234/mycoolforum"
     */
    private String getDeploymentRootUrl() {
        HttpServletRequest request = getServletRequest();
        return request.getScheme()
                + "://" + request.getServerName()
                + ":" + request.getServerPort()
                + request.getContextPath();
    }

    /**
     * Returns current deployment root without port for using as label link, for example.
     *
     * @return current deployment root without port, e.g. "http://myhost.com/mycoolforum"
     */
    private String getDeploymentRootUrlWithoutPort() {
        HttpServletRequest request = getServletRequest();
        return request.getScheme()
                + "://" + request.getServerName()
                + request.getContextPath();
    }

    /**
     * @return native {@link HttpServletRequest}
     */
    private HttpServletRequest getServletRequest() {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        return ((ServletRequestAttributes) attributes).getRequest();
    }

    /**
     * @param entity entity like "Branch/Topics"
     * @return title for Topic/Branch or "" if entity is not instanceof Topic/Branch
     */
    private String getTitleName(Entity entity) {
        if (entity instanceof Topic) {
            Topic topic = (Topic) entity;
            return ": " + topic.getTitle();
        } else if (entity instanceof Branch) {
            Branch branch = (Branch) entity;
            return ": " + branch.getName();
        } else {
            return "";
        }
    }

    /**
     * Set mail about removing topic.
     *
     * @param recipient Recipient for which send notification
     * @param topic     Current topic
     */
    public void sendRemovingTopicMail(JCUser recipient, Topic topic) {
        Locale locale = recipient.getLanguage().getLocale();
        Map<String, Object> model = new HashMap<>();
        model.put(USER, recipient);
        model.put(RECIPIENT_LOCALE, locale);
        model.put(LINK_UNSUBSCRIBE, this.getDeploymentRootUrl() + getUnsubscribeBranchLink(topic.getBranch()));
        model.put(TOPIC, topic);

        try {

            String subjectTemplate = REMOVE_TOPIC_SUBJECT_TEMPLATE;
            String messageBodyTemplate = REMOVE_TOPIC_MESSAGE_BODY_TEMPLATE;

            if (topic.isCodeReview()) {
                subjectTemplate = REMOVE_CODE_REVIEW_SUBJECT_TEMPLATE;
                messageBodyTemplate = REMOVE_CODE_REVIEW_MESSAGE_BODY_TEMPLATE;
            }

            String subject = messageSource.getMessage(subjectTemplate, new Object[]{}, locale);
            this.sendEmail(recipient.getEmail(), subject, model, messageBodyTemplate);

        } catch (MailingFailedException e) {
            LOGGER.error("Failed to sent mail about removing topic or code review for user: "
                    + recipient.getUsername());
        }
    }

    /**
     * Set mail about removing topic.
     *
     * @param recipient Recipient for which send notification
     * @param topic     Current topic
     * @param curUser   User that removed the topic
     */
    public void sendRemovingTopicMail(JCUser recipient, Topic topic, String curUser) {
        Locale locale = recipient.getLanguage().getLocale();
        Map<String, Object> model = new HashMap<>();
        model.put(USER, recipient);
        model.put(RECIPIENT_LOCALE, locale);
        model.put(CUR_USER, curUser);
        model.put(LINK_UNSUBSCRIBE, this.getDeploymentRootUrl() + getUnsubscribeBranchLink(topic.getBranch()));
        model.put(TOPIC, topic);

        try {

            String subjectTemplate = REMOVE_TOPIC_SUBJECT_TEMPLATE;
            String messageBodyTemplate = REMOVE_TOPIC_MESSAGE_BODY_TEMPLATE;

            if (topic.isCodeReview()) {
                subjectTemplate = REMOVE_CODE_REVIEW_SUBJECT_TEMPLATE;
                messageBodyTemplate = REMOVE_CODE_REVIEW_MESSAGE_BODY_TEMPLATE;
            }

            String subject = messageSource.getMessage(subjectTemplate, new Object[]{}, locale);
            this.sendEmail(recipient.getEmail(), subject, model, messageBodyTemplate);

        } catch (MailingFailedException e) {
            LOGGER.error("Failed to sent mail about removing topic or code review for user: "
                    + recipient.getUsername());
        }
    }

    /**
     * Send email about new topic in the subscribed branch.
     *
     * @param subscriber recipient
     * @param topic      newly created topic
     */
    void sendTopicCreationMail(JCUser subscriber, Topic topic) {
        try {
            String urlSuffix = getTopicUrlSuffix(topic);
            String url = this.getDeploymentRootUrl() + urlSuffix;
            Locale locale = subscriber.getLanguage().getLocale();
            Map<String, Object> model = new HashMap<>();
            model.put(LINK, url);
            model.put(LINK_UNSUBSCRIBE, this.getDeploymentRootUrl()
                    + getUnsubscribeBranchLink(topic.getBranch()));
            model.put(LINK_LABEL, getDeploymentRootUrlWithoutPort() + urlSuffix);
            sendEmailOnForumUpdates(subscriber, model, locale, topic.getBranch(),
                    "subscriptionNotification.subject", "branchSubscriptionNotification.vm");
        } catch (MailingFailedException e) {
            LOGGER.error("Failed to sent mail about creation topic for user: " + subscriber.getUsername());
        }
    }

    private String getUnsubscribeBranchLink(SubscriptionAwareEntity entity) {
        String result = "/branches/{0}/unsubscribe";
        if (entity instanceof Branch) {
            return result.replace("{0}", "" + ((Branch) entity).getId());
        }
        if (entity instanceof Topic) {
            return result.replace("{0}", "" + ((Topic) entity).getBranch().getId());
        }
        if (entity instanceof Post) {
            return result.replace("{0}", "" + ((Post) entity).getTopic().getBranch().getId());
        }
        return null;
    }

    /**
     * Gets url suffix of specified topic. Urls of topics provided by plugins can differ
     *
     * @param topic topic to get url
     *
     * @return url of specified topic
     */
    private String getTopicUrlSuffix(Topic topic) {
        return converter.convertTopicToDto(topic).getTopicUrl();
    }
}
