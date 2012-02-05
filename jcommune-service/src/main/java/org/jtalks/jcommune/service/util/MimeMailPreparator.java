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
package org.jtalks.jcommune.service.util;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;

/**
 * Helper class to create HTML emails.
 * It looks like custom MimeMessagePreparator implementation is the only
 * way to send HTML mail via Spring. The main purpose of this class is to produce
 * MimeMEssage, which otherwise can be done only via low-level Java Mail API.
 *
 * @author Evgeniy Naumenko
 */
public class MimeMailPreparator implements MimeMessagePreparator {

    private String to;
    private String from;
    private String subject;
    private String content;

    /**
     * @param to desctination mail address
     * @param from sender address, actula sending will be performed by the mail server, so do not use fake values here
     * @param subject mail headline
     * @param content HTML mail content
     */
    public MimeMailPreparator(String to, String from, String subject, String content) {
        this.to = to;
        this.from = from;
        this.subject = subject;
        this.content = content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(MimeMessage mimeMessage) throws Exception {

        MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
        message.setTo(to);
        message.setFrom(from);
        message.setSubject(subject);
        message.setText(content, true);
    }
}
