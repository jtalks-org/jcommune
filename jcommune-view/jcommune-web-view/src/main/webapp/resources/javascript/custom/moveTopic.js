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

/**
 * "Move topic" button handler.
 */
$(document).ready(function () {
    $("[name=move_topic]").click(function () {
        $.getJSON(baseUrl + "/sections/json", function (sections) {
            var htmlTemplate = prepareHtmlTemplateForModalWindow(sections);
            var eliminatedBranchId = $("#edit_button").attr("rel");
            displayAllBranches(eliminatedBranchId);
            showMoveTopicModalWindow(htmlTemplate);
        });
    });
});

/**
 * Prepares and returns html code for "Move topic" modal window in string representation.
 *
 * @param sections list of sections
 * @return html template for "Move topic" modal window
 */
function prepareHtmlTemplateForModalWindow(sections) {
    var htmlTemplate = '<b>Move topic</b><br/>' +
        '<select style="width: 45%" name="section_name" id="section_name" size="10">' +
        '<option value="all">All sections</option>';

    $.each(sections, function (i, section) {
        htmlTemplate += '<option value="' + section.id + '">' + section.name + '</option>';
    });

    htmlTemplate += '</select>' +
        '<select style="margin-left:30px; width: 45%" name="branch_name" id="branch_name" size="10">' +
        '<option value="' + 0 + '"></option>' +
        '</select>';

    return htmlTemplate;
}

/**
 * Shows "Move topic" modal window.
 *
 * @param htmlTemplate string representation of modal window html code
 */
function showMoveTopicModalWindow(htmlTemplate) {
    var branchId;
    var eliminatedBranchId = $("#edit_button").attr("rel");
    var topicId = $(".topicId").attr('id');
    $.prompt(htmlTemplate, {
        buttons:{ Move:true, Cancel:false},
        loaded:function () {
            displayBranches(eliminatedBranchId);
            $("#branch_name").change(function () {
                $("#jqi_state0_buttonMove").removeAttr("disabled");
                $("#jqi_state0_buttonMove").css({'color':'#262626'})
                branchId = $(this).val();
            });
        },
        callback:function (value) {
            if (value != undefined && value) {
                moveTopic(topicId, branchId);
            }
        }
    });
    disableMoveButton();
}

/**
 * Displays branches accordingly option chosen in section select element.
 * It may be "All sections" or particular section.
 */
function displayBranches(eliminatedBranchId) {
    $("#section_name").change(function () {
        var sectionId = $(this).val();
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
        url:baseUrl + '/branches/json/' + sectionId,
        success:function (branches) {
            rebuildBranchesList(branches, eliminatedBranchId);
        }
    });
}

/**
 * Displays all existing branches.
 */
function displayAllBranches(eliminatedBranchId) {
    $.ajax({
        url:baseUrl + '/branches/json',
        success:function (branches) {
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
    disableMoveButton();
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
            template += '<option value="' + branch.id + '">' + branch.name + '</option>';
        }
    });
    return template;
}

/**
 * Executes request to move the topic, and redirects to topic's updated location page.
 *
 * @param topicId it of topic which will move
 * @param targetBranchId id of branch which topic will move in
 */
function moveTopic(topicId, targetBranchId) {
    $.ajax({
        url:baseUrl + '/topics/move/json/' + topicId,
        type:"POST",
        data:{"branchId":targetBranchId},
        success:function () {
            document.location = baseUrl + '/topics/' + topicId;
        },
        errror:function() {
        	$.prompt($labelError500Detail);
        }
    });
}


/**
 * Disables Move button in "Move topic" modal window
 */
function disableMoveButton() {
    $("#jqi_state0_buttonMove").attr('disabled', 'disabled');
    $("#jqi_state0_buttonMove").css({'color':'grey'});
}

