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

	var contactTypeInfo = AddContact.getContactType(data.typeId, AddContact.contactTypes);
    var actualValue = contactTypeInfo.displayPattern.replace(new RegExp('%s', 'gi'), data.value);
    
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

/**
 * Reset all variables. Used when new popup is displayed
 */
AddContact.resetVariables = function() {
	AddContact.isValueValid = true;
	AddContact.contactTypes = null;
	AddContact.selectedContactType = null;
}


$(document).ready(function () {
	
	
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
            var str = '<ul><div>Add contact:</div>'
				+ '<span class="empty_cell"></span>'
				+ '<br/>';
			
			// select
			str += '<label for="contact_type">' + $labelContactType+ '</label>'
				+ '<div style="margin-left:9px;">'
				+ '<select name="contact_type" id="contact_type" style="width:312px">';

            $.each(json, function (i, obj) {
                str += '<option value="' + obj.id + '">' + obj.typeName + '</option>';
            });
            str += '</select><br/></div>';
			str += '<br/>';
			
			// text input
			str += '<label for="contact_type">' + $labelContactValue + '</label>';
            str += '<div><input type="text" name="contact" id="contact" value="' + AddContact.selectedContactType.mask + '" />';
			str += '<span class="reg_info">' + $labelContactValueInfo + '</span>';
            str += '<label for="contact" id="contact-error-status" class="error"/></div>';
			str += '</ul>';

            $.prompt(str, {
                buttons:{ Ok:true, Cancel:false},
                submit:function (value, message, form) {
                	var result = false;
					
					if (!value) {
						// cancel is pressed
						result = true
					} else if (AddContact.isValueValid && value != undefined && value) {

                        var contact = {
                            value:form.contact,
                            typeId: form.contact_type
                        };

                        $.ajax({
                            url:baseUrl + '/contacts/add',
                            type:"POST",
                            contentType:"application/json",
							async: false,
                            data:JSON.stringify(contact),
                            success:function (data) {
                            		//populate contact template and append to page
                            		$("#contacts").append(getContactHtml(data));
                            		bindDeleteHandler();
                            		
                            		// allow close popup
                            		result = true;
                            },
							error : function(data) {
								$('#contact-error-status').text($labelValidationUsercontactNotMatch);
                        		AddContact.isValueValid = false;
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
