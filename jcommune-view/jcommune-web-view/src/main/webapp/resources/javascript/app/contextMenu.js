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

/**
 * This script provides jQuery contextMenu functionality.
 * using: jquery.contextMenu.js - context menu functionality
 *        jquery-fieldselection.js - for get textarea selection
 *        textarea-helper.js - for get caret position
 */

jQuery(document).ready(function () {
    var validOptions = [];
    var recipientField = $('#recipient');
    recipientField.autocomplete({
        source : function(request, response) {
            clearError();
            sendUserRequest(function(data) {
                if (data.result && data.result.length > 0) {
                    validOptions = data.result;
                    response(data.result);
                } else {
                    validOptions = [];
                }
            });
        },
        focus: function(e, ui) {
            $(".ui-menu-item").removeClass("custom-selected-item");
            $("#ui-active-menuitem").parent().addClass("custom-selected-item");
            $("#ui-active-menuitem").removeClass("ui-corner-all");
        }
    }).autocomplete("widget").addClass("suggestion-list");

    var mousedownHappened = false;
    var mousedownTarget;

    $("input, a").mousedown(function(e) {
        mousedownHappened = true;
        mousedownTarget = $(this);
    });

    recipientField.focusout(function() {
        validateRecipient();
        //needed because there are problems with click event after blur or focusout
        if (mousedownHappened) {
            mousedownHappened = false;
            mousedownTarget.click();
        }
    });

    function validateRecipient() {
        clearError();
        sendUserRequest(function(data) {
            if (data.result && data.result.length > 0) {
                validOptions = data.result;
            } else {
                validOptions = [];
            }
            displayValidationErrorIfRecipientIncorrect();
        });
    }

    function sendUserRequest(successHandler) {
        $.ajax({
            type: 'POST',
            url: baseUrl + '/usernames',
            async: false,
            data: {pattern: recipientField.val()},
            success: successHandler,
            error: function() {
                validOptions = [];
            }
        });
    }

    function displayValidationErrorIfRecipientIncorrect() {
        if (validOptions.indexOf(recipientField.val()) == -1 && recipientField.val() != "") {
            recipientField.parent().parent().addClass("error");
            recipientField.after(getValidationErrorMessage());
        } else {
            clearError();
        }
    }

    function clearError() {
        recipientField.parent().parent().removeClass("error");
        var errorSpan = recipientField.next();
        if (errorSpan.hasClass("help-inline")) {
            errorSpan.remove();
        }
    }

    function getValidationErrorMessage() {
        return "<span class='help-inline show focusToError'>" + $wrongRecepient + "</span>"
    }

    var baseUrl = $root;
    //saved position of '@' character, -1 mean not exist
    var atPosition = -1;

    $('#postBody').keyup(autocompleteOnChange);

    function autocompleteOnChange(e) {
        var selStart = $(e.target).getSelection().start;
        var textBeforeCaretPos = $(e.target).val().substr(0, selStart);
        //exclude situation when '@' exists in previously added username
        var lastAddedUsernamePos = textBeforeCaretPos.lastIndexOf('[/user]');
        var lastAtPos = textBeforeCaretPos.lastIndexOf('@');
        if (lastAtPos >= 0 && lastAtPos > lastAddedUsernamePos) {
            // When user clicks on the page area we add 'resetPattern' class to the textarea element.
            // Here we reset saved pattern and remove this temporary class.
            if ($(e.target).hasClass('resetPattern')) {
                resetAutocompletePattern();
                $(e.target).removeClass('resetPattern');
            }
            if (atPosition >= 0) {
                lastAtPos = atPosition;
            }
            var pattern = textBeforeCaretPos.substr(lastAtPos + 1);
            var keycodeApproved = (e.keyCode != upCode && e.keyCode != downCode
                && e.keyCode != enterCode && e.keyCode != escCode);
            // show contextMenu only if there are space or new line before @, or @ is a first symbol in post/pm
            var posApproved = (lastAtPos == 0 || textBeforeCaretPos.charAt(lastAtPos - 1) == ' '
                || textBeforeCaretPos.charAt(lastAtPos - 1) == '\n');
            if (keycodeApproved) {
                if (posApproved) {
                    atPosition = lastAtPos;
                    if (pattern.length > 0) {
                        getContextMenu(pattern, e.target);
                    }
                } else {
                    hideContextMenu();
                }
            }
            if (e.keyCode == enterCode || e.keyCode == escCode) {
                resetAutocompletePattern();
            }
        } else {
            hideContextMenu();
            resetAutocompletePattern();
        }
    }

    function resetAutocompletePattern() {
        atPosition = -1;
    }

    function getContextMenu(pattern, contextMenuTarget) {
        $.ajax({
            type: 'POST',
            url: baseUrl + '/usernames',
            data: {pattern: pattern},
            success: function (data) {
                if (data.result && data.result.length > 0) {
                    var items = {};
                    $.each(data.result, function (key, username) {
                        username = escapeHtml(username);
                        items[username] = {name: username};
                    });
                    createContextMenu(contextMenuTarget, items);
                } else {
                    hideContextMenu();
                }
            }
        });
    }

    function escapeHtml(unsafe) {
        return unsafe
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    function escapeHtmlReverse(safe) {
        return safe
            .replace(/&amp;/g, "&")
            .replace(/&lt;/g, "<")
            .replace(/&gt;/g, ">")
            .replace(/&quot;/g, "\"")
            .replace(/&#039;/g, "'");
    }

    function hideContextMenu() {
        $.contextMenu('destroy');
    }

    function createContextMenu(contextMenuTarget, items) {
        hideContextMenu();
        $.contextMenu({
            selector: '#' + contextMenuTarget.id,
            trigger: 'none',
            className: 'autocompleteContextMenu',
            callback: function (username, options) {
                var selection = $(contextMenuTarget).getSelection();
                var textBeforeCaretPos = $(contextMenuTarget).val().substr(0, selection.start);
                var lastAtPos = (atPosition >= 0 ? atPosition : textBeforeCaretPos.lastIndexOf('@'));
                username = escapeHtmlReverse(username);
                contextMenuTarget.value = contextMenuTarget.value.slice(0, lastAtPos) + '[user]' + username + '[/user]'
                    + contextMenuTarget.value.slice(selection.end);
                hideContextMenu();
                resetAutocompletePattern();
            },
            items: items
        });
        if ($.browser.mozilla) {
            setTimeout(function () {
                showContextMenu(contextMenuTarget);
            }, 0);
        } else {
            showContextMenu(contextMenuTarget);
        }
    }

    function showContextMenu(el) {
        //context menu coordinates
        var xPos;
        var yPos;
        var rowHeight = 24;
        var offsetMenuInTextArea = $(el).textareaHelper('caretPos');
        if ($.browser.opera) {
            xPos = offsetMenuInTextArea.left;
            yPos = offsetMenuInTextArea.top + rowHeight;
        } else {
            var textAreaOffset = $(el).offset();
            xPos = textAreaOffset.left + offsetMenuInTextArea.left;
            yPos = textAreaOffset.top + offsetMenuInTextArea.top + rowHeight;
        }

        $('#' + el.id).contextMenu({x: xPos, y: yPos});
    }

});
