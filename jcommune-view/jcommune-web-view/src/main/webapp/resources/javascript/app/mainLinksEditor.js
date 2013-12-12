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
 * This script handles editor popup for links on top of page
 */
//handling click on admin button nearly links

//save id of link to action (delete or edit)
var actionId = null;
var baseUrl = $root;
var externalLinksGroupInTopLine = "ul .links-menu";
var externalLinksGroupId = "#externalLinks";
var externalLinksTableClass = '.list-of-links';
var idToExternalLinkMap = new Object;
var linksEditor;
var bigScreenExternalLinkIdPrefix = "big-screen-external-link-";
var smallScreenExternalLinkIdPrefix = "small-screen-external-link-";


function getLinkById(id) {
    return idToExternalLinkMap[id];
}

function showExternalLinksDialog() {
    var elements = [];
    var tabNavigationOrder = [];

    $(externalLinksGroupId).find('a').each(function (i, elem) {
        var fullId = $(elem).attr('id');
        var id = extractExternalLinkIdFrom(fullId);
        var externalLink = {};
        externalLink.id = id;
        externalLink.url = $(elem).attr('href');
        externalLink.title = $(elem).text();
        externalLink.hint = $(elem).attr('data-original-title');

        idToExternalLinkMap[id] = externalLink;
        elements[i] = externalLink;
        tabNavigationOrder.push("#editLink" + id);
        tabNavigationOrder.push("#removeLink" + id);
    });
    tabNavigationOrder.push('#addMainLink');
    tabNavigationOrder.push('button.close');

    var footerContent = '' +
        '<button id="addMainLink" class="btn btn-block list-of-links hide-element">' + $labelAdd + '</button> \
        <button id="cancelLink" class="btn  edit-links remove-links hide-element">' + $labelCancel + '</button> \
        <button id="saveLink" class="btn btn-primary  edit-links hide-element">' + $labelSave + '</button> \
        <button id="removeLink" class="btn btn-primary  remove-links hide-element">' + $labelDelete + '</button>';

    var bodyContent =
        '<table cellpadding="0" cellspacing="0" class="list-of-links"> <tbody>' +
            createLinksTableRows(elements) + ' \
         </tbody></table>' +
         "<div id='linkEditorPlaceholder'></div>"+
         '<span class="confirm-delete-text remove-links"></span> ';

    var editButtonClick = function (e) {
        e.preventDefault();
        actionId = $(e.target).parents('tr').attr('id');
        toAction('edit');
    };

    var trashButtonClick = function (e) {
        e.preventDefault();
        actionId = $(e.target).parents('tr').attr('id');
        toAction('confirmRemove');

    };

    var addButtonClick = function (e) {
        e.preventDefault();
        toAction('add');
    };

    var linksEditorCloseButtonInput = function (e) {
        if ((e.keyCode || e.charCode) == tabCode) {
            e.preventDefault();
            if ($('#mainLinksEditor #linkTitle:visible')[0]) {
                $('#mainLinksEditor #linkTitle').focus();
            } else if ($('#mainLinksEditor #removeLink:visible')[0]) {
                $('#mainLinksEditor #removeLink').focus();
            }
            else {
                $(tabNavigationOrder[0]).focus();
            }
        }
    }

    jDialog.createDialog({
        dialogId: 'mainLinksEditor',
        title: $labelLinksEditor,
        bodyContent: bodyContent,
        footerContent: footerContent,
        maxWidth: 400,
        maxHeight: 400,
        tabNavigation: tabNavigationOrder,
        dialogKeydown: Keymaps.linksEditor,
        handlers: {
            '#addMainLink': {'click': addButtonClick},
            'button.close': {'keydown': linksEditorCloseButtonInput},
            '#mainLinksEditor #linkHint': {'keydown': Keymaps.linksEditorHintInput},
            '#mainLinksEditor #saveLink': {'keydown': Keymaps.linksEditorSaveButton},
            '#mainLinksEditor #cancelLink': {'keydown': Keymaps.linksEditorCancelButton},
            '#mainLinksEditor #removeLink': {'keydown': Keymaps.linksEditorRemoveButton}
        },
        handlersDelegate: {
            '.icon-pencil': {'click': editButtonClick},
            '.icon-trash': {'click': trashButtonClick}
        }
    });

    toAction('list');
}
$(function () {
    //add links when click to button navbar (fix show elements when refresh page)
    $('.btn-navbar').on('click', function () {
        $('li.topline-links').show();
    });

    $('.btn-navbar').on('mainLinksPosition', function (e) {
        var sizeMin = $('.btn-navbar').css('display');
        if (sizeMin && sizeMin == 'block') {
            //show in topLine
            $('#externalLinks').parent('div').hide();
            $('li.topline-links').attr('style', 'display: block !important');
        } else {
            //show in mainPage
            $('li.topline-links').attr('style', 'display: none !important');
            $('#externalLinks').parent('div').show();
        }
    });

    $('.links_editor').on('click', function (e) {
        e.preventDefault();

        showExternalLinksDialog();
    });


});

function extractExternalLinkIdFrom(fullId) {
    if (fullId.indexOf(bigScreenExternalLinkIdPrefix) !== -1) {
        return fullId.replace(bigScreenExternalLinkIdPrefix, "");
    } else if (fullId.indexOf(smallScreenExternalLinkIdPrefix) !== -1) {
        return fullId.replace(smallScreenExternalLinkIdPrefix, "");
    }
}


function createLinksTableRows(elements) {
    var elementHtml = "";
    $.each(elements, function (index, element) {
        var tr =
            $("<tbody/>")//this one is needed because html() returns INNER elements
                .append($("<tr/>").attr("id", element.id)
                    .append($("<td/>").addClass("link-url").text(element.url))
                    .append($("<td/>").addClass("link-hint").text(element.hint))
                    .append($("<td/>").addClass("link-title").text(element.title))
                    .append($("<td/>").append($("<a/>").addClass("icon-pencil cursor-hand").attr("title", $linksEditIcon).attr("href","#").attr("id", "editLink" + element.id)))
                    .append($("<td/>").append($("<a/>").addClass("icon-trash cursor-hand").attr("title", $linksRemoveIcon).attr("href","#").attr("id", "removeLink" + element.id)))
                );
        elementHtml += tr.html();
    })
    return elementHtml;
}

function listOfLinksVisible(visible) {

    var intervalID = setInterval(function () {
        if ($('.edit-links').size() > 1) {

            if (visible) {
                $(externalLinksTableClass).removeClass("hide-element");
            }
            else {
                $(externalLinksTableClass).addClass("hide-element");
            }
            clearInterval(intervalID)
        }
    }, '100');
}

function getLinkEditorFormElements() {
    return  Utils.createFormElement($labelTitle, 'linkTitle', 'text', 'edit-links dialog-input') +
            Utils.createFormElement($labelUrl, 'linkUrl', 'text', 'edit-links dialog-input') +
            Utils.createFormElement($labelHint, 'linkHint', 'text', 'edit-links dialog-input');
}

function editLinksVisible(visible) {
    var intervalID = setInterval(function () {
        if ($('.edit-links')) {
            if (visible) {
                var link = getLinkById(actionId);
                $("#linkEditorPlaceholder").html(getLinkEditorFormElements());
                $('#linkTitle').val(link.title);
                $('#linkUrl').val(link.url);
                $('#linkHint').val(link.hint);
                $('.edit-links').removeClass("hide-element");
                $('#linkTitle').focus();
                //save edited link
                $('#saveLink').unbind('click').bind('click', function (e) {
                    e.preventDefault();
                    link.title = $('#linkTitle').val();
                    link.url = $('#linkUrl').val();
                    link.hint = $('#linkHint').val();
                    $.ajax({
                        url: baseUrl + "/links/save",
                        type: "POST",
                        contentType: "application/json",
                        async: false,
                        data: JSON.stringify(link),
                        success: function (resp) {
                            if (resp.status == "SUCCESS") {
                                link.url = resp.result.url; //server can correct the url and hint
                                link.hint = resp.result.hint;
                                updateExternalLink(link, bigScreenExternalLinkIdPrefix);
                                updateExternalLink(link, smallScreenExternalLinkIdPrefix);
                                toAction('list');
                            } else {
                                // remove previous errors and show new errors
                                jDialog.prepareDialog(jDialog.dialog);
                                updateUrlWithRejectedValue(resp.result);
                                jDialog.showErrors(jDialog.dialog, resp.result, "link", "");
                            }
                        },
                        error: function (resp) {
                            jDialog.createDialog({
                                type: jDialog.alertType,
                                bodyMessage: $labelErrorLinkSave
                            });
                        }
                    });

                });
                $('#cancelLink').unbind('click').bind('click', function (e) {
                    e.preventDefault();
                    toAction('list');
                });
            }
            else {
                $("#linkEditorPlaceholder").html("");
                $('.edit-links').addClass("hide-element");
            }
            clearInterval(intervalID)
        }
    }, '100');

    function updateExternalLink(externalLink, externalLinkIdPrefix) {
        idToExternalLinkMap[externalLink.id] = externalLink;

        //update in main page
        var link = $(externalLinksGroupId).find('a#' + externalLinkIdPrefix + externalLink.id);
        link.attr('href', externalLink.url);
        link.attr('name', externalLink.title);
        link.text(externalLink.title);
        link.attr('data-original-title', externalLink.hint);

        //update in popup
        $(externalLinksTableClass).find('#' + externalLink.id + ' .link-title').text(externalLink.title + " ");

        //update in top line dropdown
        var link = $(externalLinksGroupInTopLine).find('a#' + externalLink.id);
        link.attr('href', externalLink.url);
        link.attr('name', externalLink.title);
        link.text(externalLink.title);
        link.attr('data-original-title', externalLink.hint);
    }
}

function updateUrlWithRejectedValue(result) {
    for ( i = 0; i < result.length; ++i) {
        if (result[i].field == "url") {
            $('#linkUrl').val(result[i].rejectedValue);
        }
    }
}

function addLinkVisible(visible) {
    var intervalID = setInterval(function () {
        if ($('.edit-links')) {
            if (visible) {
                $("#linkEditorPlaceholder").html(getLinkEditorFormElements());
                $('#linkTitle').val("");
                $('#linkUrl').val("");
                $('#linkHint').val("");
                $('.edit-links').removeClass("hide-element");
                $('#linkTitle').focus();
                $('#saveLink').unbind('click').bind('click', function (e) {
                    e.preventDefault();
                    var link = {};
                    link.title = $('#linkTitle').val();
                    link.url = $('#linkUrl').val();
                    link.hint = $('#linkHint').val();
                    $.ajax({
                        url: baseUrl + "/links/save",
                        type: "POST",
                        contentType: "application/json",
                        async: false,
                        data: JSON.stringify(link),
                        success: function (resp) {
                            if (resp.status == "SUCCESS") {
                                link.url = resp.result.url; //server can correct the url and hint
                                link.hint = resp.result.hint;
                                link.id = resp.result.id;
                                addNewExternalLink(link);
                                $('#editLink' + resp.result.id).focus();
                            } else {
                                // remove previous errors and show new errors
                                jDialog.prepareDialog(jDialog.dialog);
                                updateUrlWithRejectedValue(resp.result);
                                jDialog.showErrors(linksEditor, resp.result, "link", "");
                            }
                        },
                        error: function (resp) {
                            jDialog.createDialog({
                                type: jDialog.alertType,
                                bodyMessage: $labelErrorLinkSave
                            });
                        }
                    });

                });
                $('#cancelLink').unbind('click').bind('click', function (e) {
                    e.preventDefault();
                    toAction('list');
                });
            }
            else {
                $("#linkEditorPlaceholder").html("");
                $('.edit-links').addClass("hide-element");
            }
            clearInterval(intervalID)
        }
    }, '100');

    function addNewExternalLink(externalLink) {
        idToExternalLinkMap[externalLink.id] = externalLink;

        //add to main page
        var bigScreenATag = prepareNewLinkATag(externalLink, bigScreenExternalLinkIdPrefix);
        $(externalLinksGroupId).append(bigScreenATag);

        //add to top line dropdown
        var smallScreenATag = prepareNewLinkATag(externalLink, smallScreenExternalLinkIdPrefix);
        $(externalLinksGroupInTopLine).append('<li>' + smallScreenATag + "</li>");

        function prepareNewLinkATag(externalLink, externalLinkIdPrefix) {
            return result = '<span><a id="' + externalLinkIdPrefix + externalLink.id + '"'
                + 'href="' + externalLink.url + '"'
                + 'data-original-title="' + externalLink.hint + '">'
                + externalLink.title + " "
                + '</a></span>';
        }

        $('#' + bigScreenExternalLinkIdPrefix + externalLink.id).tooltip();
        $('#' + smallScreenExternalLinkIdPrefix + externalLink.id).tooltip();

        jDialog.closeDialog();
        showExternalLinksDialog();
    }
}

function confirmRemoveVisible(visible) {
    var intervalID = setInterval(function () {
        if ($('.removeLinks')) {
            if (visible) {
                var link = getLinkById(actionId);
                var deleteConfirmationMessage = $labelDeleteMainLink.replace('{0}', link.title);
                var removeLinkBut = $('#removeLink');
                $('.confirm-delete-text').text(deleteConfirmationMessage);
                $('.remove-links').removeClass("hide-element");
                removeLinkBut.focus();
                //delete link
                removeLinkBut.unbind('click').bind('click', function (e) {
                    e.preventDefault();
                    $.ajax({
                        url: baseUrl + "/links/delete/" + link.id,
                        type: "DELETE",
                        contentType: "application/json",
                        async: false,
                        success: function (data) {
                            if (data.result == true) {
                                idToExternalLinkMap[link.id] = null;

                                //remove from main page
                                $(externalLinksGroupId).find('#' + bigScreenExternalLinkIdPrefix + link.id).parent('span').remove();
                                //remove from top line dropdown
                                $(externalLinksGroupInTopLine).find('#' + smallScreenExternalLinkIdPrefix + link.id).parent('li').remove();

                                jDialog.closeDialog();
                                showExternalLinksDialog();
                                $('#addMainLink').focus();
                            }
                            else {
                                jDialog.createDialog({
                                    type: jDialog.alertType,
                                    bodyMessage: $labelErrorLinkDelete
                                });
                            }
                        },
                        error: function () {
                            jDialog.createDialog({
                                type: jDialog.alertType,
                                bodyMessage: $labelErrorLinkDelete
                            });
                        }
                    });
                });
                $('#cancelLink').unbind('click').bind('click', function (e) {
                    e.preventDefault();
                    toAction('list');
                });
            }
            else {
                $('.remove-links').addClass("hide-element");
            }
            clearInterval(intervalID)
        }
    }, '100');
}

function toAction(typeOfAction) {
    addLinkVisible(false);
    editLinksVisible(false);
    confirmRemoveVisible(false);
    listOfLinksVisible(false);
    switch (typeOfAction) {
        case "list":
            jDialog.prepareDialog(jDialog.dialog);
            ErrorUtils.removeAllErrorMessages();
            listOfLinksVisible(true);
            break;
        case "add":
            addLinkVisible(true);
            break;
        case "confirmRemove":
            confirmRemoveVisible(true);
            break;
        case "edit":
            editLinksVisible(true);
            break;
        default:
            listOfLinksVisible(true);
            break;

    }
    jDialog.resizeDialog(jDialog.dialog);
}

