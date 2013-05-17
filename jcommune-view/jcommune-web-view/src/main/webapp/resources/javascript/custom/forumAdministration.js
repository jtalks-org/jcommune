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


$(function () {
    $("#cmpName").on('click', showForumConfigurationDialog);
    $("#cmpDescription").on('click', showForumConfigurationDialog);
    $("#forumLogo").on('click', showForumConfigurationDialog);
});

function showForumConfigurationDialog(e) {
    // prevent from following link
    e.preventDefault();

    var bodyContent = '<div class="control-group"> \
            <div class="controls thumbnail-logo"> \
                <img id="logoPreview" src="' + $root + '/admin/logo" alt=""/>  \
            </div> \
            \
            \
            <div class="user-profile-top-buttons"> \
                <div class="user-profile-buttons-avatar"> \
                    <a id="upload" href="#" class="btn btn-mini"> \
                        <i class="icon-picture"></i>  \
                        Load logo \
                    </a>  \
                    <a id="removeLogo" href="#" class="btn btn-mini btn-danger" \
                        title="Remove logo"> \
                        <i class="icon-remove icon-white"></i> \
                    </a> \
                </div> \
            </div>  \
            \
            \
        </div>  \
        <form:hidden id="logo" path="logo"/> \
        ' + Utils.createFormElement($labelForumTitle, 'form_title', 'text', 'edit-links dialog-input')
        + Utils.createFormElement($labelForumDescription, 'forum_description', 'text', 'edit-links dialog-input')
        + Utils.createFormElement($labelLogoTooltip, 'logo_tooltip', 'text', 'edit-links dialog-input') + ' \
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
        tabNavigation: ['#form_title','#forum_description','#logo_tooltip'],
        handlers: {
            '#administration-submit-button': {'click': sendForumConfiguration}
        }
    });

    var cmpName = $("#cmpName").text();
    var forumDescription = $("#cmpDescription").text();
    var logoTooltip = $("#forumLogo").attr("title");

    $('#form_title').val(cmpName);
    $('#forum_description').val(forumDescription);
    $('#logo_tooltip').val(logoTooltip);

    //avatar uploading handler

    //defined the URL for appropriate avatar processing depending on client browser:
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
        button: $("#upload").get(0),
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
                //if server side avatar uploading successful  a processed image displayed
                $('#logoPreview').attr('src', responseJSON.srcPrefix + responseJSON.srcImage);
                //
                $('#logo').attr('value', responseJSON.srcImage);
            } else {
                // display error message
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: responseJSON.result
                });
            }

        },
        onError: function(id, filename, xhr) {
            if (xhr.status == 413) {
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: $labelImageWrongSizeJs
                });
                return false;
            }
        },
        debug: false,
        messages: {
            emptyError: $fileIsEmpty
        }
    });
}


/**
 * Handles submit request from Administration form by sending POST request, with params
 * containing Forum Title & Description, Logo and Logo description
 */
function sendForumConfiguration(e) {
    e.preventDefault();

    var forumTitleElement = jDialog.dialog.find('#form_title');
    var forumDescriptionElement = jDialog.dialog.find('#forum_description');
    var logoTooltipElement = jDialog.dialog.find('#logo_tooltip');
    var logoElement = jDialog.dialog.find('#logo');

    var forumTitle = forumTitleElement.val();
    var forumDescription = forumDescriptionElement.val();
    var logoTooltip = logoTooltipElement.val();
    var logo = logoElement.val();

    var componentInformation = {};
    componentInformation.name = forumTitle;
    componentInformation.description = forumDescription;
    componentInformation.logoTooltip = logoTooltip;
    componentInformation.logo = logo;

    jDialog.dialog.find('*').attr('disabled', true);

    $.ajax({
        url: $root + '/admin/edit_ajax',
        type: "POST",
        contentType: "application/json",
        async: false,
        data: JSON.stringify(componentInformation),
        success: function (resp) {
            if (resp.status == 'SUCCESS') {
                location.reload();
            }
            else {
                jDialog.prepareDialog(jDialog.dialog);

                ErrorUtils.addErrorStyles('#form_title');
                ErrorUtils.addErrorStyles('#forum_description');
                ErrorUtils.addErrorStyles('#logo_tooltip');

                jDialog.resizeDialog(jDialog.dialog);
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