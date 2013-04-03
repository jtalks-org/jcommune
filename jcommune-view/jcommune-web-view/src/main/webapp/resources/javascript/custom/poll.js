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

$(document).ready(function () {

    hideAdditionalComponents();
    //attach the handler
    $("#pollSubmit").click(function () {
        var pollId = $("input:hidden[name=pollId]").val();
        var pollOptionId = getPollOptionForSingleVote();
        if (pollOptionId != null) {
            $("#pollAjaxLoader").show(); //show the ajax loader
            addSingleVote(pollOptionId, pollId);
            return false;
        } else {
            var pollDto = getPollDtoForMultipleVote(pollId);
            if (pollDto.pollOptions != null && pollDto.pollOptions.length != 0) {
                $("#pollAjaxLoader").show(); //show the ajax loader
                addMultipleVote(pollDto, pollId);
                return false;
            } else {//nothing is selected
                //show error message
                showErrorMessage();
                return false;
            }
        }
    });
});

/**
 * Hides additional components.
 */
function hideAdditionalComponents() {
    $("#pollAjaxLoader").hide(); //hide the ajax loader
    $("#pollMessage").hide(); //hide the ajax loader
}

/**
 * Shows the error message.
 * The message indicates that you must select an answer.
 */
function showErrorMessage() {
    $("#pollMessage").fadeTo("slow", 1, function () {
        setTimeout(function () {
            $("#pollMessage").fadeOut("slow");
        }, 3000);
    });
}

/**
 * Creates the data transfer object that contains list of selected options.
 * This method used in case when poll is "multiple type".
 *
 * @param pollId the poll id
 * @returns the data transfer object that contains list of selected options
 */
function getPollDtoForMultipleVote(pollId) {
    var pollOptionDtos = [];
    $('input:checkbox[name=pollAnswer]:checked').each(function () {
        var optionDto = new Object();
        optionDto.id = $(this).val();
        optionDto.votesCount = 0;//it isn't important in this case
        pollOptionDtos.push(optionDto);
    });
    var pollDto = new Object();
    pollDto.id = pollId;
    pollDto.totalVotesCount = 0;//it isn't important in this case
    pollDto.pollOptions = pollOptionDtos;
    return pollDto;
}

/**
 * Get id of selected option.
 * This method used in case when poll is "single type".
 *
 * @returns the id of selected option
 */
function getPollOptionForSingleVote() {
    var pollOptionId = $('input:radio[name=pollAnswer]:checked').val();
    return pollOptionId;
}

/**
 * Performs all operations after voting.
 *
 * @param poll the poll
 */
function applyPollResult(poll) {
    //disable and hide all check boxes
    $('input:checkbox[name=pollAnswer]').each(function () {
        $(this).attr("disabled", "disabled");
        $(this).hide();
    });
    //disable and hide all radio buttons
    $('input:radio[name=pollAnswer]').each(function () {
        $(this).attr("disabled", "disabled");
        $(this).hide();
    });

    //build charts
    for (var i = 0; i < poll.pollOptions.length; i++) {
        var pollOption = poll.pollOptions[i];
        var pollOptionId = pollOption.id;
        var pollPercentage = pollOption.votesCount / poll.totalVotesCount * 100;
        var roundedPollPercentage = (Math.round(pollPercentage * 100) / 100).toFixed(2);
        $("#pollAnswer" + pollOptionId + " .chart").css("width", roundedPollPercentage + "%");
        if (pollOption.votesCount > 0) {
            $("#pollAnswer" + pollOptionId + " .chart").text(pollOption.votesCount + " - " + roundedPollPercentage + "%");
        }
        $("#pollAnswer" + pollOptionId).show();
    }
    //disable and hide vote button
    $("#pollAjaxLoader").hide(); //hide the ajax loader again
    $("#pollSubmit").attr("disabled", "disabled"); //disable the submit button
    $("#pollSubmit").hide();
}

/**
 * Send AJAX request to the server for "single type" vote.
 *
 * @param pollOptionId the id of selected option
 * @param pollId the poll id
 */
function addSingleVote(pollOptionId, pollId) {
    $.ajax({
        url:$root + '/poll/' + pollId + '/single',
        type:"POST",
        data:{"pollOptionId":pollOptionId},
        success:function (poll) {
            applyPollResult(poll);
        },
        error: processVotingError
    });
}

/**
 * Send AJAX request to the server for "multiple type" vote.
 *
 * @param pollDto the data transfer object, that contains the list
 *                   of selected options
 * @param pollId the poll id
 */
function addMultipleVote(pollDto, pollId) {
    $.ajax({
        url:$root + "/poll/" + pollId + '/multiple',
        type:"POST",
        contentType:"application/json",
        data:JSON.stringify(pollDto),
        success:function (poll) {
            applyPollResult(poll);
        },
        error: processVotingError
    });
}

/**
 * Handles error during voting
 */
function processVotingError() {
	bootbox.alert($labelUnexpectedError);
	$("#pollAjaxLoader").hide(); //hide the ajax loader again
}
