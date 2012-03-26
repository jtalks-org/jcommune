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

function deleteMessages(identifiers) {
	// add identifiers of the checked private messages for deletion
	var deleteForm = $("#deleteForm")[0];
	var field = document.createElement("input");
	field.setAttribute("type", "hidden");
	field.setAttribute("name", "pmIdentifiers");
	field.setAttribute("value", identifiers + "");
	deleteForm.appendChild(field);
}

$(document).ready(function () {
	// collect checked private messages
    $("#deleteCheckedPM").each(function () {
        $(this).click(function (e) {
            e.preventDefault();
            var messages = $(".check");
            var identifiers = [];
            $.each(messages, function(index, value) {
            	identifiers[index] = value.id;
            });
            deleteMessages(identifiers);
        });
    });
    // get private message identifier
    $("#deleteOnePM").each(function () {
        $(this).click(function (e) {
            e.preventDefault();
            var identifiers = [];
           	identifiers[0] = $("#PMId").val();
            deleteMessages(identifiers);
        });
    });
});
