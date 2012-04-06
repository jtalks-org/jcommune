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

function deleteContactHandler() {
    var element = $(this).parent();
    var id = $(this).parent().find("input:hidden").attr("value");
    $.prompt($labelDeleteContactConfirmation, {
            buttons:{ Ok:true, Cancel:false},
            submit:function (value, message, form) {
                if (value != undefined && value) {
                    $.ajax({
                        url:baseUrl + '/contacts/remove/' + id,
                        // this is the way Spring MVC represents HTTP DELETE for better browser compatibility
                        type:"POST",
                        data:{'_method':'DELETE'}
                    });
                    element.fadeOut();
                }
            }
        }
    );
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
    var template = '<div class="contact">' +
        '     <label><img src="${icon}" alt="">${typeName}</label>' +
        '     <span>${value}</span>' +
        '     <input type="hidden" value="${contactId}"/>' +
        '     <a class="button" id="${buttonId}" href="#">X</a>' +
        ' </div>';

    var actualValue = data.type.displayPattern.replace(new RegExp('%s', 'gi'), data.value);
    
    var html = template;
    html = html.replace('${icon}', baseUrl + data.type.icon);
    html = html.replace('${typeName}', data.type.typeName);
    html = html.replace('${contactId}', data.id);
    html = html.replace('${buttonId}', data.id);
    html = html.replace('${value}', actualValue);

    return html;
}

$(document).ready(function () {
	
	var AddContact = {};
	
	AddContact.selectedContactType = null;
	
	// all contact types
	AddContact.contactTypes = null;
	
	// if entered contact is valid
	AddContact.isValueValid = true;
	
	AddContact.getContactType = function(id, contactTypes) {
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
	
	AddContact.resetVariables = function() {
		AddContact.isValueValid = true;
		AddContact.contactTypes = null;
		AddContact.selectedContactType = null;
	}
	
	$('body').on('keyup', '#contact', function() {
		var value = $(this).val();
		if (!value.match(new RegExp(AddContact.selectedContactType.validationPattern))) {
			$('#contact-error-status').text($labelValidationUsercontactNotMatch);
			AddContact.isValueValid = false;
		} else {
			$('#contact-error-status').text('');
			AddContact.isValueValid = true;
		}
	});
	
	// when user selects other contact type 
	$('body').on('change', '#contact_type', function() {
		var contactId = $(this).val();
		AddContact.selectedContactType = AddContact.getContactType(contactId, AddContact.contactTypes); 
		$('#contact').val(AddContact.selectedContactType.mask);
		$('#contact').keyup();
	});
	
    //"Add contact" button handler
    $("#add_contact").click(function () {
    	
    	AddContact.resetVariables();
    	
        $.getJSON(baseUrl + "/contacts/types", function (json) {

        	AddContact.contactTypes = json;
			AddContact.selectedContactType = json[0];
        	
            //parse returned list of contact types and generate HTML for pop-up window
            var str = '<b>Add contact:</b><br/><select name="contact_type" id="contact_type">';

            $.each(json, function (i, obj) {
                str += '<option value="' + obj.id + '">' + obj.typeName + '</option>';
            });
            str += '</select>';
            str += '<input type="text" name="contact" id="contact" value="' + AddContact.selectedContactType.mask + '" />';
            str += '<label for="contact" id="contact-error-status" class="error"/>';

            $.prompt(str, {
                buttons:{ Ok:true, Cancel:false},
                submit:function (event, value, message, form) {
                	var result = false;
                    if (AddContact.isValueValid && value != undefined && value) {

                        var contact = {
                            value:form.contact,
                            type:{
                                id:form.contact_type
                            }
                        };

                        $.ajax({
                            url:baseUrl + '/contacts/add',
                            type:"POST",
                            contentType:"application/json",
                            data:JSON.stringify(contact),
                            success:function (data) {
                            	if (data.errroMessage != null) {
                            		$('#contact-error-status').text(data.errorMessage);
                        			AddContact.isValueValid = false;
                            	} else {
                            		//populate contact template and append to page
                            		$("#contacts").append(getContactHtml(data));
                            		bindDeleteHandler();
                            		
                            		// allow close popup
                            		result = true;
                            	}
                            }
                        });
                    }
                    return result;
                }
            });
        });
    });

    bindDeleteHandler();
});
