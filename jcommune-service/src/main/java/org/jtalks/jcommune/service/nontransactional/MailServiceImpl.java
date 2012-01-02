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

import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.MailService;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * @author Evgeniy Naumenko
 */
public class MailServiceImpl implements MailService {

    private MailSender mailSender;
    private SimpleMailMessage templateMessage;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

    // todo: apply i18n settings here somehow and extract them as templates
    private static final String PASSWORD_RECOVERY_TEMPLATE =
            "Dear %s!\n" +
                    "\n" +
                    "This is a password recovery mail from JTalks forum.\n" +
                    "Your new password is: %s\n" +
                    "\n" +
                    "Best regards,\n" +
                    "\n" +
                    "Jtalks forum.";

    private static final String SUBSCRIPTION_NOTIFICATION_TEMPLATE =
            "Dear %s!\n" +
                    "\n" +
                    "Your favorite forum has some updates.\n" +
                    "Please check it out at %s" +
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
    public MailServiceImpl(MailSender mailSender, SimpleMailMessage templateMessage) {
        this.mailSender = mailSender;
        this.templateMessage = templateMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendPasswordRecoveryMail(String userName, String email, String newPassword)
            throws MailingFailedException {
        this.sendEmail(
                email,
                "Password recovery",
                String.format(String.format(PASSWORD_RECOVERY_TEMPLATE, userName, newPassword)),
                "Password recovery email sending failed");
        LOGGER.info("Password recovery email sent for {}", userName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendUpdatesOnSubscription(User user) throws MailingFailedException {
        this.sendEmail(
                user.getEmail(),
                "Forum updates",
                String.format(SUBSCRIPTION_NOTIFICATION_TEMPLATE, user.getUsername()),
                "Subscription update sending failed");
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

}
