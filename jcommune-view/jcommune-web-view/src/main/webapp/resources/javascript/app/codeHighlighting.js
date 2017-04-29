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
 * Called when document rendering is completed. Highlight code and
 * sets up handlers for code review if needed.
 */
$(document).ready(function () {
    prettyPrint(function () {
        var hasCodeReview = $('#has-code-review').val();
        if (hasCodeReview == 'true') {
        	var codeHighlighting = new CodeHighlighting();
        	codeHighlighting.initializeVariables();
            codeHighlighting.displayReviewComments();
			codeHighlighting.setupGeneralHandlers();

            if (PermissionService.getHasPermission(codeHighlighting.branchId, 'BRANCH',
                    'BranchPermission.LEAVE_COMMENTS_IN_CODE_REVIEW')) {
                codeHighlighting.setupAddCommentFormHandlers();
			}
			
			codeHighlighting.setupEditCommentHandlers();
        }
    });
});

/** Namespace for this file */
function CodeHighlighting() {
	this.ADD_COMMENT_FORM_ID = 'add-comment-form';
	this.ADD_COMMENT_BUTTON_NAME = 'add-comment-button';

	/** Div container of first post. Marked with class corresponding class */
	this.firstPostContainer = null;
	/** ID of branch where we currently are */
	this.branchId = 0;
	/** ID of current user */
	this.currentUserId = 0;
	/** Indicates if current user can add comments */
	this.canAddComents = false;
	/** Indicates if current user has EDIT_OWN_POSTS permission */
	this.canEditOwnPosts = false;
	/** Indicates if current user has EDIT_OTHERS_POSTS permission */
	this.canEditOtherPosts = false;
	/** Indicates if current user has DELETE_OWN_POSTS permission */
	this.canDeleteOwnPosts = false;
	/** Indicates if current user has DELETE_OTHERS_POSTS permission */
	this.canDeleteOtherPosts = false;

	/**
	 * Initialize variables used in this scope.
	 */
	this.initializeVariables = function() {
		this.firstPostContainer = $('.script-first-post');
		this.branchId = $('#branchId').val();
		this.currentUserId = $('#userId').val();
		this.canAddComents = PermissionService.getHasPermission(this.branchId, 'BRANCH',
						'BranchPermission.LEAVE_COMMENTS_IN_CODE_REVIEW');
		this.canEditOwnPosts = PermissionService.getHasPermission(this.branchId, 'BRANCH',
		                'BranchPermission.EDIT_OWN_POSTS');
		this.canEditOtherPosts = PermissionService.getHasPermission(this.branchId, 'BRANCH',
		                'BranchPermission.EDIT_OTHERS_POSTS');
	    this.canDeleteOwnPosts = PermissionService.getHasPermission(this.branchId, 'BRANCH',
	        'BranchPermission.DELETE_OWN_POSTS');
	    this.canDeleteOtherPosts = PermissionService.getHasPermission(this.branchId, 'BRANCH',
	        'BranchPermission.DELETE_OTHERS_POSTS');
	}

	/**
	 * Get review comments for this topic from server and display them
	 * @return deffered object for AJAX request (displaying comments)
	 */
	this.displayReviewComments = function () {
		var _this = this;
	    var postId = $('#firstPostId').val();
	    return $.ajax({
	        url: baseUrl + '/reviews/' + postId,
	        type: "GET",
            beforeSend: function(request) {
                request.setRequestHeader("Accept", "application/json");
            },
	        success: function (data) {
                data.result.comments.forEach(function(comment) {
                	_this.addComment(comment);
                });
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
	 * Initializes handlers which does not depend on any permissions
	 */
	this.setupGeneralHandlers = function() {
		// handle links even if they are located on the comment's div (clicks on which we don't handle)
		this.firstPostContainer.on('click', 'ol.linenums li .review-header a', function () {
			window.location = $(this).attr('href');
		});
	}

	/**
	 * Initializes handlers for 'Add comment' action
	 */
	this.setupAddCommentFormHandlers = function () {
		var _this = this;
		this.firstPostContainer.on('click', 'input:button[name=cancel-add]', function (event) {
	        event.stopPropagation();
	        _this.removeCommentForm();
	    });

        // don't propagate click on links in codereview and comments
        this.firstPostContainer.on('click', 'ol.linenums li a', function (event) {
            event.stopPropagation();
        });
		
        this.firstPostContainer.on('click', 'ol.linenums li', function () {
            var addCommentForm = $('#' + _this.ADD_COMMENT_FORM_ID);
            if (addCommentForm.length == 0) {
                var lineNumber = $(this).index() + 1;
                // display form before first comment
                var elementBeforeForm = $(this).find('div.review-container:last');
                var hasComments = (elementBeforeForm.length != 0);
                if (!hasComments) {
                    elementBeforeForm = $(this).find('span:last');
                }
                _this.showAddCommentForm(elementBeforeForm, lineNumber);
				if (hasComments) {
					$(document).scrollTop(addCommentForm.offset().top - 150);
				}
            }
        });

		this.firstPostContainer.on('click', 'ol.linenums li input[name="' + this.ADD_COMMENT_BUTTON_NAME + '"]', function () {
	        var addCommentForm = $('#' + _this.ADD_COMMENT_FORM_ID);
	        if (addCommentForm.length == 0) {
				var line = $(this).closest('li');
	            var lineNumber = $(line).index() + 1;
				// display form before first comment
				_this.showAddCommentForm($(line).find('div.review-container:last'), lineNumber);
	        }
	    });
	
		this.firstPostContainer.on('click', "input:button[name=add]", function (event) {
	        event.stopPropagation();
	
	        var formContainer = $('#' + _this.ADD_COMMENT_FORM_ID);
	        if (Antimultipost.beingSubmitted(formContainer)) {
	            return;
	        }
	        Antimultipost.disableSubmit(formContainer);

			var data = {id: 0, authorId:0, authorUsername:"", editorId:0, editorUsername:"", modificationDate:""};
	        data.lineNumber = $('#' + _this.ADD_COMMENT_FORM_ID + ' [name=lineNumber]').val();
	        data.body = $('#' + _this.ADD_COMMENT_FORM_ID + ' [name=body]').val();
			data.postId = $('#firstPostId').val();

	        $.post(baseUrl + '/reviewcomments/new', data)
	                .success(function (data) {
	                    if (data.status == 'SUCCESS') {
	                        _this.removeCommentForm();
							_this.addComment(data.result)
	                    } else if (data.reason == 'VALIDATION') {
	                        _this.displayValidationErrors(data.result);
	                    } else if (data.reason == 'SECURITY') {
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
	this.setupEditCommentHandlers = function() {
		var _this = this;
		this.firstPostContainer.on('click', 'input:button[name=cancel-edit]', function (event) {
	        event.stopPropagation();
			$('#' + _this.ADD_COMMENT_FORM_ID).prev().show();
	        _this.removeCommentForm();
	    });
		
		this.firstPostContainer.on('click', 'div.review-container a[name=edit-review]', function() {
			var addCommentForm = $('#' + _this.ADD_COMMENT_FORM_ID);
			if (addCommentForm.length == 0) {
				var reviewContainer = $(this).closest('.review-container');
				var comment = {};
				comment.id = reviewContainer.find('input[name=id]').val();
                $.ajax({
                    url: baseUrl + '/review/comment/edit/' + comment.id,
                    type: "GET",
                    beforeSend: function(request) {
                        request.setRequestHeader("Accept", "application/json");
                    },
                    success: function (data) {
                        comment.body = data.result.body;
                        _this.showEditCommentForm(reviewContainer, comment);
                    },
                    error: function () {
                        jDialog.createDialog({
                            type: jDialog.alertType,
                            bodyMessage: $labelUnexpectedError
                        });
                    }
                });
	        }
			
			return false;
		});
	
	    //delete comment from review
		this.firstPostContainer.on('click', 'div.review-container a[name=delete-review]', function (e) {
	        e.preventDefault();
	        var reviewContainer = $(this).closest('.review-container');
	        var commentId = reviewContainer.find('input[name=id]').val();
	        var postId = $('input[id="firstPostId"]').val();
	
	
	        var footerContent = ' \
	            <button id="remove-review-cancel" class="btn cancel">' + $labelCancel + '</button> \
	            <button id="remove-review-ok" class="btn btn-primary">' + $labelOk + '</button>'

	        var submitFunc = function (e) {
	            e.preventDefault();
	            $.ajax({
	                url:baseUrl + '/reviewcomments/delete?postId=' + postId + '&commentId=' + commentId,
	                type:"GET",
                    beforeSend: function(request) {
                        request.setRequestHeader("Accept", "application/json");
                    },
	                success:function (data) {
						if (data.status == 'SUCCESS') {
							var lineNumber = $(reviewContainer).closest('li').index() + 1;
							$(reviewContainer).remove();					
							if (_this.getNumberOfComments(lineNumber) == 0) {
								_this.removeReviewGroup(lineNumber);
							}
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
	
		this.firstPostContainer.on('click', "input:button[name=edit]", function (event) {
	        event.stopPropagation();
	
	        var formContainer = $('#' + _this.ADD_COMMENT_FORM_ID);
	        if (Antimultipost.beingSubmitted(formContainer)) {
	            return;
	        }
	        Antimultipost.disableSubmit(formContainer);
	
			var data = {authorId:0, authorUsername:""};
			data.id = $('#' + _this.ADD_COMMENT_FORM_ID + ' [name=id]').val();
	        data.lineNumber = $('#' + _this.ADD_COMMENT_FORM_ID + ' [name=lineNumber]').val();
	        data.body = $('#' + _this.ADD_COMMENT_FORM_ID + ' [name=body]').val();
			data.branchId = $('#branchId').val();
	        
	        $.post(baseUrl + '/reviewcomments/edit', data)
	                .success(function (data) {
	                    if (data.status == 'SUCCESS') {
							_this.updateComment(data.result);
							_this.removeCommentForm();
	                    } else if (data.reason == 'VALIDATION') {
	                        _this.displayValidationErrors(data.result);
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
	 * Function to append comment to HTML
	 */
	this.addComment = function (comment) {
		if (this.getNumberOfComments(comment.lineNumber) == 0) {
			this.addReviewGroup(comment.lineNumber);
		}
		this.firstPostContainer.find('ol.linenums li:nth-child(' + comment.lineNumber + ') div.review-group-comments')
			.append(this.getCommentHtml(comment));
	}

	/**
	 * Function to update existing comment. Actually removes existing comment and adds new.
	 */
	this.updateComment = function (comment) {
		_this = this;
	    var reviewContainer = this.firstPostContainer.find('.review-container input[name=id][value=' + comment.id + ']').parent();
        $.ajax({
            url: baseUrl + '/review/comment/render/' + comment.id,
            type: "GET",
            beforeSend: function(request) {
                request.setRequestHeader("Accept", "application/json");
            },
            success: function (data) {
                comment.body = data.result.body;
                reviewContainer.after(_this.getCommentHtml(comment));
                reviewContainer.remove();
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
     * If comment was changed, builds HTML footer for this comment. Includes editor and modification date
     * @param comment code review comment
     * @return div (string) with footer
     */
    this.getCommentFooter = function (comment) {
        var result = '';
		if (comment.editorId !==0) {
            result = '<div class="review-footer">'
				+ $labelModified + ' '+ '<a href="' + baseUrl + '/users/' + comment.editorId + '">' + Utils.htmlEncode(comment.editorUsername) + '</a>'
				+ '<br/><span class="post-update-mark">'
					+ new Date(comment.modificationDate).toLocaleString()
				+ '</span>'
			+ '</div>';
		}
        return result;
    }

	/**
	 * Build HTML piece for review comment
	 * @param comment code review comment
	 * @return div (string) with comment data
	 */
	this.getCommentHtml = function (comment) {
		var editButtonHtml = '';
	    var deleteButtonHtml = '';
		if ((this.currentUserId != comment.authorId
				&& this.canEditOtherPosts) 
			|| (this.currentUserId == comment.authorId 
				&& this.canEditOwnPosts)) {
			editButtonHtml = '<a href="" name=edit-review>' + $labelEdit + '</a>';
		}
	    if ((this.currentUserId != comment.authorId 
	    		&& this.canDeleteOtherPosts)
	        || (this.currentUserId == comment.authorId
	        	&& this.canDeleteOwnPosts)) {
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
							+ comment.body
	                    + '</div>'
						+ this.getCommentFooter(comment)
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
	this.getCommentForm = function (submitButtonTitle, action, lineNumber, comment) {
	    if (comment === undefined) {
			comment = {id:0,body:''};
		}
	    var result =
	            '<div id="' + this.ADD_COMMENT_FORM_ID + '" >'
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
	this.showCommentForm = function(element, submitButtonTitle, submitButtonName, lineNumber, comment) {
		if (lineNumber == 0) {
			lineNumber = $(element).closest('li').index() + 1;
		}
		element.after(this.getCommentForm(submitButtonTitle, submitButtonName, lineNumber, comment));
		var reviewContent = $('#' + this.ADD_COMMENT_FORM_ID + ' textarea');
	    reviewContent.keydown(Keymaps.review);
	    reviewContent.focus();
		
		this.toggleActionButton(lineNumber);	
	}
	
	/**
	 * Show 'add comment' form below given element
	 * @param element jquery element after which show form
	 * @param lineNumber number of line where form will be placed
	 */
	this.showAddCommentForm = function(element, lineNumber) {
		this.showCommentForm(element, $labelAdd, 'add', lineNumber);
	}
	
	/**
	 * Show 'edit comment' form at the location of given element. Element will
	 * be hide from the document.
	 * @param element jquery element after which show form
	 * @param comment data to fill form
	 */
	this.showEditCommentForm = function(element, comment) {
		this.showCommentForm(element, $labelEdit, 'edit', 0, comment);
		element.hide();
	}
	
	/**
	 * Remove comment form from the page
	 */
	this.removeCommentForm = function () {
	    var form = $('#' + this.ADD_COMMENT_FORM_ID);
		var lineNumber = $(form).find('input[name=lineNumber]').val();
		form.remove();
		this.toggleActionButton(lineNumber);
	}
	
	/**
	 * Display error messages
	 *
	 * @param errors list or errors
	 */
	this.displayValidationErrors = function (errors) {
	    $('#' + this.ADD_COMMENT_FORM_ID + ' span.help-inline').remove();
	
	    if (errors.length > 0) {
	        var currentControlGroup = $('#' + this.ADD_COMMENT_FORM_ID + ' [name=body]').parent();
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
	this.getNumberOfComments = function(lineNumber) {
		var comments = $('.script-first-post ol.linenums li:nth-child(' + lineNumber + ') div.review-container');
		return comments.length;
	}
	
	/**
	 * Add div container for comments and section with 'add comment' button 
	 * (if user has corresponding permission)
	 * @param lineNumber number of line in code
	 */
	this.addReviewGroup = function(lineNumber) {
		var addCommentButtonHtml = '';
		if (this.canAddComents) {
			addCommentButtonHtml = '<input type="button" name="' + this.ADD_COMMENT_BUTTON_NAME + '" class="btn" value="' + $labelAddReviewComment + '"/>';
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
	this.removeReviewGroup = function(lineNumber) {
		this.firstPostContainer.find('ol.linenums li:nth-child(' + lineNumber + ') div.review-group').remove();
	}
	
	/**
	 * Showes/hides button for adding new comment
	 * @param lineNumber number of line where toggle button
	 */
	this.toggleActionButton = function(lineNumber) {
		this.firstPostContainer.find('ol.linenums li:nth-child(' + lineNumber + ') div.review-group-buttons').toggle();
	}
}