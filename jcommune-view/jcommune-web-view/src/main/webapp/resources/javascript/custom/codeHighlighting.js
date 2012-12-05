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

/** Namespace for this file */
var CodeHighlighting = {}

CodeHighlighting.ADD_COMMENT_FORM_ID = 'add-comment-form';

$(document).ready(function () {
    //Code highlight
    prettyPrint(function () {
        var hasCodeReview = $('#has-code-review').val();
        if (hasCodeReview == 'true') {
            CodeHighlighting.displayReviewComments();

            var branchId = $('#branchId').val();
            PermissionService.hasPermission(branchId, 'BRANCH',
                'BranchPermission.LEAVE_COMMENTS_IN_CODE_REVIEW',
                CodeHighlighting.setupAddCommentFormHandlers);

        }

    });
});

/**
 * Function to append commentto HTML
 */
CodeHighlighting.addComment = function (comment) {
    var nextLine = $('.script-first-post ol.linenums li:nth-child(' + comment.lineNumber + ') ~ li:first');
    if (nextLine.length > 0) {
        nextLine.before(CodeHighlighting.getCommentHtml(comment));
    } else {
        $('.script-first-post ol.linenums li:nth-child(' + comment.lineNumber + ')')
            .parent('.linenums').append(CodeHighlighting.getCommentHtml(comment))
    }
}

/**
 * Get review comments for this topic from server and display them
 */
CodeHighlighting.displayReviewComments = function () {
    var codeReviewId = $('#codeReviewId').val();
    $.ajax({
        url:baseUrl + '/reviews/' + codeReviewId + '/json',
        type:"GET",
        success:function (data) {
            var comments = data.result.comments;
            for (var i = 0; i < comments.length; i++) {
                CodeHighlighting.addComment(comments[i]);
            }
        }
    });
}

CodeHighlighting.setupAddCommentFormHandlers = function () {
    $('.script-first-post').on('click', 'ol.linenums li', function () {
        var addCommentForm = $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID);
        if (addCommentForm.length == 0) {
            var index = $(this).index();
            $(this).after(CodeHighlighting.getAddCommentForm(index + 1));
        }
    });

    $('.script-first-post').on('click', "input:button[name=submit]", function (event) {
        event.stopPropagation();
        var reviewId = $('#codeReviewId').val();
        var lineNumber = $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID + ' [name=lineNumber]').val();
        var body = $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID + ' [name=body]').val();
        $.post(
            baseUrl + '/reviews/' + reviewId + '/add-comment',
            {lineNumber:lineNumber, body:body, id:0, authorId:0, authorUsername:""}
        )
            .success(function (data) {
                if (data.status == 'success') {
                    CodeHighlighting.removeAddCommentForm();
                    CodeHighlighting.addComment(data.result)
                } else {
                    bootbox.alert('Input valid data');
                }
            })
            .error(function (data) {
                bootbox.alert('Error during adding comment');
            });
    });

    $('.script-first-post').on('click', 'input:button[name=cancel]', function (event) {
        event.stopPropagation();
        CodeHighlighting.removeAddCommentForm();
    });
}

/**
 * Build HTML piece for review comment
 * @param Code review comment
 * @return div (string) with comment data
 */
CodeHighlighting.getCommentHtml = function (comment) {
    var result =
        '<div class="review-container"> '
            + '<div class="review-avatar">'
            + '<img class="review-avatar-img" src="' + baseUrl + '/users/' + comment.authorId + '/avatar"/>'
            + '</div>'
            + '<div class="review-content">'
            + '<div class="review-header">'
            + '<a href="' + baseUrl + '/users/' + comment.authorId + '">' + comment.authorUsername + '</a>'
            + ' ' + $labelReviewSays + ': '
            + '</div>'
            + '<div class="review-body">'
            + comment.body
            + '</div>'
            + '</div>'
            + '</div>';
    return result;
}

CodeHighlighting.getAddCommentForm = function (lineNumber) {
    var result =
        '<div id="' + CodeHighlighting.ADD_COMMENT_FORM_ID + '" class="review-container">'
            + '<div>'
            + '<input type=hidden name=lineNumber value="' + lineNumber + '"/>'
            + '<textarea name="body" class="review-container-content"/>'
            + '</div>'
            + '<div>'
            + '<input type=button name=submit value="Save" class="btn btn-primary review-container-controls-ok"/>'
            + '<input type=button name=cancel value="Cancel" class="btn btn-primary review-container-controls-cancel"/>'
            + '</div>'
            + '</div>';
    return result;
}

CodeHighlighting.removeAddCommentForm = function () {
    $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID).remove();
}