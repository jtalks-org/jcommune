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

//methods to dialogs
Utils.resizeDialog = function (dialog) {
    dialog.css("margin-top", function () {
        return $(this).outerHeight() / 2 * (-1)
    });
    dialog.css("margin-left", function () {
        return $(this).outerWidth() / 2 * (-1)
    });
}

/**
 * Enable all disabled elements
 * Remove previous errors
 * Show hidden hel text
 */
function prepareDialog(dialog) {
    dialog.find('*').attr('disabled', false);
    dialog.find('._error').remove();
    dialog.find(".help-block").show();
    dialog.find('.control-group').removeClass('error');
}

/**
 * Show errors under fields with errors
 * Errors overrides help text (help text will be hidden)
 */
function showErrors(dialog, errors, idPrefix, idPostfix) {
    for (var i = 0; i < errors.length; i++) {
        var e = dialog.find('#' + idPrefix + errors[i].field + idPostfix);
        e.parent().wrap('<div class="control-group error" />');
        e.parent().find(".help-block").hide();
        e.parent().last().append('<span class="help-block _error">' + errors[i].defaultMessage + '</span>');
    }
    Utils.resizeDialog(dialog);
}

/**
 * Encodes given string by escaping special HTML characters
 * 
 * @param s string to be encoded
 */
Utils.htmlEncode = function(s)
{
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
Utils.focusFirstEl = function(target)
{
    $(target).focus();
}
