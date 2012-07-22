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


function updateSaveButtonState() {
    isContainSomething = ($("#recipient")[0].value != "") || ($("#title")[0].value != "") || ($("#tbMsg")[0].value != "");
    toggleSaveButtonEnabled(isContainSomething);
}

function toggleSaveButtonEnabled(isContainSomething) {
    if (isContainSomething) {
        $("#savePM").removeAttr('disabled');
        $("#savePM").removeClass('disabled');
    } else {
        $("#savePM").attr('disabled', 'disabled');
        $("#savePM").addClass('disabled');
    }
}

$(document).ready(function () {
    updateSaveButtonState();
    var mark_class = ".script-confirm-unsaved";

    $("input" + mark_class + ", textarea" + mark_class).keyup(function () {
        updateSaveButtonState();
    });

    $("input" + mark_class + ", textarea" + mark_class).keypress(function () {
        updateSaveButtonState();
    });

});