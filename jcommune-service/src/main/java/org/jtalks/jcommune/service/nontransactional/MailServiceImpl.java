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
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * @author Evgeniy Naumenko
 */
public class MailServiceImpl implements MailService {

    private MailSender mailSender;
    private SimpleMailMessage templateMessage;

    public MailServiceImpl(MailSender mailSender, SimpleMailMessage templateMessage) {
        this.mailSender = mailSender;
        this.templateMessage = templateMessage;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void sendPasswordRecoveryMail(User user, String newPassword) {
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setTo(user.getEmail());
        msg.setSubject("Password recovery");
        msg.setText(
                "Dear " + user.getFirstName() + " This is a greeting email with password " + newPassword +
        " with a locale " + LocaleContextHolder.getLocale().toString());
        try {
            this.mailSender.send(msg);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }
}
