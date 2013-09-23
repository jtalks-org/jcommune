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
var previewFormElement, multipleButtonElement;
var oldTopicTile = "";

$(document).ready(function () {
    previewFormElement = $("#previewPoll");
    previewFormElement.hide();
    multipleButtonElement = $("#multipleChecker");
    $("#preview").click(function () {
        SwitchPoll();
    });

    $("#deleteEndingDate").click(function () {
        $("#datepicker").val("");
    });

    //setting proper datepicker locale, at current time there are not ukraine and spain datepicker locales,
    // so will be used only en and ru locales.
    if ($localeCode == 'ru') {
        $.datepicker.setDefaults($.datepicker.regional['ru']);
    } else {
        $.datepicker.setDefaults($.datepicker.regional[""]);
    }
});

/**
 * Switch between poll edit and poll preview modes.
 *
 */
function SwitchPoll() {
    pollEditFormVisible = !pollEditFormVisible;
    if (!pollEditFormVisible) {  // enter preview
        oldTopicTile = $("#topicTitle").val();
        $("#topicTitle").html($("#subject").val());
        if (isPollSet()) {
            previewFormElement.html(prepareTitle() + prepareItems());
            previewFormElement.show();
        }
    } else {
        $("#topicTitle").html(oldTopicTile);
        previewFormElement.hide();
    }
}

function isPollSet() {
    return $("#pollTitle").val() || $("#pollItems").val();
}

/**
 * Prepare resulting poll title value.
 * @return resulting poll title value.
 */
function prepareTitle() {
    title = $("#pollTitle")[0].value;
    date = $("#datepicker")[0].value;
    var result = "<h3>" + title + " ";
    if (date != "") {
        result += $labelPollTitleWithEnding.replace("{0}", date);
    }

    result += "</h3><br>";

    return result;
}

/**
 * Prepare poll items.
 *
 * @return processed poll items without leading spaces and empty strings.
 */
function prepareItems() {
    var result;
    //"normalize" line endings
    result = $("#pollItems")[0].value.replace(/(?:\r\n|\r)+/g, "\n");
    result = trim(result);
    result = result.split("\n");
    return stringItemsArrayToHtmlItems(result);
}

/**
 * Remove multiple, leading or trailing spaces.
 *
 * @param s string to remove multiple, leading or trailing spaces
 * @return processed string.
 */
function trim(s) {
    s = s.replace(/(^\s*)|(\s*$)/gi, "");
    s = s.replace(/[ ]{2,}/gi, " ");
    s = s.replace(/\n /, "\n");
    s = s.replace(/\s\s*$/gm, "");
    return s;
}

/**
 * Prepare poll items string array to HTML view.
 *
 * @param items poll items string array.
 * @return {String} poll items string ready to HTML view.
 */
function stringItemsArrayToHtmlItems(items) {

    var result = "";

    if (items.length == 1 && items[0] == "") {
        return result;
    }

    var controlWrapperLeading = "<div class='control-group'>";
    var controlWrapperTrailing = "</div>";
    var radioInputBegin = "<input type='radio' name='radioGroup' value='";
    var checkboxInputBegin = "<input type='checkbox' name='radioGroup' value='";
    var inputEnd = "'/> ";
    var br = "<br>";
    var isMultiple = multipleButtonElement.is(':checked');
    if (isMultiple) {
        for (var i = 0; i < items.length; i++) {
            items[i] = checkboxInputBegin + items[i] + inputEnd + items[i] + br;
            result += controlWrapperLeading + items[i] + controlWrapperTrailing;
        }
    } else {
        for (var i = 0; i < items.length; i++) {
            items[i] = radioInputBegin + items[i] + inputEnd + items[i] + br;
            result += controlWrapperLeading + items[i] + controlWrapperTrailing;
        }
    }

    result += "<input type='button' class='btn btn-primary' value='" + $labelPollVote + "'/>";

    return result;
}