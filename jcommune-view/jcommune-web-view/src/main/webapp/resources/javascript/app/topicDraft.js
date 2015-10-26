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
    'use strict';

    var SECOND = 1000,
        MINUTE = 60 * SECOND,
        HOUR = 60 * MINUTE;

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
     * Class for access to topic draft API
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
     * Helper for executing functions at specified interval
     *
     * @param {Function} fn the called function
     * @param {Number} delay the interval in milliseconds
     * @constructor
     */
    function IntervalTimer(fn, delay) {
        this._fn = fn;
        this._delay = delay;
        this._timeoutID = null;
    }

    /**
     * Starts timer. But if it already has been starter, silently ends.
     */
    IntervalTimer.prototype.start = function () {
        var self = this;

        if (this._timeoutID === null) {
            this._timeoutID = setTimeout(callback, self._delay);
        }

        function callback() {
            self._fn();
            self._timeoutID = setTimeout(callback, self._delay);
        }
    };

    /**
     * Stops timer. But if it already has been stopped, silently ends.
     */
    IntervalTimer.prototype.stop = function () {
        clearTimeout(this._timeoutID);
        this._timeoutID = null;
    };

    /**
     * Restarts timer by calling methods stop and start alternately.
     */
    IntervalTimer.prototype.restart = function () {
        this.stop();
        this.start();
    };


    /**
     * Class for work with counter that show time from last save of draft.
     *
     * @constructor
     */
    function Counter() {
        this._el = $("<span id='topicDraftCounter' class='topic-draft-counter pull-right'></span>");
        this._interval = 5 * SECOND;
        this._savingTimer = new IntervalTimer(this._tick.bind(this), this._interval);
        this._time = 0;
    }

    /**
     * Launches new counting.
     *
     * @param time the time (in milliseconds) that has passed from last saving (by default it is 0)
     */
    Counter.prototype.start = function (time) {
        this._time = time || 0;
        this._el.text(this._composeLabel(this._time));
        this._savingTimer.start();
    };

    /**
     * Stops counting and cleans label of this counter.
     */
    Counter.prototype.stop = function () {
        this._time = 0;
        this._el.text('');
        this._savingTimer.stop();
    };

    /**
     * Restart counting.
     */
    Counter.prototype.restart = function () {
        this.stop();
        this.start();
    };

    /**
     * Returns reference to the DOM element that contains label of this counter.
     *
     * @returns {jQuery}
     */
    Counter.prototype.getElement = function () {
        return this._el;
    };

    /**
     * Is launched by timer at specified interval and updates label of this counter.
     *
     * @private
     */
    Counter.prototype._tick = function () {
        this._time += this._interval;
        this._el.text(this._composeLabel(this._time));
    };

    /**
     * Composes label for this counter based on passed time.
     *
     * @param time passed time in milliseconds
     * @private
     */
    Counter.prototype._composeLabel = function (time) {
        if (time < MINUTE) {
            return this._composeSecondsLabel(Math.floor(time / SECOND));
        } else if (time >= MINUTE && time < HOUR) {
            return this._composeMinutesLabel(Math.floor(time / MINUTE));
        } else if (time >= HOUR && time < 24 * HOUR) {
            return this._composeHoursLabel(Math.floor(time / HOUR));
        } else {
            return this._composeDateLabel(time);
        }
    };

    /**
     * If value of the seconds lower than 5, composes label as "Saved just now", otherwise
     * as "Save N seconds ago".
     *
     * @param {Number} seconds passed time in seconds
     * @returns {string} composed label
     * @private
     */
    Counter.prototype._composeSecondsLabel = function (seconds) {
        if (seconds < 5) {
            return $labelSavedJustNow;
        }
        return $labelSaved + " " + seconds.toString() + " " + $labelSeconds + " " + $labelAgo;
    };

    /**
     * Composes label as "Saved N minutes ago".
     *
     * @param {Number} minutes passed time in minutes
     * @returns {string} composed label
     * @private
     */
    Counter.prototype._composeMinutesLabel = function (minutes) {
        var suffixGroup = minutes % 10;
        var between2And4Suffix = $labelMinutes24Suffix;
        if (minutes > 10 && minutes < 20) {
            between2And4Suffix = $labelMinutesMoreThan4Suffix;
        }
        var oneAtTheEndSuffix = $labelMinute1Suffix;
        if (minutes == 11) {
            oneAtTheEndSuffix = $label11Suffix;

        } else if (minutes > 20) {
            oneAtTheEndSuffix = $labelMinutes1AtTheEndSuffix;
        }
        switch (suffixGroup) {
            case 1:
                return $labelSaved + " " + minutes.toString() + " " + $labelMinute
                    + oneAtTheEndSuffix + " " + $labelAgo;
            case 2:
            case 3:
            case 4:
                return $labelSaved + " " + minutes.toString() + " " + $labelMinute
                    + between2And4Suffix + " " + $labelAgo;
            default:
                return $labelSaved + " " + minutes.toString() + " " + $labelMinute
                    + $labelMinutesMoreThan4Suffix + " " + $labelAgo;
        }
    };

    /**
     * Composes label as "Saved N hours ago".
     *
     * @param {Number} hours passed time in hours
     * @returns {string} composed label
     * @private
     */
    Counter.prototype._composeHoursLabel = function (hours) {
        var suffixGroup = hours % 10;
        var between2And4Suffix = $labelHours24Suffix;
        if (hours > 10 && hours < 20) {
            between2And4Suffix = $labelHoursMoreThan4Suffix;
        }
        var oneAtTheEndSuffix = $labelHours1Suffix;
        if (hours == 11) {
            oneAtTheEndSuffix = $label11HoursSuffix;
        } else if (hours > 20) {
            oneAtTheEndSuffix = $labelHours1AteTheEndSuffix;
        }
        switch (suffixGroup) {
            case 1:
                return $labelSaved + " " + hours.toString() + " " + $labelHour
                    + oneAtTheEndSuffix + " " + $labelAgo;
            case 2:
            case 3:
            case 4:
                return $labelSaved + " " + hours.toString() + " " + $labelHour
                    + between2And4Suffix + " " + $labelAgo;
            default:
                return $labelSaved + " " + hours.toString() + " " + $labelHour
                    + $labelHoursMoreThan4Suffix + " " + $labelAgo;
        }
    };

    /**
     * Composes label as "Saved DATE".
     *
     * @param {Number} milliseconds passed time in milliseconds
     * @returns {string} composed label
     * @private
     */
    Counter.prototype._composeDateLabel = function (milliseconds) {
        return $labelSaved + " " + new Date(milliseconds).toLocaleDateString();
    };


    /**
     * Class for showing alert messages.
     *
     * @constructor
     */
    function AlertMessagePopup() {
        this._element = $('' +
            '<div class="alert alert-error">' +
            '<span class="close">&times;</span>' +
            '<span id="alertMessagePopup"></span>' +
            '</div>');
        this._message = $('#alertMessagePopup', this._element);

        $('.close', this._element).on('click', this.hide.bind(this));

        this.hide();
    }

    AlertMessagePopup.prototype.show = function (message) {
        this._message.text(message);
        this._element.show();
    };

    AlertMessagePopup.prototype.hide = function () {
        this._element.hide();
    };

    /**
     * Returns reference to the DOM element that contains showed message.
     *
     * @returns {jQuery}
     */
    AlertMessagePopup.prototype.getElement = function () {
        return this._element;
    };


    /**
     * Class for work with current draft topic
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

        this._lastSavedDraftState = this._getDraftData();

        this._savingTimer = new IntervalTimer(this.save.bind(this), 15 * SECOND);

        var defaultError = $("<span class='help-inline focusToError'></span>").hide();

        /*
         * Construct elements for displaying errors. If on the current page already
         * there are such elements generated on backend, we use them.
         */
        this._errors = {
            title: {
                group: this._title.parents('.control-group'),
                error: (function() {
                    var error = $('#topic\\.title\\.errors');

                    return error.length ? error
                                        : defaultError.clone().insertAfter(self._title)
                })()
            },
            content: {
                group: this._content.parents('.control-group'),
                error: (function() {
                    var error = $('#bodyText\\.errors');

                    return error.length ? error
                                        : defaultError.clone().insertAfter(self._content);
                })()
            },
            pollTitle: {
                group: this._pollTitle.parents('.control-group'),
                error: (function() {
                    var error = $('#topic\\.poll\\.title\\.errors');

                    return error.length ? error
                                        : defaultError.clone().insertAfter(self._pollTitle);
                })()
            },
            pollItemsValue: {
                group: this._pollItemsValue.parents('.control-group'),
                error: (function() {
                    var error = $('#topic\\.poll\\.pollItems');

                    return error.length ? error
                                        : defaultError.clone().insertAfter(self._pollItemsValue)
                })()
            }
        };

        this._addEventListener('blur', this._onBlur.bind(this));
        this._addEventListener('keyup', this._onKeyUp.bind(this));
    }

    /**
     * Checks whether there is enough data in draft topic and it is valid, and if it so,
     * tries to save it, otherwise just returns failed promise.
     *
     * @param {boolean} [async] whether request is async (be default it is true)
     * @returns {jQuery.Deferred}
     */
    TopicDraft.prototype.save = function (async) {
        var self = this,
            draft = this._getDraftData();

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
     * Determines whether this draft was changed from last saving.
     *
     * @returns {boolean} whether this draft was changed
     */
    TopicDraft.prototype.wasChanged = function () {
        var currentState = this._getDraftData(),
            previousState = this._lastSavedDraftState;

        return currentState['title'] !== previousState['title'] ||
               currentState['content'] !== previousState['content'] ||
               currentState['pollTitle'] !== previousState['pollTitle'] ||
               currentState['pollItemsValue'] != previousState['pollItemsValue'];
    };

    /**
     * Validates content of this draft topic.
     * Note: it checks only that values is not more than specified maximum.
     *
     * @returns {boolean} whether content is valid
     * @private
     */
    TopicDraft.prototype._validate = function () {
        var self = this,
            success = true,
            draft = this._getDraftData();

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

            if (pollItems.length >= MAX_ITEMS_NUMBER) {
                this._showError('pollItemsValue', POLL_ITEMS_VALUE_ERROR_MESSAGE);
                success = false;
            } else {
                this._hideError('pollItemsValue');
            }

            if (success) {
                pollItems.forEach(function(item) {
                    if (item && (item.length >= MAX_POLL_ITEM_LENGTH)) {
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
    TopicDraft.prototype._getDraftData = function () {
        var draft = {
            title: this._title.val() ? this._title.val() : undefined,
            content: this._content.val() ? this._content.val() : undefined
        };

        if ((this._pollTitle && this._pollTitle.val()) ||
            (this._pollItemsValue && this._pollItemsValue.val())) {
            draft['pollTitle'] = this._pollTitle.val() ? this._pollTitle.val() : undefined;
            draft['pollItemsValue'] = this._pollItemsValue.val() ? this._pollItemsValue.val() : undefined;
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

    TopicDraft.prototype._onBlur = function () {
        this._savingTimer.stop();
        if (this.wasChanged()) {
            this.save();
        }
    };

    TopicDraft.prototype._onKeyUp = function (event) {
        var self = this;

        this._savingTimer.start();
        this._validate();

        // Remove draft if user emptied content (for instance by Ctrl-A and Backspace)
        if ($(event.target).is(this._content) && this._content.val().length == 0) {
            this._api.remove().then(function () {
                self._savingTimer.stop();
                self._counter.stop();
            });
        }
    };


    var postBody = $('#postBody'),
        subject = $('#subject'),
        lastSavedTime = $('#topicDraftLastSavedMillis').val();

    // Check that we are on the right page
    if (postBody.length > 0 && subject.length > 0) {
        var api = new TopicDraftApi(),
            counter = new Counter(),
            popup = new AlertMessagePopup();

        counter.getElement().insertAfter(postBody);
        popup.getElement().insertBefore(subject);

        if (lastSavedTime) {
            counter.start(Date.now() - lastSavedTime);
        }

        var topicDraft = new TopicDraft(api, counter, popup);

        // Try to save draft when user leaves this page
        $(window).on('unload', function () {
            if (topicDraft.wasChanged()) {
                topicDraft.save(false);
            }
        });
    }
});