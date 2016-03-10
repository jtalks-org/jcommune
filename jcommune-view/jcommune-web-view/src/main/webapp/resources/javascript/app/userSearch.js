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

var userSearch = {};

userSearch.popup = {};

userSearch.popup.show = function(message) {
    var alertError = $(".alert-error");
    alertError.show();
    alertError.find('#alertMessagePopup').text(message);

    alertError.find('.close').on('click', function() {
        alertError.hide();
    });
};

userSearch.connectionErrorCallback = function(xhr, status) {
    if (status == 'timeout' || xhr.status == 0) {
        if (xhr.status == 0) {
            // Need it because firefox makes no difference between refused connection and
            // aborted request
            setTimeout(function () {
                userSearch.popup.show($labelConnectionLostGenericError);
            }, 3000);
        } else {
            userSearch.popup.show($labelConnectionLostGenericError);
        }
    } else if (xhr.status == 403) {
        userSearch.popup.show($labelNotLoggedInError);
    }
};

userSearch.groupSelected = function(userID, groupID) {
    $.ajax({
        url: $root + "/user/" + userID + "/groups/" + groupID,
        type: 'POST',
        success: function(result) {
            if (result.status != 'SUCCESS') {
                userSearch.popup.show($labelUserGroupAddError);
            }
        }
    }).fail(userSearch.connectionErrorCallback);
};

userSearch.groupDeleted = function(userID, groupID) {
    $.ajax({
        url: $root + "/user/" + userID + "/groups/" + groupID,
        type: 'DELETE',
        success: function(result) {
            if (result.status != 'SUCCESS') {
                userSearch.popup.show($labelUserGroupDeleteError);
            }
        }
    }).fail(userSearch.connectionErrorCallback);
};

userSearch.toggleUserGroups = function(event, userID) {
    $('#user-groups-table-' + userID).toggle(0, function() {
        if($(this).is(":visible")) {
            userSearch.showUserGroups(userID);
        }
    });
};

userSearch.showUserGroups = function(userID) {
    $.get($root + "/user/" + userID + "/groups", function (result) {
        var multiSelect = $("#user-groups-table-" + userID + " .user-groups-select");
        multiSelect.val(result.result);

        // trigger changed event if chosen already applied
        if (multiSelect.next('.chosen-container').length) {
            multiSelect.trigger("chosen:updated");
            return;
        }

        var groupsSelectChosen = multiSelect.chosen({
            allow_single_deselect: true
        });

        // event for adding group
        groupsSelectChosen.on("change", function (event, params) {
            if (params.selected) {
                userSearch.groupSelected(userID, params.selected);
            }
            if (params.deselected) {
                userSearch.groupDeleted(userID, params.deselected);
            }
        });
    }).fail(userSearch.connectionErrorCallback);
};

