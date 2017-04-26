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
var currentAdminValues = getCurrentAdminValues();
var REQUEST_ENTITY_TOO_LARGE = 413;


$(function () {
    $(this).find('.management-element').hide();
    $("#cmpName").on('click', showForumConfigurationDialog);
    $("#cmpDescription").on('click', showForumConfigurationDialog);
    $("#forumLogo").on('click', showForumConfigurationDialog);
    $("#userDefinedCopyright").on('click', editCopyright);
    $("[id^=branchLabel]").on('click', showBranchEditDialog);
    $("[id^=newBranch]").on('click', showNewBranchDialog);
    $("[id^=newGroup], .edit-group").on('click', showGroupManagementDialog);
    $(".delete-group").on('click', showDeleteGroupDialog);
    $("#addSpamRuleBtn, .edit-spam-rule-btn").on('click', showSpamManagementDialog);
    $(".delete-spam-rule-btn").on('click', showDeleteSpamRuleDialog);
    $("[id^=status]").on('change', sendChangeSpamRuleStatusRequest);
    $("[id^=group-], [id^=spam-rule-]").hover(
        function () {
            $(this).find('.management-element').show()
        },
        function () {
            $(this).find('.management-element').hide()
        }
    );
});

/**
 * Show dialog for new branch.
 */
function showNewBranchDialog(e) {
    var sectionId = this.id.substring("newBranch".length);
    showBranchEditDialog(e, sectionId)
}

/**
 * Show dialog for edit or create branch.
 * sectionId should be specified if new branch created.
 * @param {type} e
 * @param {type} sectionId
 */
function showBranchEditDialog(e, sectionId) {
    e.preventDefault();
    if (!sectionId) {
        var brancLabelPrefix = "branchLabel";
        var branchId = this.id.substring(brancLabelPrefix.length);
        var descriptonLabel = $("#branchDescriptionLabel" + branchId);
    }
    var bodyContent = Utils.createFormElement($labelBranchName, 'branchName', 'text', 'first dialog-input')
        + Utils.createFormElement($labelBranchDescription, 'branchDescription', 'text', 'dialog-input') +
            '<div class="clearfix"/>';

    var footerContent = ' \
            <button id="administrationCancelButton" class="btn">' + $labelCancel + '</button> \
            <button id="administrationSubmitButton" class="btn btn-primary">' + $labelSaveChanges + '</button>';

    jDialog.createDialog({
        dialogId: 'administrationModalDialog',
        title: $labelAdministration,
        bodyContent: bodyContent,
        footerContent: footerContent,
        maxWidth: 350,
        maxHeight: 500,
        firstFocus: true,
        tabNavigation: ['#branchName', '#branchDescription',
            '#administrationSubmitButton', '#administrationCancelButton'],
        handlers: {
            '#administrationSubmitButton': {'click': sendBranchConfiguration},
            '#administrationCancelButton': {'static':'close'}
        }
    });
    if (!sectionId) {
        $('#branchName').val(this.text.trim());
        $('#branchDescription').val(descriptonLabel.text().trim());
    }
    $('#branchName').focus();

    /**
     * Handles submit request from BranchEdit form by sending POST request, with params
     * containing Branch ID, Name & Description
     */
    function sendBranchConfiguration(e) {
        e.preventDefault();

        var branchInformation = {};
        branchInformation.id = parseInt(branchId);
        branchInformation.name = jDialog.dialog.find('#branchName').val();
        branchInformation.description = jDialog.dialog.find('#branchDescription').val();
        if (sectionId) {
            branchInformation.sectionId = sectionId;
        }
        jDialog.dialog.find('*').attr('disabled', true);
        var submitUrl = '/branch/edit';
        if (sectionId) {
            submitUrl = '/branch/new';
        }
        $.ajax({
            url: $root + submitUrl,
            type: "POST",
            contentType: "application/json",
            async: false,
            data: JSON.stringify(branchInformation),
            success: function (resp) {
                if (resp.status == 'SUCCESS') {
                    location.reload();
                }
                else {
                    if (resp.result instanceof Array) {
                        jDialog.prepareDialog(jDialog.dialog);
                        jDialog.showErrors(jDialog.dialog, resp.result, "branch", "");
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
}


function getCurrentAdminValues() {
    return {
        forumName: $('#cmpName').text() || "",
        titlePrefix: $('#titlePrefixHolder').text() || "",
        forumDescription: $('#descriptionHolder').text() || "",
        logoTooltip: $("#logoTooltipHolder").text() || "",
        logoPreview: $('#forumLogo').attr("src"),
        iconPreview: getFavIconUrl() || "",
        logo: null,
        icon: null,
        copyright: $("#copyrightHolder").text() || "",
        sessionTimeout: $("#sessionTimeoutHolder").text() || "",
        avatarMaxSize: $("#avatarMaxSizeHolder").text() || "",
        emailNotification: $("#emailNotificationHolder").text() || ""
    }
}

/**
 * Open configuration dialog and focus on copyright field.
 */
function editCopyright(e) {
  showForumConfigurationDialog(e);
  $("#forumCopyright").focus();
}

function showForumConfigurationDialog(e) {
    // prevent from following link
    e.preventDefault();

    currentAdminValues = getCurrentAdminValues();

    // create the Dialog
    createAdministrationDialog();
}

/*
Creates Forum Administration dialog
 */
function createAdministrationDialog() {

    var bodyContent = '<div class="control-group"> \
            <div class="controls thumbnail-logo"> \
                <img id="logoPreview" class="forum-logo" src="" alt=""/>  \
            </div> \
            \
            \
            <div class="logo-manage-buttons-container"> \
                <div class="logo-manage-buttons"> \
                    <a id="uploadLogo" href="#" data-original-title="' + $labelUploadTitle + '" class="btn btn-mini"> \
                        <i class="icon-picture"></i>  \
                        '+ $labelUploadLogo + ' \
                    </a>  \
                    <a id="removeLogo" href="#" class="btn btn-mini btn-danger" \
                        data-original-title='+ $labelRemoveLogo + '> \
                        <i class="icon-remove icon-white"></i> \
                    </a> \
                </div> \
            </div>  \
        </div>  \
        <hr class="admin-dialog-hr"> \
        \
        <div class="control-group"> \
            <div class="controls thumbnail-logo"> \
                <img id="iconPreview" class="forum-logo" src="" alt=""/>  \
            </div> \
            \
            \
            <div class="logo-manage-buttons-container"> \
                <div class="logo-manage-buttons"> \
                    <a id="uploadIcon" href="#" data-original-title="' + $labelUploadTitle + '" class="btn btn-mini"> \
                        <i class="icon-picture"></i>  \
                        '+ $labelUploadFavIcon + ' \
                    </a>  \
                    <a id="removeIcon" href="#" class="btn btn-mini btn-danger" \
                        data-original-title='+ $labelRemoveFavIcon + '> \
                        <i class="icon-remove icon-white"></i> \
                    </a> \
                </div> \
            </div>  \
        </div>  \
        <hr class="admin-dialog-hr"> \
        \
        <form:hidden id="logo" path="logo"/> \
        <form:hidden id="icon" path="icon"/> \
        ' + Utils.createFormElement($labelForumTitle, 'forumName', 'text', 'first dialog-input')
        + Utils.createFormElement($labelForumDescription, 'forumDescription', 'text', 'dialog-input')
        + Utils.createFormElement($labelTitlePrefix, 'forumTitlePrefix', 'text', 'first dialog-input')
        + Utils.createFormElement($labelLogoTooltip, 'forumLogoTooltip', 'text', 'dialog-input')
        + Utils.createFormElement($copyrightLabel, 'forumCopyright', 'text', 'dialog-input')
        + Utils.createFormElement($labelSessionTimeout, 'forumSessionTimeout', 'text', 'dialog-input')
        + Utils.createFormElement($labelAvatarMaxSize, 'forumAvatarMaxSize', 'text', 'dialog-input')
        + Utils.createFormElement($labelEmailNotification, 'forumEmailNotification', 'checkbox', 'dialog-input', "", $labelEmailNotification) + ' \
            <div class="clearfix"></div>';

    var footerContent = ' \
            <button id="administrationCancelButton" class="btn">' + $labelCancel + '</button> \
            <button id="administrationSubmitButton" class="btn btn-primary">' + $labelSaveChanges + '</button>';

    jDialog.createDialog({
        dialogId: 'administration-modal-dialog',
        title: $labelAdministration,
        bodyContent: bodyContent,
        footerContent: footerContent,
        maxWidth: 350,
        maxHeight: 700,
        firstFocus: true,
        tabNavigation: ['#forumName', '#forumDescription', '#forumTitlePrefix', '#forumLogoTooltip', '#forumCopyright',
            '#forumSessionTimeout', '#forumAvatarMaxSize', '#forumEmailNotification',
            '#administrationSubmitButton', '#administrationCancelButton'],
        handlers: {
            '#administrationSubmitButton': {'click': sendForumConfiguration},
            '#administrationCancelButton': {'static':'close'}
        }
    });

    $('#uploadIcon').tooltip();
    $('#uploadLogo').tooltip();
    $('#removeIcon').tooltip();
    $('#removeLogo').tooltip();
    $('#forumTitlePrefix').attr('data-original-title', $labelTitlePrefixHint);
    $('#forumTitlePrefix').tooltip();
    $('#forumCopyright').attr('data-original-title', $copyrightHint);
    $('#forumCopyright').tooltip();
    $('#forumSessionTimeout').attr('data-original-title', $labelSessionTimeoutHint);
    $('#forumSessionTimeout').tooltip();
    $('#forumAvatarMaxSize').attr('data-original-title', $labelAvatarMaxSizeHint);
    $('#forumAvatarMaxSize').tooltip();

    var tabFunc = function (e) {
        if (document.activeElement.id == jDialog.options.dialogId && (e.keyCode || e.charCode) == tabCode) {
            e.preventDefault();
            $("#uploadLogo").focus();
        }
    }
    $("#" + jDialog.options.dialogId).on('keydown', tabFunc);

    fillAdminDialogInputs();
    addRemoveLogoHandler();
    addRemoveFavIconHandler();
    createLogoUploader();
    createIconUploader();
}

function getFavIconUrl(){
    var favIconUrl;

    $('link').each(function(indx, element){
        if ($(element).attr("rel") == "icon") {
            favIconUrl = $(element).attr("href");
        }
    });

    return favIconUrl;
}

/*
Fills inputs with current information or with
information stored before logo removing dialog was showed
 */
function fillAdminDialogInputs() {
    $('#forumName').val(currentAdminValues.forumName);
    $('#forumDescription').val(currentAdminValues.forumDescription);
    $('#forumLogoTooltip').val(currentAdminValues.logoTooltip);
    $('#forumTitlePrefix').val(currentAdminValues.titlePrefix);
    $('#logoPreview').attr('src', currentAdminValues.logoPreview);
    $('#iconPreview').attr('src', currentAdminValues.iconPreview);
    $('#logo').val(currentAdminValues.logo);
    $('#icon').val(currentAdminValues.icon);
    $('#forumCopyright').val(currentAdminValues.copyright);
    $('#forumSessionTimeout').val(currentAdminValues.sessionTimeout);
    $('#forumAvatarMaxSize').val(currentAdminValues.avatarMaxSize);
    $('#forumEmailNotification').prop('checked', currentAdminValues.emailNotification.toString() === 'true');
}

/*
 Creates uploader for uploading image to the server
 */
function createUploader(IFrameActionUrl, XhrActionUrl, uploadButtonId, onSuccessResponse) {
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
        action = IFrameActionUrl;
        encoding = "multipart";
    }
    else {
        action = XhrActionUrl;
    }

    var uploader = new qq.FileUploaderBasic({
        button: $("#" + uploadButtonId).get(0),
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
                saveInputValues();
                // display "unknown error" message
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: $labelUnexpectedError
                });
                $('#' + jDialog.options.alertDefaultBut).on('click', createAdministrationDialog);
                jDialog.dialog.find('.close').bind('click', createAdministrationDialog);
                return;
            }
            //
            if (responseJSON.status == "SUCCESS") {
                onSuccessResponse(responseJSON);
            } else {
                saveInputValues();
                // display error message
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: responseJSON.result
                });
                $('#' + jDialog.options.alertDefaultBut).on('click', createAdministrationDialog);
                jDialog.dialog.find('.close').bind('click', createAdministrationDialog);
            }
            $("#" + uploadButtonId).tooltip('hide');
        },
        onError: function(id, filename, xhr) {
            if (xhr.status == REQUEST_ENTITY_TOO_LARGE) {
                saveInputValues();
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: $labelImageWrongSizeJs
                });
                $('#' + jDialog.options.alertDefaultBut).on('click', createAdministrationDialog);
                jDialog.dialog.find('.close').bind('click', createAdministrationDialog);
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
Creates uploader for uploading logo to the server
 */
function createLogoUploader() {
    createUploader($root + '/admin/logo/IFrameLogoPreview', $root + '/admin/logo/XHRlogoPreview', "uploadLogo",
        function(responseJSON) {
            $('#logoPreview').attr('src', responseJSON.srcPrefix + responseJSON.srcImage);
            $('#logo').attr('value', responseJSON.srcImage);
        }
    );
}

/*
 Creates uploader for uploading icon to the server
 */
function createIconUploader() {
    createUploader($root + '/admin/icon/IFrameFavIconPreview', $root + '/admin/icon/XHRFavIconPreview', "uploadIcon",
        function(responseJSON) {
            $('#iconPreview').attr('src', responseJSON.srcPrefix + responseJSON.srcImage);
            $('#icon').attr('value', responseJSON.srcImage);
        }
    );
}
function showDeleteGroupDialog(event) {
    event.preventDefault();
    var groupRow = $(this).closest('tr');
    var groupId = groupRow.attr("data-group-id");
    var footerContent = ' \
            <button id="delete-group-cancel" class="btn">' + $labelCancel + '</button> \
            <button id="delete-group-ok" class="btn btn-primary">' + $labelOk + '</button>';

    jDialog.createDialog({
        type: jDialog.confirmType,
        title: $labelDelete,
        bodyMessage: $deleteGroupDialogMessage,
        footerContent: footerContent,
        tabNavigation: ['#delete-group-ok', '#delete-group-cancel', 'button.close'],
        handlers: {
            '#delete-group-ok': {'click': sendDeleteGroupRequest},
            '#delete-group-cancel': {'static':'close'}
        }
    });
    $('#delete-group-ok').focus();

    function sendDeleteGroupRequest(event) {
        event.preventDefault();
        $.ajax({
            url: $root + '/group/' + groupId,
            type: 'DELETE',
            async: false,
            success: function (response) {
                if (response.status === 'SUCCESS') {
                    location.reload();
                }
            },
            error: function () {
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: $labelError500Detail
                });
            }
        });
    }
}
/*
 Adds handler for remove image button
 */
function addRestoreDefaultImageHandler(buttonId, defaultImageUrl, onSuccess) {
    //remove image handler
    $('#' + buttonId).click(function () {
        saveInputValues();

        var footerContent = ' \
            <button id="restoreDefaultCancel" class="btn">' + $labelCancel + '</button> \
            <button id="restoreDefaultOk" class="btn btn-primary">' + $labelOk + '</button>';

        var submitFunc = function (e) {
            e.preventDefault();
            $.getJSON(defaultImageUrl, function (responseJSON) {
                onSuccess(responseJSON);
                createAdministrationDialog();
            });
            jDialog.closeDialog();
        };

        var cancel = function () {
            createAdministrationDialog();
            return false;
        };

        jDialog.createDialog({
            type: jDialog.confirmType,
            bodyMessage : $labelDeleteLogoConfirmation,
            firstFocus : false,
            footerContent: footerContent,
            maxWidth: 300,
            maxHeight: 500,
            tabNavigation: ['#restoreDefaultOk','#restoreDefaultCancel'],
            handlers: {
                '#restoreDefaultOk': {'click': submitFunc},
                '#restoreDefaultCancel': {'click': cancel}
            }
        });

        jDialog.dialog.find('.close').bind('click', createAdministrationDialog);

        $('#restoreDefaultOk').focus();

    });
}

/*
Adds handler for "Remove Logo" button
 */
function addRemoveLogoHandler() {
    addRestoreDefaultImageHandler("removeLogo", $root + "/admin/defaultLogo",
        function(responseJSON) {
            currentAdminValues.logoPreview = responseJSON.srcPrefix + responseJSON.srcImage;
            currentAdminValues.logo = responseJSON.srcImage;
        }
    );
}

/*
 Adds handler for "Remove Icon" button
 */
function addRemoveFavIconHandler() {
    addRestoreDefaultImageHandler("removeIcon", $root + "/admin/defaultIcon",
        function(responseJSON) {
            currentAdminValues.iconPreview = responseJSON.srcPrefix + responseJSON.srcImage;
            currentAdminValues.icon = responseJSON.srcImage;
        }
    );
}

/*
Copies values for Forum Name, Description and Logo from the Dialog
 */
function saveInputValues() {
    currentAdminValues = {
        forumName: jDialog.dialog.find('#forumName').val(),
        forumDescription: jDialog.dialog.find('#forumDescription').val(),
        titlePrefix: jDialog.dialog.find('#forumTitlePrefix').val(),
        logoTooltip: jDialog.dialog.find('#forumLogoTooltip').val(),
        logo: jDialog.dialog.find('#logo').val(),
        logoPreview: jDialog.dialog.find('#logoPreview').attr("src"),
        icon: jDialog.dialog.find('#icon').val(),
        iconPreview: jDialog.dialog.find('#iconPreview').attr("src"),
        copyright: jDialog.dialog.find('#forumCopyright').val(),
        sessionTimeout: jDialog.dialog.find('#forumSessionTimeout').val(),
        avatarMaxSize: jDialog.dialog.find('#forumAvatarMaxSize').val(),
        emailNotification: jDialog.dialog.find('#forumEmailNotification').prop('checked')
    }
}

/**
 * Handles submit request from Administration form by sending POST request, with params
 * containing Forum Name & Description, Logo and Logo description
 */
function sendForumConfiguration(e) {
    e.preventDefault();

    saveInputValues();

    var componentInformation = {};
    componentInformation.name = currentAdminValues.forumName;
    componentInformation.description = currentAdminValues.forumDescription;
    componentInformation.logoTooltip = currentAdminValues.logoTooltip;
    componentInformation.logo = currentAdminValues.logo;
    componentInformation.icon = currentAdminValues.icon;
    componentInformation.titlePrefix = currentAdminValues.titlePrefix;
    componentInformation.copyright = currentAdminValues.copyright;
    componentInformation.sessionTimeout = currentAdminValues.sessionTimeout;
    componentInformation.avatarMaxSize = currentAdminValues.avatarMaxSize;
    componentInformation.emailNotification = currentAdminValues.emailNotification;

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
                    jDialog.showErrors(jDialog.dialog, resp.result, "forum", "");
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

/**
 * Show dialog for create or edit group.
 *
 * @param event
 */
function showGroupManagementDialog(event) {
    event.preventDefault();
    // Create a new group or edit an existing?
    var editMode = this.className.indexOf("edit-group") >= 0;

    if (editMode){
        // find row with group and extract all data that we need.
        var groupRow = $(this).closest('tr');
        var groupId = groupRow.attr('data-group-id');
        var groupName = groupRow.attr('data-group-name');
        var groupDescription = groupRow.attr('data-group-description');
    }
    var bodyContent =
        Utils.createFormElement($labelGroupPlaceholderName, 'groupName', 'text', 'first dialog-input') +
        Utils.createFormElement($labelGroupPlaceholderDescription, 'groupDescription', 'text', 'dialog-input') +
        '<div class="clearfix"/>';

    var footerContent = ' \
          <button id="cancelGroupButton" class="btn">' + $labelCancel + '</button> \
          <button id="saveGroupButton" class="btn btn-primary">' + $labelSave + '</button>';

    jDialog.createDialog({
        dialogId: 'groupCreateDialog',
        title: editMode ? $labelGroupEditTitle : $labelGroupCreateTitle,
        bodyContent: bodyContent,
        footerContent: footerContent,
        maxWidth: 350,
        maxHeight: 500,
        firstFocus: true,
        tabNavigation: ['#groupName', '#groupDescription',
            '#saveGroupButton', '#cancelGroupButton', 'button.close'],
        handlers: {
            '#saveGroupButton': {'click': sendNewGroup},
            '#cancelGroupButton': {'static': 'close'}
        }
    });

    if (editMode) fillDialogInputFields([
        {id: '#groupName', value: groupName},
        {id: '#groupDescription', value: groupDescription}]);

    /**
     * Handles submit request from groupManagementDialog by sending POST or PUT request, with params
     * containing group information.
     * 
     * @param event
     */
    function sendNewGroup(event) {
        event.preventDefault();

        var groupInformation = {};
        groupInformation.id = groupId;
        groupInformation.name = jDialog.dialog.find('#groupName').val();
        groupInformation.description = jDialog.dialog.find('#groupDescription').val();

        jDialog.dialog.find('*').attr('disabled', true);

        $.ajax({
            url: $root + '/group/' + (editMode ? groupId : ''),
            type: editMode ? 'PUT' : 'POST',
            contentType: 'application/json',
            async: false,
            data: JSON.stringify(groupInformation),
            success: function (response) {
                if (response.status === 'SUCCESS') {
                    location.reload();
                } else {
                    if (response.result instanceof Array) {
                        jDialog.prepareDialog(jDialog.dialog);
                        jDialog.showErrors(jDialog.dialog, response.result, 'group', '');
                    } else {
                        jDialog.createDialog({
                            type: jDialog.alertType,
                            bodyMessage: response.result
                        });
                    }
                }
            },
            error: function () {
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: $labelError500Detail
                });
            }
        });
    }
}

function showSpamManagementDialog(event) {
    event.preventDefault();
    var spamRule = {
        id: '',
        regex: '',
        description: '',
        enabled: ''
    };
    var editMode = this.className.indexOf("edit-spam-rule-btn") >= 0;

    var bodyContent =
        Utils.createFormElement($spamProtectionRegexPlaceholder, 'spamRegex', 'text', 'first dialog-input') +
        Utils.createFormElement($spamProtectionDescriptionPlaceholder, 'spamDescription', 'text', 'dialog-input') +
        '<div class="clearfix"/>';

    var footerContent = ' \
          <button id="cancelSpamRuleButton" class="btn">' + $labelCancel + '</button> \
          <button id="saveSpamRuleButton" class="btn btn-primary">' + $labelSave + '</button>';

    jDialog.createDialog({
        dialogId: 'spamProtectionDialog',
        title: $labelNewSpamRule,
        bodyContent: bodyContent,
        footerContent: footerContent,
        maxWidth: 350,
        maxHeight: 500,
        firstFocus: true,
        tabNavigation: ['#spamRegex', '#spamDescription',
            '#saveSpamRuleButton', '#cancelSpamRuleButton', 'button.close'],
        handlers: {
            '#saveSpamRuleButton': {'click': saveOrUpdateSpamRule},
            '#cancelSpamRuleButton': {'static': 'close'}
        }
    });

    if (editMode) {
        var row = $(this).closest('tr');
        spamRule = parseSpamRuleDataFrom(row);
        fillDialogInputFields([
            {id: '#spamRegex', value: spamRule.regex},
            {id: '#spamDescription', value: spamRule.description}
        ]);
    }

    function saveOrUpdateSpamRule(event) {
        event.preventDefault();
        spamRule.regex = jDialog.dialog.find('#spamRegex').val();
        spamRule.description = jDialog.dialog.find('#spamDescription').val();
        if (!editMode) spamRule.enabled = true;

        $.ajax({
            url: $root + '/api/spam-rules/' + (editMode ? spamRule.id : ''),
            type: editMode ? 'PUT' : 'POST',
            contentType: 'application/json',
            async: false,
            data: JSON.stringify(spamRule),
            success: successHandler,
            statusCode: {403: showAccessDeniedAlert}
        });

        function successHandler(response) {
            if (response.status === 'FAIL' && response.result instanceof Array) {
                jDialog.prepareDialog(jDialog.dialog);
                jDialog.showErrors(jDialog.dialog, response.result, 'spam', '');
            } else {
                location.reload();
            }
        }
    }
}

function showAccessDeniedAlert() {
    jDialog.createDialog({
        type: jDialog.alertType,
        bodyMessage: $labelAccessDeniedMessage
    });
}

function fillDialogInputFields(elements) {
    elements.forEach(function (element) {
        jDialog.dialog.find(element.id).val(element.value);
    });
}

function showDeleteSpamRuleDialog(event) {
    event.preventDefault();
    var spamRuleId = $(this).closest('tr').attr('data-rule-id');
    var footerContent = ' \
            <button id="delete-spam-rule-cancel" class="btn">' + $labelCancel + '</button> \
            <button id="delete-spam-rule-ok" class="btn btn-primary">' + $labelOk + '</button>';

    jDialog.createDialog({
        type: jDialog.confirmType,
        title: $labelDelete,
        bodyMessage: $labelDeleteSpamRule,
        footerContent: footerContent,
        tabNavigation: ['#delete-spam-rule-ok', '#delete-spam-rule-cancel', 'button.close'],
        handlers: {
            '#delete-spam-rule-ok': {'click': sendDeleteSpamRuleRequest},
            '#delete-spam-rule-cancel': {'static': 'close'}
        }
    });
    $('#delete-spam-rule-ok').focus();

    function sendDeleteSpamRuleRequest(event) {
        event.preventDefault();
        $.ajax({
            url: $root + '/api/spam-rules/' + spamRuleId,
            type: 'DELETE',
            async: false,
            statusCode: {403: showAccessDeniedAlert},
            success: function (response) {
                if (response.status === 'SUCCESS') {
                    $('#spam-rule-' + spamRuleId).remove();
                    jDialog.closeDialog();
                }
            }
        });
    }
}
function sendChangeSpamRuleStatusRequest(event) {
    event.preventDefault();
    var checkbox = $(this);
    var row = checkbox.closest('tr');
    var spamRule = parseSpamRuleDataFrom(row);
    $.ajax({
        url: $root + '/api/spam-rules/' + spamRule.id,
        type: 'PUT',
        contentType: 'application/json',
        async: false,
        data: JSON.stringify(spamRule),
        statusCode: {
            403: function (result) {
                showAccessDeniedAlert(result);
                checkbox[0].checked = !checkbox[0].checked;
            }
        }
    });
}

function parseSpamRuleDataFrom(row) {
    var ruleId = row.attr('data-rule-id');
    return {
        id: ruleId,
        regex: $("#regex-" + ruleId)[0].textContent,
        description: $("#description-" + ruleId)[0].textContent,
        enabled: $("#status-" + ruleId)[0].checked
    };
}
