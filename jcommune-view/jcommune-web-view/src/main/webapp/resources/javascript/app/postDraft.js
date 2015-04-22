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
    var savingThreshold = 100;
    var characterCounter = 0;
    var prevLength, currentLength = postTextArea.val().length;
    var typingTimer;
    var doneTypingInterval = 15000;

    postTextArea.bind('keyup change', function() {
        prevLength = currentLength;
        currentLength = postTextArea.val().length;
        if (currentLength - prevLength > 0) {
            characterCounter += currentLength - prevLength;
        }
        if (characterCounter >= savingThreshold) {
            saveDraft();
            characterCounter = 0;
        }
    });

    postTextArea.keyup(function () {
        clearTimeout(typingTimer);
        typingTimer = setTimeout(function (){saveDraft()}, doneTypingInterval);
    });

    postTextArea.keydown(function () {
        clearTimeout(typingTimer);
    });

    function saveDraft() {
        clearTimeout(typingTimer);
        var content = postTextArea.val();
        var topicId = $("#topicId").val();
        var data = {bodyText: content, topicId: topicId};
        $.ajax({
            url: baseUrl + "/posts/savedraft",
            type: 'POST',
            contentType: "application/json",
            data: JSON.stringify(data),
            success: function() {
                console.log("SUCCESS");
            }
        });
    }
});
