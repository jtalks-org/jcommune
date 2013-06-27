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
    var baseUrl = $root;

    $('#tbMsg').keyup(autocompleteOnChange);

    function autocompleteOnChange(e) {
        var selStart = $(e.target).getSelection().start;
        var textBeforePattern = $(e.target).val().substr(0, selStart);
        if (textBeforePattern.indexOf('@') >= 0) {
            var pattern = textBeforePattern.split('@').pop();
            var lastAtPos = textBeforePattern.lastIndexOf('@');
            var keycodeApproved = (e.keyCode != upCode && e.keyCode != downCode  && e.keyCode != enterCode && e.keyCode != escCode);
            var posApproved = (lastAtPos == 0 || textBeforePattern.charAt(lastAtPos - 1) == ' ');
            if (keycodeApproved) {
                if (posApproved && pattern.length > 0) {
                    getContextMenu(pattern, e.target);
                } else {
                    hideContextMenu(e.target);
                }
            }
        }else{
            hideContextMenu(e.target);
        }
    }

    function getContextMenu(pattern, el) {
        $.ajax({
            type: 'POST',
            url: baseUrl + '/usernames',
            data: {pattern: pattern},
            success: function (data) {
                if (data.result && data.result.length > 0) {
                    var items = {};
                    $.each(data.result, function(key, value) {
                        var val = value.replace(pattern, '<b>' + pattern + '</b>');
                        items[value] = {name: val};
                    });
                    createContextMenu(el, items);
                } else {
                    hideContextMenu(el);
                }
            }
        });
    }

    function hideContextMenu(el) {
        // 'destroy' doesn't remove class 'context-menu-active' from contextMenu target,
        // so we call 'hide' before him
        //(need more elegant solution, as example - rewrite some functionality of contextMenu plugin)
        if ($('.autocompleteContextMenu').size() > 0 && $(el).hasClass('context-menu-active')) {
            $(el).contextMenu('hide');
        }
        $.contextMenu('destroy');
    }

    function createContextMenu(el, items) {
        hideContextMenu(el);
        $.contextMenu({
            selector: '#' + el.id,
            trigger: 'none',
            className: 'autocompleteContextMenu',
            callback: function(key, options) {
                var selection = $(el).getSelection();
                var val = $(el).val().substr(0, selection.start);
                var lastAtPos = val.lastIndexOf("@");
                el.value = el.value.slice(0, lastAtPos) + '[user]' + key + '[/user]' + el.value.slice(selection.end);
                hideContextMenu(el);
            },
            items: items
        });
        if ($.browser.mozilla) {
            setTimeout(function() {
                showContextMenu(el);
            }, 0);
        } else {
            showContextMenu(el);
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
