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
var pollEditFormVisible = true;
var editFormElement, previewFormElement, multipleButtonElement;

$(document).ready(function () {
    init("editPoll", "previewPoll", "multipleBtn")
});

function init(pollEditFormId, pollPreviewFormId, multipleBtnId) {
    editFormElement = document.getElementById(pollEditFormId);
    previewFormElement = document.getElementById(pollPreviewFormId);
    multipleButtonElement = document.getElementById(multipleBtnId);
}

function SwitchPoll() {
    pollEditFormVisible = !pollEditFormVisible;
    if (pollEditFormVisible) { // exit preview
        editFormElement.style.display = "";
        previewFormElement.style.display = "none";
    }
    else { // enter preview
        pollPreview(pollTitle.value, pollItems.value, datepicker.value);
        previewFormElement.style.display = "";
        editFormElement.style.display = "none";
    }
}


function pollPreview(pollTitleValue, pollItemsValue, endingDateValue) {
    var title = prepareTitle(pollTitleValue, endingDateValue);
    var items = prepareItems(pollItemsValue);
    previewFormElement.innerHTML = title + items;
}

function prepareTitle(title, date) {

    var result;
    if (date == "") {
        result = "<h3>" + title + "</h3><br>";
    } else {
        result = $labelPollTitleWithEnding.replace("{0}", title);
        result = result.replace("{1}", date);
        result = "<h3>" + result + "</h3><br>";
    }

    return result;
}

/**
 *
 * @param items
 * @return {*}
 */
function prepareItems(items) {
    var result;
    //"normalize" line endings
    result = items.replace(/(?:\r\n|\r)+/g, "\n");
    result = trim(result);
    result = result.split("\n");
    return stringItemsArrayToHtmlItems(result);
}

/**
 * Remove multiple, leading or trailing spaces
 *
 * @param s
 * @return {*}
 */
function trim(s) {
    s = s.replace(/(^\s*)|(\s*$)/gi, "");
    s = s.replace(/[ ]{2,}/gi, " ");
    s = s.replace(/\n /, "\n");
    s = s.replace(/\s\s*$/gm, "");
    return s;
}

function stringItemsArrayToHtmlItems(items) {

    var result = "";

    if (items.length == 1 && items[0] == "") {
        return result;
    }

    var radioInputBegin = "<input type='radio' name='radioGroup' value='";
    var checkboxInputBegin = "<input type='checkbox' name='radioGroup' value='";
    var inputEnd = "'/>";
    var br = "<br>";
    var isMultiple = document.getElementById('multipleBtn').checked;
    if (isMultiple) {
        for (var i = 0; i < items.length; i++) {
            items[i] = checkboxInputBegin + items[i] + inputEnd + items[i] + br;
            result += items[i];
        }
    } else {
        for (var i = 0; i < items.length; i++) {
            items[i] = radioInputBegin + items[i] + inputEnd + items[i] + br;
            result += items[i];
        }
    }

    result += "<input type='button' value='" + $labelPollVote + "'/>";

    return result;
}

