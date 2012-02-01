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

$(document).ready(function () {
    var action;
    if (navigator.appName.indexOf("Microsoft") != -1 ||
        navigator.appName.indexOf("Opera") != -1) {
        action = $root + '/users/IFrameAvatarpreview';
    }
    else {
        action = $root + '/users/XHRavatarpreview';
    }

    console.log('Action: %s', action);
    var uploader = new qq.FileUploaderBasic({
        button:$("#upload").get(0),
        action:action,
        multiple:false,
        allowedExtensions:['jpg', 'jpeg', 'png', 'gif'],
        sizeLimit:4194304, // max size
        onSubmit:function (id, filename) {
            console.log('File upload: %s, ID: %s', filename, id);
        },
        onProgress:function (id, filename, loaded, total) {
            console.log('Progress for file: %s, ID: %s, loaded: %s, total: %s', filename, id, loaded, total);
        },
        onComplete:function (id, filename, responseJSON) {
            console.log('File upload for file %s, id %s done with status %s', filename, id, responseJSON);
            if (responseJSON.success == "true") {
                document.getElementById('avatarPreview').setAttribute('src', responseJSON.srcPrefix
                    + responseJSON.srcImage);
                document.getElementById('avatarTempValue').setAttribute('value', responseJSON.srcImage);
            } else {
                document.getElementById('avatarPreview').setAttribute('src', "");
                document.getElementById('avatarPreview').setAttribute('alt', responseJSON.message);
            }

        },
        debug:false
    });

});

function submitForm(formName) {

    if (formName == "editProfileForm") {
        document.getElementById('avatar').setAttribute('value',
            document.getElementById('avatarTempValue').value);
    } else {
        document.getElementById('avatar').setAttribute('value', null);
    }

    document.forms[formName].submit();

}

