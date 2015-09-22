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

    if (typeof postTextArea.val() != 'undefined') {
        var characterCounter = 0;
        var currentSaveLength = postTextArea.val().length;
        var interval = 15000;
        var intervalId;
        var isSaved = true;
        var minDraftLen = 2;
        var lastSavingDate;
        var dateUpdateCounter = 0;
        var dateUpdateInterval;
        var prevSavedMilis = 0;
        var maxTextLength = 20000;
        var postPressed = false;
        var errorSpan = "<div id='bodyText-errors' class='cleared'><span class='help-inline focusToError' data-original-title=''>"
            + $labelMessageSizeValidation.replace('{min}', minDraftLen.toString()).replace('{max}', maxTextLength.toString()) + "</span></div>";

        $("<span id='counter' class='keymaps-caption pull-right'></span>").insertAfter("#editorBbCodeDiv");
        if (!isNaN($("#savedMilis").val()) && parseInt($("#draftId").val()) != 0) {
            prevSavedMilis = parseInt($("#savedMilis").val());
            var differenceMillis = parseInt($("#differenceMillis").val());
            var difSeconds = Math.floor(differenceMillis / 1000);
            if (difSeconds <= 60) {
                dateUpdateCounter = Math.floor(difSeconds / 5);
                startFiveSecondsInterval();
            } else if (difSeconds > 60 && difSeconds <= 3600) {
                dateUpdateCounter = Math.floor(difSeconds / 60);
                $("#counter").text(composeMinuteLabel(dateUpdateCounter));
                dateUpdateInterval = setInterval(function () {
                    minuteIntervalHandler();
                }, 60000);
            } else if (difSeconds > 3600 && difSeconds <= 86400) {
                dateUpdateCounter = Math.floor(difSeconds / 3600);
                $("#counter").text(composeHourLabel(dateUpdateCounter));
                dateUpdateInterval = setInterval(function () {
                    hourIntervalHandler();
                }, 3600000);
            } else {
                printDate(new Date(prevSavedMilis));
            }
        }

        postTextArea.bind('keyup change', function () {
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
            if (!postPressed) {
                saveEvent();
            } else {
                postPressed = false;
            }
        });


        $(window).on('unload', function () {
            saveEvent();
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
            if (currentSaveLength >= minDraftLen && !isSaved && !postPressed) {
                postPressed = false;
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
            pendingSaveRequest = $.ajax({
                url: baseUrl + "/posts/savedraft",
                type: 'POST',
                contentType: "application/json",
                timeout: 15000,
                async: false,
                data: JSON.stringify(data),
                success: function (resp) {
                    removeConnectionErrorAlert();
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
                        if (jqHXHR.status == 0) {
                            //Need it because firefox makes no difference between refused connection and
                            //aborted request
                            setTimeout(function () {
                                showConnectionErrorPopUp($labelConnectionLost);
                            }, 3000);
                        } else {
                            showConnectionErrorPopUp($labelConnectionLost);
                        }

                    } else if (jqHXHR.status == 403) {
                        showConnectionErrorPopUp($labelNotLoggedInError);
                    }
                }
            });
        }

        function removeConnectionErrorAlert() {
            $("#connectionErrorAlert").remove();
        }

        function showConnectionErrorPopUp(text) {
            removeConnectionErrorAlert();

            // for some reason standard "close" handler cause refresh of the page
            var closeButton = $("<a>").addClass('close').html("&times;").click(removeConnectionErrorAlert);
            var alert = $("<div>").attr("id", "connectionErrorAlert").addClass("alert alert-error")
                .append(closeButton)
                .append(document.createTextNode(text));

            $("form.submit-form .btn-toolbar").after(alert);
        }

        $("#post").mousedown(function (e) {
            //We need to abort save request in case if user posts message to prevent race condition
            postPressed = true;
        });

        $(".submit-form").submit(function (e) {
            //We need to abort save request in case if user posts message with hot-keys 'ctr+enter' to prevent race condition
            postPressed = true;
        });
    }
    /**
     * Sends request for deletion draft
     */
    function deleteDraft() {
        var draftId = parseInt($("#draftId").val());
        if (draftId != 0) {
            $.ajax({
                url: baseUrl + "/drafts/" + draftId + "/delete",
                success: function () {
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
            dateUpdateInterval = setInterval(function () {
                minuteIntervalHandler();
            }, 60000);
        } else {
            dateUpdateCounter += 1;
            counterSpan.text(composeSecondsLabel(dateUpdateCounter * 5));
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
            dateUpdateInterval = setInterval(function () {
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
     * Starts timer for update label
     */
    function startFiveSecondsInterval() {
        $("#counter").text(composeSecondsLabel(dateUpdateCounter * 5));
        dateUpdateInterval = setInterval(function () {
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
        var between2And4Suffix = $labelMinutes24Suffix;
        if (numberMinutes > 10 && numberMinutes < 20) {
            between2And4Suffix = $labelMinutesMoreThan4Suffix;
        }
        var oneAtTheEndSuffix = $labelMinute1Suffix;
        if (numberMinutes == 11) {
            oneAtTheEndSuffix = $label11Suffix;

        } else if (numberMinutes > 20) {
            oneAtTheEndSuffix = $labelMinutes1AtTheEndSuffix;
        }
        switch (suffixGroup) {
            case 1:
                return $labelSaved + " " + numberMinutes.toString() + " " + $labelMinute
                  + oneAtTheEndSuffix + " " + $labelAgo;
            case 2:
            case 3:
            case 4:
                return $labelSaved + " " + numberMinutes.toString() + " " + $labelMinute
                  + between2And4Suffix + " " + $labelAgo;
            default:
                return $labelSaved + " " + numberMinutes.toString() + " " + $labelMinute
                  + $labelMinutesMoreThan4Suffix + " " + $labelAgo;
        }
    }

    function composeHourLabel(numberHours) {
        var suffixGroup = numberHours % 10;
        var between2And4Suffix = $labelHours24Suffix;
        if (numberHours > 10 && numberHours < 20) {
            between2And4Suffix = $labelHoursMoreThan4Suffix;
        }
        var oneAtTheEndSuffix = $labelHours1Suffix;
        if (numberHours == 11) {
            oneAtTheEndSuffix = $label11HoursSuffix;
        } else if (numberHours > 20) {
            oneAtTheEndSuffix = $labelHours1AteTheEndSuffix;
        }
        switch (suffixGroup) {
            case 1:
                return $labelSaved + " " + numberHours.toString() + " " + $labelHour
                  + oneAtTheEndSuffix + " " + $labelAgo;
            case 2:
            case 3:
            case 4:
                return $labelSaved + " " + numberHours.toString() + " " + $labelHour
                  + between2And4Suffix + " " + $labelAgo;
            default:
                return $labelSaved + " " + numberHours.toString() + " " + $labelHour
                  + $labelHoursMoreThan4Suffix + " " + $labelAgo;
        }
    }
});
