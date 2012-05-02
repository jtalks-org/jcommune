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
 * Handles login request and displays form with user and password field
 */

$(function () {
    $("#signin").on('click', function (e) {
        signinPopup();
        //if JS off, then open standart page
        e.preventDefault();
    });
    function signinPopup() {
        //POST-query
        $.ajax({
            type:"GET",
            url:$root + "/login",
            dataType:"html",
            //handling query answer, create registration form
            success:function (data) {
                var form_elements = [];
                $.each($(data).find("div.forum_row"), function (index, value) {
                    $(value).find("span.error").remove();
                    form_elements[index] = $(value).html();
                });
                var content = '<ul><div>' + $(data).find("span.forum_header_answer").html() +
                              '</div><br/><span class="empty_cell"></span>' + form_elements[0] +
                              form_elements[1] + form_elements[2] + '</ul>'+
                              '<div class="form_controls">' +
                              '<a href ="/jcommune/password/restore">' +
                              $(data).find('a[href$="/jcommune/password/restore"]').html() + "</a>" +
                              '</div>'  ;
                //Check the query answer and displays prompt
                if ($(data).find("span.forum_header_answer").html() != null) {
                    $.prompt(content,
                    {buttons:{OK:true}, focus:0,
                        submit:sendLoginPost});
                } 
            }});
    }

    ;
});


/**
 * Handles submit request from login form by sending POST request, with params
 * such as user and password, if "remember me" wasn't checked, for request will be
 * used two params: user and password, otherwise "remember me" param will be appended
 * to request, after successfully login current page will be reloaded, otherwise you will
 * get error message, providing user with opportunity to change login or password
 */
function sendLoginPost() {
    var query;
    if ($('input[name=_spring_security_remember_me]').is(':checked')) {
        query = "j_username=" + $('#j_username').val() + "&" + "j_password=" +
            $('#j_password').val() + "&" + "_spring_security_remember_me=on";
    }  else {
         query = "j_username=" + $('#j_username').val() + "&" + "j_password=" +
            $('#j_password').val();
    }

    $.ajax({
        type:"POST",
        url:$root + "/j_spring_security_check",
        data:query,
        dataType:"html",
        //handling query answer, create registration form
        success:function (data) {
            var form_elements = [];
            $.each($(data).find("div.forum_row"), function (index, value) {
                $(value).find("span.error").prepend('<br>');
                form_elements[index] = $(value).html();
            });
            var content = '<ul><div>' + $(data).find("span.forum_header_answer").html() +
                          '</div><br/><span class="empty_cell"></span>' + form_elements[0] +
                          form_elements[1] + form_elements[2] + '</ul>' +
                          '<div class="form_controls">' +
                          '<a href ="/jcommune/password/restore">' +
                          $(data).find('a[href$="/jcommune/password/restore"]').html() + "</a>" +
                          '</div>'  ;
            //Check the query answer and displays prompt
            if ($(data).find("span.forum_header_answer").html() != null) {
                $.prompt(content,
                {buttons:{OK:true}, focus:0,
                    submit:sendLoginPost});
            } else {
                history.go(0);
            }
        }});


}