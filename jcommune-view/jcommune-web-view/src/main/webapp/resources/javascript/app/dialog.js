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

/**
 * This is a central class for creation dialog windows in the project. Each dialog consists of three parts: header, body, footer.
 * For each of these parts there is a function that generates the content: rootPanelFunc, bodyContentFunc, footerContentFunc,
 * you can override them for your particular dialog. The main method of creating the Dialog is 'createDialog' where you can
 * specify your custom configuration parameters. Note, that each parameter has a default option and if you don't override it, then default will be taken.
 * Also, there are 3 types of dialogs: alert, info, confirm with predefined markup and functions. E.g. to create an alert, write this:
 * jDialog.createDialog({
 *     type: jDialog.alertType,
 *     bodyMessage: 'some message'
 *  });
 *
 *  Options.
 *  tabNavigation - this option sets the order of elements that get focused when user presses TAB, you just need to specify a list of selectors here
 *  Example:
 *  jDialog.createDialog({
 *      ...
 *      tabNavigation : ['#fieldId', '.className', 'input[name="fieldName"]', etc.]
 *      ...
 *  })
 *  handlers - a list of objects to configure a UI event and the function to process this event. Instead of passing actual functions, you can use mnemonics
 *  (predefined names functions). Example of both:
 *   handlers: {
 *         '#signin-submit-button': {'click': sendLoginPost, 'keydown': someFunc, ...}
 *         '#signin-cancel-button': 'close'
 *   }
 *  handlersDelegate & handlersLive - same purpose as 'handlers', but instead of 'onSomeEvent' functions JQuery uses 'delegate' and 'live' functions.
 *  This is needed when while dialog creation
 *  you don't yet have elements to describe events for (these elements are dynamically created after the dialog is already in place)
 */

var jDialog = {};

$(function () {
        //types of dialog
        jDialog.confirmType = 'confirm';
        jDialog.alertType = 'alert';
        jDialog.infoType = 'info';
        jDialog.options = {};
        jDialog.dialog;

        jDialog.rootPanelFunc = function () {
            var dialog = $(' \
        <form style="display: none" method="post" class="modal" id="' + jDialog.options.dialogId + '" tabindex="-1" role="dialog" \
                    aria-hidden="true"> \
            <div class="modal-header"> \
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button> \
                <h3>' + jDialog.options.title + '</h3> \
            </div> \
            ' + jDialog.bodyContentFunc() + ' \
            ' + jDialog.footerContentFunc() + ' \
        </form> \
    ');

            return dialog;
        };

        jDialog.bodyContentFunc = function () {
            var body = '<div class="modal-body">';
            switch (jDialog.options.type) {
                case jDialog.alertType :
                case jDialog.confirmType :
                {
                    body += '<div class="dialog-message"><h4>' + jDialog.options.bodyMessage + '</h4></div>';
                    break;
                }
                default :
                {
                    body += jDialog.options.bodyContent;
                    break;
                }
            }
            return body + '</div>';
        };

        jDialog.footerContentFunc = function () {
            var footer = '<div class="modal-footer">';
            switch (jDialog.options.type) {
                case jDialog.alertType :
                {
                    footer += '<button id="' + jDialog.options.alertDefaultBut + '" class="btn btn-primary">' + $labelOk
                        + '</button>';
                    break;
                }
                default :
                {
                    footer += jDialog.options.footerContent;
                    break;
                }
            }

            return footer + '</div>';
        };

        var body = $('body');

        jDialog.closeDialog = function () {
            jDialog.dialog.modal('hide');
            jDialog.dialog.remove();
            body.css('overflow','auto');
        };

        //if user sets options with name which exists in default then default option overridden, else adds
        jDialog.defaultOptions = {
            'type': jDialog.infoType,
            'dialogId': '',
            //for header height
            'title': '&nbsp;',
            'rootPanelFunc': jDialog.rootPanelFunc,
            'bodyContentFunc': jDialog.bodyContentFunc,
            'footerContentFunc': jDialog.footerContentFunc,
            'closeDialog': jDialog.closeDialog,
            'bodyContent': '',
            //for confirm, alert types
            'bodyMessage': '',
            'footerContent': '',
            'maxWidth': 300,
            'maxHeight': 400,
            'overflow': 'auto',  //The "overflow: auto" fixes the problem of small screens for other dialogs.
            'overflowBody': 'hidden',
            'modal' : true,
            //first element focus
            'firstFocus': true,
            'tabNavigation': [],
            //contained selector of object (key of object), handler to object (value of object)
            'handlers': {},
            'handlersDelegate': {},
            'handlersLive': {},
            'dialogKeydown': Keymaps.defaultDialog,
            'alertDefaultBut': 'alert-ok',
            'backdrop': 'static'
        };

        jDialog.createDialog = function (opts) {
            if (jDialog.dialog) {
                jDialog.closeDialog();
            }
            body.css('overflow','hidden');
            //merge default options and users option
            jDialog.options = $.extend({}, jDialog.defaultOptions, opts);
            jDialog.dialog = jDialog.rootPanelFunc();

            var enforceModalFocusFn = $.fn.modal.Constructor.prototype.enforceFocus;
            if (jDialog.options.modal == false) {
                jDialog.options.backdrop = false;
                // this code removes "enforce focus" mechanism when user can't select any input outside the dialog
                // Dialog becomes non-modal
                $.fn.modal.Constructor.prototype.enforceFocus = function() {};
            }

            //modal function is bootstrap
            jDialog.dialog.modal({
                'backdrop' : jDialog.options.backdrop,
                'keyboard': false,
                'show': false
            }).css(
                {'max-width': jDialog.options.maxWidth,
                    'max-height': jDialog.options.maxHeight,
                    'overflow': jDialog.options.overflow}
            );

            //we need add element to calculate width and height
            body.append(jDialog.dialog);

            jDialog.resizeDialog(jDialog.dialog);

            jDialog.dialog.modal('show');

            addHandlers();

            if (jDialog.options.firstFocus && jDialog.options.type == jDialog.infoType) {
                jDialog.focusFirstElement();
            }

            // html5 placeholder emulation for old IE
            jDialog.dialog.find('input[placeholder]').placeholder();

            $(jDialog.dialog).on('hidden', function() {
                $.fn.modal.Constructor.prototype.enforceFocus = enforceModalFocusFn;
            });

            return jDialog.dialog;
        };

        /*
         * first elemnts it is element which have class "first",
         * or first "input" element, or first "button"
         */
        jDialog.focusFirstElement = function () {
            var firsts = ['.first', 'input:first', 'button:first'];
            var first;

            $.each(firsts, function (idx, v) {
                first = jDialog.dialog.find(v);
                if (first.length != 0) {
                    first.focus();
                    return false;
                }
            });
        };

        //methods to dialogs
        jDialog.resizeDialog = function (dialog) {
            if (dialog) {
                dialog.css("top","50%");
                dialog.css("margin-top", function () {
                    return $(this).outerHeight() / 2 * (-1)
                });

                // Starting from 480px class "modal" has new properties (see details in bootstrap-responsive.css).
                // In this case when we call the resizeDialog function right after the dialog creation
                // actually size of the dialog is not calculated yet so we should just leave
                // default value for the "left" = 10px. New value will be calculated
                // during the next window resize event.
                if ($(window).width() > 480) { //The bootstrup .modal class has this styles, but ...
                    dialog.css("left","50%"); //... has not correct behavior when the screen is small.
                    dialog.css("margin-left", function () {
                        return $(this).outerWidth() / 2 * (-1)
                    });
                }
            }
        };

        /**
         * Enable all disabled elements
         * Remove previous errors
         * Show hidden hel text
         */
        jDialog.prepareDialog = function (dialog) {
            dialog.find('*').attr('disabled', false);
            dialog.find('._error').remove();
            dialog.find(".help-block").show();
            dialog.find('.control-group').removeClass('error');
        };


        var capitaliseFirstLetter = function (string)
        {
            return string.charAt(0).toUpperCase() + string.slice(1);
        };

        /**
         * Show errors under fields with errors
         * Errors overrides help text (help text will be hidden)
         */
        jDialog.showErrors = function (dialog, errors, idPrefix, idPostfix) {
            ErrorUtils.removeAllErrorMessages();
            for (var i = 0; i < errors.length; i++) {
                var idField = '#' + idPrefix + errors[i].field + idPostfix;
                if (idPrefix.length > 0 && $(idField).length == 0) {
                    idField = '#' + idPrefix + capitaliseFirstLetter(errors[i].field) + idPostfix;
                }
                ErrorUtils.addErrorMessage(idField, errors[i].defaultMessage);
            }
            jDialog.resizeDialog(dialog);
        };


        var addHandlers = function () {
            $('.modal-backdrop').live('click', function (e) {
                jDialog.options.closeDialog();
            });

            jDialog.dialog.find('.close').bind('click', function (e) {
                jDialog.options.closeDialog();
            });

            jDialog.dialog.on('keydown', jDialog.options.dialogKeydown);

            if (jDialog.options.type == jDialog.alertType) {
                tabNavigation([jDialog.options.alertDefaultBut]);
                $('#' + jDialog.options.alertDefaultBut).on('click', getStaticHandler('close'))
            }

            $.each(jDialog.options.handlers, function (k, v) {
                $.each(v, function (ke, ve) {
                    if (ke == 'static') {
                        $(k).on('click', getStaticHandler(ve))
                    } else {
                        $(k).on(ke, ve);
                    }
                })
            });

            $.each(jDialog.options.handlersDelegate, function (k, v) {
                $.each(v, function (ke, ve) {
                        if (ke == 'static') {
                            $(document).delegate(k, 'click', getStaticHandler(ve));
                        } else {
                            $(document).delegate(k, ke, ve);
                        }
                    }
                )
            });

            $.each(jDialog.options.handlersLive, function (k, v) {
                $.each(v, function (ke, ve) {
                    if (ke == 'static') {
                        $(document).live(k, 'click', getStaticHandler(ve));
                    } else {
                        $(document).live(k, ke, ve);
                    }
                })
            });

            tabNavigation(jDialog.options.tabNavigation);
        };

        var tabNavigation = function (selectors) {
            $.each(selectors, function (idx, v) {
                var func = function (e) {
                    if ((e.keyCode || e.charCode) == tabCode) {
                        e.preventDefault();
                        var nextElement = e.shiftKey? prevTabElm(selectors, idx) : nextTabElm(selectors, idx);
                        nextElement.focus();
                    }
                };
                $(v).on('keydown', func);
            });
        };

        var getStaticHandler = function (key) {
            switch (key) {
                case 'close':
                   return function(e) {
                        e.preventDefault();
                       jDialog.options.closeDialog();
                   };
                    break;
            }
        };

        var nextTabElm = function (els, curIdx) {
            if (els.length == curIdx + 1) {
                return jDialog.dialog.find(els[0]);
            } else {
                return jDialog.dialog.find(els[curIdx + 1])
            }
        }

        var prevTabElm = function (els, curIdx) {
            if (curIdx === 0) {
                return jDialog.dialog.find(els[els.length - 1]);
            } else {
                return jDialog.dialog.find(els[curIdx - 1])
            }
        }
    }
)
;

