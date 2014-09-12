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

    if ($("#errorHolder").length > 0) {

        var bodyContent = $("#errorHolder").html() +
                        '<textarea id="errorArea" class="hide-element" readonly="readonly" cols="200">' +
                        $("#errorInformationHolder").text() + '</textarea>';
        var errorAreaCollapse = function (e) {
            e.preventDefault();
            var errorArea = $("#errorArea");
            var errorAreaControlButton = $("#errorAreaControlButton");
            if (errorArea.hasClass("hide-element")) {
                errorAreaControlButton.html($labelHideDetails);
                errorArea.removeClass("hide-element");
            } else {
                errorAreaControlButton.html($labelShowDetails);
                errorArea.addClass("hide-element");
            }
            jDialog.resizeDialog(jDialog.dialog);
        };

        var footerContent = ' \
                    <button id="errorCloseButton" class="btn">' + $labelCloseDialog + '</button> \
                    <button id="errorAreaControlButton" class="btn">' + $labelShowDetails + '</button>';

        jDialog.createDialog({
            dialogId: 'pluginErrorDialog',
            title: $labelPluginConfigError,
            bodyContent: bodyContent,
            footerContent: footerContent,
            maxWidth: "70%",
            maxHeight: 600,
            modal: false,
            firstFocus: true,
            tabNavigation: ['#errorAreaControlButton', '#errorCloseButton'],
            handlers: {
                '#errorCloseButton': {'static':'close'},
                '#errorAreaControlButton' : {'click': errorAreaCollapse}
            }
        });

        $("#pluginErrorDialog").draggable({
            handle: ".modal-header"
        });
    }
    
    $(".plugin-checkbox").on('change', function (e) {
        $(this).prop( "disabled", true );
        var id = $(this).attr('id');
        var pluginName = $("#"+id+"-name").val();
        activated = $(this).is(':checked');
        var pluginActivatingDto = {
            pluginName: pluginName,
            activated: activated
        }
        
        function showPopup(plugin, message, status){
            $("#status-message").html("<span>" + plugin + " " + message + "</span>");
            if (status == "success")
                $("#status-message").addClass("alert-success");
            else
                $("#status-message").addClass("alert-error");
            
            var width = $("#status-message").width();
            $("#status-message").css("margin-left", "-" + width / 2 + 'px');
                
            $("#status-message").show();
            
            setTimeout(function() {
                $("#status-message").hide();
            }, 2000);
        }
        
        function successActivationHandler(resp){
            if (activated){
                showPopup(pluginName, $pluginStatusActivated, "success");
            } else {
                showPopup(pluginName, $pluginStatusDeactivated, "success");
            }
        }
        
        function errorActivationHandler(){
            showPopup(pluginName, $pluginStatusFailed, "failed");
            $("#" + id).removeAttr("checked");
        }
        
        $.ajax({
            type: 'POST',
            url: $root + '/plugins/activate',
            data: JSON.stringify(pluginActivatingDto),
            contentType: "application/json",
            async: false,
            success: function (resp) {
                successActivationHandler(resp);
            },
            error: errorActivationHandler,
            complete: function () {
                $("#" + id).attr('disabled', false);
            }
        });
    });
})