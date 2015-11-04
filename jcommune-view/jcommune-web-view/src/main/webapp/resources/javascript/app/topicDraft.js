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

(function(draft) {
    'use strict';

    var SAVE_INTERVAL = 15 * 1000;

    var MIN_TITLE_LENGTH = 1,
        MAX_TITLE_LENGTH = 120,

        MIN_CONTENT_LENGTH = 2,
        MAX_CONTENT_LENGTH = 20000,

        MIN_POLL_TITLE_LENGTH = 3,
        MAX_POLL_TITLE_LENGTH = 120,

        MIN_POLL_ITEM_LENGTH = 2,
        MAX_POLL_ITEM_LENGTH = 50,

        MIN_ITEMS_NUMBER = 2,
        MAX_ITEMS_NUMBER = 50;

    var TITLE_ERROR_MESSAGE = $labelMessageSizeValidation
            .replace('{min}', MIN_TITLE_LENGTH).replace('{max}', MAX_TITLE_LENGTH),

        CONTENT_ERROR_MESSAGE = $labelMessageSizeValidation
            .replace('{min}', MIN_CONTENT_LENGTH).replace('{max}', MAX_CONTENT_LENGTH),

        POLL_TITLE_ERROR_MESSAGE = $labelMessageSizeValidation
            .replace('{min}', MIN_POLL_TITLE_LENGTH).replace('{max}', MAX_POLL_TITLE_LENGTH),

        POLL_ITEMS_VALUE_ERROR_MESSAGE = $pollItemsSize
            .replace('{min}', MIN_ITEMS_NUMBER).replace('{max}', MAX_ITEMS_NUMBER),

        POLL_ITEM_LENGTH_ERROR = $pollItemLength
            .replace('{min}', MIN_POLL_ITEM_LENGTH).replace('{max}', MAX_POLL_ITEM_LENGTH);

    /**
     * Class for access to topic draft API.
     *
     * @constructor
     */
    function TopicDraftApi() {}

    /**
     * Saves or update draft.
     *
     * @param draft object with content of draft topic
     * @param async whether request is async (be default it is true)
     * @returns {$.Deferred}
     */
    TopicDraftApi.prototype.save = function (draft, async) {
        async = async !== false;

        return $.ajax({
            url: baseUrl + '/topics/draft',
            type: 'POST',
            async: async,
            data: JSON.stringify(draft),
            contentType: "application/json"
        });
    };

    /**
     * Deletes draft.
     *
     * @returns {$.Deferred}
     */
    TopicDraftApi.prototype.remove = function () {
        return $.ajax({
            type: 'DELETE',
            url: baseUrl + '/topics/draft'
        });
    };

    /**
     * Class for work with current draft topic.
     *
     * @constructor
     */
    function TopicDraft(api, counter, popup) {

        var self = this;

        this._api = api;
        this._counter = counter;
        this._popup = popup;

        this._title = $('#subject');
        this._content = $('#postBody');
        this._pollTitle = $('#pollTitle');
        this._pollItemsValue = $('#pollItems');

        this._lastSavedDraftState = this._getDraftState();

        this._savingTimer = new draft.IntervalTimer(this.save.bind(this), SAVE_INTERVAL);

        /*
         * Construct elements for displaying errors. If on the current page already
         * there are such elements generated on backend, we use them.
         */
        this._errors = {
            title: {
                group: this._title.parents('.control-group'),
                error: error(this._title, '#topic\\.title\\.errors')
            },
            content: {
                group: this._content.parents('.control-group'),
                error: error(this._content, '#bodyText\\.errors')
            },
            pollTitle: {
                group: this._pollTitle.parents('.control-group'),
                error: error(this._pollTitle, '#topic\\.poll\\.title\\.errors')
            },
            pollItemsValue: {
                group: this._pollItemsValue.parents('.control-group'),
                error: error(this._pollItemsValue, '#topic\\.poll\\.pollItems')
            }
        };

        function error(element, selector) {
            var error = $(selector),
                defaultError = $("<span class='help-inline focusToError'></span>").hide();

            return error.length ? error
                                : defaultError.clone().insertAfter(element)
        }

        this._addEventListener('blur', this._onBlur.bind(this));
        this._addEventListener('keyup', this._onKeyUp.bind(this));
    }

    /**
     * Checks whether there is enough data in topic and it is valid, and if it so,
     * tries to save it's draft, otherwise just returns failed promise.
     *
     * @param {boolean} [async] whether request is async (be default it is true)
     * @returns {jQuery.Deferred}
     */
    TopicDraft.prototype.save = function (async) {
        var self = this,
            draft = this._getDraftState();

        async = async !== false;

        if (enoughData(draft) && this._validate()) {
            return this._api.save(draft, async)
                .done(function () {
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
            return (draft['title'] && draft['title'].length > 0) ||
                   (draft['content'] && draft['content'].length > 0) ||
                   (draft['pollTitle'] && draft['pollTitle'].length > 0) ||
                   (draft['pollItemsValue'] && draft['pollItemsValue'].length > 0);
        }
    };

    /**
     * Determines whether the topic was changed from last saving.
     *
     * @returns {boolean} whether this draft was changed
     */
    TopicDraft.prototype.wasChanged = function () {
        var currentState = this._getDraftState(),
            previousState = this._lastSavedDraftState;

        return currentState['title'] !== previousState['title'] ||
               currentState['content'] !== previousState['content'] ||
               currentState['pollTitle'] !== previousState['pollTitle'] ||
               currentState['pollItemsValue'] !== previousState['pollItemsValue'];
    };

    /**
     * Validates content of the topic.
     * Note: it checks only that values is not more than specified maximum.
     *
     * @returns {boolean} whether content is valid
     * @private
     */
    TopicDraft.prototype._validate = function () {
        var self = this,
            success = true,
            draft = this._getDraftState();

        success &= validateStringField('title', MAX_TITLE_LENGTH, TITLE_ERROR_MESSAGE);
        success &= validateStringField('content', MAX_CONTENT_LENGTH, CONTENT_ERROR_MESSAGE);
        success &= validateStringField('pollTitle', MAX_POLL_TITLE_LENGTH, POLL_TITLE_ERROR_MESSAGE);

        function validateStringField(name, max, message) {
            if (draft[name] && (draft[name].length > max)) {
                self._showError(name, message);
                return false;
            } else {
                self._hideError(name);
                return true;
            }
        }

        if (draft['pollItemsValue']) {
            var pollItems = draft['pollItemsValue'].split("\n");

            if (pollItems.length > MAX_ITEMS_NUMBER) {
                this._showError('pollItemsValue', POLL_ITEMS_VALUE_ERROR_MESSAGE);
                success = false;
            } else {
                this._hideError('pollItemsValue');
            }

            if (success) {
                pollItems.forEach(function(item) {
                    if (item && (item.length > MAX_POLL_ITEM_LENGTH)) {
                        success = false;
                    }
                });

                if (!success) {
                    this._showError('pollItemsValue', POLL_ITEM_LENGTH_ERROR);
                } else {
                    this._hideError('pollItemsValue');
                }
            }
        } else {
            this._hideError('pollItemsValue');
        }

        return success;
    };

    /**
     * Shows error message for specified element and highlight it.
     *
     * @param element the element
     * @param message the message
     * @private
     */
    TopicDraft.prototype._showError = function(element, message) {
        if (this._errors[element]) {
            this._errors[element].error.text(message).show();
            this._errors[element].group.addClass('error');
        }
    };

    /**
     * Hides error message for specified element.
     *
     * @param element the element
     * @private
     */
    TopicDraft.prototype._hideError = function(element) {
        if (this._errors[element]) {
            this._errors[element].error.text('').hide();
            this._errors[element].group.removeClass('error');
        }
    };

    /**
     * Collects data from fields and returns it as an object.
     *
     * @returns {*}
     * @private
     */
    TopicDraft.prototype._getDraftState = function () {
        var draft = {
            title: this._title.val(),
            content: this._content.val()
        };

        if ((this._pollTitle) ||
            (this._pollItemsValue)) {
            draft['pollTitle'] = this._pollTitle.val();
            draft['pollItemsValue'] = this._pollItemsValue.val();
        }

        return draft;
    };

    /**
     * Attach event listener to all fields of the topic form.
     *
     * @param event the name of event which will be listened for
     * @param callback the callback which will be called when the event happen
     * @private
     */
    TopicDraft.prototype._addEventListener = function (event, callback) {
        this._title.on(event, callback);
        this._content.on(event, callback);

        if (this._pollTitle) {
            this._pollTitle.on(event, callback);
        }

        if (this._pollItemsValue) {
            this._pollItemsValue.on(event, callback);
        }
    };

    TopicDraft.prototype._onBlur = function (event) {
        this._savingTimer.stop();

        // If user emptied content and then left content field, we don't save draft
        if (this._isEmpty()) {
            return;
        }

        if (this.wasChanged()) {
            this.save();
        }
    };

    TopicDraft.prototype._onKeyUp = function (event) {
        var self = this;

        this._savingTimer.start();
        this._validate();

        // Remove draft if user emptied content (for instance by Ctrl-A and Backspace)
        if (this._isEmpty()) {
            this._api.remove().then(function () {
                self._savingTimer.stop();
                self._counter.stop();
            });
        }
    };

    TopicDraft.prototype._isEmpty = function() {
        var state = this._getDraftState();

        return !state['title'] && !state['content'] &&
               !state['pollTitle'] && !state['pollItemsValue'];
    };

    $(function() {
        var postBody = $('#postBody'),
            subject = $('#subject'),
            lastSavedTime = $('#topicDraftLastSavedMillis').val();

        // Check that we are on the right page
        if (postBody.length > 0 && subject.length > 0) {
            var api = new TopicDraftApi(),
                counter = new draft.Counter(),
                popup = new draft.AlertMessagePopup();

            counter.getElement().insertAfter(postBody);
            popup.getElement().insertBefore(subject);

            if (lastSavedTime) {
                counter.start(Date.now() - lastSavedTime);
            }

            var topicDraft = new TopicDraft(api, counter, popup);

            // Try to save draft when user leaves this page
            window.addEventListener('beforeunload', function() {
                if (topicDraft.wasChanged()) {
                    topicDraft.save(false);
                }
            });
        }
    });
})(draft);