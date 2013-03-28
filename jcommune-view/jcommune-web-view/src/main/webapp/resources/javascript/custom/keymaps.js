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
var tabCode = 9;

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

Keymaps.registrationSubmit = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $('#signup-modal-dialog').find("#username").focus();
    }
}

Keymaps.registrationPassConfirm = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $('#signup-modal-dialog').find("#captcha").focus();
    }
}

Keymaps.signinSubmit = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $('#signin-modal-dialog').find("#j_username").focus();
    }
}

Keymaps.linksEditor = function (e) {
    if ((e.keyCode || e.charCode) == enterCode) {
        var but = $('#main-links-editor  #save-link:visible')[0]
        if (but && $(e.target).attr('id') != 'cancel-link') {
            e.preventDefault();
            but.click();
        }
    }

    if ((e.keyCode || e.charCode) == escCode) {
        var but = $('#main-links-editor #cancel-link:visible')[0]
        if (but) {
            e.preventDefault();
            but.click();
        }
    }
}

Keymaps.linksEditorRemoveButton = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $('#main-links-editor #cancel-link').focus();
    }
}

Keymaps.linksEditorCancelButton = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        if ($('#main-links-editor #remove-link:visible')[0]) {
            $('#main-links-editor #remove-link').focus();
        } else {
            $('#main-links-editor #link-title').focus();
        }
    }
}

Keymaps.linksEditorHintInput = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $('#main-links-editor #save-link').focus();
    }
}

Keymaps.linksEditorSaveButton = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $('#main-links-editor #cancel-link').focus();
    }
}

Keymaps.uploadBannerCancelButton = function (e) {
    if ((e.keyCode || e.charCode) == tabCode) {
        e.preventDefault();
        $(this).closest("form[id^=uploadBannerModal]").find('#body').focus();
    }
}

Keymaps.post = function (e) {
    if (e.ctrlKey && e.keyCode == enterCode) {
        e.preventDefault();
        $('input[type="submit"]').click();
    }
};

Keymaps.moveTopicEditor = function (e) {
    if ((e.keyCode || e.charCode) == escCode) {
        var but = $('#move-topic-editor .close:visible')[0]
        if (but) {
            e.preventDefault();
            but.click();
        }
    }
}

