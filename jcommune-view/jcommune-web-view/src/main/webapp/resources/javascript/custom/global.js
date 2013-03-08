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

$(document).ready(function () {

    if($(".sapaLinkRow").length > 0){
        $(".sapaLinkRow").dotdotdot();
    }
    //Sets timezone cookie for the server to show all the dates in a client timezone
    document.cookie = "GMT=" + new Date().getTimezoneOffset() + "; path=/";
    // Initializes image previewing
    $("a[rel^='prettyPhoto']").prettyPhoto({social_tools: false});
    // popups for individual post links
    $("a.postLink").each(function () {
        $(this).click(function (e) {
            e.preventDefault();
            var path = window.location.protocol + "//" + window.location.host;
            $.prompt(path + $(this).attr("href"), {buttons: {}, persistent: false});
        })
    })
    // popups to confirm post/topic/pm deletion
    $("a.delete").each(function () {
        $(this).click(function (e) {
            e.preventDefault();
            deletePath = $(this)[0].href;
            $.prompt($(this)[0].rel,
                {buttons: [
                    {title: $labelOk, value: true},
                    {title: $labelCancel, value: false}
                ],
                    persistent: false,
                    submit: function (confirmed) {
                        if (confirmed) {
                            var deleteForm = $('#deleteForm')[0];
                            deleteForm.action = deletePath;
                            deleteForm.submit();
                        }
                    }
                }
            );
        })
    })

    /** Handler to prevent multiposting. */
    $('form.anti-multipost').submit(function () {
        if (Antimultipost.beingSubmitted($(this))) {
            return false;
        }
        Antimultipost.disableSubmit($(this));
    });

    //for change externalLinks position (top line or main page)
    $(window).resize(function (e) {
        e.preventDefault();
        $('.btn-navbar').trigger('mainLinksPosition');
    });

    //disable or enable sape configuration inputs of form
    $('#enableSape1').bind('change', function(e){
        $('#sape-configuration-form input:not(#enableSape1, .btn)').attr('readonly', !e.target.checked);
    })

    $(window).resize();
});

