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
CodeHighlighting.ADD_COMMENT_BUTTON_NAME = 'add-comment-button';

/** ID of branch where we currently are */
CodeHighlighting.branchId = 0;
/** ID of current user */
CodeHighlighting.currentUserId = 0;
/** Indicates if current user can add comments */
CodeHighlighting.canAddComents = false;
/** Indicates if current user has EDIT_OWN_POSTS permission */
CodeHighlighting.canEditOwnPosts = false;
/** Indicates if current user has EDIT_OTHERS_POSTS permission */
CodeHighlighting.canEditOtherPosts = false;
/** Indicates if current user has DELETE_OWN_POSTS permission */
CodeHighlighting.canDeleteOwnPosts = false;
/** Indicates if current user has DELETE_OTHERS_POSTS permission */
CodeHighlighting.canDeleteOtherPosts = false;

/**
 * Called when document rendering is completed. Highlight code and
 * sets up handlers for code review if needed.
 */
$(document).ready(function () {
    prettyPrint(function () {
        var hasCodeReview = $('#has-code-review').val();
        if (hasCodeReview == 'true') {
        	CodeHighlighting.initializeVariables();
            CodeHighlighting.displayReviewComments();
			CodeHighlighting.setupGeneralHandlers();

            var branchId = $('#branchId').val();
            PermissionService.hasPermission(branchId, 'BRANCH',
                    'BranchPermission.LEAVE_COMMENTS_IN_CODE_REVIEW',
                    CodeHighlighting.setupAddCommentFormHandlers);
			
			CodeHighlighting.setupEditCommentHandlers();
        }
    });
});

/**
 * Function to append comment to HTML
 */
CodeHighlighting.addComment = function (comment) {
	if (CodeHighlighting.getNumberOfComments(comment.lineNumber) == 0) {
		CodeHighlighting.addReviewGroup(comment.lineNumber);
	}
    $('.script-first-post ol.linenums li:nth-child(' + comment.lineNumber + ') div.review-group-comments')
			.append(CodeHighlighting.getCommentHtml(comment));
}

/**
 * Function to update existing comment. Actually removes existing comment and adds new.
 */
CodeHighlighting.updateComment = function (comment) {
    var reviewContainer = $('.script-first-post .review-container input[name=id][value=' + comment.id + ']').parent();
	reviewContainer.after(CodeHighlighting.getCommentHtml(comment));
	reviewContainer.remove();
}

/**
 * Get review comments for this topic from server and display them
 * @return deffered object for AJAX request (displaying comments)
 */
CodeHighlighting.displayReviewComments = function () {
    var codeReviewId = $('#codeReviewId').val();
    return $.ajax({
        url: baseUrl + '/reviews/' + codeReviewId + '/json',
        type: "GET",
        success: function (data) {
            var comments = data.result.comments;
            for (var i = 0; i < comments.length; i++) {
                CodeHighlighting.addComment(comments[i]);
            }
        },
        error: function () {
            jDialog.createDialog({
                type: jDialog.alertType,
                bodyMessage: $labelUnexpectedError
            });
        }
    });
}

/**
 * Initialize variables used in this scope.
 */
CodeHighlighting.initializeVariables = function() {
	CodeHighlighting.branchId = $('#branchId').val();
	CodeHighlighting.currentUserId = $('#userId').val();
	CodeHighlighting.canAddComents = PermissionService.getHasPermission(CodeHighlighting.branchId, 'BRANCH',
					'BranchPermission.LEAVE_COMMENTS_IN_CODE_REVIEW');
	CodeHighlighting.canEditOwnPosts = PermissionService.getHasPermission(CodeHighlighting.branchId, 'BRANCH',
	                'BranchPermission.EDIT_OWN_POSTS');
	CodeHighlighting.canEditOtherPosts = PermissionService.getHasPermission(CodeHighlighting.branchId, 'BRANCH',
	                'BranchPermission.EDIT_OTHERS_POSTS');
    CodeHighlighting.canDeleteOwnPosts = PermissionService.getHasPermission(CodeHighlighting.branchId, 'BRANCH',
        'BranchPermission.DELETE_OWN_POSTS');
    CodeHighlighting.canDeleteOtherPosts = PermissionService.getHasPermission(CodeHighlighting.branchId, 'BRANCH',
        'BranchPermission.DELETE_OTHERS_POSTS');
}

CodeHighlighting.setupGeneralHandlers = function() {
	// handle links even if they are located on the comment's div (clicks on which we don't handle)
	$('.script-first-post').on('click', 'ol.linenums li .review-header a', function () {
		window.location = $(this).attr('href');
	});
}

/**
 * Initializes handlers for 'Add comment' action
 */
CodeHighlighting.setupAddCommentFormHandlers = function () {
    $('.script-first-post').on('click', 'input:button[name=cancel-add]', function (event) {
        event.stopPropagation();
        CodeHighlighting.removeCommentForm();
    });
	
	// don't handle clicks on comments
	$('.script-first-post').on('click', 'ol.linenums li div.review-group', function () {
		return false;
	});	
	
    $('.script-first-post').on('click', 'ol.linenums li', function () {
        var addCommentForm = $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID);
        if (addCommentForm.length == 0) {
            var lineNumber = $(this).index() + 1;
			// display form before first comment
			var elementBeforeForm = $(this).find('div.review-container:last');
			var hasComments = (elementBeforeForm.length != 0);
			if (!hasComments) {
				elementBeforeForm = $(this).find('span:last');
			}
			CodeHighlighting.showAddCommentForm(elementBeforeForm, lineNumber);
			if (hasComments) {
				$(document).scrollTop($('#' + CodeHighlighting.ADD_COMMENT_FORM_ID).offset().top - 150);
			}
        }
        
    });	
	$('.script-first-post').on('click', 'ol.linenums li input[name="' + CodeHighlighting.ADD_COMMENT_BUTTON_NAME + '"]', function () {
        var addCommentForm = $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID);
        if (addCommentForm.length == 0) {
			var line = $(this).closest('li');
            var lineNumber = $(line).index() + 1;
			// display form before first comment
			CodeHighlighting.showAddCommentForm($(line).find('div.review-container:last'), lineNumber);
        }
        
    });

    $('.script-first-post').on('click', "input:button[name=add]", function (event) {
        event.stopPropagation();

        var formContainer = $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID);
        if (Antimultipost.beingSubmitted(formContainer)) {
            return;
        }
        Antimultipost.disableSubmit(formContainer);

        
		var data = {id: 0, authorId:0, authorUsername:""};
        data.lineNumber = $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID + ' [name=lineNumber]').val();
        data.body = $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID + ' [name=body]').val();
		data.reviewId = $('#codeReviewId').val();
		
        $.post(baseUrl + '/reviewcomments/new', data)
                .success(function (data) {
                    if (data.status == 'SUCCESS') {
                        CodeHighlighting.removeCommentForm();
						CodeHighlighting.addComment(data.result)
                    } else if (data.reason == 'VALIDATION') {
                        CodeHighlighting.displayValidationErrors(data.result);
                    } else if (data.reason == 'SECURITY'){
                        jDialog.createDialog({
                            type: jDialog.alertType,
                            bodyMessage: $labelYouDontHavePermissions
                        });
                    } else if (data.reason == 'ENTITY_NOT_FOUND') {
                        jDialog.createDialog({
                            type: jDialog.alertType,
                            bodyMessage: $labelTopicWasRemoved
                        });
                    } else {
                        jDialog.createDialog({
                            type: jDialog.alertType,
                            bodyMessage: $labelUnexpectedError
                        });
                    }
                })
                .error(function (data) {
                    jDialog.createDialog({
                        type: jDialog.alertType,
                        bodyMessage: $label.topicWasRemovedOrYouDontHavePermissions
                    });
                })
                .complete(function () {
                    Antimultipost.enableSubmit(formContainer);
                });
    });
}

/**
 * Initializes handlers for 'Edit comment' actions
 */
CodeHighlighting.setupEditCommentHandlers = function() {
    $('.script-first-post').on('click', 'input:button[name=cancel-edit]', function (event) {
        event.stopPropagation();
		$('#' + CodeHighlighting.ADD_COMMENT_FORM_ID).prev().show();
        CodeHighlighting.removeCommentForm();
    });
	
	$('.script-first-post').on('click', 'div.review-container a[name=edit-review]', function() {
		var addCommentForm = $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID);
		if (addCommentForm.length == 0) {
			var reviewContainer = $(this).closest('.review-container');
			var comment = {};
			comment.id = reviewContainer.find('input[name=id]').val();
			comment.body = Utils.br2lf(reviewContainer.find('.review-body').html());
			CodeHighlighting.showEditCommentForm(reviewContainer, comment);
        }
		
		return false;
	});

    //delete comment from review
    $('.script-first-post').on('click', 'div.review-container a[name=delete-review]', function (e) {
        e.preventDefault();
        var reviewContainer = $(this).closest('.review-container');
        var commentId = reviewContainer.find('input[name=id]').val();
        var reviewId = $('input[id="codeReviewId"]').val();


        var footerContent = ' \
            <button id="remove-review-cancel" class="btn cancel">' + $labelCancel + '</button> \
            <button id="remove-review-ok" class="btn btn-primary">' + $labelOk + '</button>'

        var submitFunc = function (e) {
            e.preventDefault();
            $.ajax({
                url:baseUrl + '/reviewcomments/delete?reviewId=' + reviewId + '&commentId=' + commentId,
                type:"GET",
                success:function () {
					var lineNumber = $(reviewContainer).closest('li').index() + 1;
                    $(reviewContainer).remove();					
					if (CodeHighlighting.getNumberOfComments(lineNumber) == 0) {
						CodeHighlighting.removeReviewGroup(lineNumber);
					}
                },
                error:function () {
                    jDialog.createDialog({
                        type: jDialog.alertType,
                        bodyMessage: $labelUnexpectedError
                    });
                }
            });
            jDialog.closeDialog();
        };

        jDialog.createDialog({
            type: jDialog.confirmType,
            bodyMessage : $labelDeleteCommentConfirmation,
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

        return false;
    });

	$('.script-first-post').on('click', "input:button[name=edit]", function (event) {
        event.stopPropagation();

        var formContainer = $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID);
        if (Antimultipost.beingSubmitted(formContainer)) {
            return;
        }
        Antimultipost.disableSubmit(formContainer);

		var data = {authorId:0, authorUsername:""};
		data.id = $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID + ' [name=id]').val();
        data.lineNumber = $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID + ' [name=lineNumber]').val();
        data.body = $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID + ' [name=body]').val();
		data.branchId = $('#branchId').val();
        
        $.post(baseUrl + '/reviewcomments/edit', data)
                .success(function (data) {
                    if (data.status == 'SUCCESS') {
						CodeHighlighting.updateComment(data.result);
						CodeHighlighting.removeCommentForm();
                    } else if (data.reason == 'VALIDATION') {
                        CodeHighlighting.displayValidationErrors(data.result);
                    } else if (data.reason == 'SECURITY'){
                        jDialog.createDialog({
                            type: jDialog.alertType,
                            bodyMessage: $labelYouDontHavePermissions
                        });
                    } else if (data.reason == 'ENTITY_NOT_FOUND') {
                        jDialog.createDialog({
                            type: jDialog.alertType,
                            bodyMessage: $labelTopicWasRemoved
                        });
                    } else {
                        jDialog.createDialog({
                            type: jDialog.alertType,
                            bodyMessage: $labelUnexpectedError
                        });
                    }
                })
                .error(function (data) {
                    jDialog.createDialog({
                        type: jDialog.alertType,
                        bodyMessage: $labelUnexpectedError
                    });
                })
                .complete(function () {
                    Antimultipost.enableSubmit(formContainer);
                });
    });
}

/**
 * Build HTML piece for review comment
 * @param comment code review comment
 * @return div (string) with comment data
 */
CodeHighlighting.getCommentHtml = function (comment) {
	var editButtonHtml = '';
    var deleteButtonHtml = '';
	if ((CodeHighlighting.currentUserId != comment.authorId
			&& CodeHighlighting.canEditOtherPosts) 
		|| (CodeHighlighting.currentUserId == comment.authorId 
			&& CodeHighlighting.canEditOwnPosts)) {
		editButtonHtml = '<a href="" name=edit-review>' + $labelEdit + '</a>';
	}
    if ((CodeHighlighting.currentUserId != comment.authorId 
    		&& CodeHighlighting.canDeleteOtherPosts)
        || (CodeHighlighting.currentUserId == comment.authorId
        	&& CodeHighlighting.canDeleteOwnPosts)) {
        deleteButtonHtml = '<a href="" rel="'+$labelDeleteCommentConfirmation+'" name=delete-review>' + $labelDelete + '</a>';
    }
    var result =
            '<div class="review-container"> '
				+ '<input type=hidden name=id value="' + comment.id + '"/>'
				+ '<input type=hidden name=authorId value="' + comment.authorId + '"/>'
                + '<div class="left-aligned">'
                    + '<img class="review-avatar-img" src="' + baseUrl + '/users/' + comment.authorId + '/avatar"/>'
                + '</div>'
                + '<div class="review-content">'
				    + '<div class="review-buttons" style="float:right">'
				    	+ editButtonHtml
                        +'&nbsp;'
                        + deleteButtonHtml
				    + '</div>'
                    + '<div class="review-header">'
						+ '<a href="' + baseUrl + '/users/' + comment.authorId + '">' + Utils.htmlEncode(comment.authorUsername) + '</a>'
						+ ' ' + $labelReviewSays + ': '
                    + '</div>'
                    + '<div class="review-body">'
						+ Utils.lf2br(Utils.htmlEncode(comment.body));
                    + '</div>'
                + '</div>'
            + '</div>';
    return result;
}

/**
 * Build HTML piece for add comment form
 * @param submitButtonTitle title of submit button
 * @param action name of action (name being added to button names)
 * @param lineNumber number of line where form will be placed
 * @param comment data to fill form if defined
 * @return string with HTML piece for the form
 */
CodeHighlighting.getCommentForm = function (submitButtonTitle, action, lineNumber, comment) {
    if (comment === undefined) {
		comment = {id:0,body:''};
	}
    var result =
            '<div id="' + CodeHighlighting.ADD_COMMENT_FORM_ID + '" >'
                + '<div class="control-group">'
                    + '<input type=hidden name=lineNumber value="' + lineNumber + '"/>'
					+ '<input type=hidden name=id value="' + comment.id + '"/>'
                    + '<textarea name="body" class="review-container-content">' + comment.body + '</textarea>'
                + '</div>'
                + '<div>'
                    + '<input type=button name="' + action + '" value="' + submitButtonTitle + '" class="btn btn-primary review-container-controls-ok"/>'
                    + '<input type=button name=cancel-' + action + ' value="' + $labelCancel + '" class="btn review-container-controls-cancel"/>'
                + '</div>'
                + '<span class="keymaps-caption">' + $labelKeymapsReview + '</span>'
            + '</div>';
    return result;
}

/**
 * Show comment form below given element
 * @param element jquery element after which show form
 * @param submitButtonTitle title of submit button
 * @param submitButtonName name of submit button element
 * @param lineNumber number of line where form will be placed
 * @param comment data to fill form if defined
 */
CodeHighlighting.showCommentForm = function(element, submitButtonTitle, submitButtonName, lineNumber, comment) {
	if (lineNumber == 0) {
		lineNumber = $(element).closest('li').index() + 1;
	}
	element.after(CodeHighlighting.getCommentForm(submitButtonTitle, submitButtonName, lineNumber, comment));
	var reviewContent = $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID + ' textarea');
    reviewContent.keydown(Keymaps.review);
    reviewContent.focus();
	
	CodeHighlighting.toggleActionButton(lineNumber);	
}

/**
 * Show 'add comment' form below given element
 * @param element jquery element after which show form
 * @param lineNumber number of line where form will be placed
 */
CodeHighlighting.showAddCommentForm = function(element, lineNumber) {
	CodeHighlighting.showCommentForm(element, $labelAdd, 'add', lineNumber);
}

/**
 * Show 'edit comment' form at the location of given element. Element will
 * be hide from the document.
 * @param element jquery element after which show form
 * @param comment data to fill form
 */
CodeHighlighting.showEditCommentForm = function(element, comment) {
	CodeHighlighting.showCommentForm(element, $labelEdit, 'edit', 0, comment);
	element.hide();
}

/**
 * Remove comment form from the page
 */
CodeHighlighting.removeCommentForm = function () {
    var form = $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID);
	var lineNumber = $(form).find('input[name=lineNumber]').val();
	form.remove();
	CodeHighlighting.toggleActionButton(lineNumber);
}

/**
 * Display error messages
 *
 * @param errors list or errors
 */
CodeHighlighting.displayValidationErrors = function (errors) {
    $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID + ' span.help-inline').remove();

    if (errors.length > 0) {
        var currentControlGroup = $('#' + CodeHighlighting.ADD_COMMENT_FORM_ID + ' [name=body]').parent();
        currentControlGroup.addClass("error");
        currentControlGroup.append('<span class="help-inline"/>');

        var errorsContainer = currentControlGroup.find('span.help-inline');
        for (var i = 0; i < errors.length; i++) {
            errorsContainer.append(errors[i].message + '<br/>');
        }
    }
}

/**
 * @param lineNumber number of code line
 * @return number of comments for this line of code
 */
CodeHighlighting.getNumberOfComments = function(lineNumber) {
	var comments = $('.script-first-post ol.linenums li:nth-child(' + lineNumber + ') div.review-container');
	return comments.length;
}

/**
 * Add div container for comments and section with 'add comment' button 
 * (if user has corresponding permission)
 * @param lineNumber number of line in code
 */
CodeHighlighting.addReviewGroup = function(lineNumber) {
	var addCommentButtonHtml = '';
	if (CodeHighlighting.canAddComents) {
		addCommentButtonHtml = '<input type="button" name="' + CodeHighlighting.ADD_COMMENT_BUTTON_NAME + '" class="btn" value="' + $labelAddReviewComment + '"/>';
	}
	var reviewGroup = 
		'<div class="review-group">' 
			+ '<div class="review-group-comments"/>'
			+ '<div class="review-group-buttons">'
				+ addCommentButtonHtml
			+ '</div>';
	$('.script-first-post ol.linenums li:nth-child(' + lineNumber + ')').append(reviewGroup);
}

/**
 * Removes container for comments (with 'add comment' button). 
 * @param lineNumber number of line in code
 */
CodeHighlighting.removeReviewGroup = function(lineNumber) {
	$('.script-first-post ol.linenums li:nth-child(' + lineNumber + ') div.review-group').remove();
}

/**
 * Showes/hides button for adding new comment
 * @param lineNumber number of line where toggle button
 */
CodeHighlighting.toggleActionButton = function(lineNumber) {
	$('.script-first-post ol.linenums li:nth-child(' + lineNumber + ') div.review-group-buttons').toggle();
}
