<%@include file="/apps/dash/core/renderers/layout/before.jsp" %>
<c:set var="searchModel" value="<%= com.cognifide.aem.dash.core.finder.SearchModel.fromRequest(slingRequest) %>" />
<div id="dash-finder" class="container-fluid">
	<div class="page-header">
		<h2>Finder
	</div>

	<div class="panel panel-info">
		<div class="panel-heading">
			<i class="glyphicon glyphicon-info-sign"></i> About
		</div>
		<div class="panel-body">
			<p> 
				Is a tool which provides &quot;search everywhere&quot; feature. Configuration is available via following OSGi services.
			</p>
			<p>
				If you want to improve search performance, you can disable some provider or change content root of Site Admin / CRX DE provider.
				After enabling / configuring some provider, please manually <a class="dash-finder-reset" href="/bin/dash/finder/search?action=reset">clear cache</a>. Recently used phrases are cached (LRU).
			</p>
		</div>
	</div>

	<div class="panel panel-default">
		<div class="panel-heading"> 
			<i class="glyphicon glyphicon-cog"></i> Configuration
		</div>

		<div class="panel-body">
			<table class="table table-hover">
				<thead>
					<tr>
						<th>Service</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>${searchModel.search.label}</td>
						<td>
							<a class="btn btn-default" href="/system/console/configMgr/${searchModel.search.class.name}"
								x-cq-linkchecker="skip">
								<i class="glyphicon glyphicon-wrench"></i> Configure
							</a>

							<a class="btn btn-default dash-finder-reset" href="/bin/dash/finder/search?action=reset"
								rel="tooltip" title="Clear Cache">
								<i class="glyphicon glyphicon-refresh"></i> Clear cache
							</a>
						</td>
					</tr>
					<c:forEach var="provider" items="${searchModel.providers}">
						<tr>
							<td>${provider.label}</td>
							<td>
								<a class="btn btn-default" href="/system/console/configMgr/${provider.class.name}"
									x-cq-linkchecker="skip">
									<i class="glyphicon glyphicon-wrench"></i> Configure
								</a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>

<%@include file="/apps/dash/core/renderers/layout/after.jsp" %>
