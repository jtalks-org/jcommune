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
 * Provides UI for contact management in user profile.
  */

$(document).ready(function() {
    var baseUrl = $("base").attr("href");
	$("#add_contact").click(function() {
		$.getJSON(baseUrl + "/contacts/types", function(json) {
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
                            data: JSON.stringify(contact)
                        });

                    }
                }
            });
		});
	});

    $("#contacts").find(".contact").find("a.button").click(function(){
        var id = $(this).parent().find(":hidden").attr("value");
        $.ajax({
            url: baseUrl + '/contacts/remove/' + id,
            type: "DELETE"
        });
        $(this).parent().fadeOut();
    });
});
