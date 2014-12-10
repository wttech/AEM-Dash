<script id="dash-finder-launcher-result" type="text/x-handlebars-template">
	<h3>Launcher result</h3>

	<table class="table table-striped">
		<tr>
			<th>Name</th>
			<td>{{launcher}}</td>
		</tr>
		{{#notEmptyObject options}}
			<tr>
				<th>Options</th>
				<td>
					<table class="table table-bordered">
						<tr>
							<th>Name</th>
							<th>Value</th>
						</tr>
						{{#each options}}
							<tr>
								<td>{{@key}}</td>
								<td>{{this}}</td>
							</tr>
						{{/each}}
					</table>
				</td>
			</tr>
		{{/notEmptyObject}}
		<tr>
			<th>Elapsed time:</th>
			<td>{{elapsed}} ({{duration}} ms)</td>
		</tr>
		<tr>
			<th>Started at:</th>
			<td>{{start}}</td>
		</tr>
		<tr>
			<th>Finished at:</th>
			<td>{{stop}}</td>
		</tr>
	</table>

	<h3>Progress steps</h3>

	<table class="table table-striped">
			<tr>
				<th>#</th>
				<th>Info</th>
			</tr>
			{{#each steps}}
				<tr>
					<td>{{math @index "+" 1}}</td>
					<td>{{this}}</td>
				</tr>
			{{/each}}
	</table>

	{{#notEmptyObject context}}
		<h3>Context</h3>

		<table class="table table-striped">
			<thead>
				<tr>
					<th>Property</th>
					<th>Value</th>
				</tr>
			</thead>
			<tbody>
				{{#each context}}
					<tr>
						<td>{{@key}}</td>
						<td><pre class="pre-scrollable">{{json this}}</pre></td>
					</tr>
				{{/each}}
			</tbody>
		</table>
	{{/notEmptyObject}}
</script>

<script id="dash-finder-credentials" type="text/x-handlebars-template">
	{{#if this}}
		<table class="table small">
			<tr>
				<th>Playground:</th>
				<td>{{name}}</td>
			</tr>
			<tr>
				<th>URL:</th>
				<td>
					<a href="//{{loginUrl}}/system/console/configMgr" target="dash-frame"
						rel="tooltip" title="Login and open console on that instance">{{url}}</a>
				</td>
			</tr>
			<tr>
				<th>Username:</th>
				<td>{{username}}</td>
			</tr>
			<tr>
				<th>Password:</th>
				<td>{{password}}</td>
			</tr>
		</table>
	{{else}}
		<p class="small">No recent playground used</p>
	{{/if}}
</script>

<script id="dash-finder-playground" type="text/x-handlebars-template">
	{{#each playgrounds}}
		<li class="dash-finder-playground" data-playground="{{@index}}" tabindex="-1">
			<a class="small" target="dash-frame" href="//{{url}}{{../path}}" x-cq-linkchecker="skip">{{name}}</a>
		</li>
	{{else}}
		<li><a tabindex="-1" href="javascript:" class="small">Lack of playgrounds</a></li>
	{{/each}}
</script>

<script id="dash-finder-recent-search" type="text/x-handlebars-template">
	{{#each this}}
		<li class="dash-finder-recent-search" data-playground="{{playground.index}}" data-path="{{path}}" tabindex="-1">
			<a class="small" target="dash-frame" href="//{{url}}" x-cq-linkchecker="skip">
				<strong>{{playground.name}}</strong><br>
				{{path}}
			</a>
		</li>
	{{else}}
		<li><a tabindex="-1" href="javascript:" class="small">Lack of searches</a></li>
	{{/each}}
</script>

<script id="dash-finder-search-default" type="text/x-handlebars-template">
	<div class="dash-finder-search">
		<div>
			<span class="badge pull-right">{{provider}}</span>
			<strong>{{label}}</strong>
		</div>
		<div class="small">
			{{path}}
			{{#if description}}
				<br>{{description}}
			{{/if}}
		</div>
		<div>
			{{#if playable}}
				{{#each playgrounds}}
					<div class="btn-group">
						<a href="//{{url}}{{../path}}" class="playground playground-internal btn btn-xs btn-default"
							data-playground="{{@index}}" data-path="{{../path}}"
							title="URL: {{url}}, Username: {{username}}, Password: {{password}}">
							{{name}}
						</a>
					</div>
				{{/each}}
			{{/if}}
		</div>
	</div>
</script>

<script id="dash-finder-search-siteAdmin" type="text/x-handlebars-template">
	<div class="dash-finder-search">
		<div>
			<span class="badge pull-right">{{provider}}</span>
			<strong>{{label}}</strong>
		</div>
		<div class="small">
			{{path}}
			{{#if description}}
				<br>{{description}}
			{{/if}}
		</div>
		<div>
			<a href="{{context/preview}}" class="playground btn btn-xs btn-default"
				target="dash-frame" rel="tooltip" title="Preview on current playground">
				<i class="glyphicon glyphicon-zoom-in"></i>
			</a>

			{{#if playable}}
				{{#each playgrounds}}
					<div class="btn-group">
						<a href="//{{url}}{{../path}}" class="playground btn btn-xs btn-default"
							target="dash-frame" rel="tooltip" title="Open in Site Admin on {{name}}">{{name}}</a>

						<a href="//{{url}}{{../context/preview}}" class="playground btn btn-xs btn-default"
							target="dash-frame" rel="tooltip" title="Preview on {{name}}">
							<i class="glyphicon glyphicon-zoom-in"></i>
						</a>
					</div>
				{{/each}}
			{{/if}}
		</div>
	</div>
</script>

<script id="dash-finder-search-launcher" type="text/x-handlebars-template">
	<div class="dash-finder-search">
		<div>
			<span class="badge pull-right">{{provider}}</span>
			<strong>{{label}}</strong>
		</div>
		<div class="small">
			{{path}}
			{{#if description}}
				<br>{{description}}
			{{/if}}
		</div>
	</div>
</script>