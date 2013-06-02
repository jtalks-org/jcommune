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

/*
Object storing input values in the dialog.
Used to keep values when logo removing dialog is showed
 */
var currentAdminValues = {
   'forumName': null,
   'forumDescription': null,
   'logoTooltip': null,
   'logo': null,
   'logoPreview': null,
    'valid' : false
}


$(function () {
    $("#cmpName").on('click', showForumConfigurationDialog);
    $("#cmpDescription").on('click', showForumConfigurationDialog);
    $("#forumLogo").on('click', showForumConfigurationDialog);
});

function showForumConfigurationDialog(e) {
    // prevent from following link
    e.preventDefault();

    currentAdminValues.valid = false
    // create the Dialog
    createAdministrationDialog();
}

/*
Creates Forum Administration dialog
 */
function createAdministrationDialog() {

    var bodyContent = '<div class="control-group"> \
            <div class="controls thumbnail-logo"> \
                <img id="logoPreview" class="forum-logo" src="' + currentAdminValues.logoPreview + '" alt=""/>  \
            </div> \
            \
            \
            <div class="logo-manage-buttons-container"> \
                <div class="logo-manage-buttons"> \
                    <a id="upload-logo" href="#" class="btn btn-mini"> \
                        <i class="icon-picture"></i>  \
                        '+ $labelUploadLogo + ' \
                    </a>  \
                    <a id="removeLogo" href="#" class="btn btn-mini btn-danger" \
                        title='+ $labelRemoveLogo + '> \
                        <i class="icon-remove icon-white"></i> \
                    </a> \
                </div> \
            </div>  \
            \
            \
        </div>  \
        <hr class="admin-dialog-hr"> \
        <form:hidden id="logo" path="logo"/> \
        ' + Utils.createFormElement($labelForumTitle, 'forum_name', 'text', 'first dialog-input')
        + Utils.createFormElement($labelForumDescription, 'forum_description', 'text', 'dialog-input')
        + Utils.createFormElement($labelLogoTooltip, 'forum_logoTooltip', 'text', 'dialog-input') + ' \
            <div class="clearfix"';

    var footerContent = ' \
            <button id="administration-cancel-button" class="btn">' + $labelCancel + '</button> \
            <button id="administration-submit-button" class="btn btn-primary">' + $labelSaveChanges + '</button>';

    jDialog.createDialog({
        dialogId: 'administration-modal-dialog',
        title: $labelAdministration,
        bodyContent: bodyContent,
        footerContent: footerContent,
        maxWidth: 350,
        maxHeight: 500,
        firstFocus: true,
        tabNavigation: ['#upload-logo', '#removeLogo', '#forum_name', '#forum_description','#forum_logoTooltip',
                        '#administration-submit-button', '#administration-cancel-button'],
        handlers: {
            '#administration-submit-button': {'click': sendForumConfiguration},
            '#administration-cancel-button': {'static':'close'}
        }
    });

    var tabFunc = function (e) {
        if (document.activeElement.id == jDialog.options.dialogId && (e.keyCode || e.charCode) == tabCode) {
            e.preventDefault();
            $("#upload-logo").focus();
        }
    }
    $("#" + jDialog.options.dialogId).on('keydown', tabFunc);

    fillAdminDialogInputs();
    addRemoveLogoHandler();
    createUploader();
}

/*
Fills inputs with current information or with
information stored before logo removing dialog was showed
 */
function fillAdminDialogInputs() {
    if (currentAdminValues.valid == true) {
        $('#forum_name').val(currentAdminValues.forumName);
        $('#forum_description').val(currentAdminValues.forumDescription);
        $('#forum_logoTooltip').val(currentAdminValues.logoTooltip);
        $('#logoPreview').attr('src', currentAdminValues.logoPreview);
        $('#logo').val(currentAdminValues.logo);
    }
    else {
        var cmpNameText = $("#cmpName").text();
        var forumDescriptionText = $("#descriptionHolder").text();
        var logoTooltipText = $("#logoTooltipHolder").text();

        $('#forum_name').val(cmpNameText);
        $('#forum_description').val(forumDescriptionText);
        $('#forum_logoTooltip').val(logoTooltipText);
        $('#logoPreview').attr("src", $('#forumLogo').attr("src"));
    }
}

/*
Creates uploader for uploading logo to the server
 */
function createUploader() {
    //defined the URL for appropriate logo processing depending on client browser:
    // Opera, IE - multipart file using iFrame
    // Chrome, Opera - byte [] using XHR
    var action;
    //this parameter tells to valums file uploader the appropriate content type
    //if encoding != multipart, it will use "application/octet-stream" content type
    //otherwise it will use "multipart/form-data"
    var encoding = "not-multipart";
    if (navigator.appName.indexOf("Microsoft") != -1 ||
        navigator.appName.indexOf("Opera") != -1) {
        action = $root + '/admin/logo/IFrameLogoPreview';
        encoding = "multipart";
    }
    else {
        action = $root + '/admin/logo/XHRlogoPreview';
    }

    var uploader = new qq.FileUploaderBasic({
        button: $("#upload-logo").get(0),
        //server side uploading handler
        action: action,
        //
        encoding: encoding,
        //is multiple file upload available
        multiple: false,
        onSubmit: function (id, filename) {
        },
        onProgress: function (id, filename, loaded, total) {
        },
        onComplete: function (id, filename, responseJSON) {
            // response is empty when response status is not 200
            if (jQuery.isEmptyObject(responseJSON)) {
                return;
            }
            //
            if (responseJSON.status == "SUCCESS") {
                //if server side logo uploading successful  a processed image displayed
                $('#logoPreview').attr('src', responseJSON.srcPrefix + responseJSON.srcImage);
                $('#logo').attr('value', responseJSON.srcImage);
            } else {
                saveInputValues();
                // display error message
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: responseJSON.result
                });
                $('#' + jDialog.options.alertDefaultBut).on('click', createAdministrationDialog);
            }

        },
        onError: function(id, filename, xhr) {
            if (xhr.status == 413) {
                saveInputValues();
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: $labelImageWrongSizeJs
                });
                $('#' + jDialog.options.alertDefaultBut).on('click', createAdministrationDialog);
                return false;
            }
        },
        debug: false,
        messages: {
            emptyError: $fileIsEmpty
        }
    });
}

/*
Adds handler for "Remove Logo" button
 */
function addRemoveLogoHandler() {
    //remove logo handler
    $('#removeLogo').click(function () {
        saveInputValues();

        var footerContent = ' \
            <button id="remove-logo-cancel" class="btn">' + $labelCancel + '</button> \
            <button id="remove-logo-ok" class="btn btn-primary">' + $labelOk + '</button>';

        var submitFunc = function (e) {
            e.preventDefault();
            $.getJSON($root + "/admin/defaultLogo", function (responseJSON) {
                // save logo information and show main dialog again
                currentAdminValues.logoPreview = responseJSON.srcPrefix + responseJSON.srcImage;
                currentAdminValues.logo = responseJSON.srcImage;

                createAdministrationDialog();
            });
            jDialog.closeDialog();
        };

        jDialog.createDialog({
            type: jDialog.confirmType,
            bodyMessage : $labelDeleteLogoConfirmation,
            firstFocus : false,
            footerContent: footerContent,
            maxWidth: 300,
            maxHeight: 500,
            tabNavigation: ['#remove-logo-ok','#remove-logo-cancel'],
            handlers: {
                '#remove-logo-ok': {'click': submitFunc},
                '#remove-logo-cancel': {'click': createAdministrationDialog}
            }
        });

        jDialog.dialog.find('.close').bind('click', createAdministrationDialog);

        $('#remove-logo-ok').focus();

    });
}

/*
Copies values for Forum Name, Description and Logo from the Dialog
 */
function saveInputValues() {
    var forumNameElement = jDialog.dialog.find('#forum_name');
    var forumDescriptionElement = jDialog.dialog.find('#forum_description');
    var logoTooltipElement = jDialog.dialog.find('#forum_logoTooltip');
    var logoElement = jDialog.dialog.find('#logo');
    var logoPreview = jDialog.dialog.find('#logoPreview');

    currentAdminValues.forumName = forumNameElement.val();
    currentAdminValues.forumDescription = forumDescriptionElement.val();
    currentAdminValues.logoTooltip = logoTooltipElement.val();
    currentAdminValues.logo = logoElement.val();
    currentAdminValues.logoPreview = logoPreview.attr("src");
    currentAdminValues.valid = true;
}

/**
 * Handles submit request from Administration form by sending POST request, with params
 * containing Forum Name & Description, Logo and Logo description
 */
function sendForumConfiguration(e) {
    e.preventDefault();

    saveInputValues();
    currentAdminValues.valid = false;

    var componentInformation = {};
    componentInformation.name = currentAdminValues.forumName;
    componentInformation.description = currentAdminValues.forumDescription;
    componentInformation.logoTooltip = currentAdminValues.logoTooltip;
    componentInformation.logo = currentAdminValues.logo;

    jDialog.dialog.find('*').attr('disabled', true);

    $.ajax({
        url: $root + '/admin/edit',
        type: "POST",
        contentType: "application/json",
        async: false,
        data: JSON.stringify(componentInformation),
        success: function (resp) {
            if (resp.status == 'SUCCESS') {
                location.reload();
            }
            else {
                if (resp.result instanceof Array) {
                    jDialog.prepareDialog(jDialog.dialog);
                    jDialog.showErrors(jDialog.dialog, resp.result, "forum_", "");
                } else {
                    jDialog.createDialog({
                        type: jDialog.alertType,
                        bodyMessage: resp.result
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