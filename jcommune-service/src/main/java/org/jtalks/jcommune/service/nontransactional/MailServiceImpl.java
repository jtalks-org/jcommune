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

import org.jtalks.jcommune.service.MailService;
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

    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    // todo: apply i18n settings here somehow
    private static final String PASSWORD_RECOVERY_TEMPLATE =
            "Dear %s!\n" +
                    "\n" +
                    "This is a password recovery mail from JTalks forum.\n" +
                    "Your new password is: %s\n" +
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
    public void sendPasswordRecoveryMail(String userName, String email, String newPassword) {
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setTo(email);
        msg.setSubject("Password recovery");
        msg.setText(String.format(PASSWORD_RECOVERY_TEMPLATE, userName, newPassword));
        try {
            this.mailSender.send(msg);
            logger.info("Password recovery email sent for {}", userName);
        } catch (MailException e) {
            logger.error("Password recovery email sending failed", e);
        }
    }
}
