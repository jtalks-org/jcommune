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
        var footerContent = ' \
            <button id="remove-avatar-cancel" class="btn">' + $labelCancel + '</button> \
            <button id="remove-avatar-ok" class="btn btn-primary">' + $labelOk + '</button>';

        var submitFunc = function (e) {
            e.preventDefault();
            $.getJSON($root + "/defaultAvatar", function (responseJSON) {
                $('#avatarPreview').attr('src', responseJSON.srcPrefix + responseJSON.srcImage);
                $('#avatar').val(responseJSON.srcImage);
            });
            jDialog.closeDialog();
        };

        jDialog.createDialog({
            type: jDialog.confirmType,
            bodyMessage : $labelDeleteAvatarConfirmation,
            firstFocus : false,
            footerContent: footerContent,
            maxWidth: 300,
            tabNavigation: ['#remove-avatar-ok','#remove-avatar-cancel'],
            handlers: {
                '#remove-avatar-ok': {'click': submitFunc},
                '#remove-avatar-cancel': {'static':'close'}
            }
        });

        $('#remove-avatar-ok').focus();

    });

    //avatar uploading handler

    //defined the URL for appropriate avatar processing depending on client browser:
    // Opera, IE - multipart file using iFrame
    // Chrome, Opera - byte [] using XHR
    var     action;
    //this parameter tells to valums file uploader the appropriate content type
    //if encoding != multipart, it will use "application/octet-stream" content type
    //otherwise it will use "multipart/form-data"
    var encoding = "not-multipart";
    if (navigator.appName.indexOf("Microsoft") != -1 ||
        navigator.appName.indexOf("Opera") != -1) {
        action = $root + '/users/IFrameAvatarpreview';
        encoding = "multipart";
    }
    else {
        action = $root + '/users/XHRavatarpreview';
    }

    var uploader = new qq.FileUploaderBasic({
        button: $("#upload").get(0),
        //server side uploading handler
        action: action,
        //
        encoding: encoding,
        //is multiple file upload available
        multiple: false,
        onSubmit: function (id, filename) {
        },
        onProgress: function (id, filename, loaded, total) {
        },
        onComplete: function (id, filename, responseJSON) {
            // response is empty when response status is not 200
		    if (jQuery.isEmptyObject(responseJSON)) {
				return;
			}
            //If the picture has been replaced by alternative text in the previous avatar uploading,
            //we need to restore it, because the next uploading may be successful.
            if (!$('#avatarPreview').length) {
                $('#avatarPreviewContainer').empty();
                $('#avatarPreviewContainer')
                    .append('<img id="avatarPreview" src="data:image/jpeg;base64,${editedUser.avatar}" alt="" />');
            }
            //
            if (responseJSON.status == "SUCCESS") {
                //if server side avatar uploading successful  a processed image displayed
                $('#avatarPreview').attr('src', responseJSON.srcPrefix + responseJSON.srcImage);
                //
                $('#avatar').attr('value', responseJSON.srcImage);
            } else {
                // display error message
            	jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: responseJSON.result
                });
            }

        },
        onError: function(id, filename, xhr) {
        	if (xhr.status == 413) {
        		jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: $labelImageWrongSizeJs
                });
				return false;
        	}
        },
        debug: false,
        messages: {
            emptyError: $fileIsEmpty
        }
    });

});

/**
 * Remove the component and replace it with alternate text.
 */
function showAlt() {
    $(this).replaceWith(this.alt);
};

/**
 * Remove the component and replace it with alternate text
 * for given selector.
 *
 * @param selector selector for component, which we should replace
 */
function addShowAlt(selector) {
    $(selector).error(showAlt).attr("src", $(selector).src);
};

