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

import com.sun.mail.smtp.SMTPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;

import javax.mail.Address;
import javax.mail.internet.MimeMessage;

/**
 * Object sends email in a separate thread.
 * Amount of thread depends on parameter "pool-size" in spring task.
 * See http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/scheduling.html
 *
 * @author Andrey Ivanov
 */
public class MailSender extends JavaMailSenderImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailSender.class);

    @Async
    @Override
    public void send(MimeMessage mimeMessage) throws MailException {
        try {
            String subject = mimeMessage.getSubject();
            StringBuilder to = new StringBuilder();
            for (Address address : mimeMessage.getRecipients(MimeMessage.RecipientType.TO)) {
                to.append(address.toString());
            }
            SMTPMessage message = new SMTPMessage(mimeMessage);
            message.setEnvelopeFrom(getUsername());
            long started = System.currentTimeMillis();
            super.send(message);
            long secsTook = (System.currentTimeMillis() - started) / 1000;
            if (secsTook > 30) {
                LOGGER.warn("Sending email took long time [{}] for receiver: [{}]. Subject: [{}]",
                        new Object[]{secsTook, to, subject});
            } else if (secsTook > 5) {
                LOGGER.info("Sending email took long time [{}] for receiver: [{}]. Subject: [{}]",
                        new Object[]{secsTook, to, subject});
            }
            LOGGER.debug("Email was sent to [{}] with subject [{}]. Note that this doesn't mean the mail" +
                    " is delivered to the end user, this only means that mail server accepted the email and will" +
                    " try to send it further.", to, subject);
        } catch (Exception e) {
            LOGGER.error("Mail sending failed", e);
        }
    }
}
