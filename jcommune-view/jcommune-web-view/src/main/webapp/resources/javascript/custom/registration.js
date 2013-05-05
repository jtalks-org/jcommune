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

    $("#signup").on('click', function (e) {
        e.preventDefault();

        var bodyContent =
            Utils.createFormElement($labelUsername, 'username', 'text', 'first') +
                Utils.createFormElement($labelEmail, 'email', 'text') +
                Utils.createFormElement($labelPassword, 'password', 'password') +
                Utils.createFormElement($labelPasswordConfirmation, 'passwordConfirm', 'password') +
                createCaptchaElements();

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
                            type: jDialog.alertType,
                            bodyMessage: $labelRegistrationSuccess
                        });
                    }
                    else {
                        // remove previous errors and show new errors
                        jDialog.prepareDialog(jDialog.dialog);
                        refreshCaptcha(jDialog.dialog);
                        jDialog.showErrors(jDialog.dialog, resp.result, '', '');
                    }
                },
                error: function (resp) {
                    jDialog.createDialog({
                        type: jDialog.alertType,
                        bodyMessage: $labelRegistrationFailture
                    });
                }
            });
        }

        //check to fix double windows when click registration on jsp registration page
        if($('#signup-modal-dialog').length == 0){
            jDialog.createDialog({
                dialogId: 'signup-modal-dialog',
                title: $labelRegistration,
                bodyContent: bodyContent,
                footerContent: footerContent,
                maxWidth: 400,
                maxHeight: 600,
                tabNavigation: ['#username', '#email', '#password', '#passwordConfirm', '#captcha', '#signup-submit-button'],
                handlers: {
                    '#signup-submit-button': {'click': submitFunc},
                    '#captcha-refresh, #captcha-img': {'click' : refreshCaptcha}
                }
            });
        }
    });

    clearPageForm();

    // captcha container on jsp page (if open jsp page)
    var captchaContainer = $('.registration-page');
    if (captchaContainer) {

        captchaContainer.find('#captcha-refresh, #captcha-img').click(function (e) {
            e.preventDefault();
            refreshCaptchaJsp();
        });
    }

    //to registration jsp
    function clearPageForm() {
        if ($('.capcha-field')) {
            $('.capcha-field').val('');
        }
    }
});


/**
 * Get new captcha image
 */
function refreshCaptcha() {
    jDialog.dialog.find('#captcha-img').removeAttr('src').attr('src', captchaUrl());
    jDialog.dialog.find('#captcha').val('');
}

function refreshCaptchaJsp() {
    $('#form').find('#captcha-img').removeAttr('src').attr('src', captchaUrl());
    $('#form').find('#captcha').val('');
}

function captchaUrl() {
    return $root + '/captcha/image?param=' + $.now();
}

/**
 * POST request query
 */
function composeQuery(signupDialog) {
    return 'username=' + encodeURIComponent(signupDialog.find('#username').val()) +
        '&password=' + encodeURIComponent(signupDialog.find('#password').val()) +
        '&passwordConfirm=' + encodeURIComponent(signupDialog.find('#passwordConfirm').val()) +
        '&email=' + encodeURIComponent(signupDialog.find('#email').val()) +
        '&captcha=' + encodeURIComponent(signupDialog.find('#captcha').val());
}

/**
 * Create captcha form elements: captcha image, refresh button, input field
 */
function createCaptchaElements() {
    return '\
        <div class="control-group"> \
            <div class="controls captcha-images"> \
                <img id="captcha-img" src="' + captchaUrl() + '" /> \
                <img id="captcha-refresh" src="' + $root + "/resources/images/captcha-refresh.png" + '" /> \
            </div> \
            <div class="controls"> \
                <input type="text" id="captcha" placeholder="' + $labelCaptcha + '" class="input-xlarge" /> \
            </div> \
        </div> \
    ';
}
