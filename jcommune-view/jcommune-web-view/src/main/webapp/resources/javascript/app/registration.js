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

    $("#dialog-signup-link, #top-signup-link, #page-signup-link").on('click', signUp);

    clearPageForm();

    // captcha container on jsp page (if open jsp page)
    var captchaContainer = $('.registration-page');
    if (captchaContainer) {

        captchaContainer.find('.captcha-refresh, .captcha-img').click(function (e) {
            e.preventDefault();
            refreshCaptchaJsp();
        });
    }

    //to registration jsp
    function clearPageForm() {
        if ($('.capcha')) {
            $('.capcha').val('');
        }
    }
});

function signUp(e) {

    e.preventDefault();

    $.ajax({
        type: 'GET',
        url: $root + '/user/new_ajax',
        dataType: 'json',
        success: function (resp) {
            if (resp.status == 'SUCCESS') {
                createRegistrationForm(resp.result);
            }
        },
        error: function (resp) {
            jDialog.prepareDialog(jDialog.dialog);
            jDialog.showErrors(jDialog.dialog, resp.result, '', '');
        }
    });

    function createRegistrationForm(params) {

        var widthStyle = 'width:90%'; //The class may be overridden by other classes. But the attribute Style has highest priority.
        var bodyContent =
            Utils.createFormElement($labelUsername, 'username', 'text', 'first', widthStyle) +
            Utils.createFormElement($labelEmail, 'email', 'text', null, widthStyle) +
            Utils.createFormElement($labelPassword, 'password', 'password', null, widthStyle) +
            Utils.createFormElement($labelPasswordConfirmation, 'passwordConfirm', 'password', null, widthStyle) +
            Utils.createFormElement($lableHoneypotCaptcha, 'honeypotCaptcha', 'text', 'hide-element', widthStyle);
        for (var pluginId in params) {
            bodyContent += params[pluginId];
        }

        var footerContent = '<button id="signup-submit-button" class="btn btn-primary btn-block" name="commit"> \
            ' + $signupButtonLabel + '</button>';

        // submit button handler
        var submitFunc = function (e) {
            e.preventDefault();
            // disable all elements before and during submission
            jDialog.dialog.find('*').attr('disabled', true);
            var query = composeQuery(jDialog.dialog);
            $.ajax({
                type: 'POST',
                url: $root + '/user/new_ajax',
                data: query,
                dataType: 'html',
                success: function (resp) {
                    resp = eval('(' + resp + ')'); // warning: not safe
                    if (resp.status == 'SUCCESS') {
                        // hide dialog and show success message
                        jDialog.createDialog({
                            dialogId: 'registration-successful-dialog',
                            type: jDialog.alertType,
                            bodyMessage: $labelRegistrationSuccess
                        });
                    } else {
                        if (!resp.result.customError) {
                            // we need to remove complex part (userDto. and userDto.captchas[..]) of complex fields,
                            // such as userDto.email, because these fields are used as elements ids,
                            // and jquery selector doesn't work with id containing dot.
                            for (var i = 0; i < resp.result.length; i++) {
                                resp.result[i].field = resp.result[i].field.replace(/userDto.captchas\[/g, '')
                                    .replace(/]/g, '').replace(/userDto./g, '');
                                if (resp.result[i].field == "passwordConfirm") {
                                    $('#password').val('');
                                    $('#passwordConfirm').val('');
                                }
                            }
                            // remove previous errors and show new errors
                            jDialog.prepareDialog(jDialog.dialog);
                            refreshCaptcha(jDialog.dialog);
                            jDialog.showErrors(jDialog.dialog, resp.result, '', '');
                            jDialog.focusFirstElement();
                        } else {
                            jDialog.createDialog({
                                type: jDialog.alertType,
                                bodyMessage: getCustomErrorMessage(resp.result.customError)
                            });
                        }
                    }
                },
                error: function (resp) {
                    jDialog.createDialog({
                        type: jDialog.alertType,
                        bodyMessage: $labelRegistrationFailture
                    });
                }
            });
        };

        //check to fix double windows when click registration on jsp registration page
        if($('#signup-modal-dialog').length == 0){
            jDialog.createDialog({
                dialogId: 'signup-modal-dialog',
                title: $labelRegistration,
                bodyContent: bodyContent,
                footerContent: footerContent,
                maxWidth: 400,
                maxHeight: 600,
                tabNavigation: ['#username', '#email', '#password', '#passwordConfirm', '.captcha', '#signup-submit-button'],
                handlers: {
                    '#signup-submit-button': {'click': submitFunc},
                    '.captcha-refresh, .captcha-img': {'click' : refreshCaptcha}
                }
            });
        }

    }
}

/**
 * Get new captcha images
 */
function refreshCaptcha() {
    jDialog.dialog.find('.captcha-img').each(function(){
        var attrSrc = $(this).attr("src").split("?")[0] + "?param=" + $.now();
        $(this).removeAttr('src');
        $(this).attr('src', attrSrc);
    });
    jDialog.dialog.find('.captcha').val('');
}

function refreshCaptchaJsp() {
    $('#form').find('.captcha-img').each(function(){
        var attrSrc = $(this).attr("src").split("?")[0] + "?param=" + $.now();
        $(this).removeAttr('src');
        $(this).attr('src', attrSrc);
    });
    $('#form').find('.captcha').val('');
}

/**
 * POST request query
 */
function composeQuery(signupDialog) {
    var query = 'userDto.username=' + encodeURIComponent(signupDialog.find('#username').val()) +
        '&userDto.password=' + encodeURIComponent(signupDialog.find('#password').val()) +
        '&passwordConfirm=' + encodeURIComponent(signupDialog.find('#passwordConfirm').val()) +
        '&userDto.email=' + encodeURIComponent(signupDialog.find('#email').val()) + 
        '&honeypotCaptcha=' +encodeURIComponent(signupDialog.find('#honeypotCaptcha').val());
    signupDialog.find('.captcha').each(function() {
        query += '&userDto.captchas[' + $(this).attr('id') + ']=' + encodeURIComponent($(this).val());
    });
    return query;
}

function getCustomErrorMessage(customError) {
    switch (customError) {
        case 'connectionError' : return $labelRegistrationConnectionError;
        case 'honeypotCaptchaNotNull' : return $labelHoneypotCaptchaFilled;
        default : return $labelRegistrationFailture;
    }
}
