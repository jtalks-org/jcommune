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
 * Provides confirmation dialog when user tries to leave a page with unsaved form.
 * Just mark important input fields with "confirm-unsaved" class.
 *
 * Hyperlinks/buttons which bypass confirmation declared in allowed_transitions variable.
 *
 * Opera doesn't support "beforeunload" event, thus code below does nothing in Opera.
 */
$(document).ready(function () {

    var mark_class = ".script-confirm-unsaved";
    var allowed_transitions = ["input[type=submit]", ".submit-form"];
    var currentData = "";
    var newData;

    $(window).bind('beforeunload', function () {
        newData = "";
        $("input" + mark_class + ", textarea" + mark_class).each(function () {
            newData += this.value;
        });
        if (currentData != newData) return '';
    });

    $(document).on('click', allowed_transitions, function (event) {
        $(window).off('beforeunload');
    });
    
    $(document).on('submit', allowed_transitions, function (event) {
        $(window).off('beforeunload');
    });

    $("input" + mark_class + ", textarea" + mark_class).each(function () {
        currentData += this.value;
    });


});