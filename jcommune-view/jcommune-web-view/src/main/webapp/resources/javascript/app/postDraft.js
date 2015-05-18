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

$(document).ready(function() {
    var postTextArea = $("#postBody");
    var characterCounter = 0;
    var currentSaveLength = postTextArea.val().length;
    var interval = 15000;
    var intervalId;
    var isSaved = false;
    var minDraftLen = 2;
    var lastSavingDate;
    var dateUpdateCounter = 0;
    var dateUpdateInterval;
    var prevSavedMilis = 0;
    var maxTextLength = 20000;
    var errorSpan = "<div id='bodyText-errors' class='cleared'><span class='help-inline focusToError' data-original-title=''>"
        + $labelMessageSizeValidation.replace('{min}',minDraftLen.toString()).replace('{max}',maxTextLength.toString()) + "</span></div>";

    $("<span id='counter' class='keymaps-caption pull-right'></span>").insertAfter("#editorBbCodeDiv");
    if (!isNaN($("#savedMilis").val()) && parseInt($("#draftId").val()) != 0) {
        prevSavedMilis = parseInt($("#savedMilis").val());
        var difSeconds = Math.floor((new Date().getTime() - prevSavedMilis)/1000);
        if (difSeconds <= 60) {
            dateUpdateCounter = Math.floor(difSeconds/5);
            startFiveSecondsInterval();
        } else if (difSeconds > 60 && difSeconds <= 3600) {
            dateUpdateCounter = Math.floor(difSeconds/60);
            $("#counter").text(composeMinuteLabel(dateUpdateCounter));
            dateUpdateInterval = setInterval(function() {
                minuteIntervalHandler();
            }, 60000);
        } else if (difSeconds > 3600 && difSeconds <= 86400) {
            dateUpdateCounter = Math.floor(difSeconds/3600);
            $("#counter").text(composeHourLabel(dateUpdateCounter));
            dateUpdateInterval = setInterval(function() {
                hourIntervalHandler();
            }, 3600000);
        } else {
            printDate(new Date(prevSavedMilis));
        }
    }

    postTextArea.bind('keyup change', function() {
        updateValidationErrors();
        isSaved = false;
        startTimer();
        if (postTextArea.val().length == 0 && currentSaveLength != 0) {
            currentSaveLength = 0;
            deleteDraft();
            characterCounter = 0;
        }
    });

    postTextArea.blur(function () {
        setTimeout(saveEvent, 1000);
    });

    $(".btn-toolbar").mouseup(function () {
        isSaved = false;
        startTimer();
    });

    /**
     * Starts timer of saving draft
     */
    function startTimer() {
        if (postTextArea.val().length >= minDraftLen && !intervalId) {
            intervalId = setInterval(function () {
                saveEvent();
            }, interval);
        }
    }

    /**
     * Checks if it is necessary to save draft and saves if necessary
     */
    function saveEvent() {
        currentSaveLength = postTextArea.val().length;
        if (currentSaveLength >= minDraftLen && !isSaved) {
            saveDraft();
        }
    }

    /**
     * Sends request for saving draft
     */
    function saveDraft() {
        //should be set here to prevent race condition
        isSaved = true;
        var content = postTextArea.val();
        var topicId = $("#topicId").val();
        var data = {bodyText: content, topicId: topicId};
        $.ajax({
            url: baseUrl + "/posts/savedraft",
            type: 'POST',
            contentType: "application/json",
            timeout: 15000,
            data: JSON.stringify(data),
            success: function(resp) {
                if (resp.status == 'SUCCESS') {
                    $("#draftId").val(resp.result);
                    lastSavingDate = new Date();
                    dateUpdateCounter = 0;
                    clearInterval(dateUpdateInterval);
                    startFiveSecondsInterval();
                } else {
                    isSaved = false;
                    updateValidationErrors();
                }
            },
            error: function (jqHXHR, status, e) {
                isSaved = false;
                if (status == 'timeout' || jqHXHR.status == 0) {
                    clearInterval(intervalId);
                    if (jqHXHR.status == 0) {
                        //Need it because firefox makes no difference between refused connection and
                        //aborted request
                        setTimeout(showConnectionErrorPopUp, 3000);
                    } else {
                        showConnectionErrorPopUp();
                    }

                }
            }
        });
    }

    function showConnectionErrorPopUp() {
        jDialog.createDialog({
            type: jDialog.alertType,
            bodyMessage: $labelConnectionLost
        });
    }

    /**
     * Sends request for deletion draft
     */
    function deleteDraft() {
        var draftId = parseInt($("#draftId").val());
        if (draftId != 0) {
            $.ajax({
                url: baseUrl + "/posts/" + draftId + "/delete",
                success: function() {
                    $("#counter").text("");
                    clearInterval(dateUpdateInterval);
                }
            })
        }
    }

    /**
     * Handler for update label "Saved xx seconds ago" every 5 seconds
     */
    function fiveSecondsIntervalHandler() {
        var counterSpan = $("#counter");
        if (dateUpdateCounter == 11) {
            clearInterval(dateUpdateInterval);
            dateUpdateCounter = 1;
            counterSpan.text(composeMinuteLabel(dateUpdateCounter));
            dateUpdateInterval = setInterval(function() {
                minuteIntervalHandler();
            }, 60000);
            return;
        } else {
            dateUpdateCounter += 1;
            counterSpan.text(composeSecondsLabel(dateUpdateCounter*5));
        }

    }

    /**
     * Handler for update label "Saved xx minutes ago" every minute
     */
    function minuteIntervalHandler() {
        var counterSpan = $("#counter");
        dateUpdateCounter += 1;
        if (dateUpdateCounter == 60) {
            clearInterval(dateUpdateInterval);
            dateUpdateCounter = 1;
            counterSpan.text(composeHourLabel(dateUpdateCounter));
            dateUpdateInterval = setInterval(function() {
                hourIntervalHandler();
            }, 3600000);
        }
        else {
            counterSpan.text(composeMinuteLabel(dateUpdateCounter));
        }
    }

    /**
     * Handler for update label "Saved xx hours ago" every hour
     */
    function hourIntervalHandler() {
        var counterSpan = $("#counter");
        dateUpdateCounter += 1;
        if (dateUpdateCounter == 24) {
            printDate(lastSavingDate);
            clearInterval(dateUpdateInterval);
            dateUpdateCounter = 0;
        } else {
            counterSpan.text(composeHourLabel(dateUpdateCounter));
        }
    }

    /**
     * Prints saving date
     *
     * @param date saving date to print
     */
    function printDate(date) {
        $("#counter").text($labelSaved + " " + date.toLocaleDateString());
    }

    /**
     * Gets current UTC time
     *
     * @returns {Date} current UTC time
     */
    function getUtcCurrentTime() {
        var now = new Date();
        return new Date(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate(),  now.getUTCHours(), now.getUTCMinutes(), now.getUTCSeconds(), now.getUTCMilliseconds());
    }

    /**
     * Starts timer for update label
     */
    function startFiveSecondsInterval() {
        $("#counter").text(composeSecondsLabel(dateUpdateCounter*5));
        dateUpdateInterval = setInterval(function (){
            fiveSecondsIntervalHandler();
        }, 5000);
    }

    /**
     * Highlights textarea and prints error message if validation error occurs
     * or clears highlight and validation messages
     */
    function updateValidationErrors() {
        $(".control-group").removeClass("error");
        $("#bodyText-errors").remove();
        $(".focusToError").remove();
        $(".help-inline").remove();
        if (postTextArea.val().length > maxTextLength) {
            $(".control-group").addClass("error");
            $(errorSpan).insertAfter(".keymaps-caption.pull-left");
        }
    }

    function composeSecondsLabel(numberSeconds) {
        if (numberSeconds == 0) {
            return $labelSavedJustNow;
        }
        return $labelSaved + " " + numberSeconds.toString() + " " + $labelSeconds + " " + $labelAgo;
    }

    function composeMinuteLabel(numberMinutes) {
        var suffixGroup = numberMinutes % 10;
        switch (suffixGroup) {
            case 1: return $labelSaved + " " + numberMinutes.toString() + " " + $labelMinute
                + $labelMinute1Suffix + " " + $labelAgo;
            case 2:case 3:case 4: return $labelSaved + " " + numberMinutes.toString() + " " + $labelMinute
            + $labelMinutes24Suffix + " " + $labelAgo;
            default: return  $labelSaved + " " + numberMinutes.toString() + " " + $labelMinute
                + $labelMinutesMoreThan4Suffix + " " + $labelAgo;
        }
    }

    function composeHourLabel(numberHours) {
        var suffixGroup = numberHours % 10;
        switch (suffixGroup) {
            case 1: return $labelSaved + " " + numberHours.toString() + " " + $labelHour
                + $labelHours1Suffix + " " + $labelAgo;
            case 2:case 3:case 4: return $labelSaved + " " + numberHours.toString() + " " + $labelHour
                + $labelHours24Suffix + " " + $labelAgo;
            default: return $labelSaved + " " + numberHours.toString() + " " + $labelHour
                + $labelHoursMoreThan4Suffix + " " + $labelAgo;
        }
    }
});
