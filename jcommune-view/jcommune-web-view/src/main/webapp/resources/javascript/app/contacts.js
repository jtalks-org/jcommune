/*
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

/**
 * This script provides interactive UI for contact management in user profile.
 */


/**
 * Application base path with trailing slash. Must be defined somewhere within the global scope.
 */
var baseUrl = $root;

/**
 * Namespace for this file.
 */
var AddContact = {};

/** Currently selected contact type */
AddContact.selectedContactType = null;

/** Aall contact types */
AddContact.contactTypes = null;

/** if entered contact is valid */
AddContact.isValueValid = true;

function deleteContactHandler(e) {
    $(this).parent().remove();
    var contacts = $("#contacts").find("li.contact");
    // We need rebind all id's and names (paths to properties of contacts)
    // for correct processing backing object by spring
    contacts.each(function (index, el) {
        $(el).find('.contact input, .controls select, .controls input').each(function(){
            $(this).attr('name', '');
        });
    });
    contacts.each(function (index, el) {
        $(el).find('.contact input').each(function(){
            $(this).attr('id', 'userContactsDto.contacts' + index + '.id');
            $(this).attr('name', 'userContactsDto.contacts[' + index + '].id')
        });
        $(el).find(' .controls select').each(function(){
            $(this).attr('id', 'userContactsDto.contacts' + index + '.type.id');
            $(this).attr('name', 'userContactsDto.contacts[' + index + '].type.id')
        });
        $(el).find('.controls input').each(function(){
            $(this).attr('id', 'userContactsDto.contacts' + index + '.value');
            $(this).attr('name', 'userContactsDto.contacts[' + index + '].value')
        });
    });
    e.preventDefault();
}

/**
 * Binds click handler for "X" buttons (delete contact)
 */
function bindDeleteHandler() {
    $("#contacts").find(".contact").find("a.button").each(function () {
        this.onclick = deleteContactHandler;
    });
}

$(document).ready(function () {
    $('body').on('keyup', '#contact', function () {
        var value = $(this).val();
        if (value.length > 0 && !value.match(new RegExp(AddContact.selectedContactType.validationPattern))) {
            if (AddContact.isValueValid) {
                ErrorUtils.removeErrorMessage('#contact');
                ErrorUtils.addErrorMessage('#contact', $labelValidationUsercontactNotMatch);
                AddContact.isValueValid = false;
            }
        } else {
            ErrorUtils.removeErrorMessage('#contact');
            AddContact.isValueValid = true;
        }
    });

    // when user selects other contact type
    $('body').on('change', '#contact_type', function () {
        var contactId = $(this).val();
        AddContact.selectedContactType = AddContact.getContactType(contactId, AddContact.contactTypes);
        $('#contact').attr('placeholder', AddContact.selectedContactType.mask);
        $('#contact').keyup();
    });

    //"Add contact" button handler
    $("#add_contact").on("click", function () {

        $.getJSON(baseUrl + "/contacts/types", function (json) {
            var contactItems = $('li.contact');
            var contactIndex = contactItems.length;
            var content = '<li class="contact" data-original-title=""><input id="contactId" type="hidden">' +
                '<a href="#" class="btn btn-mini btn-danger button" data-original-title="Удалить контакт">' +
                '<i class="icon-remove icon-white"></i></a><span class="contact" data-original-title="">' +
                '<input id="userContactsDto.contacts' + contactIndex + '.id" ' +
                'name="userContactsDto.contacts[' + contactIndex + '].id" type="hidden">' +
                '<div class="controls">';
            content += '<select id="userContactsDto.contacts' + contactIndex + '.type.id"' +
                ' name="userContactsDto.contacts[' + contactIndex + '].type.id" class="input-medium">';
            $.each(json, function (i, obj) {
                content += '<option value="' + obj.id + '">' + obj.typeName + '</option>';
            });
            content += '</select>';
            content += '<input id="userContactsDto.contacts' + contactIndex + '.value" ' +
                'name="userContactsDto.contacts[' + contactIndex + '].value" ' +
                'tabindex="45" class="input-large" type="text"><br></div></li>';

            contactItems.last().parent().append(content);
        });
    });

    bindDeleteHandler();
});
