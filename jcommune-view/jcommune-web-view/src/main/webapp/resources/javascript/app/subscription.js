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
 * Assigns subscription AJAX logic to the anchor with specific id.
 * Subscription it toggled asynchronously without page reload.
 *
 * This script also alters the "subscription" control to "unsubscription"
 * one and vice versa
 */
$(document).ready(function () {
    $('a#subscription').click(function (e) {
        var link = $(this)[0];
        $.ajax({
            url:link.href,
            type:"GET",
            async:false,
            success:function (data) {
                $(e.target).tooltip('destroy');
                if (link.href.indexOf("unsubscribe") == -1) {
                    // subscribe operation success
                    link.textContent = $labelUnsubscribe;
                    link.setAttribute('data-original-title', $labelUnsubscribeTooltip);
                    link.href = link.href.replace("subscribe", "unsubscribe");
                } else {
                    // unsubscribe operation success
                    link.textContent = $labelSubscribe;
                    link.setAttribute('data-original-title', $labelSubscribeTooltip);
                    link.href = link.href.replace("unsubscribe", "subscribe");
                }
                $(e.target).tooltip();
            }});
        return false;
    })
});