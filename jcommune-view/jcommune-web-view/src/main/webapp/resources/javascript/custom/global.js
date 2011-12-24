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
 * Sets timezone cookie for the server to show all the dates in a client timezone
 */
$(document).ready(function() {
   document.cookie = "GMT=" + new Date().getTimezoneOffset() + "; path=/"
});

/**
 * Checks whether an error has been caught during image loading and replaces it with a default error picture
 */
$("img").error(function() {this.src("http://localhost:8080/resources/images/no_image.png")});
