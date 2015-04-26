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

    $("<span id='counter' class='keymaps-caption'></span>").insertAfter("#postBody");

    if (parseInt($("#draftId").val()) != 0) {
        prevSavedMilis = parseInt($("#savedMilis").val()) - (new Date().getTimezoneOffset() * 60000);
        var difSeconds = Math.floor((new Date().getTime() - prevSavedMilis)/1000);
        console.log("prevSavedMilis " + prevSavedMilis);
        console.log("offset " + new Date().getTimezoneOffset());
        console.log("difSeconds" + difSeconds);
        console.log("now ~ " + new Date().getTime());
        if (difSeconds <= 60) {
            console.log("difSeconds < 60");
            dateUpdateCounter = Math.floor(difSeconds/5);
            fiveSecondsIntervalHandler();
        } else if (difSeconds > 60 && difSeconds <= 3600) {
            console.log("difSeconds between 60 and 3600");
            dateUpdateCounter = Math.floor(difSeconds/60);
            $("#counter").text("Saved " + dateUpdateCounter.toString() + " minutes ago");
            dateUpdateInterval = setInterval(function() {
                minuteIntervalHandler();
            }, 60000);
        } else if (difSeconds > 3600 && difSeconds <= 86400) {
            console.log("difSeconds between 3600 and 86400");
            dateUpdateCounter = Math.floor(difSeconds/3600);
            $("#counter").text("Saved " + dateUpdateCounter.toString() + " hours ago");
            dateUpdateInterval = setInterval(function() {
                hourIntervalHandler();
            }, 3600000);
        } else {
            printDate(new Date(prevSavedMilis));
        }
    }

    postTextArea.bind('keyup change', function() {
        startTimer();
        console.log("trigger");
        isSaved = false;
        console.log("is saved " + isSaved);
        if (postTextArea.val().length == 0 && currentSaveLength != 0) {
            console.log(currentSaveLength);
            currentSaveLength = 0;
            deleteDraft();
            characterCounter = 0;
        }
    });

    postTextArea.blur(function () {
       saveEvent();
    });

    $(".btn-toolbar").click(function () {
        isSaved = false;
        startTimer();
    });

    function startTimer() {
        if (currentSaveLength >= minDraftLen && !intervalId) {
            intervalId = setInterval(function () {
                saveEvent();
            }, interval);
        }
    }

    function saveEvent() {
        console.log("is saved " + isSaved);
        currentSaveLength = postTextArea.val().length;
        if (currentSaveLength >= minDraftLen && !isSaved) {
            saveDraft();
        }
    }

    function saveDraft() {
        var content = postTextArea.val();
        var topicId = $("#topicId").val();
        var data = {bodyText: content, topicId: topicId};
        $.ajax({
            url: baseUrl + "/posts/savedraft",
            type: 'POST',
            contentType: "application/json",
            data: JSON.stringify(data),
            success: function(resp) {
                $("#draftId").val(resp.result);
                isSaved = true;
                lastSavingDate = new Date();
                dateUpdateCounter = 0;
                clearInterval(dateUpdateInterval);
                fiveSecondsIntervalHandler();
                console.log("SUCCESS");
            }
        });
    }

    function deleteDraft() {
        var draftId = parseInt($("#draftId").val());
        if (draftId != 0) {
            $.ajax({
                url: baseUrl + "/posts/" + draftId + "/delete",
                success: function() {
                    console.log("SUCCESS");
                }
            })
        }
    }

    function fiveSecondsIntervalHandler() {
        console.log("fiveSecondsIntervalHandler");
        var counterSpan = $("#counter");
        if (dateUpdateCounter == 0) {
            counterSpan.text("Saved just now");
            dateUpdateCounter += 1;
            dateUpdateInterval = setInterval(function (){
                fiveSecondsIntervalHandler();
            }, 5000);
        } else if (dateUpdateCounter == 12) {
            counterSpan.text("Saved minute ago");
            clearInterval(dateUpdateInterval);
            dateUpdateCounter = 1;
            dateUpdateInterval = setInterval(function() {
                minuteIntervalHandler();
            }, 60000);
            return;
        } else {
            counterSpan.text("Saved " + (dateUpdateCounter * 5).toString() + " seconds ago");
            dateUpdateCounter += 1;
        }

    }

    function minuteIntervalHandler() {
        console.log("minuteIntervalHandler");
        var counterSpan = $("#counter");
        dateUpdateCounter += 1;
        if (dateUpdateCounter == 60) {
            counterSpan.text("Saved hour ago");
            clearInterval(dateUpdateInterval);
            dateUpdateCounter = 1;
            dateUpdateInterval = setInterval(function() {
                hourIntervalHandler();
            }, 3600000);
        }
        else {
            counterSpan.text("Saved " + dateUpdateCounter.toString() + " minutes ago");
        }
    }

    function hourIntervalHandler() {
        console.log("hourIntervalHandler");
        var counterSpan = $("#counter");
        dateUpdateCounter += 1;
        if (dateUpdateCounter == 24) {
            printDate(lastSavingDate);
            clearInterval(dateUpdateInterval);
            dateUpdateCounter = 0;
        } else {
            counterSpan.text("Saved " + dateUpdateCounter.toString() + " hours ago");
        }
    }

    function printDate(date) {
        $("#counter").text("Saved " + date.toDateString());
    }
});
