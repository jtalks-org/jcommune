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
        $('#signin-modal-dialog').modal('hide');
    });

    $("#signin").on('click', function (e) {
        // prevent from following link
        e.preventDefault();
        var signinDialog = showSigninDialog(); // open dialog
        Utils.resizeDialog(signinDialog);
    });
});


/**
 * Handles submit request from login form by sending POST request, with params
 * such as user and password, if "remember me" wasn't checked, for request will be
 * used two params: user and password, otherwise "remember me" param will be appended
 * to request, after successfully login current page will be reloaded, otherwise you will
 * get error message, providing user with opportunity to change login or password
 */
function sendLoginPost() {
    var dialog = $("#signin-modal-dialog");

    // parse values from form and disable elements
    var submitButtonElement = dialog.find("#signin-submit-button");
    var rememberMeElement = dialog.find('input[name=_spring_security_remember_me]');
    var usernameElement = dialog.find('#j_username');
    var passwordElement = dialog.find('#j_password');

    var remember_me = rememberMeElement.is(':checked');
    var username = usernameElement.val();
    var password = passwordElement.val();

    dialog.find('*').attr('disabled', true);

    var query = "j_username=" + encodeURIComponent(username) + "&" + "j_password=" + encodeURIComponent(password);
    if (remember_me) {
        query = query + "&_spring_security_remember_me=on";
    }

    $.ajax({
        type: "POST",
        url: $root + "/login_ajax",
        data: query,
        dataType: "html",
        success: function (resp) {
            resp = eval('(' + resp + ')');

            if (resp.status == "SUCCESS") {
                location.reload();
            }
            else {
                var signInDialog = $('#signin-modal-dialog');
                prepareDialog(signInDialog);

                ErrorUtils.addErrorStyles("#j_username");
                ErrorUtils.addErrorStyles("#j_password");

                passwordElement.after('<span class="help-inline _error">' + $labelLoginError + '</span>');
                Utils.resizeDialog(dialog);
            }
        },
        error: function (data) {
            bootbox.alert($labelError500Detail);
        }
    });
}

/**
 * Create form element using data from server.
 */
function composeForm() {
    var signinDialog = $(' \
        <form class="modal" id="signin-modal-dialog" tabindex="-1" role="dialog" \
                    aria-labelledby="sign in" aria-hidden="true"> \
            <div class="modal-header"> \
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button> \
                <h3>' + $labelSignin + '</h3> \
            </div> \
            <div class="modal-body"> \
                <div class="control-group"> \
                    <div class="controls"> \
                        <input placeholder="' + $labelUsername + '" name="j_username" id="j_username" type="text"> \
                    </div> \
                </div> \
                <div class="control-group"> \
                    <div class="controls"> \
                        <input placeholder="' + $labelPassword + '" name="j_password" id="j_password" type="password"> \
                    </div> \
                </div> \
                <div id="rememberme-area" class="control-group"> \
                    <input name="_spring_security_remember_me" class="form-check-radio-box" type="checkbox"> \
                    <label class="string optional">' + $labelRememberMe + '</label> \
                </div> \
                <div class="clearfix" /> \
                <a href="' + $root + '/password/restore' + '">' + $labelRestorePassword + '</a> \
            </div> \
            <div class="modal-footer"> \
                <button id="signin-submit-button" class="btn btn-primary" name="commit" type="submit">' + $labelSignin + '</button> \
            </div> \
        </form> \
    ');

    return signinDialog;
}

/**
 * Show modal dialog.
 */
function showSigninDialog() {
    var signinDialog = composeForm();

    // trigger checkbox on click inside outer div
    var checkbox = signinDialog.find("input[name=_spring_security_remember_me]");
    signinDialog.find('#rememberme-area').click(function (e) {
        if (!$(e.target).is(':checkbox')) { // prevent from handling event when clicked on checkbox
            if (checkbox.is(':checked')) {
                checkbox.removeAttr('checked');
            }
            else {
                checkbox.attr('checked', 'checked');
            }
        }
    });

    // send ajax-request on submit button click
    var submitButton = signinDialog.find("#signin-submit-button");
    submitButton.click(function (e) {
        e.preventDefault();
        sendLoginPost();
        return false;
    });

    // returns focus back to uername field
    submitButton.keydown(Keymaps.signinSubmit);

    // remove dialog from DOM on hide
    signinDialog.bind("hide", function (e) {
        signinDialog.remove();
    });


    // show dialog
    signinDialog.modal({
        "backdrop": "static",
        "keyboard": true,
        "show": true
    });

    signinDialog.find("#j_username").focus();

    return signinDialog;
}
;
