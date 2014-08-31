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

var Keymaps = {};

var escCode = 27;
var enterCode = 13;
var charMin = 60;
var charMax = 90;
var tabCode = 9;
var upCode = 38;
var downCode = 40;

Keymaps.review = function (e) {
    if (e.ctrlKey && e.keyCode == enterCode) {
        e.preventDefault();
        $(".review-container-controls-ok").click();
    }
    else if (e.keyCode == escCode) {
        e.preventDefault();
        $(".review-container-controls-cancel").click();
    }
};

Keymaps.reviewConfirmRemoveButton = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $(e.target).parents('.modal').find('.cancel').focus();
    }
}

Keymaps.reviewCancelRemoveButton = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $(e.target).parents('.modal').find('.btn-primary').focus();
    }
}

Keymaps.registrationSubmit = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $('#signup-modal-dialog').find("#username").focus();
    }
}

Keymaps.registrationPassConfirm = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $('#signup-modal-dialog').find(".captcha").focus();
    }
}

Keymaps.signinSubmit = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $('#signin-modal-dialog').find("#userName").focus();
    }
}

Keymaps.linksEditor = function (e) {
    if ((e.keyCode || e.charCode) == enterCode) {
        var but = $('#mainLinksEditor  #saveLink:visible')[0];
        var targetId = $(e.target).attr('id');

        var enterNavigation = ['linkTitle', 'linkUrl', 'linkHint', 'saveLink'];

        if ($('#mainLinksEditor #linkTitle:visible')[0]) {
            for (var i = 0; i < enterNavigation.length - 1; ++i) {
                if (targetId == enterNavigation[i]) {
                    var nextElementSelector = '#mainLinksEditor  #' + enterNavigation[i + 1];
                    e.preventDefault();
                    if($.browser.mozilla){
                        setTimeout(function(){
                            $(nextElementSelector).focus();
                        }, 0);
                    }
                    else {
                        $(nextElementSelector).focus();
                    }
                    return;
                }
            }
        }

        if (but && $(e.target).attr('id') != 'cancelLink') {
            e.preventDefault();
            if($.browser.mozilla){
                setTimeout(function(){
                    but.click();
                }, 0);
            }
            else {
                but.click();
            }
        }
    }

    if ((e.keyCode || e.charCode) == escCode) {
        var but = $('#mainLinksEditor #cancelLink:visible')[0]
        if (but) {
            e.preventDefault();
            but.click();
            $('#mainLinksEditor').focus();
        } else {
            $('#mainLinksEditor .close').click();
        }
    }
}

Keymaps.linksEditorRemoveButton = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $('#mainLinksEditor #cancelLink').focus();
    }
}

Keymaps.linksEditorCancelButton = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $('#mainLinksEditor button.close').focus();
    }
}

Keymaps.linksEditorHintInput = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $('#mainLinksEditor #saveLink').focus();
    }
}

Keymaps.linksEditorSaveButton = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $('#mainLinksEditor #cancelLink').focus();
    }
}

Keymaps.uploadBannerCancelButton = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $(this).closest("form[id^=uploadBannerModal]").find('#body').focus();
    }
}

Keymaps.moveTopicEditor = function (e) {
    if ((e.keyCode || e.charCode) == escCode) {
        var but = $('#move-topic-editor .close:visible')[0]
        if (but) {
            e.preventDefault();
            but.click();
        }
    }
}

Keymaps.defaultDialog = function (e) {
    //disable submit by enter
    if (e.keyCode == enterCode) {
        //if focus on button then do action of button, else click submit
        if (!$(e.target).hasClass('btn')) {
            e.preventDefault();
            jDialog.dialog.find('.btn-primary:first').click();
        }

    }
    if ((e.keyCode || e.charCode) == escCode) {
        jDialog.dialog.find('.close').click();
    }
}

//post,topic,pm forms
Keymaps.bbeditor = function (e) {
    if (e.ctrlKey && e.keyCode == enterCode) {
        e.preventDefault();
        if($('.keymaps-caption:visible').length > 0){
            $('form[id=topicForm]').submit();
        }
    }
    //check bb-editor toolbar
    if ($(e.target).parents('form').find('.btn-toolbar').length > 0) {
        //if editor contains button with tooltip Ctrl + <char> than click
        if (e.ctrlKey && e.keyCode >= charMin && e.keyCode <= charMax) {
            var keyVal = String.fromCharCode(e.keyCode);
            var but = $('[data-original-title$="(Ctrl+' + keyVal + ')"]')
            if (but && but.length > 0) {
                e.preventDefault();
                but.click();
            }
        }
    }
}


