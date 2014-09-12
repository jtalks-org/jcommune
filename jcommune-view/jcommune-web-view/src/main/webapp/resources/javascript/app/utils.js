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

function quote(postId, postNumber) {
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
            selection: getSelectedPostText(postNumber)
        },
        success: function (data) {
            callback(data.result);
        },
        error: function () {
            callback('');
        }
    });
}

function getSelectedPostText(postNumber) {
    var txt = '';
    if (window.getSelection) {
        if (window.getSelection().toString().length > 0 && isRangeInPost(window.getSelection().getRangeAt(0))
            && isSelectedPostQuoted(postNumber)) {
            txt = window.getSelection().toString();
        }
    }
    else if (document.selection) {
        if (isRangeInPost(document.selection.createRange()) && isSelectedPostQuoted(postNumber)) {
            txt = document.selection.createRange().text;
        }
    }
    return txt;
}

/**
 * Checks if selected document fragment is a part of the post content.
 * @param {Range} range Range object which represent current selection.
 * @return {boolean} <b>true</b> if if selected document fragment is a part of the post content
 *                   <b>false</b> otherwise.
 */
function isRangeInPost(range) {
    return $(range.startContainer).closest(".post-content-body").length > 0;
}

/**
 * Checks if "quote" button pressed on the post which was selected.
 * @param {Number} postNumber number of the post on the page which "quote" button was pressed.
 * @return {boolean} <b>true</> if selected text is a part of the post which will be quoted
 *                   <b>false</b> otherwise.
 */
function isSelectedPostQuoted(postNumber) {
    return $(window.getSelection().getRangeAt(0).startContainer).closest('.post').prevAll().length == postNumber;
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
 * Create form field with given label(placeholder), id, type, class and style.
 */
Utils.createFormElement = function (label, id, type, cls, style) {
    var elementHtml = ' \
        <div class="control-group"> \
            <div class="controls"> \
                <input type="' + type + '" id="' + id + '" name="' + id + '" placeholder="' + label + '" class="input-xlarge ' + cls + '" style="'+ style +'"  /> \
            </div> \
        </div> \
    ';

    return elementHtml;
}

/**
 * Handling "onError" event for images if it's can't loaded. Invoke in config kefirbb.xml for [img] bbtag.
 * */
function imgError(image) {
    var imageDefault = window.location.protocol + "//" + window.location.host + "/resources/images/noimage.jpg";
    image.src = imageDefault;
    image.className = "thumbnail-default";
    image.parentNode.href = imageDefault;
    image.onerror = "";
}