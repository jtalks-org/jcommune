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

var username;
var remember_me;

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
                var form_elements = formatFormElements(data);
                var content = formatHTMLContent(data, form_elements);
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
    remember_me = $('input[name=_spring_security_remember_me]').is(':checked');
    username = $('#j_username').val();
    if (remember_me) {
        query = "j_username=" + username + "&" + "j_password=" +
            $('#j_password').val() + "&" + "_spring_security_remember_me=on";
    }  else {
         query = "j_username=" + username + "&" + "j_password=" +
            $('#j_password').val();
    }

    $.ajax({
        type:"POST",
        url:$root + "/j_spring_security_check",
        data:query,
        dataType:"html",
        //handling query answer, create registration form
        success:function (data) {
             var form_elements = formatFormElements(data);
             var content = formatHTMLContent(data, form_elements);
            //Check the query answer and displays prompt
            if ($(data).find("span.forum_header_answer").html() != null) {
                $.prompt(content,
                {buttons:{OK:true}, focus:0,
                    submit:sendLoginPost});
                 $('#j_username').val(username);
                 $('input[name=_spring_security_remember_me]').attr('checked', remember_me);

            } else {
                history.go(0);
            }
        }});
}

/**
 * Formats form elements such as text fields with login and password
 * for login popup page
 * @param data html page
 * @return form_elements array of form elements
 */
function formatFormElements(data){
     var form_elements = [];
            $.each($(data).find("div.forum_row"), function (index, value) {
                $(value).find("span.error").prepend('<br>');
                form_elements[index] = $(value).html();
            });
    return form_elements;
}


/**
 * Formats html content from given data, for representing login page
 * @param data html data
 * @param form_elements form elements
 * @return content formatted html content
 */
function formatHTMLContent(data,form_elements){
     var content = '<ul><div>' + $(data).find("span.forum_header_answer").html() +
                          '</div><br/><span class="empty_cell"></span>' + form_elements[0] +
                          form_elements[1] + form_elements[2] + '</ul>' +
                          '<div class="form_controls">' +
                          '<a href ="/jcommune/password/restore">' +
                          $(data).find('a[href$="/jcommune/password/restore"]').html() + "</a>" +
                          '</div>'  ;
    return content;
}