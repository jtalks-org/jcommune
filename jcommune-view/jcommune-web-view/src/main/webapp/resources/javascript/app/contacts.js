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

function deleteContactHandler() {
    var element = $(this).parent();
    var contactId = $(this).parent().find("#contactId:hidden").attr("value");
    var contactOwnerId = $(this).parent().find("#contactOwnerId:hidden").attr("value");

    var footerContent = ' \
            <button id="remove-contact-cancel" class="btn">' + $labelCancel + '</button> \
            <button id="remove-contact-ok" class="btn btn-primary">' + $labelOk + '</button>'

    var submitFunc = function (e) {
        e.preventDefault();
        $.ajax({
            url: baseUrl + '/contacts/remove/' + contactOwnerId + '/' + contactId,
            // this is the way Spring MVC represents HTTP DELETE for better browser compatibility
            type: "POST",
            data: {'_method': 'DELETE'},
            success: function () {
                element.fadeOut();
                jDialog.closeDialog();
            },
            error: function () {
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: $labelDeleteContactFailture
                });
            }
        });
    };

    jDialog.createDialog({
        type: jDialog.confirmType,
        bodyMessage: $labelDeleteContactConfirmation,
        firstFocus: false,
        footerContent: footerContent,
        maxWidth: 300,
        tabNavigation: ['#remove-contact-ok', '#remove-contact-cancel'],
        handlers: {
            '#remove-contact-ok': {'click': submitFunc},
            '#remove-contact-cancel': {'static':'close'}
        }
    });

    $('#remove-contact-ok').focus();
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
 *  Returns HTML code for new contact populated with data
 * @param data user contact
 */
function getContactHtml(data) {
    //HTML template for single contact. Should be in sync with corresponding JSP.
    var template =
        '	<li class="contact">'
            + '		<input type="hidden" value="${contactId}"/>'
            + '		<a href="#" id="${buttonId}" class="btn btn-mini btn-danger button" title="' + $labelContactsTipsDelete + '">'
            + '			<i class="icon-remove icon-white"></i>'
            + '     </a>'
            + '		<span class="contact" title="${typeName}">'
            + '     	<img src="${icon}">'
            + '         ${value}'
            + '     </span>'
            + '</li>';

    var contactTypeInfo = AddContact.getContactType($('#contact_type').val(), AddContact.contactTypes);
    var actualValue = contactTypeInfo.displayPattern.replace(new RegExp('%s', 'gi'), $('#contact').val());

    var html = template;
    html = html.replace('${icon}', baseUrl + contactTypeInfo.icon);
    html = html.replace('${typeName}', contactTypeInfo.typeName);
    html = html.replace('${contactId}', data.id);
    html = html.replace('${buttonId}', data.id);
    html = html.replace('${value}', actualValue);

    return html;
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
}

/**
 * Reset all variables. Used when new popup is displayed
 */
AddContact.resetVariables = function () {
    AddContact.isValueValid = true;
    AddContact.contactTypes = null;
    AddContact.selectedContactType = null;
}


$(document).ready(function () {


    $('body').on('keyup', '#contact', function () {
        var value = $(this).val();
        if (value.length > 0 && !value.match(new RegExp(AddContact.selectedContactType.validationPattern))) {
            if (AddContact.isValueValid) {
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

        AddContact.resetVariables();

        $.getJSON(baseUrl + "/contacts/types", function (json) {

            AddContact.contactTypes = json;
            AddContact.selectedContactType = json[0];

            // select
            var bodyContent = '<div class="control-group">'
                + '<label for="contact_type" class="control-label">' + $labelContactType + '</label>'
                + '<div class="controls">'
                +   '<select name="contact_type" id="contact_type" class="first dialog-input">';

            $.each(json, function (i, obj) {
                bodyContent += '<option value="' + obj.id + '">' + obj.typeName + '</option>';
            });
            bodyContent += '</select></div></div>';

            // text input
            bodyContent += '<div class="control-group">'
            bodyContent += '	<label for="contact_type" class="control-label">' + $labelContactValue + '</label>';
            bodyContent += '	<div class="controls">';
            bodyContent += '		<input type="text" name="contact" id="contact" placeholder="'
                + AddContact.selectedContactType.mask + '" />';
            bodyContent += '	</div>';
            bodyContent += '	<span class="dialog-info">' + $labelContactValueInfo + '</span>';
            bodyContent += '</div>';

            var footerContent = ' \
            <button id="add-contact-cancel" class="btn">' + $labelCancel + '</button> \
            <button id="add-contact-ok" class="btn btn-primary">' + $labelOk + '</button>';

            var submitFunc = function (e) {
                e.preventDefault();
               if (AddContact.isValueValid && $('#contact').val() != '' && $('#contact').length > 0) {
                    var ownerId = $("#editedUserId").val();

                    var contact = {
                        ownerId: ownerId,
                        value: $('#contact').val(),
                        typeId: $('#contact_type').val()
                    };

                    $.ajax({
                        url: baseUrl + '/contacts/add',
                        type: "POST",
                        contentType: "application/json",
                        async: false,
                        data: JSON.stringify(contact),
                        success: function (data) {
                            if(data.status === 'SUCCESS'){
                                //populate contact template and append to page
                                $("#contacts").append(getContactHtml(data.result));
                                bindDeleteHandler();
                                jDialog.closeDialog();
                            }else{
                                ErrorUtils.addErrorMessage('#contact', $labelValidationUsercontactNotMatch);
                                AddContact.isValueValid = false;
                            }
                        },
                        error: function (data) {
                            ErrorUtils.addErrorMessage('#contact', $labelValidationUsercontactNotMatch);
                            AddContact.isValueValid = false;
                        }
                    });
                }else{
                   ErrorUtils.addErrorMessage('#contact', $labelValidationUsercontactNotMatch);
                   AddContact.isValueValid = false;
               }
                jQuery('.contact').tooltip({placement: 'right'});
                jQuery("a").tooltip();
                jQuery('.btn').tooltip({placement: 'bottom'});
                jQuery('.script-has-tooltip').tooltip();
            }

            jDialog.createDialog({
                dialogId: 'add-contact-dialog',
                title: $labelContactsAddDialog,
                bodyContent: bodyContent,
                footerContent: footerContent,
                maxWidth: 350,
                maxHeight: 350,
                tabNavigation: ['#contact_type', '#contact', '#add-contact-ok', '#add-contact-cancel'],
                handlers: {
                    '#add-contact-ok': {'click': submitFunc},
                    '#add-contact-cancel': {'static':'close'}
                }
            });
        });
    });

    bindDeleteHandler();
});
