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
$(document).ready(function ($) {
    window.history.pushState("forward", null, document.location);
    setTimeout(function () {
        window.onpopstate = function (e) {
            e.preventDefault();
            //if we came from login's page it means we are already requested current page
            //so we have to be returned more than 2 pages when click to back button in browser to avoid cycling
            if (new RegExp("(.*)(/login$)", "igm").test(document.referrer)) {
                window.history.go(-2);
            } else {
                window.history.back();
            }
        };
    }, 300);
});
