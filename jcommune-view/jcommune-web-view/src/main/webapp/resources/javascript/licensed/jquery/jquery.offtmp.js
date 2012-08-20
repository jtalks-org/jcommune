/*
	jquery.offtmp 1.0 for jQuery 1.7

	this work is licensed under bobef license:
	USE WITHOUT RESTRICTIONS AT YOUR OWN RESPONSIBILITY

	originaly created for *undisclosed at this time* by bobef; http://bobef.net
*/


/*
	Provides an easy way to quickly turn off events for an element and then turn them back on
	.offtmp() will turn off the event for all selectors and handlers
	Calling .on() for event that has been temporarily turned off will call .ontmp() before binding the new event handler. This is to maintain the proper callback order
	Calling .off() for event that has been temporarily turned off will turn off the event forever so .ontmp() will do nothin for this event
	Keep in mind this function overrides the .on() and .off() functions so it may be incompatible with other plugins that do the same
	
	$('#mybutton').on('click', function() {
		var me = $(this);
		me.offtmp('click'); //disable the click event temporaraly while performing ajax request
		$.post(...).always(function() {
			me.ontmp('click'); //the ajax request is ready, now we can enable clicks again
		});
	});
*/

(function() {

	//save the original jQuery on/off functions
	var on = jQuery.fn.on;
	var off = jQuery.fn.off;
	
	//common logic for applying same thing for each comma separated event
	function applyCommaSeparatedEvents(obj, fn, events, args) {
		events = events.split(',');
		var regex = new RegExp('^\\s*([^\\s]+)\\s*$');
		for(var e in events) {
			args[0] = events[e].replace(regex, '$1');
			obj[fn].apply(obj, args);
		}
		return obj;
	}
	
	//common logic for traversing the saved event map
	function applyForEachEvent(obj, events, selector, callback, callback_per_handler, callback_per_event, callback_event_check) {
		var offtmp = obj.data('offtmp');
		if(offtmp === undefined) return;
		
		var parts = events.split('.');
		var left = parts[0];
		if(parts.length == 1) parts.push('.');
		
		for(var i = 1, len = parts.length; i < len; ++i) {
			var ns = parts[i];
	
			var nslist;
			if(ns == '.') nslist = offtmp; //all namespaces
			else {
				nslist = {};
				nslist[ns] = null; //specific namespace
			}
			
			for(var ns in nslist) {
				ns = offtmp[ns];
				if(ns === undefined) continue;
				
				var eventslist;
				if(left.length == 0) eventslist = ns; //all events
				else {
					eventslist = {};
					eventslist[left] = null; //specific event
				}
				
				for(var e in eventslist) {
					var event = ns[e];
					if(event === undefined || (callback_event_check instanceof Function && !callback_event_check(event))) continue;
					
					for(var n = 0; n < event.length; ++n) {
						callback_per_handler(obj, event, n, selector, callback);
					}
					callback_per_event(event, ns, e);
				}
			}
		}
	}
	
	//override .on() to track the event listeners to be able to .offtmp() them later
	jQuery.fn.on = function(events, selector, data, fn, one) {
		//if(arguments.length < 2) throw 'Invalid number of arguments for .on()';
		
		if(events instanceof Object && !(events instanceof String)) {
			if(arguments.length == 2) for(var e in events) this.on(e, events[e]);
			else if(arguments.length == 3) for(var e in events) this.on(e, arguments[1], events[e]);
			else if(arguments.length == 4) for(var e in events) this.on(e, arguments[1], arguments[2], events[e]);
			else if(arguments.length == 5) for(var e in events) this.on(e, arguments[1], arguments[2], events[e], arguments[4]);
			return this;
		}
		
		if(typeof events == 'string' || events instanceof String) {
			if(events.indexOf(',') > 0) {
				return applyCommaSeparatedEvents(this, 'on', events, arguments);
			}
			
			if ( data == null && fn == null ) {
				// ( types, fn )
				fn = selector;
				data = selector = undefined;
			} else if ( fn == null ) {
				if ( typeof selector === 'string' || selector instanceof String ) {
					// ( types, selector, fn )
					fn = data;
					data = undefined;
				} else {
					// ( types, data, fn )
					fn = data;
					data = selector;
					selector = undefined;
				}
			}
			
			if(!fn) return this;
			
			var args = {events: null, selector: selector, data: data, callback: fn, one: one};
			
			var offtmp = this.data('offtmp');
			if(offtmp === undefined) {
				offtmp = {};
				this.data('offtmp', offtmp);
			}
			
			var parts = events.split('.');
			var left = parts[0];
			if(parts.length == 1) parts.push('.');
			
			for(var i = 1, len = parts.length; i < len; ++i) {
				var ns = parts[i];
				args.events = left + (ns == '.' ? '' : '.' + ns);
				
				var namespace = offtmp[ns];
				if(namespace === undefined) {
					namespace = {};
					offtmp[ns] = namespace;
				}
				
				var event = namespace[left];
				if(event === undefined) {
					event = []; //array of arrays to support stacking
					namespace[left] = event;
				}
				else if(event.offtmp) this.ontmp(args.events);
				event.push(args);
			}

		}
		
		return on.call(this, events, selector, data, fn, one);
	};
	
	//override .off() to remove listners from our offtmp event map
	jQuery.fn.off = function(events, selector, callback) {
		//if(arguments.length < 1) throw 'Invalid number of arguments for .off()';
		
		if(events instanceof Object) {
			if(events.preventDefault && events.handleObj) {
				// ( event )  dispatched jQuery.Event
				var handleObj = events.handleObj;
				jQuery( events.delegateTarget ).off(
					handleObj.namespace? handleObj.type + "." + handleObj.namespace : handleObj.type,
					handleObj.selector,
					handleObj.handler
				);
				return this;
			}

			if(arguments.length == 1) for(var e in events) this.off(e, events[e]);
			else if(arguments.length == 2) for(var e in events) this.off(e, arguments[1], events[e]);
			return this;
		}
		
		if(typeof events == 'string' || events instanceof String) {
			if(events.indexOf(',') > 0) {
				return applyCommaSeparatedEvents(this, 'on', events, arguments);
			}
			
			if(arguments.length == 2) {
				if(selector instanceof Function) {
					callback = selector;
					selector = undefined;
				}
			}
			
			applyForEachEvent(this, events, selector, callback,
				function(obj, event, index, selector, callback) {
					var handler = event[index];
					if(handler && (!selector || handler.selector == selector) && (!callback || handler.callback.guid === callback.guid)) {
						event[index] = undefined;
					}
				},
				function(event, namespace, index) {
					var deleted = 0;
					for(var i  = 0; i < event.length; ++i) {
						if(event[i] === undefined) ++deleted;
					}
					if(deleted == event.length) delete namespace[index];
				}
			);
		}

		return off.call(this, events, selector, callback);
	};
	
	//turn on the handlers mapped by .on() for the specific event
	jQuery.fn.offtmp = function(events) {
		if(events.indexOf(',') > 0) {
			return applyCommaSeparatedEvents(this, 'offtmp', events, arguments);
		}
		
		applyForEachEvent(this, events, null, null,
			function(obj, event, index) {
				var handler = event[index];
				if(handler) off.call(obj, handler.events, handler.selector, handler.callback);
			},
			function(event, namespace, index) {
				event.offtmp = true;
			},
			function(event) {
				return event.length > 0;
			}
		);
		
		return this;
	};
	
	//turn on the handlers mapped by .on() for the specific event if .offtmp()'s been called already
	jQuery.fn.ontmp = function(events) {
		if(events.indexOf(',') > 0) {
			return applyCommaSeparatedEvents(this, 'ontmp', events, arguments);
		}
		
		applyForEachEvent(this, events, null, null,
			function(obj, event, index) {
				var handler = event[index];
				if(handler) on.call(obj, handler.events, handler.selector, handler.data, handler.callback, handler.one);
			},
			function(event, namespace, index) {
				event.offtmp = false;
			},
			function(event) {
				return event.offtmp == true;
			}
		);
		
		return this;
	};
	
})();