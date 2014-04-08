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

$(function () {
    var baseUrl = $root;

    $(".editAllowedPermission").on('click', function (e) {
        e.preventDefault();
        editGroups($(this).data("branch"), $(this).data("permission"), true);
    });

    $(".editRestrictedPermission").on('click', function (e) {
        e.preventDefault();
        editGroups($(this).data("branch"), $(this).data("permission"), false);
    });

    function editGroups(branchId, permission, allowed) {
        var permissionInfo = {
            branchId: branchId,
            permissionMask: permission,
            allowed: allowed
        };

        $.ajax({
            url: baseUrl + "/branch/permissions/json",
            type: "POST",
            contentType: "application/json",
            async: false,
            data: JSON.stringify(permissionInfo),
            success: function (resp) {
                showDialog(resp.result.selectedGroups, resp.result.remainingGroups);
            },
            error: function (resp) {
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: "Failed to load group list"
                });
            }
        });
    }

    function showDialog(selectedGroups, remainingGroups) {
        var content = "<div class='two-list-selector'> <div class='pull-left from-list-container'>";
        for(var i = 0, size = selectedGroups.length; i < size ; i++){
            content += "<div><input type='checkbox'>" + selectedGroups[i].name + "</input></div>";
        }
        content += "</select></div> \
                    <div class='two-list-selector-controls'> \
                        <div><a class='btn'>→</a></div>\
                        <div><a class='btn'>←</a></div> \
                    </div>";
        content += "<div class='pull-right to-list-container'>";
        for(var i = 0, size = remainingGroups.length; i < size ; i++){
            content += "<div><input type='checkbox'>" + remainingGroups[i].name + "</input></div>";
        }
        content += "</div></div>";

        jDialog.createDialog({
            dialogId: 'mainLinksEditor',
            title: "Permissions",
            bodyContent: content,
            maxWidth: 800,
            maxHeight: 600
        });
    }
});
