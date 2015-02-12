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
    var baseUrl = $root;

    $('.comment-prompt').click(function (e) {
        e.preventDefault();
        var postId = $(this).attr("id").split("-")[1];
        if (isCommentsHidden(postId)) {
            toggleCommentsFor(postId);
        }
        $(this).hide();
        $(this).next(".comment-container").show();
        $(this).next(".comment-container").children(".comment-textarea").focus();
    });

    $('.comment-cancel').click(function (e) {
        e.preventDefault();
        $(this).parents(".comment-container").children(".comment-textarea").val("");
        $(this).parents(".comment-container").hide();
        $(this).parents(".comments").children(".comment-prompt").show();
    });

    $('.expand').click(function(e){
        e.preventDefault();
        var postId = $(this).attr("data-postId");
        toggleCommentsFor(postId);
    });

    $(".vote-up").mouseup(voteUpHandler);

    $(".vote-down").mouseup(voteDownHandler);

    $('#answer').click(function(e){
        e.preventDefault();
        $('#postBody').focus();
    });

    if ($('#answerForm .error:visible').length > 0) {
        $('#postBody').focus();
    }

    $('.comment-submit').click(function (e){
        var commentDto = {};
        var postId = $(this).attr('data-post-id');
        commentDto.postId = postId;
        commentDto.body = $("#commentBody-" + postId).val();
        console.log(commentDto);
        $.ajax({
            url: baseUrl + "/topics/question/newcomment",
            type: "POST",
            contentType: "application/json",
            async: false,
            data: JSON.stringify(commentDto),
            success: function(data) {
                console.log(data);
                hideCommentForm(postId);
                addCommentToPost(postId, data.result);
            },
            error: function() {
                console.log("error");
            }
        });
    });
    updateCommentHandlers()
});

var editHandler = function(e) {
    if (e.which != 1) {
        return;
    }

    var commentId = $(this).attr("data-comment-id");
    enableEditMode(commentId);
}

var editSubmitHandler = function(e) {
    if (e.which != 1) {
        return;
    }
    var commentDto = {};
    commentId = $(this).attr("data-comment-id");
    commentDto.id = commentId;
    commentDto.body = $("#editable-" + commentId).val();
    console.log(commentDto);
    $.ajax({
        url: baseUrl + "/topics/question/editcomment?branchId=" + branchId,
        type: "POST",
        contentType: "application/json",
        async: false,
        data: JSON.stringify(commentDto),
        success: function(data) {
            console.log(data.status);
            if(data.status == 'SUCCESS') {
                $("#body-" + commentId).text(data.result);
                enableViewMode(commentId);
            } else {
                displayValidationErrors(data.result, "editable-" + commentId);
            }
        },
        error: function() {
            console.log("error");
        }
    });
}

var editCancelHandler = function(e) {
    if (e.which != 1) {
        return;
    }

    var commentId = $(this).attr("data-comment-id");
    enableViewMode(commentId);
}

var voteUpHandler = function voteUp(e) {
    if (e.which != 1) {
        return;
    }
    var postId = getVotedPostId($(this).attr('id'));
    if (isOwnPost(postId)) {
        showErrorPopUp(postId, labelVoteErrorOwnPost);
        return;
    }
    if (isVotedUp(postId)) {
        return;
    }
    $(".vote-up").unbind("mouseup");
    $.ajax({
        url: baseUrl + "/posts/" + postId + "/voteup",
        type: "GET",
        success: function(data) {
            if (isVotedDown(postId)) {
                changeRating($("#" + postId + "-rating"), 2);
            } else {
                changeRating($("#" + postId + "-rating"), 1);
            }
            markVoteUpAsPressed(postId);
        },
        error: function() {
            showErrorPopUp(postId, labelVoteErrorNoPermissions);
        }
    });
    setTimeout(function() {
        $(".vote-up").mouseup(voteUpHandler);
    }, 1000);
}

var voteDownHandler = function voteDown(e) {
    if (e.which != 1) {
        return;
    }
    var postId = getVotedPostId($(this).attr('id'));
    if (isOwnPost(postId)) {
        showErrorPopUp(postId, labelVoteErrorOwnPost);
        return;
    }
    if (isVotedDown(postId)) {
        return;
    }
    $(".vote-down").unbind("mouseup");
    $.ajax({
        url: baseUrl + "/posts/" + postId + "/votedown",
        type: "GET",
        success: function() {
            if (isVotedUp(postId)) {
                changeRating($("#" + postId + "-rating"), -2);
            } else {
                changeRating($("#" + postId + "-rating"), -1);
            }
            markVoteDownAsPressed(postId);
        },
        error: function() {
            showErrorPopUp(postId, labelVoteErrorNoPermissions);
        }
    });
    setTimeout(function() {
        $(".vote-down").mouseup(voteDownHandler);
    }, 1000);
}

/**
 * Shows pop-up with vote error message
 *
 * @param postId id of post where vote error occurred
 * @param message error message to be displayed
 */
function showErrorPopUp(postId, message) {
    $('#error-message-' + postId + " > span").text(message)
    $("#error-message-" + postId).show();
    setTimeout(function() {
        $("#error-message-" + postId).hide();
    }, 2000);
}

/**
 * Determines whatever current post is created by current user
 *
 * @param postId id of post to check
 * @return {boolean} true if post with specified id was created by current user, false otherwise
 */
function isOwnPost(postId) {
    return $("#" + postId + "-rating").attr("data-viewer-is-post-owner") == "true";
}

/**
 * Increment post rating which displayed in specified span
 *
 * @param ratingSpan span with rating
 * @param value value of rating changes
 */
function changeRating(ratingSpan, value) {
    var rating = parseInt(ratingSpan.text());
    rating = rating + value;
    ratingSpan.text(rating);
    // we need apply new style only if rating state changed
    // but rating value may changed by 2 if user revotes for post
    if ((rating > - 3) && (rating < 3)) {
        applyRatingStyle(ratingSpan, rating);
    }
}

/**
 * Applies css classes such as positive, negative and neutral to rating span
 *
 * @param ratingSpan span to apply classes
 * @param rating new rating value
 */
function applyRatingStyle(ratingSpan, rating) {
    ratingSpan.removeClass("positive negative");
    if (rating > 0) {
        ratingSpan.addClass("positive");
    }
    if(rating < 0) {
        ratingSpan.addClass("negative");
    }
}

/**
 * Marks vote up arrow as pressed for specified post id
 *
 * @param postId id  for post to mark arrow
 */
function markVoteUpAsPressed(postId) {
    var voteDownArrow = $("#" + postId + "-down");
    if (voteDownArrow.hasClass("vote-down-pressed")) {
        voteDownArrow.removeClass("vote-down-pressed");
        voteDownArrow.addClass("vote-down-unpressed");
    }
    var voteUpArrow = $("#" + postId + "-up");
    voteUpArrow.removeClass("vote-up-unpressed");
    voteUpArrow.addClass("vote-up-pressed");
}

/**
 * Marks vote down arrow as pressed for specified post id
 *
 * @param postId id  for post to mark arrow
 */
function markVoteDownAsPressed(postId) {
    var voteUpArrow = $("#" + postId + "-up");
    if (voteUpArrow.hasClass("vote-up-pressed")) {
        voteUpArrow.removeClass("vote-up-pressed");
        voteUpArrow.addClass("vote-up-unpressed");
    }
    var voteDownArrow = $("#" + postId + "-down");
    voteDownArrow.removeClass("vote-down-unpressed");
    voteDownArrow.addClass("vote-down-pressed");
}

/**
 * Determines whatever user voted down for post with specified id
 *
 * @param postId id of post to check
 * @returns {boolean} true if user voted down for post otherwise false
 */
function isVotedDown(postId) {
    return $("#" + postId + "-down").hasClass("vote-down-pressed");
}

/**
 * Determines whatever user voted up for post with specified id
 *
 * @param postId id of post to check
 * @returns {boolean} true if user voted up for post otherwise false
 */
function isVotedUp(postId) {
    return $("#" + postId + "-up").hasClass("vote-up-pressed");
}

/**
 * Gets id of voted post based on arrow element id
 *
 * @param elementId arrow element id
 * @return {String} id of voted post
 */
function getVotedPostId(elementId) {
    return elementId.split("-")[0];
}

$(document).mouseup(function(e) {
    if (!($(e.target).hasClass("comment-submit") || $(e.target).hasClass("comment-textarea"))) {
       hideEmptyCommentTextArea();
    }
});

$(document).keyup(function(e){
    //esc button
    if (e.keyCode == 27) {
        hideEmptyCommentTextArea();
    }
});

function hideEmptyCommentTextArea() {
    $(".comment-container").each(function() {
        if (!$(this).children(".comment-textarea").val()) {
            $(this).hide();
            $(this).parent().children(".comment-prompt").show();
        }
    });
}

/**
 * Hides comment form for post with specified id
 *
 * @param postId id of the post to hide comment form
 */
function hideCommentForm(postId) {
    $("#commentForm-" + postId).hide();
    $("#commentBody-" + postId).val("");
    $("#prompt-" + postId).show();
}

function addCommentToPost(postId, comment) {
    var commentList = $("#comments-" + postId).children();
    if (commentList.length != 0) {
        $(commentList[commentList.length - 1]).addClass("bordered");
    }
    if(commentList.length == 3) {
        $("#btns-" + postId).show();
        $($("#btns-" + postId).children()[0]).hide();
    }
    $("#comments-" + postId).append(getCommentHtml(comment));
    updateCommentHandlers();
    if (commentList.length > 2) {
        applyCommentsCssClasses(postId);
    }
}

function updateCommentHandlers() {
    $('.icon-pencil').click(editHandler);
    $('.edit-cancel').click(editCancelHandler);
    $('.edit-submit').click(editSubmitHandler);
}

function getCommentHtml(comment) {
    return "<div>"
        + "<div class='comment-header'>"
        + "<div class='comment-author pull-left'>"
        + "<a class='no-right-space' href='/jcommune/users/" + comment.authorId
        + " data-original-title='Нажмите для просмотра профиля'>" + comment.authorUsername + "</a>,</div>"
        + "<div class='comment-buttons pull-left'>"
        + "<a class='comment-button' data-original-title='Редактировать комментарий'><i class='icon-pencil' data-comment-id='"
        + comment.id + "'></i></a>&nbsp;"
        + "<a class='comment-button' data-original-title='Удалить комментарий'><i class='icon-trash'></i></a></div>"
        + "<div class='comment-date pull-left'>" + comment.formattedCreationDate + "</div><div class='cleared'></div>"
        + "</div><div class='comment-body'><span id='body-" + comment.id + "'>" + comment.body + "</span>"
        + "<div id='edit-" + comment.id + "' class='control-group comment-container edit' style='display: none'>"
        + "<textarea id='editable-" + comment.id + "' name='commentBody' class='comment-textarea' rows='3'>"
        + comment.body + "</textarea> <div class='comment-buttons-container'><div class='pull-right'>"
        + "<a class='btn btn-primary edit-submit' data-comment-id='" + comment.id + "'>Comment</a>"
        + "<a class='btn edit-cancel' data-comment-id='" + comment.id + "'>Cancel</a></div></div></div></div></div>";
}

function toggleCommentsFor(postId) {
    console.log($("#comments-" + postId).children().not(":visible").length);
    var commentList = $("#comments-" + postId);
    commentList.children(".togglable").toggle();
    var buttons = $("#btns-" + postId);
    buttons.children(".togglable").toggle();
    buttons.focus();
    var thirdComment = commentList.children(".hiddenBorder");
    if (thirdComment.hasClass("bordered")) {
        thirdComment.removeClass("bordered");
    } else {
        thirdComment.addClass("bordered");
    }
}

function isCommentsHidden(postId) {
    return $("#comments-" + postId).children().not(":visible").length == 0;
}

function applyCommentsCssClasses(postId) {
    var i = 0;
    $("#comments-" + postId).children().each(function() {
        $(this).removeClass("bordered");
        $(this).removeClass("togglable");
        $(this).removeClass("hiddenBorder");
        if (i != $("#comments-" + postId).children().length - 1) {
            $(this).addClass("bordered");
            console.log(i + " bordered");
        }
        if (i == 2) {
            $(this).addClass("hiddenBorder");
            console.log(i + " hiddenBorder");
        }
        if (i > 2) {
            $(this).addClass("togglable");
            console.log(i + " togglable");
        }
        i ++;
    });
}

function enableEditMode(commentId) {
    $("#body-" + commentId).hide();
    $("#edit-" + commentId).show();
}

function enableViewMode(commentId) {
    $("#body-" + commentId).show();
    $("#edit-" + commentId).hide();
}

function displayValidationErrors(errors, elementId) {
    console.log("displayValidationErrors");
    var element = $("#" + elementId);
    element.parent().addClass("error");
    $(getValidationErrorView(errors)).insertAfter("#" + elementId);
}

function getValidationErrorView(errors) {
    if(errors.length > 0) {
        var errorsView = "<span class='help-inline'>";
        for (var i = 0; i < errors.length; i ++) {
            errorsView += errors[i].message + '<br/>';
        }
        errorsView += "</span>";
        return errorsView;
    }
    return "";
}