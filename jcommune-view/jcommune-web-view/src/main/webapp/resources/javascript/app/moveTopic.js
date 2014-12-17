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
 * Application base path with trailing slash. Must be defined somewhere within the global scope.
 */
var baseUrl = $root;

var branchId;
var sectionId;
var topicId;

$(function () {

    /**
     * "Move topic" button handler.
     */
    $(".move_topic").on('click', function () {

        topicId = $(this).attr('data-topicId');

        $.getJSON(baseUrl + "/sections/json/" + topicId, function (sections) {
            var branchNameLive = function (e) {
                e.preventDefault();
                disableMoveButton(false);
                branchId = $(this).val();
            };

            var submitFunc = function (e) {
                e.preventDefault();
                $.ajax({
                    url: baseUrl + '/topics/move/json/' + topicId,
                    type: 'POST',
                    data: {"branchId": branchId},
                    dataType: 'html',
                    success: function (resp) {
                        resp = eval('(' + resp + ')');
                        document.location = baseUrl + resp.result;
                    },
                    error: function () {
                        jDialog.createDialog({
                            type: jDialog.alertType,
                            bodyMessage: $labelError500Detail
                        });
                    }
                });
            };

            var bodyContent = createSectionsForModalWindow(sections);

            var footerContent = ' \
            <button id="move-button-cancel" class="btn">' + $labelCancel + '</button> \
            <button id="move-button-save" class="btn btn-primary">' + $labelTopicMove + '</button>';

            jDialog.createDialog({
                dialogId: 'move-topic-editor',
                title: $labelTopicMoveFull,
                bodyContent: bodyContent,
                footerContent: footerContent,
                maxWidth: 350,
                tabNavigation: ['select:first', 'select:last', '#move-button-save', '#move-button-cancel'],
                handlers: {
                    '#move-button-cancel' : {'static':'close'}
                },
                handlersDelegate: {
                    '#branch_name': {'change': branchNameLive},
                    '#move-button-save': {'click': submitFunc}
                }
            });

            var eliminatedBranchId = $(".edit_button").attr("data-rel");

            displayAllBranches(eliminatedBranchId);

            displayBranches(eliminatedBranchId);

            jDialog.dialog.find('select:first').focus()
        });
    });

    /**
     * Prepares and returns html code for "Move topic" modal window in string representation.
     *
     * @param sections list of sections
     * @return html template for "Move topic" modal window
     */
    function createSectionsForModalWindow(sections) {
        var sectionsHTML = '<select style="width: 45%" name="section_name" id="section_name" size="10">' +
            '<option value="all">All sections</option>';

        $.each(sections, function (i, section) {
            sectionsHTML += '<option value="' + section.id + '">' + section.name + '</option>';
        });

        sectionsHTML += '</select>' +
            '<select style="margin-left:30px; width: 45%" name="branch_name" id="branch_name" size="10">' +
            '<option value="' + 0 + '"></option>' +
            '</select>';

        return sectionsHTML;
    }

    /**
     * Displays branches accordingly option chosen in section select element.
     * It may be "All sections" or particular section.
     */
    function displayBranches(eliminatedBranchId) {
        $("#section_name").on('change', function () {
            sectionId = $(this).val();
            if (sectionId == "all") {
                displayAllBranches(eliminatedBranchId);
            } else {
                displayBranchesFromSection(sectionId, eliminatedBranchId);
            }
        });
        $("#section_name").val("all");
    }

    /**
     *Displays all branches from section with given sectionId.
     */
    function displayBranchesFromSection(sectionId, eliminatedBranchId) {
        $.ajax({
            url: baseUrl + '/branches/json/' +topicId + '/' + sectionId,
            success: function (branches) {
                rebuildBranchesList(branches, eliminatedBranchId);
            }
        });
    }

    /**
     * Displays all existing branches.
     */
    function displayAllBranches(eliminatedBranchId) {
        $.ajax({
            url: baseUrl + '/branches/json/' + topicId,
            success: function (branches) {
                rebuildBranchesList(branches, eliminatedBranchId);
            }
        });
    }

    /**
     * Clears branches select element and inserts new values.
     *
     * @param branches list of branches to present
     */
    function rebuildBranchesList(branches, eliminatedBranchId) {
        disableMoveButton(true);
        $("#branch_name").children().remove();
        $("#branch_name").append(getBranchItemHtml(branches, eliminatedBranchId));
    }

    /**
     * Returns HTML code for options in branches select element.
     *
     * @param branches list of branches to present
     * @return html template of options in select
     */
    function getBranchItemHtml(branches, eliminatedBranchId) {
        var template = '';
        $.each(branches, function (i, branch) {
            if (eliminatedBranchId != branch.id) {
                template += '<option value="' + branch.id + '">' + Utils.htmlEncode(branch.name) + '</option>';
            }
        });
        return template;
    }

    /**
     * Disables Move button in "Move topic" modal window
     */
    function disableMoveButton(action) {
        $("#move-button-save").attr('disabled', action);
    }
});

