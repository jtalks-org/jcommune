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
    $('.comment-prompt').click(function (e) {
        e.preventDefault();
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
        window.console.log(this);
        $(this).parent().prev(".comment-list").children(".togglable").toggle();
        var buttons = $(this).parent().children(".togglable");
        buttons.toggle();
        buttons.focus();
        var thirdComment = $(this).parent().prev(".comment-list").children(".hiddenBorder");
        console.log(thirdComment);
        if (thirdComment.hasClass("bordered")) {
            thirdComment.removeClass("bordered");
        } else {
            thirdComment.addClass("bordered");
        }
    });
});

$(document).mouseup(function(e) {
    if (!($(e.target).hasClass("comment-submit") || $(e.target).hasClass("comment-textarea"))) {
        $(".comment-container").each(function() {
            console.log(this);
            if (!$(this).children(".comment-textarea").val()) {
                $(this).hide();
                $(this).parent().children(".comment-prompt").show();
            }
        });
    }
});