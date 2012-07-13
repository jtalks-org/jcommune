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
    //save form handler
    $('#saveChanges').click(function () {
        document.editProfileForm.submit();
    });

    //remove avatar handler
    $('#removeAvatar').click(function () {
    	$.prompt($labelDeleteAvatarConfirmation,
    	    {buttons:{ Ok:true, Cancel:false },
            persistent:false,
            submit:function (confirmed) {
               	if (confirmed) {
               		$.getJSON($root + "/defaultAvatar", function (responseJSON) {
               			document.getElementById('avatarPreview').setAttribute('src', 
               					responseJSON.srcPrefix + responseJSON.srcImage);
                        document.getElementById('avatar').setAttribute('value', responseJSON.srcImage);
                    });
                }
            }
        });
    });

    //avatar uploading handler

    //defined the URL for appropriate avatar processing depending on client browser:
    // Opera, IE - multipart file using iFrame
    // Chrome, Opera - byte [] using XHR
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
        //server side uploading handler
        action:action,
        //is multiple file upload available
        multiple:false,
        allowedExtensions:['jpg', 'jpeg', 'png', 'gif'],
        // max uploaded file size (bytes)
        sizeLimit:4194304,
        onSubmit:function (id, filename) {},
        onProgress:function (id, filename, loaded, total) {},
        onComplete:function (id, filename, responseJSON) {
            if (responseJSON.success == "true") {
                //if server side avatar uploading successful  a processed image displayed
                document.getElementById('avatarPreview').setAttribute('src', responseJSON.srcPrefix
                    + responseJSON.srcImage);
                //
                document.getElementById('avatar').setAttribute('value', responseJSON.srcImage);
            } else {
                //if server side avatar uploading error occurred instead image an error message displayed
                document.getElementById('avatarPreview').setAttribute('src', "");
                document.getElementById('avatarPreview').setAttribute('alt', responseJSON.message);
            }

        },
        debug:false
    });

});

