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
        </div>  \
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

    var forumTitle = forumTitleElement.val();
    var forumDescription = forumDescriptionElement.val();
    var logoTooltip = logoTooltipElement.val();

    var componentInformation = {};
    componentInformation.name = forumTitle;
    componentInformation.description = forumDescription;
    componentInformation.logoTooltip = logoTooltip;

    jDialog.dialog.find('*').attr('disabled', true);

    var query = 'formTitle=' + encodeURIComponent(forumTitle) + '&'
        + 'forumDescription=' + encodeURIComponent(forumDescription) + '&'
        + 'logoTooltip=' + encodeURIComponent(logoTooltip);

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