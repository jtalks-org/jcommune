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

(function (draft) {
    'use strict';

    var SAVE_INTERVAL = 15 * 1000;

    var MIN_CONTENT_LENGTH = 2,
        MAX_CONTENT_LENGTH = 20000;

    var CONTENT_ERROR_MESSAGE = $labelMessageSizeValidation
        .replace('{min}', MIN_CONTENT_LENGTH).replace('{max}', MAX_CONTENT_LENGTH);

    /**
     * Class for access to post draft API.
     *
     * @constructor
     */
    function PostDraftApi() {}

    /**
     * Saves or update draft.
     *
     * @param draft object with content of draft post
     * @param async whether request is async (be default it is true)
     * @returns {$.Deferred}
     */
    PostDraftApi.prototype.save = function (draft, async) {

        async = async !== false;

        return $.ajax({
            url: baseUrl + '/posts/savedraft',
            type: 'POST',
            async: async,
            data: JSON.stringify(draft),
            contentType: 'application/json'
        });
    };

    /**
     * Deletes draft.
     *
     * @param id the draft id
     * @returns {jQuery.Deferred}
     */
    PostDraftApi.prototype.remove = function (id) {
        return $.ajax({
            url: baseUrl + '/drafts/' + id + '/delete'
        });
    };

    /**
     * Class for work with current draft post
     *
     * @constructor
     */
    function PostDraft(api, counter, popup) {
        var self = this;

        this._api = api;
        this._counter = counter;
        this._popup = popup;

        this._topicId = $('#topicId').val();
        this._draftId = $('#draftId').val();

        this._bodyText = $('#postBody');

        /*
         * Construct element for displaying error. If on the current page already
         * there is such element generated on backend, we use it.
         */
        var error = $('#bodyText\\.errors'),
            defaultError = $('<span class="help-inline focusToError"></span>').hide();

        this._bodyTextError = error.length ? error
                                           : defaultError.clone().insertAfter(self._bodyText);

        this._bodyTextGroup = this._bodyText.parents('.control-group');

        this._lastSavedDraftState = this._getDraftState();
        this._savingTimer = new draft.IntervalTimer(this.save.bind(this), SAVE_INTERVAL);

        this._bodyText.on('blur', this._onBlur.bind(this));
        this._bodyText.on('input', this._onInput.bind(this));
    }

    /**
     * Checks whether there is enough data in post and it is valid, and if it so,
     * tries to save it's draft, otherwise just returns failed promise.
     *
     * @param {boolean} [async] whether request is async (be default it is true)
     * @returns {jQuery.Deferred}
     */
    PostDraft.prototype.save = function (async) {
        var self = this,
            draft = this._getDraftState();

        async = async !== false;

        if (enoughData(draft) && this._validate()) {
            return this._api.save(draft, async)
                .done(function (response) {
                    self._draftId = response.result;

                    self._counter.restart();
                    self._savingTimer.stop();
                    self._popup.hide();

                    self._lastSavedDraftState = draft;
                })
                .fail(function (xhr, status) {
                    if (status == 'timeout' || xhr.status == 0) {
                        if (xhr.status == 0) {
                            // Need it because firefox makes no difference between refused connection and
                            // aborted request
                            setTimeout(function () {
                                self._popup.show($labelConnectionLost);
                            }, 3000);
                        } else {
                            self._popup.show($labelConnectionLost);
                        }
                    } else if (xhr.status == 403) {
                        self._popup.show($labelNotLoggedInError);
                    }
                });
        } else {
            return $.Deferred(function (deferred) {
                deferred.fail();
            });
        }

        function enoughData(draft) {
            return (draft['bodyText'] && draft['bodyText'].length > 0);
        }
    };

    /**
     * Determines whether the post was changed from last saving.
     *
     * @returns {boolean} whether this draft was changed
     */
    PostDraft.prototype.wasChanged = function () {
        var currentState = this._getDraftState(),
            previousState = this._lastSavedDraftState;

        return currentState['bodyText'] !== previousState['bodyText'];
    };

    /**
     * Validates content of the post.
     * Note: it checks only that values is not more than specified maximum.
     *
     * @returns {boolean} whether content is valid
     * @private
     */
    PostDraft.prototype._validate = function () {
        var self = this,
            draft = this._getDraftState();

        if (draft['bodyText'] && (draft['bodyText'].length > MAX_CONTENT_LENGTH)) {
            self._showError(CONTENT_ERROR_MESSAGE);
            return false;
        } else {
            self._hideError();
            return true;
        }
    };

    /**
     * Shows error message and highlight it.
     *
     * @param message the message
     * @private
     */
    PostDraft.prototype._showError = function (message) {
        this._bodyTextError.text(message).show();
        this._bodyTextGroup.addClass('error');
    };

    /**
     * Hides error message.
     *
     * @private
     */
    PostDraft.prototype._hideError = function () {
        this._bodyTextError.text('').hide();
        this._bodyTextGroup.removeClass('error');
    };

    /**
     * Collects data from fields and returns it as an object.
     *
     * @returns {*}
     * @private
     */
    PostDraft.prototype._getDraftState = function () {
        return {
            bodyText: this._bodyText.val(),
            topicId: this._topicId
        };
    };

    PostDraft.prototype._onBlur = function () {
        this._savingTimer.stop();
        if (this.wasChanged()) {
            this.save();
        }
    };

    PostDraft.prototype._onInput = function (event) {
        var self = this;

        // Remove draft if user emptied content (for instance by Ctrl-A and Backspace)
        if ($(event.target).is(this._bodyText) && this._bodyText.val().length == 0) {
            this._api.remove(this._draftId).then(function () {
                self._savingTimer.stop();
                self._counter.stop();
                self._lastSavedDraftState = self._getDraftState();
            });
        } else {
            this._savingTimer.start();
            this._validate();
        }
    };

    $(function () {
        var postBody = $('#postBody'),
            lastSavedTime = $('#savedMillis').val();

        // Check that we are on the right page
        if (postBody.length > 0) {
            var api = new PostDraftApi(),
                counter = new draft.Counter(),
                popup = new draft.AlertMessagePopup();

            counter.getElement().insertAfter(postBody);
            popup.getElement().insertBefore(postBody);

            if (lastSavedTime) {
                counter.start(Date.now() - lastSavedTime);
            }

            var postDraft = new PostDraft(api, counter, popup);

            // Try to save draft when user leaves this page
            window.addEventListener('beforeunload', function() {
                if (postDraft.wasChanged()) {
                    postDraft.save(false);
                }
            });
        }
    });
})(draft);