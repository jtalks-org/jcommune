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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jtalks.jcommune.service.MailService;
import org.jtalks.jcommune.service.exceptions.MailingFailedException;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test for {@link MailServiceImpl}.
 *
 * @author Evgeniy Naumenko
 */
public class MailServiceTest {

    private MailService service;
    private MailSender sender;
    private SimpleMailMessage message;

    private static final String FROM = "lol@wut.zz";
    private static final String TO = "foo@bar.zz";
    private static final String username = "user";
    private static final String password = "new_password";

    @BeforeMethod
    public void setUp() {
        sender = mock(MailSender.class);
        message = new SimpleMailMessage();
        message.setFrom(FROM);
        service = new MailServiceImpl(sender, message);
    }

    @Test
    public void testSendPasswordRecoveryMail() throws MailingFailedException {
        service.sendPasswordRecoveryMail(username, TO, password);
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(captor.capture());
        assertEquals(captor.getValue().getTo().length, 1);
        assertEquals(captor.getValue().getTo()[0], TO);
        assertEquals(captor.getValue().getFrom(), FROM);
        assertTrue(captor.getValue().getText().contains(username));
        assertTrue(captor.getValue().getText().contains(password));
    }

    @Test(expectedExceptions = MailingFailedException.class)
    public void testRestorePasswordFail() throws NotFoundException, MailingFailedException {
        Exception fail = new MailSendException("");
        doThrow(fail).when(sender).send(Matchers.<SimpleMailMessage>any());

        service.sendPasswordRecoveryMail(username, TO, password);
    }

}
