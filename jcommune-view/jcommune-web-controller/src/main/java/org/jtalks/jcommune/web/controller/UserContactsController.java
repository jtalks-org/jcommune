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

import org.jtalks.jcommune.model.entity.UserContact;
import org.jtalks.jcommune.model.entity.UserContactType;
import org.jtalks.jcommune.service.UserContactsService;
import org.jtalks.jcommune.web.dto.UserContactDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Evgeniy Naumenko
 * @author Michael Gamov
 */
@Controller
public class UserContactsController {

    private UserContactsService service;

    /**
     *
     * @param service
     */
    @Autowired
    public UserContactsController(UserContactsService service) {
        this.service = service;
    }

    /**
     *
     * @return
     */
    @RequestMapping(value="/contacts/types", method = RequestMethod.GET)
    public @ResponseBody UserContactType[] getContactTypes() {
       return service.getAvailableContactTypes().toArray(new UserContactType[0]);
    }

    /**
     *
     * @param userContact
     */
    @RequestMapping(value="/contacts/add", method = RequestMethod.POST)
    public @ResponseBody UserContactDto addContact(@RequestBody UserContact userContact) {
        return UserContactDto.getDtoFor(service.addContact(userContact));
    }


    @RequestMapping(value = "/contacts/remove/{contactId}", method = RequestMethod.DELETE)
    public void removeContact(@PathVariable Long contactId){
        service.removeContact(contactId);
    }
}
