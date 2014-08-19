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
        
        var query = "pluginName=" + pluginName + "&activated=" + activated;
        
        function showPopup(title, message, status){
            var body = "<div class=\"alert ";
            if (status == "success")
                body += "alert-success";
            else
                body += "alert-error";
            body = body + "\">" + message + "</div>";
            jDialog.createDialog({
                dialogId: 'plugin-activation-popup',
                title: title,
                bodyContent: body,
                footerContent: '',
                maxWidth: 350,
                tabNavigation: ['']
            });
            
            setTimeout(function() {
                jDialog.closeDialog({
                    dialogId: 'plugin-activation-popup'
                })
            }, 1000);
        }
        
        function successActivationHandler(){
            if (activated){
                showPopup($pluginStatusTitle, $pluginStatusActivated, "success");
            } else {
                showPopup($pluginStatusTitle, $pluginStatusDeactivated, "success");
            }
        }
        
        function errorActivationHandler(){
            showPopup($pluginStatusTitle, $pluginStatusFailed, "failed");
        }
        
        $.ajax({
            type: 'POST',
            url: $root + '/plugins/activate',
            data: query,
            dataType: 'json',
            success: successActivationHandler,
            error: errorActivationHandler,
            complete: function () {
                $("#" + id).attr('disabled', false);
            }
        });
    });
})