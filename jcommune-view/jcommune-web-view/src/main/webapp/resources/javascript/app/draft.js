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
 * Common module for topicDraft and postDraft
 */
draft = (function() {
    'use strict';

    var SECOND = 1000,
        MINUTE = 60 * SECOND,
        HOUR = 60 * MINUTE;

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
        return $labelSaved + " " + new Date(Date.now() - milliseconds).toLocaleDateString();
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

    return {
        IntervalTimer: IntervalTimer,
        Counter: Counter,
        AlertMessagePopup: AlertMessagePopup
    };
})();