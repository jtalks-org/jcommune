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
        // prevent from following link
        e.preventDefault();

        var bodyContent = '\
        ' + Utils.createFormElement($labelUsername, 'j_username', 'text', 'first')
            + Utils.createFormElement($labelPassword, 'j_password', 'password') + ' \
            <div id="rememberme-area" class="control-group"> \
                <label class="rememberme-lbl"><input name="_spring_security_remember_me" class="form-check-radio-box" type="checkbox" checked="checked">' + $labelRememberMe + '</label> \
            </div> \
            <div class="clearfix" /> \
            <div id="restore-passwd" class="control-group"> \
                <a href="' + $root + '/password/restore' + '">' + $labelRestorePassword + '</a> \
            </div>';

        var footerContent = '<button id="signin-submit-button" class="btn btn-primary" name="commit"> \
            ' + $labelSignin + '</button>';

        var submitDialog = function (e) {
            if (e.keyCode == enterCode) {
                //if focus on username then select password field
                if ($(e.target).is('#j_username')) {
                    e.preventDefault();
                    if ($.browser.mozilla) {
                        setTimeout(function () {
                            jDialog.dialog.find('#j_password').focus();
                        }, 0);
                    }
                    else {
                        jDialog.dialog.find('#j_password').focus();
                    }
                }
            }
            if ((e.keyCode || e.charCode) == escCode) {
                jDialog.dialog.find('.close').click();
            }
        };

        jDialog.createDialog({
            dialogId: 'signin-modal-dialog',
            title: $labelSignin,
            bodyContent: bodyContent,
            footerContent: footerContent,
            maxWidth: 350,
            tabNavigation: ['#j_username', '#j_password', '#rememberme-area input', '#restore-passwd a',
                '#signin-submit-button'],
            handlers: {
                '#signin-submit-button': {'click': sendLoginPost}
            },
            dialogKeydown: submitDialog
        });
    });


    var success = $('#restorePassSuccess');
    if (success.length > 0) {
        var bodyContent = '\
            <div id="restore-passwd" class="control-group"> \
                <h4>' + success.val() + '</h4> \
            </div>';

        var footerContent = '<button id="restore-ok-button" class="btn btn-primary" name="confirm"> \
            ' + $labelOk + '</button>';

        var goToLoginPage = function (e) {
            if (e) {
                e.preventDefault();
            }
            window.location.href = $('.brand').attr('href') + 'login';
        };

        jDialog.createDialog({
            dialogId: 'restore-password-modal-dialog',
            closeDialog: goToLoginPage,
            bodyContent: bodyContent,
            footerContent: footerContent,
            maxWidth: 350,
            tabNavigation: ['#restore-ok-button'],
            handlers: {
                '#restore-ok-button': {'click': goToLoginPage}
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
function sendLoginPost(e) {
    e.preventDefault();

    var rememberMeElement = jDialog.dialog.find('input[name=_spring_security_remember_me]');
    var usernameElement = jDialog.dialog.find('#j_username');
    var passwordElement = jDialog.dialog.find('#j_password');

    var remember_me = rememberMeElement.is(':checked');
    var username = usernameElement.val();
    var password = passwordElement.val();

    jDialog.dialog.find('*').attr('disabled', true);

    var query = 'j_username=' + encodeURIComponent(username) + '&' + 'j_password=' + encodeURIComponent(password);
    if (remember_me) {
        query = query + '&_spring_security_remember_me=on';
    }

    $.ajax({
        type: 'POST',
        url: $root + '/login_ajax',
        data: query,
        dataType: 'html',
        success: function (resp) {
            resp = eval('(' + resp + ')');

            if (resp.status == 'SUCCESS') {
                location.reload();
            }
            else {
                if (resp.result && resp.result.customError) {
                    jDialog.createDialog({
                        type: jDialog.alertType,
                        bodyMessage: $labelAuthenticationConnectionError
                    });
                } else {
                    jDialog.prepareDialog(jDialog.dialog);

                    ErrorUtils.addErrorStyles('#j_username');
                    ErrorUtils.addErrorStyles('#j_password');

                    passwordElement.val("");
                    passwordElement.parent().append('<span class="help-inline _error">' + $labelLoginError + '</span>');
                    jDialog.resizeDialog(jDialog.dialog);
                }
            }
        },
        error: function (data) {
            jDialog.createDialog({
                type: jDialog.alertType,
                bodyMessage: $labelError500Detail
            });
        }
    });
};
