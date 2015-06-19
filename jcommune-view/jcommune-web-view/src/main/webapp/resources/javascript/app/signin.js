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
        ' + Utils.createFormElement($labelUsername, 'userName', 'text', 'first', 'width:90%')
            + Utils.createFormElement($labelPassword, 'password', 'password', null, 'width:90%') + ' \
            <div id="rememberme-area" class="control-group"> \
                <label class="rememberme-lbl"><input name="_spring_security_remember_me" class="form-check-radio-box" type="checkbox" checked="checked">' + $labelRememberMe + '</label> \
            </div> \
            <div class="signup">\
                <a id="dialog-signup-link" href="' + $root + '/user/new' + '">' + $labelSignupRightNow + '</a> \
            </div> \
            <div class="clearfix" /> \
            <div id="restore-passwd" class="control-group"> \
                <a href="' + $root + '/password/restore' + '">' + $labelRestorePassword + '</a> \
            </div>';

        var footerContent = '<input  type="submit" id="signin-submit-button" value="' + $labelSignin + '" class="btn btn-primary" name="commit"/>';

        var submitDialog = function (e) {
            if (e.keyCode == enterCode) {
                //if focus on username then select password field
                if ($(e.target).is('#userName')) {
                    e.preventDefault();
                    if ($.browser.mozilla) {
                        setTimeout(function () {
                            jDialog.dialog.find('#password').focus();
                        }, 0);
                    }
                    else {
                        jDialog.dialog.find('#password').focus();
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
            tabNavigation: ['#userName', '#password', '#rememberme-area input', '#restore-passwd a',
                '#signin-submit-button'],
            handlers: {
                '#signin-modal-dialog': {'submit': sendLoginPost},
                '#dialog-signup-link': {'click': function(e){
                    jDialog.closeDialog();
                    signUp(e);
                }}

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

function sendEmailConfirmation(recipient) {
  $.ajax({
          type: 'GET',
          url: $root + '/confirm?id='+recipient,
          success: function () {
          var message = "";
             if (resp.status == 'SUCCESS') {
                message = $labelEmailConfirmationWasSent;
              } else {
                message = $labelError500Detail
              }
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: $labelEmailConfirmationWasSent
              });
          },
          error: function (jqXHR, textStatus, errorThrown) {
            jDialog.createDialog({
               type: jDialog.alertType,
               bodyMessage: $labelError500Detail
             });
          }
  });
};

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
    var usernameElement = jDialog.dialog.find('#userName');
    var passwordElement = jDialog.dialog.find('#password');

    var remember_me = rememberMeElement.is(':checked');
    var username = usernameElement.val();
    var password = passwordElement.val();

    jDialog.dialog.find('*').attr('disabled', true);

    var query = 'userName=' + encodeURIComponent(username) + '&' + 'password=' + encodeURIComponent(password);
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
                    var error_message = '';
                    var userId = resp.result;
                    if (userId == null) {
                        error_message = $labelLoginError;
                    } else {
                        error_message=$labelSendConfirmationEmail
                            .replace("{0}",'<a id="confirm_email_link" href="'+ $root + '/confirm" > ')
                            .replace("{1}","</a>");
                    }

                    jDialog.prepareDialog(jDialog.dialog);

                    ErrorUtils.addErrorStyles('#userName');
                    ErrorUtils.addErrorStyles('#password');

                    passwordElement.val("");
                    passwordElement.parent().append('<span class="help-inline _error">' + error_message + '</span>');
                    jDialog.resizeDialog(jDialog.dialog);
                    jDialog.focusFirstElement();

                    $("#confirm_email_link").on('click', function (e) {
                        e.preventDefault();
                        sendEmailConfirmation(userId);
                    });
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