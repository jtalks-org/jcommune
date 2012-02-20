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
 * Binds click handler for "X" buttons (delete contact)
 */
function bindDeleteHandler() {
    $("#contacts").find(".contact").find("a.button").click(function(){
        var id = $(this).parent().find("input:hidden").attr("value");
        $.ajax({
            url: baseUrl + '/contacts/remove/' + id,
            // this is the way Spring MVC represents HTTP DELETE for better browser compartibility
            type: "POST",
            data: {'_method': 'DELETE'}

        });
        $(this).parent().fadeOut();
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
                   '     <input type="hidden" value="${id}"/>' +
                   '     <a class="button" href="#">X</a>' +
                   ' </div>';

    var html = template;
    html = html.replace('${icon}', baseUrl + data.type.icon);
    html = html.replace('${typeName}', data.type.typeName);
    html = html.replace('${id}', data.id);
    html = html.replace('${value}', data.value);

    return html;
}

$(document).ready(function() {

    //"Add contact" button handler
	$("#add_contact").click(function() {
		$.getJSON(baseUrl + "/contacts/types", function(json) {

            //parse returned list of contact types and generate HTML for pop-up window
			var str = '<b>Add contact:</b><br/><select name="contact_type" id="contact_type">';

			$.each(json, function(i, obj) {
				str += '<option value="' + obj.id + '">' + obj.typeName +'</option>';
			});
			str += '</select>';
			str += '<input type="text" name="contact" id="contact"/>'

			$.prompt(str, {
                buttons: { Ok: true, Cancel: false},
                callback: function(value, message, form) {
                    if (value != undefined && value) {

                        var contact = {
                            value: form.contact,
                            type: {
                                id: form.contact_type
                            }
                        };

                        $.ajax({
                            url: baseUrl + '/contacts/add',
                            type: "POST",
                            contentType: "application/json",
                            data: JSON.stringify(contact),
                            success: function(data) {
                                //populate contact template and append to page
                                $("#contacts").append(getContactHtml(data));
                                bindDeleteHandler();
                            }
                        });
                    }
                }
            });
		});
	});

    bindDeleteHandler();
});
