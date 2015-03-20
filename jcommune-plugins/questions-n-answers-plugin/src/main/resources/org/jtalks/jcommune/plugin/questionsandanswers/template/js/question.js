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
var numberOfCommentsToShow = 3;
$(function () {
    var baseUrl = $root;

    $('.comment-prompt').click(function (e) {
        e.preventDefault();
        var postId = $(this).attr("id").split("-")[1];
        if (isCommentsHidden(postId)) {
            toggleCommentsFor(postId);
        }
        $(this).hide();
        hideVisibleEditPrompts();
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

    $(".new-comment").keydown(function (e) {
        if (e.ctrlKey && e.keyCode == enterCode) {
            e.preventDefault();
            var postId = $(this).attr("id").split("-")[1];
            $(".comment-submit[data-post-id='" + postId +"']")[0].click();
        }
    });

    $('#answer').click(function(e){
        e.preventDefault();
        $('#postBody').focus();
    });

    if ($('#answerForm .error:visible').length > 0) {
        $('#postBody').focus();
    }

    $("#answerForm").submit(function(e) {
        var url = $(this).attr("action") + "/canpost";
        var canPost = false;

        $.ajax({
            url: url,
            type: "GET",
            async: false,
            success: function(data) {
                canPost = data.status == 'SUCCESS';
                console.log("success canpost=" + canPost);
            },
            error: function() {
                //if error occurred let user see it
                canPost = true;
            }
        });
        if (!canPost) {
            e.preventDefault();
            jDialog.createDialog({
                type: jDialog.alertType,
                bodyMessage: labelLimitOfAnswersReached
            });
        }
    });

    $('.comment-submit').click(function (e){
        var commentDto = {};
        var postId = $(this).attr('data-post-id');
        clearValidationErrors($("#commentForm-" + postId));
        commentDto.postId = postId;
        commentDto.body = $("#commentBody-" + postId).val();
        $.ajax({
            url: baseUrl + "/topics/question/newcomment",
            type: "POST",
            contentType: "application/json",
            async: false,
            data: JSON.stringify(commentDto),
            success: function(data) {
                if (data.status == 'SUCCESS') {
                    hideCommentForm(postId);
                    addCommentToPost(postId, data.result);
                } else if (data.reason == 'VALIDATION') {
                    displayValidationErrors(data.result, "commentBody-" + postId);
                } else if (data.reason == 'ENTITY_NOT_FOUND') {
                    jDialog.createDialog({
                        type: jDialog.alertType,
                        bodyMessage: labelAddCommentPostNotFound
                    });
                } else {
                    //looks like limit of comments was reached
                    jDialog.createDialog({
                        type: jDialog.alertType,
                        bodyMessage: labelLimitOfCommentsReached
                    });
                    $("#commentForm-" + postId).hide();
                }
            },
            error: function() {
                // Should not occur
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: labelUnexpectedError
                });
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

var deleteCommentHandler = function(e) {
    if (e.which != 1) {
        return;
    }

    var commentId = $(this).attr("data-comment-id");

    var submitFunc = function(e) {
        e.preventDefault();
        $.ajax({
            url: baseUrl + "/topics/question/deletecomment?commentId=" + commentId + "&postId=" + getPostIdForComment(commentId),
            type: "GET",
            success: function(data) {
                if (data.status == 'SUCCESS') {
                    removeCommentHtml(commentId);
                    jDialog.closeDialog();
                } else if (data.reason == 'ENTITY_NOT_FOUND') {
                    jDialog.createDialog({
                        type: jDialog.alertType,
                        bodyMessage: labelDeleteCommentNotFound
                    });
                }
            },
            error: function() {
                // Should not occur
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: labelUnexpectedError
                });
            }
        });
    };

    var footerContent = ' \
	            <button id="remove-review-cancel" class="btn cancel">' + $labelCancel + '</button> \
	            <button id="remove-review-ok" class="btn btn-primary">' + $labelOk + '</button>';
    jDialog.createDialog({
        type: jDialog.confirmType,
        bodyMessage : deleteCommentConfirmation,
        firstFocus : false,
        footerContent: footerContent,
        maxWidth: 300,
        tabNavigation: ['#remove-review-ok','#remove-review-cancel'],
        handlers: {
            '#remove-review-ok': {'click': submitFunc, 'keydown' : Keymaps.reviewConfirmRemoveButton},
            '#remove-review-cancel': {'keydown': Keymaps.reviewCancelRemoveButton, 'static':'close'}
        }
    });
    $('#remove-review-ok').focus();
}

var editSubmitHandler = function(e) {
    if (e.which != 1) {
        return;
    }
    var commentDto = {};
    commentId = $(this).attr("data-comment-id");
    clearValidationErrors($("#edit-" + commentId));
    commentDto.id = commentId;
    commentDto.body = $("#editable-" + commentId).val();
    $.ajax({
        url: baseUrl + "/topics/question/editcomment?branchId=" + branchId,
        type: "POST",
        contentType: "application/json",
        async: false,
        data: JSON.stringify(commentDto),
        success: function(data) {
            if(data.status == 'SUCCESS') {
                $("#body-" + commentId).text(data.result);
                enableViewMode(commentId);
            } else if (data.reason == 'VALIDATION') {
                enableEditMode(commentId);
                displayValidationErrors(data.result, "editable-" + commentId);
            } else if (data.reason == 'ENTITY_NOT_FOUND') {
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: labelEditCommentNotFound
                });
            }
        },
        error: function() {
            // Should not occur
            jDialog.createDialog({
                type: jDialog.alertType,
                bodyMessage: labelUnexpectedError
            });
        }
    });
}

var editCancelHandler = function(e) {
    if (e.which != 1) {
        return;
    }

    hideVisibleEditPrompts();
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
        hideVisibleEditPrompts();
    }
});

function hideVisibleEditPrompts() {
    $(".edit:visible").each(function() {
        $(this).hide();
        $(this).prev().show();
    });
}

function hideEmptyCommentTextArea() {
    $(".comment-container").each(function() {
        var commentTextArea = $(this).children(".comment-textarea");
        if (!commentTextArea.val() && commentTextArea.is(":visible")) {
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

/**
 * Adds new comment to post with specified if
 *
 * @param postId post to add comment
 * @param comment comment to be added
 */
function addCommentToPost(postId, comment) {
    var commentList = $("#comments-" + postId).children();
    if (commentList.length != 0) {
        $(commentList[commentList.length - 1]).addClass("bordered");
    }
    if(commentList.length == numberOfCommentsToShow) {
        $("#btns-" + postId).show();
        $($("#btns-" + postId).children()[0]).hide();
        $($("#btns-" + postId).children()[1]).show();
    }
    if (isCommentsHidden(postId)) {
        toggleCommentsFor(postId);
    }
    $("#comments-" + postId).append(getCommentHtml(comment));
    updateCommentHandlers();
    if (commentList.length > numberOfCommentsToShow - 1) {
        applyCommentsCssClasses(postId);
    }
    if(commentList.length >= maxCommentNumber - 1) {
        $("#prompt-" + postId).hide();
    }
}

/**
 *  Rebinds edit and delete comment's handlers. Should be called after addition of new comment
 */
function updateCommentHandlers() {
    $('.icon-pencil').click(editHandler);
    $('.edit-cancel').click(editCancelHandler);
    $('.edit-submit').click(editSubmitHandler);
    $('.icon-trash').click(deleteCommentHandler);
    $("a").tooltip();
    $(".edit-comment").keydown(function (e){
        if (e.ctrlKey && e.keyCode == enterCode) {
            e.preventDefault();

            var commentId = $(this).attr("id").split("-")[1];
            $(".edit-submit[data-comment-id='" + commentId +"']")[0].click();
        }
    });
}

/**
 * Gets HTML view of comment. This view should be appended to other comments
 *
 * @param comment comment for which HTML view will be generated
 * @returns {string} HTML view of specified comment
 */
function getCommentHtml(comment) {
    var result = "<div id='comment-" + comment.id + "'>"
        + "<div class='comment-header'>"
        + "<div class='comment-author pull-left'>"
        + "<a class='no-right-space' href='/jcommune/users/" + comment.authorId
        + "' data-original-title='" + labelProfileTip + "'>" + comment.authorUsername + "</a> , </div>"
        + "<div class='comment-buttons pull-left'>";
    if (canEditNewlyAddedComments) {
        result = result + "<a class='comment-button' data-original-title='" + labelEditCommentTip + "'><i class='icon-pencil' "
        + "data-comment-id='" + comment.id + "'></i></a>";
    }
    if (canDeleteNewlyAddedComments) {
        result = result + "<a class='comment-button delete-comment' style='margin-left: 10px' data-original-title='" + labelDeleteCommentTip + "'><i class='icon-trash' "
        + "data-comment-id='" + comment.id + "'></i></a>"
    }
    result = result + "</div><div class='comment-date pull-left'>" + comment.formattedCreationDate + "</div><div class='cleared'></div>"
        + "</div><div class='comment-body'><span id='body-" + comment.id + "' class='comment-content'>"
        + Utils.lf2br(Utils.htmlEncode(comment.body))
        + "</span><div id='edit-" + comment.id + "' class='control-group comment-container edit' style='display: none'>"
        + "<textarea id='editable-" + comment.id + "' name='commentBody' class='comment-textarea edit-comment' rows='3'>"
        + comment.body + "</textarea> <div class='comment-buttons-container'><div class='pull-right'>"
        + "<a class='btn btn-primary edit-submit' data-comment-id='" + comment.id + "'>" + labelSave + "</a>"
        + "<a class='btn edit-cancel' data-comment-id='" + comment.id + "'>" + labelCancel + "</a></div></div></div>"
        + "</div></div>";
    return result;
}

/**
 * Toggles visibility of comments of specified post
 *
 * @param postId post id for which visibility of comments will be toggled
 */
function toggleCommentsFor(postId) {
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

/**
 * Checks if post with specified id has hidden comments
 *
 * @param postId id of post to check presents of hidden comments
 * @returns {boolean} true if post have hidden comments, false otherwise
 */
function isCommentsHidden(postId) {
    return $("#comments-" + postId).children().not(":visible").length > 0;
}

/**
 * Applies new CSS classes for comments of post with specified id. Should be called after addition or deletion
 * of comments. Also can be called after toggling visibility of comments
 *
 * @param postId id of the post to apply new CSS classes for comments
 */
function applyCommentsCssClasses(postId) {
    var i = 0;
    var commentCount = $("#comments-" + postId).children().length;
    $("#comments-" + postId).children().each(function() {
        $(this).removeClass("bordered");
        $(this).removeClass("togglable");
        $(this).removeClass("hiddenBorder");
        if (i != commentCount - 1 && i != numberOfCommentsToShow - 1) {
            $(this).addClass("bordered");
        }
        if (i == numberOfCommentsToShow - 1) {
            $(this).show(); //for case of removing comment
            if (i != commentCount - 1) {
                $(this).addClass("hiddenBorder");
                $(this).addClass("bordered");
            }
        }
        if (i > numberOfCommentsToShow - 1) {
            $(this).addClass("togglable");
        }
        i ++;
    });
}

/**
 * Shows edit form for comment with specified id
 *
 * @param commentId id of comment to enable edit mode
 */
function enableEditMode(commentId) {
    $("#body-" + commentId).hide();
    $("#edit-" + commentId).show();
}

/**
 * Hides edit form and shows comment itself
 *
 * @param commentId id of comment to enable view mode
 */
function enableViewMode(commentId) {
    $("#body-" + commentId).show();
    $("#edit-" + commentId).hide();
}

/**
 * Shows validation error on element with specified id (typically textarea)
 *
 * @param errors errors to be displayed
 * @param elementId id of element to show errors
 */
function displayValidationErrors(errors, elementId) {
    var element = $("#" + elementId);
    element.parent().addClass("error");
    $(getValidationErrorView(errors)).insertAfter("#" + elementId);
}

/**
 * Gets HTML error view for specified validation errors
 *
 * @param errors validation errors to generate HTML view
 * @returns {string} HTML view for specified errors
 */
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

/**
 * Clears validation errors for specified element (typically div)
 *
 * @param element element to clear validation errors
 */
function clearValidationErrors(element) {
    element.removeClass("error");
    element.children().remove(".help-inline");

}

/**
 * Removes HTML view of comment with specified id
 *
 * @param commentId id of comment to remove HTML view
 */
function removeCommentHtml(commentId) {
    var commentElement = $("#comment-" + commentId);
    var postId = commentElement.parent().attr("id").split("-")[1];
    $("#comment-" + commentId).remove();
    applyCommentsCssClasses(postId);
    var numberOfComments = $("#comments-" + postId).children().length;
    if (numberOfComments == numberOfCommentsToShow) {
        $("#btns-" + postId).hide();
    }
    if (numberOfComments < maxCommentNumber) {
        console.log("show prompt");
        $("#prompt-" + postId).show();
    }
}

/**
 * Gets id of the post to which comment with specified id belong
 *
 * @param commentId id of the comment
 * @returns {*} id of the post
 */
function getPostIdForComment(commentId) {
    return $("#comment-" + commentId).parent().attr("id").split("-")[1];
}