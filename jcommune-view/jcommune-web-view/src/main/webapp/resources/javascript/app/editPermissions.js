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
        showEditGroupsDialog($(this).data("branch"), $(this).data("permission"), $(this).data("permission-name"), true);
    });

    $(".editRestrictedPermission").on('click', function (e) {
        e.preventDefault();
        showEditGroupsDialog($(this).data("branch"), $(this).data("permission"), $(this).data("permission-name"), false);
    });

    /**
     * Shows dialog for set up permission groups details: User can select which groups will have the selected
     * permission from the list of the all available groups
     * @param branchId id of the branch for which permission is edited
     * @param permissionMask mask of the permission to be changed
     * @param permissionName name of the permission to be changed
     * @param allowed allow or restrict selected permission for the selected groups
     */
    function showEditGroupsDialog(branchId, permissionMask, permissionName, allowed) {
        var permissionInfo = {
            branchId: branchId,
            permissionMask: permissionMask,
            allowed: allowed
        };

        var newlyAdded = [];
        var removed = [];

        $.ajax({
            url: baseUrl + "/branch/permissions/json",
            type: "POST",
            contentType: "application/json",
            async: false,
            data: JSON.stringify(permissionInfo),
            success: function (resp) {
                showDialog(resp.result.selectedGroups, resp.result.availableGroups, permissionName, allowed);
            },
            error: function (resp) {
                jDialog.createDialog({
                    type: jDialog.alertType,
                    bodyMessage: $labelFailedToLoadGroups
                });
            }
        });

        function getGroupListHtml(idSuffix, groupsInfo, selectAllCaption) {
            var content = "<div class='group-list-caption-container'>\
                        <input type='checkbox' id='selectAll" + idSuffix + "'/>\
                        <label for='selectAll" + idSuffix + "' class='group-caption'>" + selectAllCaption + ":</label>\
                        </div><div class='group-list' id='groupList" + idSuffix + "'>";
            for(var i = 0, size = groupsInfo.length; i < size ; i++){
                var className = "group-container";
                if ((newlyAdded.indexOf(groupsInfo[i].id) != -1) || (removed.indexOf(groupsInfo[i].id) != -1)) {
                    className += " group-container-highlighted";
                }
                content += "<div id='list-item' class='" + className +"'><input type='checkbox' id='" + groupsInfo[i].id + "' /> \
                        <label data-original-title='" + groupsInfo[i].name + "' id='" + groupsInfo[i].id
                    + "' for='" + groupsInfo[i].id + "'><span id='" + groupsInfo[i].id + "'>" + groupsInfo[i].name
                    + "</span></label> </div>";
            }
            content += "</div>";
            return content;
        }

        function showDialog(selectedGroups, availableGroups, permissionName, allowed) {
            var footerContent = ' \
            <button id="cancelEditPermission" class="btn">' + $labelCancel + '</button> \
            <button id="savePermission" class="btn btn-primary">' + $labelOk + '</button>';

            var content = "<div class='two-list-selector'> <div id='left-container' class='pull-left list-container'>"
                + getGroupListHtml("Available", availableGroups, $permissionsGroupAvailable);

            content += "</div>\
                    <div class='two-list-selector-controls'> \
                        <div class='two-list-selector-control'>\
                            <a id='addSelected' class='btn'><i class='icon-chevron-right'></i></a>\
                        </div>\
                        <div class='two-list-selector-control'>\
                            <a id='addAll' class='btn'><i class='icon-forward'></i></a>\
                        </div>\
                        <div class='two-list-selector-control'>\
                            <a id='removeSelected' class='btn'><i class='icon-chevron-left'></i></a>\
                        </div> \
                        <div class='two-list-selector-control'>\
                            <a id='removeAll' class='btn'><i class='icon-backward'></i></a>\
                        </div> \
                    </div>";
            content += "<div id='right-container' class='pull-right list-container'>"
                + getGroupListHtml("AlreadyAdded", selectedGroups, $permissionsGroupAlreadyAdded);

            content += "</div></div>";

            var submitFunc = function(e) {
                e.preventDefault();
                if ((newlyAdded.length == 0) && (removed.length == 0)) {
                    jDialog.closeDialog();
                    return;
                }
                var permissionInfo = {
                    allowed: allowed,
                    branchId: branchId,
                    permissionMask: permissionMask,
                    newlyAddedGroupIds: newlyAdded,
                    removedGroupIds: removed
                };
                $.ajax ({
                    url: baseUrl + "/branch/permissions/edit",
                    type: "POST",
                    contentType: "application/json",
                    async: false,
                    data: JSON.stringify(permissionInfo),
                    success : function(resp) {
                        location.reload();
                    },
                    error : function(resp) {
                        jDialog.createDialog({
                            type: jDialog.alertType,
                            bodyMessage: $labelUnexpectedError
                        });
                    }
                });
            };


            jDialog.createDialog({
                dialogId: 'permissionsEditor',
                footerContent: footerContent,
                title: (allowed == true ? $allowPermission : $restrictPermission) + ' ' + permissionName,
                bodyContent: content,
                maxWidth: 800,
                maxHeight: 600,
                handlers: {
                    '#cancelEditPermission' : {'static':'close'},
                    '#savePermission' : {'click':submitFunc}
                }
            });

            var selectAllAvailableFunc = function() {
                $("#groupListAvailable input[type='checkbox']").each(function() {
                    $(this).prop('checked', $("#selectAllAvailable").prop('checked'));
                });
            };

            var selectAllAlreadyAddedFunc = function() {
                $("#groupListAlreadyAdded input[type='checkbox']").each(function() {
                    $(this).prop('checked', $("#selectAllAlreadyAdded").prop('checked'));
                });
            };


            $("#selectAllAvailable").bind('click', selectAllAvailableFunc);
            $("#selectAllAlreadyAdded").bind('click', selectAllAlreadyAddedFunc);
            enableTooltipsForLongNames();

            $("#addSelected").on('click', function(e) {
                selectedGroups = getSelectedAvailableGroups().concat(selectedGroups);
                updateContent();
            });

            $("#addAll").on('click', function(e) {
                for (var i = 0; i < availableGroups.length; i ++) {
                    markOrUnmarkGroupAsEditing(availableGroups[i], newlyAdded, removed);
                }
                selectedGroups = availableGroups.concat(selectedGroups);
                availableGroups.length = 0;
                updateContent()
            });

            $("#removeSelected").on('click', function(e) {
                availableGroups = getSelectedAlreadyAddedGroups().concat(availableGroups);
                updateContent();
            });

            $("#removeAll").on('click', function(e) {
                for (var i = 0; i < selectedGroups.length; i ++) {
                    markOrUnmarkGroupAsEditing(selectedGroups[i], removed, newlyAdded);
                }
                availableGroups = selectedGroups.concat(availableGroups);
                selectedGroups.length = 0;
                updateContent();
            });

            /**
             * Gets list of groups which was selected in "Available" column
             * @return {Array} list of selected groups
             */
            function getSelectedAvailableGroups() {
                var selected = [];
                $("#groupListAvailable input[type='checkbox']:checked").each(function() {
                    var group = getGroupInfoById(availableGroups, parseInt($(this).prop('id'), 10));
                    markOrUnmarkGroupAsEditing(group, newlyAdded, removed);
                    selected.push(group);
                });
                return selected;
            }

            /**
             * Gets list of groups which was selected in "AlreadyAdded" column
             * @return {Array} list of selected groups
             */
            function getSelectedAlreadyAddedGroups() {
                var selected = [];
                $("#groupListAlreadyAdded input[type='checkbox']:checked").each(function() {
                    var group = getGroupInfoById(selectedGroups, parseInt($(this).prop('id'), 10));
                    markOrUnmarkGroupAsEditing(group, removed, newlyAdded);
                    selected.push(group);
                });
                return selected;
            }

            /**
             * Gets info about group from specified array by id
             * @param {Array} groupsInfo array in which info about group will be fetched
             * @param {Number} id identificator of needed group info
             * @return {GroupDto} founded info about group
             */
            function getGroupInfoById(groupsInfo, id) {
                for (var i = 0; i < groupsInfo.length; i ++) {
                    if (groupsInfo[i].id == id) {
                        tmp = groupsInfo[i];
                        groupsInfo.splice(i, 1);
                        return tmp;
                    }
                }
            }

            /**
             * Updates content of popup
             */
            function updateContent() {
                $("#left-container").html(getGroupListHtml("Available", availableGroups, $permissionsGroupAvailable));
                $("#right-container").html(getGroupListHtml("AlreadyAdded", selectedGroups, $permissionsGroupAlreadyAdded));

                $("#selectAllAvailable").bind('click', selectAllAvailableFunc);
                $("#selectAllAlreadyAdded").bind('click', selectAllAlreadyAddedFunc);
                enableTooltipsForLongNames();
            }

            /**
             * To mark group as editing we use 2 arrays: newlyAdded and removed. Before mark group as newly added we should
             * check is it marked as removed and vice versa. If group already marked as removed we should not mark it
             * as newly added. We should just unmark it as removed. Method performs this.
             * @param {GroupDto} group group info to be checked
             * @param {Array} markBuffer  buffer for mark group if it not marked in <b>unmarkBuffer</b>
             * @param {Array} unmarkBuffer buffer for unmark group
             */
            function markOrUnmarkGroupAsEditing(group, markBuffer, unmarkBuffer) {
                if (unmarkBuffer.indexOf(group.id) == -1) {
                    markBuffer.push(group.id);
                } else {
                    unmarkBuffer.splice(unmarkBuffer.indexOf(group.id), 1);
                }
            }

            /**
             * Enables tooltips for groups with long names
             */
            function enableTooltipsForLongNames() {
                for (var i = 0; i < selectedGroups.length; i ++) {
                    if ($('span[id="' + selectedGroups[i].id + '"]').width() > $('label[id="' + selectedGroups[i].id + '"]').width()) {
                        $('label[id="' + selectedGroups[i].id + '"]').tooltip();
                    }
                }
                for (var i = 0; i < availableGroups.length; i ++) {
                    if ($('span[id="' + availableGroups[i].id + '"]').width() > $('label[id="' + availableGroups[i].id + '"]').width()) {
                        $('label[id="' + availableGroups[i].id + '"]').tooltip();
                    }
                }
            }

        }
    }
});
