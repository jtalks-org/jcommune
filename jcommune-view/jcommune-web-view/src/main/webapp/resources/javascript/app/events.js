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

/*
* This script contains custom JQuery events
*/
(function ($) {
    /**
     * valuechange
     *
     * This event fires whenever the value changes in text field
     *
     * Example of use:
     *      $("#text-field").bind('valuechange', function() {
	 *          //your code
	 *      });
     */
    $.event.special.valuechange = {
        setup: function () {
            $(this).bind('keyup.valuechange input.valuechange', $.event.special.valuechange.handler);
            $(this).bind('cut.valuechange', $.event.special.valuechange.delayedHandler);
            // for IE
            $(this).bind('propertychange.valuechange', $.event.special.valuechange.ieHandler);
        },
        teardown: function () {
            $(this).unbind('.valuechange');


        },
        handler: function () {
            $.event.special.valuechange.triggerEvent($(this));
        },
        ieHandler: function () {
            if (event.propertyName === "value") {
                $.event.special.valuechange.triggerEvent($(this));
            }
        },
        delayedHandler: function () {
            var element = $(this);
            //setTimeout need because the cut event fires before the selected data is removed
            setTimeout(function () {
                $.event.special.valuechange.triggerEvent(element);
            }, 10);
        },
        triggerEvent: function (element) {
            element.trigger('valuechange');
        }

    };
})(jQuery);