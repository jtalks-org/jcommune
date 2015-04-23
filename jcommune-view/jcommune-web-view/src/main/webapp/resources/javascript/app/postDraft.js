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
});
