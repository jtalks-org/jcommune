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
var previewFormElement, multipleButtonElement;

$(document).ready(function () {
    previewFormElement = $("#previewPoll");
    previewFormElement.hide();
    multipleButtonElement = $("#multipleChecker");

    $("#deleteEndingDate").click(function () {
        $("#datepicker").val("");
    });

    //setting proper datepicker locale, at current time there are not ukraine and spain datepicker locales,
    // so will be used only en and ru locales.
    switch($localeCode) {
        case "uk":
            $.datepicker.setDefaults($.datepicker.regional.uk)
            break;
        case "es":
            $.datepicker.setDefaults($.datepicker.regional.es)
            break;
        case "ru":
            $.datepicker.setDefaults($.datepicker.regional.ru)
            break;
        default:
            $.datepicker.setDefaults($.datepicker.regional[""])
    }
});

/**
 * exits the poll preview mode and enters the poll edit mode.
 *
 */
function exitPollPreviewMode() {
    previewFormElement.hide();
}

/**
 * Enters the poll preview mode and exits the poll edit mode
 * only if the poll data was specified by user. Otherwise exits the poll preview mode.
 *
 */
function enterPollPreviewMode() {
    if (isPollSet()) {
        previewFormElement.html(prepareTitle() + prepareItems());
        previewFormElement.show();
    } else {
        exitPollPreviewMode();
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
    var result = "<h3>" + previewFormElement.text(title).html() + " ";
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
            items[i] = checkboxInputBegin + xssProtection(items[i]) + inputEnd + xssProtection(items[i]) + br;
            result += controlWrapperLeading + items[i] + controlWrapperTrailing;
        }
    } else {
        for (var i = 0; i < items.length; i++) {
            items[i] = radioInputBegin + xssProtection(items[i]) + inputEnd + xssProtection(items[i]) + br;
            result += controlWrapperLeading + items[i] + controlWrapperTrailing;
        }
    }

    result += "<input type='button' class='btn btn-primary' value='" + $labelPollVote + "'/>";

    return result;
}

function xssProtection(value) {
    return previewFormElement.text(value).html();
}