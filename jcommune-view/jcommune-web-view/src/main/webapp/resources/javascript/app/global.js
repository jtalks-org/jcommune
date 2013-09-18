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

    //Sets timezone cookie for the server to show all the dates in a client timezone
    document.cookie = "GMT=" + new Date().getTimezoneOffset() + "; path=/";
    // Initializes image previewing
    $("a[rel^='prettyPhoto']").prettyPhoto({social_tools: false});
    // popups for individual post links
    $('a.postLink').each(function () {
        $(this).click(function (e) {
            e.preventDefault();
            var path = window.location.protocol + '//' + window.location.host;
            jDialog.createDialog({
                type: jDialog.alertType,
                maxWidth: 800,
                bodyMessage: path + $(this).attr('href')
            });
        })
    })
    // popups to confirm post/topic deletion
    $(document).delegate('a.delete', 'click', function (e) {
        e.preventDefault();
        deletePath = $(this).attr('href');
        var footerContent = ' \
            <button id="remove-entity-cancel" class="btn">' + $labelCancel + '</button> \
            <button id="remove-entity-ok" class="btn btn-primary">' + $labelOk + '</button>';

        var submitFunc = function (e) {
            e.preventDefault();
            var deleteForm = $('#deleteForm').get(0);
            deleteForm.action = deletePath;
            deleteForm.submit();
            jDialog.closeDialog();
        };

        jDialog.createDialog({
            type: jDialog.confirmType,
            bodyMessage: $(this).attr('data-confirmationMessage'),
            firstFocus: false,
            footerContent: footerContent,
            maxWidth: 440,
            tabNavigation: ['#remove-entity-ok', '#remove-entity-cancel'],
            handlers: {
                '#remove-entity-ok': {'click': submitFunc},
                '#remove-entity-cancel': {'static': 'close'}
            }
        });

        $('#remove-entity-ok').focus();
    });

    /** Handler to prevent multiposting. */
    $('form.anti-multipost').submit(function () {
        if (Antimultipost.beingSubmitted($(this))) {
            return false;
        }
        Antimultipost.disableSubmit($(this));
    });

    //keyamaps to html forms
    $(document).delegate('form.anti-multipost', 'keydown', Keymaps.bbeditor);

    //for change externalLinks position (top line or main page)
    $(window).resize(function (e) {
        e.preventDefault();
        $('.btn-navbar').trigger('mainLinksPosition');
    });

    //disable or enable sape configuration inputs of form
    $('#enableSape1').bind('change', function (e) {
        var elements = $('#sape-configuration-form input:not(#enableSape1, input[name="_enableSape"],' +
            ' .btn, input[type="checkbox"])').parents('.control-group');
        if (e.target.checked) {
            elements.show();
        } else {
            elements.hide();
        }
    })

    var searchInput = $('#searchText');
    searchInput.on('focus', function (e) {
        e.preventDefault();
        $(e.target).addClass('search-query-focus');
        $(e.target).removeClass('search-query-focusout');
    });

    searchInput.on('focusout', function (e) {
        e.preventDefault();
        if ($(e.target).val() == '') {
            $(e.target).addClass('search-query-focusout');
            $(e.target).removeClass('search-query-focus');
        }
    });

    if (searchInput.val() == '') {
        searchInput.addClass('search-query-focusout');
    }

    //caps lock alert to passwords fields
    $(document).delegate('input[type="password"]', 'keypress keydown', function (e) {
        var container = $(e.target).parent('div');
        var el = $('<i class="icon-exclamation-sign icon-password-exclamation" data-original-title="' + $capsLock + '"/>');
        if (e.type == 'keydown' && container.find('.icon-exclamation-sign').length > 0) {
            container.find('.icon-exclamation-sign').remove();
//            container.find('.tooltip').remove();
            $(e.target).css('padding-right', (parseInt($(e.target).css('padding-right')) - 20) + 'px');
            $(e.target).width($(e.target).width() + 20);
        }

        var s = String.fromCharCode(e.which);
        if (e.type == 'keypress' && s.toUpperCase() === s && s.toLowerCase() !== s && !e.shiftKey
            && container.find('.icon-exclamation-sign').length == 0) {
            $(e.target).after(el);
            $(e.target).css('padding-right', (parseInt($(e.target).css('padding-right')) + 20) + 'px');
            $(e.target).width($(e.target).width() - 20);
            //fix to modal dialog tooltip
            if ($(e.target).parents('.modal').length > 0) {
                el.tooltip({placement: 'bottom', cls: 'modal-tooltip-caps-lock'});
            } else {
                el.tooltip({placement: 'bottom'});
            }

        }

    });

    //redirect to external links in the body of posts (and signature, profile contacts)
    $(document).delegate('.post-content-td a, #contacts a, .test-signature a, .pm_message_view a, #editorBBCODEdiv a',
        'mousedown', function (e) {
            var tagName = $(e.target).prop("tagName").toLowerCase();
            var link = $(e.target);
            //prettyPhoto img link
            if (tagName == 'img' || tagName != 'link') {
                link = link.parent('a');
            }

            link.attr('href', link.attr('href').replace('/out?=', ''));
        });

    $(window).resize();

    // html5 placeholder emulation for old IE
    $('input[placeholder]').placeholder();
});

