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
 * Registers image preview handler for user-specified images
 */
$(document).ready(function() {
    $('img.thumbnail').imgPreview({
        srcAttr: 'src',
        containerID: 'img_preview',
        imgCSS: {
            'max-height': '500px',
            'max-width': '700px'
        },
        distanceFromCursor: {top: - 150, left:10},
        onShow: function(link) {
            // Animate link:
            $(link).stop().animate({opacity:0.4});
            // Reset image:
            $('img', this).stop().css({opacity:0});
        },
        onLoad: function() {
            // Animate image
            $(this).animate({opacity:1}, 300);
        },
        onHide: function(link) {
            // Animate link:
            $(link).stop().animate({opacity:1});
        }});
});
