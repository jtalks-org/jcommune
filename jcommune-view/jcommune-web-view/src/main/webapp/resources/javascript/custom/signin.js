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

    // hide dialog on backdrop click
    $('.modal-backdrop').live('click', function (e) {
        $('#signin-dialog').modal('hide');
    });

    // send ajax-request on submit button click
    $("#signin-submit-button").live('click', function (e) {
        sendLoginPost();
    });

    // submit on press enter
    $("#j_username, #j_password, input[name=_spring_security_remember_me]").live("keyup", function (event) {
        if (event.keyCode == 13) {
            sendLoginPost();
        }
    });

    // remove dialog from DOM on hide
    $("#signin-dialog").live("hide", function (e) {
        $("#signin-dialog").remove();
    });

    $("#signin").on('click', function (e) {
        // prevent from following link
        e.preventDefault();
        signinDialog(); // open dialog
    });

    function signinDialog() {
        $.ajax({
            type:"GET",
            url:$root + "/login",
            dataType:"html",
            success:function (data) {
                var dataJq = $(data);
                if (dataJq.find("legend").html() != null) {
                    showSigninDialog(dataJq);
                }
            }
        });
    }
});


/**
 * Handles submit request from login form by sending POST request, with params
 * such as user and password, if "remember me" wasn't checked, for request will be
 * used two params: user and password, otherwise "remember me" param will be appended
 * to request, after successfully login current page will be reloaded, otherwise you will
 * get error message, providing user with opportunity to change login or password
 */
function sendLoginPost() {
    var dialog = $("#signin-dialog");

    // parse values from form and disable elements
    dialog.find("#signin-submit-button").attr('disabled', true);
    var remember_me = dialog.find('input[name=_spring_security_remember_me]').attr('disabled', true).is(':checked');
    var username = dialog.find('#j_username').attr('disabled', true).val();
    var password = dialog.find('#j_password').attr('disabled', true).val();

    var query = "j_username=" + encodeURIComponent(username) + "&" + "j_password=" + encodeURIComponent(password);
    if (remember_me) {
        query = query + "&_spring_security_remember_me=on";
    }
    console.log("before " + $.now());
    $.ajax({
        type:"POST",
        url:$root + "/j_spring_security_check",
        data:query,
        dataType:"html",
        success:function (data) {
            var dataJq = $(data);
            //Check the query answer and displays prompt
            if (dataJq.find("legend").html() != null) { // signin failure
                // hide dialog in order to show new dialog 
                dialog.modal('hide');
                // show new dialog with errors
                dialog = showSigninDialog(dataJq);
                dialog.find('#j_username').val(username);
                dialog.find('input[name=_spring_security_remember_me]').attr('checked', remember_me);
            }
            else { // signin success
                location.reload();
            }
        },
        error:function (data) {
            $.prompt($labelError500Detail);
        }
    });
}

/**
 * Create form elements using data from server.
 */
function getFormElements(data) {
    var formElements = [];
    $.each(data.find("div.control-group").wrap('<p>').parent(), function (index, value) {
        value = $(value);
        if (index < 2) { // text inputs
            // get element label
            var controlLabel = value.find("label.control-label");
            var text = controlLabel.html();
            // label is no longer needed
            controlLabel.remove();
            // set label text as text field placeholder
            value.find('input').attr("placeholder", text);
        }
        else if (index == 3) { // checkbox
            var checkboxCss = { 'float':'left', 'margin-right':'10px' }
            value.css(checkboxCss);
        }
        ErrorUtils.addErrorStyles(value.find('span.help-inline'));
        formElements[index] = value.html();
    });
    return formElements;
}
;

/**
 * Create form element using data from server.
 */
function composeForm(data) {
    var legendText = data.find("legend").html();
    var restorePasswordUrl = data.find('.form-actions a').attr("href");
    var restorePasswordText = data.find('.form-actions a').html();

    var formElements = getFormElements(data);
    var usernameDiv = formElements[0];
    var passwordDiv = formElements[1];
    var rememberMeDiv = formElements[2];

    var signinDialog = $(' \
        <div class="modal" id="signin-dialog" tabindex="-1" role="dialog" aria-labelledby="sign in" aria-hidden="true" style="width: 300px; margin: -150px 0px 0px -150px;"> \
            <div class="modal-header"> \
                <button type="button" class="close" style="padding: 0; cursor: pointer; background: transparent; border: 0; -webkit-appearance: none;" data-dismiss="modal" aria-hidden="true">&times;</button> \
                <h3>' + legendText + '</h3> \
            </div> \
            <div class="modal-body">'
            + usernameDiv
            + passwordDiv
            + rememberMeDiv +
            '<div class="clearfix"> \
           <a href="' + restorePasswordUrl + '">' + restorePasswordText + '</a> \
                </div> \
            </div> \
            <div class="modal-footer"> \
                <button id="signin-submit-button" class="btn btn-primary" style="clear: left; width: 100%; height: 32px; font-size: 13px;" name="commit" type="submit">' + legendText + '</button> \
            </div> \
        </div> \
    ');

    return signinDialog;
}
;

/**
 * Show modal dialog.
 */
function showSigninDialog(data) {
    var signinDialog = composeForm(data);

    // show dialog
    signinDialog.modal({
        "backdrop":"static",
        "keyboard":true,
        "show":true // this parameter ensures the modal is shown immediately
    });

    signinDialog.find("#j_username").focus();
    return signinDialog;
}
;