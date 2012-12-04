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

$(document).ready(function () {
    //Code highlight
    prettyPrint(function() {
		var hasCodeReview = $('#has-code-review').val();
		if (hasCodeReview == 'true') {
			CodeHighlighting.displayReviewComments();
		}
			
	});
});

/**
 * Get review comments for this topic from server and display them
 */
CodeHighlighting.displayReviewComments = function() {
	var codeReviewId = $('#codeReviewId').val();
	$.ajax({
		url: baseUrl + '/reviews/' + codeReviewId + '/json',
		type: "GET",
		success: function(data) {
			var comments = data.result.comments;
			for (var i = 0; i < comments.length; i++) {
				$('.script-first-post ol.linenums li:nth-child(' + comments[i].lineNumber + ')')
					.append(CodeHighlighting.getCommentHtml(comments[i]));
			}
		}
	});
}

/**
 * Build HTML piece for review comment 
 * @param Code review comment 
 * @return div (string) with comment data
 */
CodeHighlighting.getCommentHtml = function(comment) {
	var result = 
		'<div class="review-container"> '
			+ '<div class="review-avatar">'
				+'<img class="review-avatar-img" src="' + baseUrl + '/users/' + comment.authorId + '/avatar"/>'
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