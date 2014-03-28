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

function deleteMessages(identifiers) {
    // add identifiers of the checked private messages for deletion
    $('[name=pmIdentifiers]').remove();

    var deleteForm = $('#deleteForm');
    var field = '<input type="hidden" name="pmIdentifiers" value="%value%">';
    for (i = 0; i < identifiers.length; i++) {
        var actualField = field.replace('%value%', identifiers[i]);
        deleteForm.append(actualField);
    }
}

/**
 * Edits the selected message. This function performs edit action
 * if and only if exactly
 */
function editMessage() {
    selectedCheckboxes = $('.checker:checked');
    if (selectedCheckboxes.size() == 1) {
        id = selectedCheckboxes[0].id;
        document.location = $root + '/pm/' + id + '/edit';
    }
}

// enable/disable delete button
function toggleButtonEnabled(id, isEnabled) {
    if (isEnabled) {
        $(id).removeAttr('disabled');
        $(id).removeClass('disabled');
    } else {
        $(id).attr('disabled', 'disabled');
        $(id).addClass('disabled');
    }
}

// setup enable/disable state of delete and edit button based on number of
// selected messages
function updateButtonsState() {
    numberOfSelectedMessages = $('.checker:checked').length;
    toggleButtonEnabled('#deleteCheckedPM', numberOfSelectedMessages > 0);
    toggleButtonEnabled('#editCheckedPM', numberOfSelectedMessages == 1);
}


$(document).ready(function () {
    //cleanup
    updateButtonsState();

    // collect checked private messages

    $("#deleteCheckedPM").each(function () {
        $(this).click(function (e) {
            e.preventDefault();
            var messages = $(".check");
            var identifiers = [];
            $.each(messages, function (index, value) {
                identifiers[index] = value.id;
            });

            if (identifiers.length > 0) {
                var deletePath = $(this)[0].href;
                var deletePmPromt = $labelDeletePmGroupConfirmation.replace('%s', identifiers.length);
                var footerContent = ' \
                    <button id="remove-pm-cancel" class="btn">' + $labelCancel + '</button> \
                    <button id="remove-pm-ok" class="btn btn-primary">' + $labelOk + '</button>';

                var submitFunc = function (e) {
                    e.preventDefault();
                    deleteMessages(identifiers);
                    var deleteForm = $('#deleteForm')[0];
                    deleteForm.action = deletePath;
                    deleteForm.submit();
                    jDialog.closeDialog();
                };

                jDialog.createDialog({
                    type: jDialog.confirmType,
                    bodyMessage: deletePmPromt,
                    firstFocus: false,
                    footerContent: footerContent,
                    maxWidth: 300,
                    tabNavigation: ['#remove-pm-ok', '#remove-pm-cancel'],
                    handlers: {
                        '#remove-pm-ok': {'click': submitFunc},
                        '#remove-pm-cancel': {'static':'close'}
                    }
                });

                $('#remove-pm-ok').focus();
            }
        });
    });
    // get private message identifier
    $('#deleteOnePM').each(function () {
        $(this).click(function (e) {
            e.preventDefault();
            var identifiers = [];
            identifiers[0] = $('#pmId').val();
            deleteMessages(identifiers);
        });
    });

    //bind edit message handler
    $('#editCheckedPM').click(function (e) {
        e.preventDefault();
        editMessage();
        return false;
    });

    /**
     * This script enables checking private
     * messages in table and highlights the checked.
     */
    $(document).ready(function () {
        var c = 0;
        $('.messages tr.mess:even').css('background', '#d4d9df');
        $('.messages tr.mess:odd').css('background', '#cdcdcd');


        $('.checker').on("click", function () {
            if ($(this).is(':checked')) {
                if (++c === $('.checker').length) {
                    $('.check_all').attr('checked', true);
                }
                ;
                $(this).closest('.mess').addClass('check');
            } else {
                --c;
                $(this).closest('.mess').removeClass('check');
                $('.check_all').attr('checked', false);
            }
            updateButtonsState();
        });

        $('.check_all').on('click', function () {
            if ($(this).is(':checked')) {
                $('.checker').attr('checked', true);
                c = $('.checker').length;
                $('.counter').text(c + ' �������');
                $('.mess').addClass('check');
            } else {
                $('.checker').attr('checked', false);
                c = 0;
                $('.mess').removeClass('check');
            }
            updateButtonsState();
        });
    });

    /**
     * Highlights currently opened PM folder
     */
    $(document).ready(function () {
        var url = document.URL.toString();
        if (url.match(/inbox/)) {
            $('#inbox_link').addClass('active');
        } else if (url.match(/outbox/)) {
            $('#outbox_link').addClass('active');
        } else if (url.match(/drafts$/)) {
            $('#draft_link').addClass('active');
        }
    });
});
