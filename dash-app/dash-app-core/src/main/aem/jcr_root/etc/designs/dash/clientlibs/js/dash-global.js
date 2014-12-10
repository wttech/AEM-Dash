var dash = dash || {};

dash.global = {

	/**
	 * Initialize libraries
	 */
	bootstrap: function () {
		// Error dialog on ajax error
		$.ajaxSetup({
			cache: false,
			error: function (xhr) {
				bootbox.alert(xhr.statusText + ' (' + xhr.status + ')');
			}
		});

		// Custom handlebars extensions
		Handlebars.registerHelper('json', function (obj) {
        	return JSON.stringify(obj, undefined, 2);
        });

        Handlebars.registerHelper('notEmptyObject', function (obj, options) {
        	if (!$.isEmptyObject(obj)) {
				return options.fn(this);
			}
        });

        Handlebars.registerHelper('math', function (lvalue, operator, rvalue, options) {
            lvalue = parseFloat(lvalue);
            rvalue = parseFloat(rvalue);

            return {
                '+': lvalue + rvalue,
                '-': lvalue - rvalue,
                '*': lvalue * rvalue,
                '/': lvalue / rvalue,
                '%': lvalue % rvalue
            }[operator];
        });

        // String extensions
		String.prototype.toCamelCase = function() {
			var s = this.replace(/([^a-zA-Z0-9_\- ])|^[_0-9]+/g, "").trim().toLowerCase();

			s = s.replace(/([ -]+)([a-zA-Z0-9])/g, function(a,b,c) {
				return c.toUpperCase();
			});
			s = s.replace(/([0-9]+)([a-zA-Z])/g, function(a,b,c) {
				return b + c.toUpperCase();
			});

			return s;
		}
	},

	/**
 	 * Callback when DOM is ready
 	 */
	init: function () {
		var context = $(this),
			body = $('body', context);

		// Nicer tooltips
		$('[rel=tooltip]', context).each(function () {
			var obj = $(this);

			obj.tooltip({
				placement: function () {
					var offset = obj.offset(),
						top = offset.top,
						left = offset.left,
						height = $(document).outerHeight(),
						width = $(document).outerWidth(),
						vert = 0.5 * height - top,
						vertPlacement = vert > 0 ? 'bottom' : 'top',
						horiz = 0.5 * width - left,
						horizPlacement = horiz > 0 ? 'right' : 'left';

					return Math.abs(horiz) > Math.abs(vert) ? horizPlacement : vertPlacement
				}
			});
		});

		// Detect whether cross domain frame communication is supported
		if (!window.postMessage) {
			return;
		}

		if (dash.global.framed()) {
			// Track links with dash frame as target
			body.delegate('a[target=dash-frame]', 'click', function () {
				window.parent.postMessage({
					key: 'aem-dash',
					method: 'open',
					args: [$(this).attr('href')]
				}, '*');

				return false;
			});
		} else {
			// Dash frame as message dispatcher
			window.addEventListener('message', function (message) {
				var key = message.data.key,
					method = message.data.method,
					args = message.data.args;

				if (!key) {
					return;
				}

				if (!method || !args) {
					window.console.log("Invalid dash post message data");
				}

				if (!dash.dashboard.hasOwnProperty(method)) {
					window.console.log("Invalid dash method requested", method);
				}

				if (!$.isArray(args)) {
					window.console.log("Post message arguments should be an array");
				}

				dash.dashboard[method].apply(dash.dashboard, args);
			}, false);
		}
	},

	/**
	 * Callback after all stylesheets, scripts and images are loaded
	 */
	load: function () {
	},

	/**
	 * Detect whether we are on child frame (if not on top frame)
	 */
	framed: function () {
		try {
			return window.self !== window.top;
		} catch (e) {
			return true;
		}
	}
}

$(document).ready(dash.global.bootstrap);
$(document).ready(dash.global.init);
$(window).load(dash.global.load);