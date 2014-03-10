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
AddContact.isValueValid = [];

function deleteContactHandler(e) {
    $(this).parent().parent().remove();
    var contacts = $("#contacts").find("li.contact");
    // We need rebind all id's and names (paths to properties of contacts)
    // for correct processing backing object by spring
    contacts.each(function (index, el) {
        $(el).find('.contact input').each(function(){
            $(this).attr('id', 'userContactsDto.contacts' + index + '.id');
            $(this).attr('name', 'userContactsDto.contacts[' + index + '].id')
        });
        $(el).find('.controls select').each(function(){
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

/**
 * Find contact type with given id in given array and return it
 */
AddContact.getContactType = function (id, contactTypes) {
    var result = null;

    if (id !== undefined && contactTypes !== undefined) {
        $.each(contactTypes, function (i, obj) {
            if (obj.id == id) {
                result = obj;
                return;
            }
        });
    }

    return result;
};

function validationHandler() {
    var contactTypeId = $(this).find('.controls select').val();
    var contactId = $(this).find('input').val();
    var input = $(this).find('.controls input');
    var value = input.val();
    AddContact.selectedContactType = AddContact.getContactType(contactTypeId, AddContact.contactTypes);
    input.attr('placeholder', AddContact.selectedContactType.mask);
    if (value.length == 0
        || (value.length > 0 && !value.match(new RegExp(AddContact.selectedContactType.validationPattern)))) {
        if (contactId == '' || AddContact.isValueValid[contactId] == undefined || AddContact.isValueValid[contactId]) {
            ErrorUtils.removeErrorMessage(input);
            ErrorUtils.addErrorMessage(input, $labelValidationUsercontactNotMatch);
            AddContact.isValueValid[contactId] = false;
        }
    } else {
        ErrorUtils.removeErrorMessage(input);
        AddContact.isValueValid[contactId] = true;
    }
    if ($('#contacts').find('div.control-group.error').size() > 0) {
        $('#saveChanges').attr('disabled', 'disabled');
    } else {
        $('#saveChanges').removeAttr('disabled')
    }
}

function bindReceivedContactTypes(json, contacts) {
    AddContact.contactTypes = json;
    AddContact.selectedContactType = json[0];
    bindValidationHandler(contacts);
}


function bindValidationHandler(contacts) {
    if (AddContact.contactTypes === null) {
        getContactTypes(bindReceivedContactTypes, contacts);
    } else {
        contacts.each(function (index, el) {
            $(el).find('.controls select').each(function(){
                $(el).on('change', validationHandler);
            });
            $(el).find('.controls input').each(function(){
                $(el).on('keyup', validationHandler);
            });
        });
    }
}

/**
 * Creates empty contact row
 *
 * @param json contact types
 */
function addContact(json) {
    var contactItems = $('li.contact');
    var contactIndex = contactItems.length;
    var content = '<li class="contact" data-original-title=""><div class="control-group">' +
        '<input id="contactId" type="hidden">' +
        '<a href="#" class="btn btn-mini btn-danger button" data-original-title="' + $labelContactsTipsDelete + '">' +
        '<i class="icon-remove icon-white"></i></a><span class="contact" data-original-title="">' +
        '<input id="userContactsDto.contacts' + contactIndex + '.id" ' +
        'name="userContactsDto.contacts[' + contactIndex + '].id" type="hidden">' +
        '<div class="controls">';
    content += '<select id="userContactsDto.contacts' + contactIndex + '.type.id" tabindex="' + contactIndex + '"' +
        ' name="userContactsDto.contacts[' + contactIndex + '].type.id" class="input-medium">';
    $.each(json, function (i, obj) {
        content += '<option value="' + obj.id + '">' + obj.typeName + '</option>';
    });
    content += '</select>';
    content += '<input id="userContactsDto.contacts' + contactIndex + '.value" ' +
        'name="userContactsDto.contacts[' + contactIndex + '].value" ' +
        'tabindex="' + (contactIndex + 1) + '" class="input-large" type="text"><br></div></div></li>';

    $('#contacts').append(content);
    var addedContact = $('li.contact').last();
    bindValidationHandler(addedContact);
    $("#add_contact").removeAttr('disabled');
    addedContact.keyup();
    addedContact.focus();
}

function getContactTypes(callback, parameters) {
    $.getJSON(baseUrl + "/contacts/types", function (json) {
        callback(json, parameters);
    });
}

$(document).ready(function () {
    //"Add contact" button handler
    $("#add_contact").on("click", function () {
        if (!$(this).attr('disabled')) {
            $(this).attr('disabled', 'disabled');
            if (AddContact.contactTypes === null) {
                getContactTypes(addContact);
            } else {
                addContact(AddContact.contactTypes);
            }
        }
    });

    bindDeleteHandler();
    bindValidationHandler($('li.contact'));
});
