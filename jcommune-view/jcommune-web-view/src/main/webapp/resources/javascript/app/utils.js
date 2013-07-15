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


function quote(postId, branchId) {
    // we need a synchronous POST here so we're creating a form. Found no better way to do it(
    var form = document.createElement("form");
    form.setAttribute("action", $root + "/posts/" + postId + "/quote?branchId=" + branchId);
    form.setAttribute("method", "POST");
    var field = document.createElement("input");
    field.setAttribute("type", "hidden");
    field.setAttribute("name", "selection");
    field.setAttribute("value", getSelectedPostText());
    form.appendChild(field);
    document.body.appendChild(form);
    form.submit();
}

function getSelectedPostText() {
    var txt = '';
    if (document.getSelection) {
        txt = document.getSelection().toString();
    }
    else if (window.getSelection) {
        txt = window.getSelection().toString();
    }
    else if (document.selection) {
        txt = document.selection.createRange().text;
    }
    return txt;
}

/**
 * Encodes given string by escaping special HTML characters
 *
 * @param s string to be encoded
 */
Utils.htmlEncode = function (s) {
    var el = document.createElement("div");
    el.innerText = el.textContent = s;
    s = el.innerHTML;
    return s;
}

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
Utils.createFormElement = function(label, id, type, cls) {
    var elementHtml = ' \
        <div class="control-group"> \
            <div class="controls"> \
                <input type="' + type + '" id="' + id + '" name="' + id + '" placeholder="' + label + '" class="input-xlarge ' + cls + '" /> \
            </div> \
        </div> \
    ';

    return elementHtml;
}
