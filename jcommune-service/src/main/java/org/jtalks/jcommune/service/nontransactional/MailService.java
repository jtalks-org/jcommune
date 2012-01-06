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

import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * This service is focused on sending e-mail to the forum users.
 * Notifications, confirmations or e-mail based subscriptions of a various
 * kind should use this service to perform e-mail sending.
 *
 * @author Evgeniy Naumenko
 */
public class MailService  {

    private MailSender mailSender;
    private SimpleMailMessage templateMessage;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
    private static final String LOG_TEMPLATE = "Error occured while sending updates of %s %d to %s";

    // todo: apply i18n settings here somehow and extract them as templates (velocity?)
    private static final String PASSWORD_RECOVERY_TEMPLATE =
            "Dear %s!\n" +
                    "\n" +
                    "This is a password recovery mail from JTalks forum.\n" +
                    "Your new password is: %s\n" +
                    "Feel free to log in at %s.\n" +
                    "\n" +
                    "Best regards,\n" +
                    "\n" +
                    "Jtalks forum.";

    private static final String SUBSCRIPTION_NOTIFICATION_TEMPLATE =
            "Dear %s!\n" +
                    "\n" +
                    "Your favorite forum has some updates.\n" +
                    "Please check it out at %s.\n" +
                    "\n" +
                    "Best regards,\n" +
                    "\n" +
                    "Jtalks forum.";

    /**
     * Creates a mailing service with a default template message autowired.
     * Template message contains sender address and can be configured via spring
     * xml configuration. Please note, that this address should be valid email address
     * as most e-mail servers will reject e-mail if sender is not really correlated with
     * the letters "from" value.
     *
     * @param mailSender      spring mailing tool
     * @param templateMessage blank message with "from" filed preset
     */
    public MailService(MailSender mailSender, SimpleMailMessage templateMessage) {
        this.mailSender = mailSender;
        this.templateMessage = templateMessage;
    }

        /**
     * Sends a password recovery message for the user with a given email.
     * This method does not generate new password, just sends a message.
     *
     * @param userName    username to be used in a mail
     * @param email       address to mail to
     * @param newPassword new user password to be placed in an email
     * @throws MailingFailedException when mailing failed
     */
    public void sendPasswordRecoveryMail(String userName, String email, String newPassword)
            throws MailingFailedException {
        String url = this.getDeploymentRootUrl() + "/login/";
        this.sendEmail(
                email,
                "Password recovery",
                String.format(String.format(PASSWORD_RECOVERY_TEMPLATE, userName, newPassword, url)),
                "Password recovery email sending failed");
        LOGGER.info("Password recovery email sent for {}", userName);
    }

    /**
     * Sends update notification to user specified, e.g. when some new
     * posts were added to the topic. This method won't check if user
     * is subscribed to the particular notification or not.
     *
     * @param user a person to be notified about updates by email
     * @param topic topic changed (to include more detailes in email)
     */
    public void sendTopicUpdatesOnSubscription(User user, Topic topic) {
        String url = this.getDeploymentRootUrl() + "/posts/" + topic.getLastPost().getId();
        try {
            this.sendEmail(
                    user.getEmail(),
                    "Forum updates",
                    String.format(SUBSCRIPTION_NOTIFICATION_TEMPLATE, user.getUsername(), url),
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
     * @param user a person to be notified about updates by email
     * @param branch branch changed (to include more detailes in email)
     */
    public void sendBranchUpdatesOnSubscription(User user, Branch branch) {
        String url = this.getDeploymentRootUrl() + "/branches/" + branch.getId();
        try {
            this.sendEmail(
                    user.getEmail(),
                    "Forum updates",
                    String.format(SUBSCRIPTION_NOTIFICATION_TEMPLATE, user.getUsername(), url),
                    "Subscription update sending failed");
        } catch (MailingFailedException e) {
            LOGGER.error(String.format(LOG_TEMPLATE, "Branch", branch.getId(), user.getUsername()));
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
