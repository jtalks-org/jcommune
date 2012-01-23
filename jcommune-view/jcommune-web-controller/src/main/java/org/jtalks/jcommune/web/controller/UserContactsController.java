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
package org.jtalks.jcommune.web.controller;

import org.jtalks.jcommune.model.dao.UserContactsDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.UserContact;
import org.jtalks.jcommune.model.entity.UserContactType;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Evgeniy Naumenko
 */
@Controller
public class UserContactsController {

    private SecurityService securityService;
    private UserContactsDao dao;

    /**
     *
     * @param securityService
     * @param dao
     */
    @Autowired
    public UserContactsController(SecurityService securityService, UserContactsDao dao) {
        this.securityService = securityService;
        this.dao = dao;
    }

    /**
     *
     * @return
     */
    @RequestMapping(value="contacts/types", method = RequestMethod.GET)
    @ResponseBody
    public List<UserContactType> getContactTypes() {
       return dao.getAvailableContactTypes();
    }

    /**
     *
     * @param type
     * @param value
     */
    @RequestMapping(value="contacts/add", method = RequestMethod.POST)
    public void addContact(UserContactType type, String value) {
        JCUser user = securityService.getCurrentUser();
        UserContact contact = new UserContact(value, type);
        user.addContact(contact);
    }

    /**
     *
     */
    @RequestMapping(value = "contacts/remove", method = RequestMethod.POST)
    public void removeContact(){

    }
}
