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

$(function () {
    $('#links_editor').on('click', function (e) {
        e.preventDefault();
//        elemnts for test ui.
        var elements = [
            {id: '1', url: 'http://jtalks.org', title: 'JTalks', hint: "hint"},
            {id: '2', url: 'http://jtalks2.org', title: 'JTalks2', hint: "hint"},
            {id: '3', url: 'http://jtalks3.org', title: 'JTalks3', hint: "hint"},
            {id: '4', url: 'http://jtalks4.org', title: 'JTalks4', hint: "hint"},
            {id: '5', url: 'http://jtalks5.org', title: 'JTalks5', hint: "hint"},
            {id: '6', url: 'http://jtalks6.org', title: 'JTalks6', hint: "hint"},
            {id: '7', url: 'http://jtalks7.org', title: 'JTalks7', hint: "hint"},
            {id: '8', url: 'http://jtalks8.org', title: 'JTalks8', hint: "hint"},
            {id: '9', url: 'http://jtalks9.org', title: 'JTalks9', hint: "hint"},
            {id: '10', url: 'http://jtalks10.org', title: 'JTalks10', hint: "hint"},
            {id: '11', url: 'http://jtalks11.org', title: 'JTalks11', hint: "hint"},
            {id: '12', url: 'http://jtalks12.org', title: 'JTalks12', hint: "hint"},
            {id: '13', url: 'http://jtalks13.org', title: 'JTalks13', hint: "hint"},
            {id: '14', url: 'http://jtalks14.org', title: 'JTalks14', hint: "hint"}
        ];

        var linksEditor = createMainLinkEditor(elements);

        linksEditor.modal({
            "backdrop": "static",
            "keyboard": true,
            "show": true
        });

        linksEditor.on('hidden', function () {
            linksEditor.remove();
        })

        toAction('list');

        $(document).delegate('.icon-pencil', 'click', function (e) {
            e.preventDefault();
            actionId = $(e.target).parent('tr').attr('id');
            toAction('edit');
        });
        $(document).delegate('.icon-trash', 'click', function (e) {
            e.preventDefault();
            actionId = $(e.target).parent('tr').attr('id');
            toAction('confirmRemove');
        })


        $('#add-main-link').bind('click', function (e) {
            e.preventDefault();
            toAction('add');
        });

        Utils.resizeDialog(linksEditor);

    });

});

function createFormElement(label, id, type, cls) {
    var elementHtml = ' \
        <div class="control-group"> \
            <div class="controls"> \
                <input type="' + type + '" id="' + id + '" name="' + id + '" placeholder="' + label + '" class="input-xlarge ' + cls + '" /> \
            </div> \
        </div> \
    ';
    return $(elementHtml).html();
}

function createLinksTable(elements) {
    var elementHtml = "";
    $.each(elements, function (index, element) {
        elementHtml = elementHtml + ' \
                    <tr id="' + element.id + '"> \
                    <td class="link-url">' + element.url + '</td> \
                    <td class="link-hint">' + element.hint + '</td> \
                    <td class="link-title">' + element.title + '</td> \
                    <td class="icon-pencil cursor-hand" ></td> \
                    <td class="icon-trash cursor-hand" /> \
                    </tr> \
            ';
    })
    return elementHtml;
}

function createMainLinkEditor(elements) {
    return $(' \
        <div class="modal" id="main-links-editor" align="center"> \
            <div class="modal-header"> \
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button> \
                <h3>' + $labelLinksEditor + '</h3> \
            </div> \
            <div class="modal-body"> \
            <table cellpadding="0" cellspacing="0" class="list-of-links"> ' +
            createLinksTable(elements) + '\
            </table>' +
            createFormElement($labelTitle, 'link-title', 'text', 'edit-links') +
            createFormElement($labelUrl, 'link-url', 'text', 'edit-links') +
            createFormElement($labelHint, 'link-hint', 'text', 'edit-links') + ' \
            <span class="confirm-delete-text remove-links"></span>\
            </div> \
            <div class="modal-footer"> \
                <button id="add-main-link" class="btn btn-block list-of-links">' + $labelAdd + '</button> \
                <button id="cancel-link" class="btn  edit-links remove-links">' + $labelCancel + '</button> \
                <button id="save-link" class="btn btn-primary  edit-links">' + $labelSave + '</button> \
                <button id="remove-link" class="btn btn-primary  remove-links">' + $labelDelete + '</button> \
            </div> \
        </div> \
        ');
}

function listOfLinksVisible(visible) {

    var intervalID = setInterval(function () {
        if ($('.edit-links').size() > 1) {
            if (visible) {
                $('.list-of-links').show();
            }
            else {
                $('.list-of-links').hide();
            }
            clearInterval(intervalID)
        }
    }, '100');
}

function editLinksVisible(visible) {
    var intervalID = setInterval(function () {
        if ($('.edit-links')) {
            if (visible) {
                var link = $('#' + actionId);
                $('#link-title').val(link.children('.link-title').text());
                $('#link-url').val(link.children('.link-url').text());
                $('#link-hint').val(link.children('.link-hint').text());
                $('.edit-links').show();
                //save edited link
                $('#save-link').unbind("click").bind('click', function () {

                });
                $('#cancel-link').unbind("click").bind('click', function () {
                    toAction('list');
                });
            }
            else {
                $('.edit-links').hide();
            }
            clearInterval(intervalID)
        }
    }, '100');
}

function addLinkVisible(visible) {
    var intervalID = setInterval(function () {
        if ($('.edit-links')) {
            if (visible) {
                $('#link-title').val("");
                $('#link-url').val("");
                $('#link-hint').val("");
                $('.edit-links').show();
                $('#save-link').unbind("click").bind('click', function () {
                    var link = {
                        title:$('#link-title')[0].value,
                        url:$('#link-url')[0].value,
                        url:$('#link-hint')[0].value,
                        hint: "hint content"
                    };

                    $.ajax({
                        url:baseUrl + "/links/add",
                        type:"POST",
                        contentType:"application/json",
                        async: false,
                        data: JSON.stringify(link),
                        success: function(data) {
                            //todo populate links
                        },
                        error: function(data) {
                            //todo highlight errors
                        }
                    });

                });
                $('#cancel-link').unbind("click").bind('click', function () {
                    toAction('list');
                });
            }
            else {
                $('.edit-links').hide();
            }
            clearInterval(intervalID)
        }
    }, '100');
}

function confirmRemoveVisible(visible) {
    var intervalID = setInterval(function () {
        if ($('.remove-links')) {
            if (visible) {
                var link = $('#' + actionId);
                var linkTitle = $labelDeleteMainLink.replace('{0}', link.children('.link-title').text());
                var linkHint = $labelDeleteMainLink.replace('{0}', link.children('.link-hint').text());
                $('.confirm-delete-text').text(linkTitle);
                $('.remove-links').show();

                //delete link
                $('#remove-link').unbind("click").bind('click', function () {
                    link.remove();
                    toAction('list');
                });
                $('#cancel-link').unbind("click").bind('click', function () {
                    toAction('list');
                });
            }
            else {
                $('.remove-links').hide();
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
}

