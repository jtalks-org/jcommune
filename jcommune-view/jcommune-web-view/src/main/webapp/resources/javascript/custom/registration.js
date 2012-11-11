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

    // hide dialog on backdrop click
    $('.modal-backdrop').live('click', function (e) {
        $('#signup-modal-dialog').modal('hide');
    });

    $("#signup").on('click', function (e) {
        e.preventDefault();

        var signupDialog = createDialog();

        // show dialog
        signupDialog.modal({
            "backdrop":"static",
            "keyboard":true,
            "show":true
        });

        Utils.resizeDialog(signupDialog);
        // refreshes captcha on click refresh button or captcha image
        signupDialog.find("#captcha-refresh, #captcha-img").click(function (e) {
            e.preventDefault();
            refreshCaptcha(signupDialog);
        });

        // focus on first element
        signupDialog.find('#username').focus();

        var submitButton = signupDialog.find('#signup-submit-button');

        // returns focus back to uername field
        submitButton.keydown(function (e) {
            if ((e.keyCode || e.charCode) == 9) { //TAB key
                e.preventDefault();
                signupDialog.find("#username").focus();
            }
        });

        // skip captcha image and refresh and go to captcha input
        signupDialog.find('#passwordConfirm').keydown(function (e) {
            if ((e.keyCode || e.charCode) == 9) { //TAB key
                e.preventDefault();
                signupDialog.find("#captcha").focus();
            }
        });

        // remove dialog from DOM on hide
        signupDialog.bind("hide", function (e) {
            signupDialog.remove();
        });

        // submit button handler
        signupDialog.submit(function (e) {
            e.preventDefault();
            // disable all elements before and during submission
            signupDialog.find('*').attr('disabled', true);
            var query = composeQuery(signupDialog);
            $.ajax({
                type:"POST",
                url:$root + "/user/new_ajax",
                data:query,
                dataType:"html",
                success:function (resp) {
                    resp = eval('(' + resp + ')'); // warning: not safe
                    if (resp.status == "success") {
                        // hide dialog and show success message
                        signupDialog.modal('hide');
                        bootbox.alert($labelRegistrationSuccess);
                    }
                    else {
                        // remove previous errors and show new errors
                        prepareDialog(signupDialog);
                        showErrors(signupDialog, resp.result);
                    }
                },
                error:function (resp) {
                    bootbox.alert($labelRegistrationFailture);
                }
            });
        });
    });
});

/**
 * Enable all disabled elements
 * Remove previous errors
 * Show hidden hel text
 */
function prepareDialog(signupDialog) {
    refreshCaptcha(signupDialog);
    signupDialog.find('*').attr('disabled', false);
    signupDialog.find('._error').remove();
    signupDialog.find(".help-block").show();
    signupDialog.find('.control-group').removeClass('error');
}

/**
 * Get new captcha image
 */
function refreshCaptcha(signupDialog) {
    //this parameter forces browser to reload image every time
    signupDialog.find("#captcha-img").removeAttr("src").attr("src", captchaUrl());
    signupDialog.find("#captcha").val("");
}

function captchaUrl() {
    return $root + "/captcha/image?param=" + $.now();
}

/**
 * Show errors under fields with errors
 * Errors overrides help text (help text will be hidden)
 */
function showErrors(signupDialog, errors) {
    for (var i = 0; i < errors.length; i++) {
        var e = signupDialog.find('#' + errors[i].field);
        e.parent().wrap('<div class="control-group error" />');
        e.parent().find(".help-block").hide();
        e.parent().last().append('<span class="help-block _error">' + errors[i].defaultMessage + '</span>');
    }
    Utils.resizeDialog(signupDialog);
}

/**
 * POST request query
 */
function composeQuery(signupDialog) {
    return "username=" + encodeURIComponent(signupDialog.find('#username').val()) +
            "&password=" + encodeURIComponent(signupDialog.find('#password').val()) +
            "&passwordConfirm=" + encodeURIComponent(signupDialog.find('#passwordConfirm').val()) +
            "&email=" + encodeURIComponent(signupDialog.find('#email').val()) +
            "&captcha=" + encodeURIComponent(signupDialog.find('#captcha').val());
}

/**
 * Create captcha form elements: captcha image, refresh button, input field
 */
function createCaptchaElements() {
    return $(' \
        <div class="control-group"> \
            <div class="controls captcha-images"> \
                <img id="captcha-img" src="' + captchaUrl() + '" /> \
                <img id="captcha-refresh" src="' + $root + "/resources/images/captcha-refresh.png" + '" /> \
            </div> \
            <div class="controls"> \
                <input type="text" id="captcha" placeholder="' + $labelCaptcha + '" class="input-xlarge" /> \
            </div> \
        </div> \
    ').html();
}

/**
 * Create form field with given label(placeholder), id, type
 */
function createFormElement(label, id, type) {
    var elementHtml = ' \
        <div class="control-group"> \
            <div class="controls"> \
                <input type="' + type + '" id="' + id + '" name="' + id + '" placeholder="' + label + '" class="input-xlarge" /> \
            </div> \
        </div> \
    ';
    return $(elementHtml).html();
}

function createDialog() {
    return $(' \
        <form class="modal" id="signup-modal-dialog" tabindex="-1" role="dialog" \
                    aria-labelledby="sign in" aria-hidden="true"> \
            <div class="modal-header"> \
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button> \
                <h3>' + $labelRegistration + '</h3> \
            </div> \
            <div class="modal-body">' +
            createFormElement($labelUsername, 'username', 'text') +
            createFormElement($labelEmail, 'email', 'text') +
            createFormElement($labelPassword, 'password', 'password') +
            createFormElement($labelPasswordConfirmation, 'passwordConfirm', 'password') +
            createCaptchaElements() + ' \
            </div> \
            <div class="modal-footer"> \
                <button id="signup-submit-button" class="btn btn-primary btn-block" name="commit" type="submit">' + $signupButtonLabel + '</button> \
            </div> \
        </form> \
        ');
}
