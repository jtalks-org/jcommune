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
var Utils = {};

function quote(postId, index) {
	var callback = function (text) {
        $('#post').focus();
        console.log(text);
        var answer = $('#postBody');
        answer.focus();
        if (answer) {
            answer.val(answer.val() + text);
        }

    }

    $.ajax({
        url: baseUrl + '/posts/' + postId + '/quote',
        type: 'POST',
        data: {
            selection: getSelectedPostText(index)
        },
        success: function (data) {
            callback(data.result);
        },
        error: function () {
            callback('');
        }
    });
}

function getSelectedPostText(index) {
    var txt = '';
    if (window.getSelection && isRangeInPost(window.getSelection().getRangeAt(0)) && isSelectedPostQuoted(index)) {
        txt = window.getSelection().toString();
    }
    else if (document.selection && isRangeInPost(document.selection.createRange()) && isSelectedPostQuoted(index)) {
        txt = document.selection.createRange().text;
    }
    return txt;
}

/**
 * Check is selected range in post
 */
function isRangeInPost(range) {
    return range.startContainer.parentNode.classList.contains("post-content-body");
}

/**
 * Checks if "quote" button pressed on selected post
 * @param index index of post on which "quote" button was pressed
 */
function isSelectedPostQuoted(index) {
    return $(window.getSelection().getRangeAt(0).startContainer).closest('.post').length == index;
}

/**
 * Encodes given string by escaping special HTML characters
 *
 * @param s string to be encoded
 */
Utils.htmlEncode = function (s) {
    return $('<div/>').text(s).html();
};

/**
 * Do focus to element
 *
 * @param target selector of element to focus
 */
Utils.focusFirstEl = function (target) {
    $(target).focus();
}

/**
 * Replaces all \n characters by <br> tags. Used for review comments.
 *
 * @param s string where perform replacing
 */
Utils.lf2br = function (s) {
    return s.replace(/\n/g, "<br>");
}

/**
 * Replaces all \<br> tags by \n characters. Used for review comments.
 *
 * @param s string where perform replacing
 */
Utils.br2lf = function (s) {
    return s.replace(/<br>/gi, "\n");
}

/**
 * Create form field with given label(placeholder), id, type
 */
Utils.createFormElement = function (label, id, type, cls) {
    var elementHtml = ' \
        <div class="control-group"> \
            <div class="controls"> \
                <input type="' + type + '" id="' + id + '" name="' + id + '" placeholder="' + label + '" class="input-xlarge ' + cls + '" /> \
            </div> \
        </div> \
    ';

    return elementHtml;
}
