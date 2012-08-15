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
 * This script handles registration popup
 * todo: split and refine it
 */
//handling click on menu link Sign Up
$(function () {
    var firstView;
    $("#signup").on('click', function (e) {
        firstView = true;
        signupPopup();
        //if JS off, then open standart page
        e.preventDefault();
    });
    function signupPopup() {
        var query;
        //data for query
        if (!firstView) {
            query = "username=" + encodeURIComponent($('#username').val()) +
                "&password=" + encodeURIComponent($('#password').val()) +
                "&passwordConfirm=" + encodeURIComponent($('#passwordConfirm').val()) +
                "&email=" + encodeURIComponent($('#email').val()) +
                "&captcha=" + encodeURIComponent($('#captcha').val());
        } else {
            query = "username=&password=&passwordConfirm=&email=&captcha&firstView=false";
        }

        //POST-query
        $.ajax({
            type:"POST",
            url:$root + "/user/new",
            data:query,
            dataType:"html",
            //handling query answer, create registration form
            success:function (data) {
                var form_elements = [];
                $.each($(data).find("div.control-group").wrap('<p>').parent(), function (index, value) {
                    if (firstView) {
						$(value).find("span.help-inline").remove();
					}
					ErrorUtils.addErrorStyles($(value).find('span.help-inline'));
                    form_elements[index] = $(value).html();
                });
                var content = '<ul><div>' + $(data).find("legend").html() +
                    '</div><br/><span class="empty_cell"></span>' + form_elements[0] +
                    form_elements[1] + form_elements[2] + form_elements[3] + form_elements[4] +
                    '</ul>';
                //Check the query answer and displays prompt
                if ($(data).find("legend").html() != null) {
                    firstView = false;
                    $.prompt(content,
                        {buttons:{OK:true}, focus:0, submit:signupPopup, zIndex: 1050});
                    refreshCaptcha();
                    refreshCaptchaOnClick();
                } else if ($(data).find("span.error_errorpage").html() != null) {
                    $.prompt($labelRegistrationFailture);
                } else {
                    $.prompt($labelRegistrationSuccess);
                }
            }});
    }
});


function refreshCaptchaOnClick() {
    $("#captcha_refresh").on('click', function (e) {
        refreshCaptcha();
    });
}

function refreshCaptcha() {
    var url = $root + "/captcha/image?param=" + $.now();
    //this parameter forces browser to reload image every time
    $("#captcha_img").removeAttr("src").attr("src", url).attr("src", url);
	document.getElementById("captcha").value = "";
}
