var dash = dash || {};

dash.dashboard = {
	context: null,
	doc: null,

	templates: {},
	playgrounds: [],
	recentSearchTerm: '',

	recentPath: $.jStorage.get('recentPath', null),
	recentPlayground: $.jStorage.get('recentPlayground', null),
	recentSearches: $.jStorage.get('recentSearches', []),

	/**
	 * Callback when DOM is ready
	 */
	init: function () {
		var context = $(this);

		$('#dash-frame', context).each(function () {
			var body = $('body', context);
				frame = $('#dash-frame', context),
				menu = $('#dash-menu', context),
				container = $('#dash-container', context);
				
			dash.dashboard.context = context;
			dash.dashboard.doc = frame.get(0).contentWindow;

			// Adapt frame height
			$(window).on('load resize', function () {
				body.css('overflow', 'hidden');
				menu.show();
				frame.height($(window).outerHeight() - menu.outerHeight() - container.outerHeight()).show();
			});

			// Handle closing tool
			$('#dash-closer', context).each(function () {
				var closer = $(this);

				closer.on('click', function (e) {
					window.location.href = dash.dashboard.doc.location.href;
					return false;
				});
			});

			// Search everywhere input
			$('#dash-finder', context).each(function () {
				var finder = $(this),
					phrase = $('input[name=phrase]', finder);

				frame.on('load', function () {
					try {
						// Handle child document URL change
						var url = dash.dashboard.doc.location.href,
							path = dash.dashboard.doc.location.pathname,
							hash = dash.dashboard.doc.location.hash;

						if (hash) {
							path += hash;
						}

						dash.dashboard.recentPath = dash.dashboard.parsePath(path);
						dash.dashboard.recentPlayground = dash.dashboard.detectPlayground(url);
						dash.dashboard.updateLocation(url);

						// Reload dash on configuration change (go back from Felix Web Console)
						frame.contents().find('body.ui-widget').delegate('button', 'click', function () {
							// Give the service some time to restart
							window.setTimeout(function () {
								window.location.reload();
							}, 1000);

							return false;
						});
					} catch (e) {
						// Cross domain location cannot be retrieved due to security exception
						dash.dashboard.updateLocation(false);
					}
				});

				// Download playground data
				$.ajax({
					async: false,
					url: '/bin/dash/playground/manager?action=list',
					dataType: 'json',
					success: function (playgrounds) {
						dash.dashboard.playgrounds = $.map(playgrounds, function (playground, i) {
							playground.index = i;

							return playground;
						});

						if (!playgrounds.length) {
							return;
						}

						dash.dashboard.recentPlayground = playgrounds[0];
					}
				});

				// Typehead search
				phrase.select2({
					placeholder: phrase.data('placeholder'),
					minimumInputLength: 3,
					ajax: {
						url: '/bin/dash/finder/search?action=byPhrase',
						quietMillis: 250,
						dataType: 'json',
						data: function (phrase) {
							return {
								phrase: phrase
							};
						},
						results: function (response) {
							if (response.steps.length) {
								window.console.log('Finder errors:', response.steps);
							}

							return {
								results: response.results
							};
						}
					},
					id: function (result) {
						return result.path;
					},
					formatResult: function (result) {
						var template = dash.dashboard.getTemplate(result.provider);

						result.playgrounds = dash.dashboard.playgrounds;
						result.recentPlayground = dash.dashboard.recentPlayground;
						for (var i = 0; i < result.playgrounds.length; i++) {
							result.playgrounds[i].current = (dash.dashboard.recentPlayground && (result.playgrounds[i].url == dash.dashboard.recentPlayground.url));
						}

						return template(result);
					},
					formatSelection: function (result) {
						return result.label;
					},
					escapeMarkup: function (m) {
						return m;
					}
				}).on('select2-selecting', function (item) {
					var result = item.object,
						playground = result.playground ? result.playground : true,
						url = result.contextPath ? result.contextPath : item.val;

					if (result.provider.toLowerCase() == 'launcher') {
						$.ajax({
							url: result.path,
							data: {
								phrase: dash.dashboard.recentSearchTerm,
							},
							dataType: 'json',
							success: function (response) {
								var template = Handlebars.compile($('#dash-finder-launcher-result').html());

								bootbox.alert(template(response));
							}
						});
					} else {
						dash.dashboard.open(url, playground);
					}
				});

				// Dirty (yes I know...selector not even scoped)
				$(document).delegate('.select2-input', 'change keyup', function(e) {
					var phrase = $(this).val();

					// Track only with minimum length
					if (phrase.length >= 3) {
						dash.dashboard.recentSearchTerm = phrase;
					}
                });

				// Overwrite default select action to provide link clicking (on default it is prevented)
				var picker = phrase.data('select2');

				picker.onSelect = (function (fn) {
					return function (data, options) {
						var target = null;

						if (options != null) {
							target = $(options.target);
						}

						// Just add info about playground in which item should be opened
						if (target) {
							// Handle icon as event target
							if (target.is('i')) {
								target = target.closest('a');
							}

							if (target.hasClass('playground')) {
								arguments[0].contextPath = target.attr('href');
							}
						}

						return fn.apply(this, arguments);
					}
				})(picker.onSelect);

				// Menu actions
				$('.dash-finder-credentials', context).each(function () {
					var trigger = $(this),
						template = Handlebars.compile($('#dash-finder-credentials').html());

					trigger.popover({
						trigger: 'click',
						placement: 'bottom',
						html: true,
						content: function () {
							return template(dash.dashboard.recentPlayground);
						}
					});

					// Close on somewhere outside click
					$('body').on('click', function (e) {
						if (!trigger.is(e.target) && trigger.has(e.target).length === 0 && $('.popover').has(e.target).length === 0) {
							trigger.popover('hide');
						}
					});
				});

				$('#dash-finder-playgrounds', context).each(function () {
					var group = $(this),
						button = $('button', group);
						list = $('ul', group);
						template = Handlebars.compile($('#dash-finder-playground', context).html());

					button.on('click', function () {
						list.empty().append(template({
							path: dash.dashboard.recentPath,
							playgrounds: dash.dashboard.playgrounds
						}));
					});
				});

				// Location change
				$('input[name=location]', context).on('click', function () {
					bootbox.prompt({
						title: 'Change playground location',
						value: dash.dashboard.recentPath,
						callback: function (path) {
							if (path && path != dash.dashboard.recentPath) {
								dash.dashboard.open(path, true);
							}
						}
                    });
				});

				// Track links with dash frame as target
				body.delegate('a[target=dash-frame]', 'click', function () {
					dash.dashboard.open($(this).attr('href'));

					return false;
				});

				dash.dashboard.updateRecentSearches();
				dash.dashboard.openStartup();
			});
		});
	},

	/**
	 * Callback after all stylesheets, scripts and images are loaded
	 */
	load: function () {
	},

	/**
	 * Troubleshoot path, eliminate possible bugs in paths (double hashes, slashes, etc)
	 */
	parsePath : function (path) {
		if (!path) {
			return '/';
		}

        var link = document.createElement('a');
        link.href = path;

        var path = link.pathname;
        if (link.hash) {
        	path += link.hash;
        }

        return path;
    },

	parseCredentials: function (path) {
		var parts = /\w+:\w+@/.exec(path);

		return (parts && parts.length) ? parts[0] : '';
	},

	/**
	 * Open path on specified playground in dash frame (core functionality)
	 */
	open: function (path, playground) {
		if (path === true) {
			path = this.recentPath;
		}

		if (playground === true) {
			playground = this.recentPlayground;
		}

		// Force playground if it is defined directly in path
		this.recentPlayground = this.detectPlayground(path, playground);
		this.recentPath = this.parsePath(path);

		// Change page in frame, update hashes on both parent and child document (for some AEM tools)
		var local = (!this.playgrounds.length || (this.playgrounds[0].url == this.recentPlayground.url)),
			credentials = this.parseCredentials(path),
			url = (local ? this.recentPath : ("//" + credentials + this.recentPlayground.url + this.recentPath)),
			parts = url.split("#");

		window.console.log("Opening URL: " + url);

		try {
			this.doc.location.href = url;
			if (parts.length == 2) {
				window.location.hash = parts[1];
				this.doc.location.hash = parts[1];
			}
		} catch (e) {
			// Cross domain location hash cannot be updated, affects CRX DE (on CQ 5.5) on remote playground
		}

		this.updateLocation(url);

		// Save in recent searches
		var duplicatedSearches = $.grep(this.recentSearches, function (search) {
			return search.url == url;
		});

		if (!duplicatedSearches.length) {
			this.recentSearches.unshift({
				url: url,
				path: this.recentPath,
				playground : this.recentPlayground
			});
		}

		if (this.recentSearches.length > 10) {
			this.recentSearches.splice(-1, 1);
		}

		$.jStorage.set('startupUrl', url);
		$.jStorage.set('recentSearches', this.recentSearches);
		$.jStorage.set('recentPath', this.recentPath);
		$.jStorage.set('recentPlayground', this.recentPlayground);

		this.updateRecentSearches();

		return true;
	},

	/**
	 * Get compiled, cached template by provider name (or default)
	 */
	getTemplate: function (provider) {
		provider = provider ? provider.toCamelCase() : 'default;'

		if (this.templates[provider]) {
			return this.templates[provider];
		}

		var source = $('#dash-finder-search-' + provider, this.context);
		if (!source.size()) {
			source = $('#dash-finder-search-default', this.context);
		}

		var template = Handlebars.compile(source.html());
		this.templates[provider] = template;

		return template;
	},

	/**
	 * Update path with playground name as prefix
	 */
	updateLocation: function (url) {
		var input = $('#dash-finder input[name=location]', this.context),
			group = input.closest('.control-group');

		if (url === false) {
			group.toggleClass('has-error', true);
		} else {
			group.toggleClass('has-error', false);

			var path = this.parsePath(url),
				playground = this.detectPlayground(url),
				location =  '(' + playground.name + ') ' + path;

			input.val(location);
		}
	},

	/**
	 * Render history about recently searched phrases
	 */
	updateRecentSearches: function () {
		var searches = $('#dash-finder-recent-searches', this.context),
			template = Handlebars.compile($('#dash-finder-recent-search', this.context).html());

		searches.empty().append(template(this.recentSearches));

		$('.dash-finder-recent-search', searches).on('click', function () {
			var search = $(this),
				path = search.data('path'),
				playground = dash.dashboard.playgrounds[search.data('playground')];

			dash.dashboard.open(path, playground);

			return false;
		});
	},

	/**
	 * Determine playground by path (use default if it is not possible)
	 */
	detectPlayground: function (href, playground) {
		if (href) {
			for (var i = 0; i < this.playgrounds.length; i++) {
				if (href.indexOf(this.playgrounds[i].url) != -1) {
					playground = this.playgrounds[i];
					break;
				}
			}
		}

		if (!playground) {
			if (this.playgrounds.length) {
				playground = this.playgrounds[0];
			} else if (this.recentPlayground) {
				playground = this.recentPlayground;
			}
		}

		return playground;
	},

	/**
	 * Open dash on previously visited page even it was closed in the meantime
	 */
	openStartup: function () {
		var url = $.jStorage.get('startupUrl');
		if (url) {
			this.open(url);
		}
	}
}

$(document).ready(dash.dashboard.init);
$(window).load(dash.dashboard.load);