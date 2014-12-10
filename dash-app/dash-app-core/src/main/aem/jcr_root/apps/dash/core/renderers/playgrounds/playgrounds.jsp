<%@include file="/apps/dash/core/renderers/layout/before.jsp" %><%
%><c:set var="playgroundModel" value="<%= com.cognifide.aem.dash.core.playgrounds.PlaygroundModel.fromRequest(slingRequest) %>" />
<div id="dash-playgrounds" class="container-fluid">
	<div class="page-header">
		<h2>Playgrounds
	</div>

	<div class="panel panel-info">
		<div class="panel-heading">
			<i class="glyphicon glyphicon-info-sign"></i> About
		</div>
		<div class="panel-body">
			<p>
				This feature extends Finder. It is adding a support for multi-instance AEM management.
			</p>
			<p>
				After changing configuration, please manually <a class="dash-menu-reload" href="javascript:">reload menu</a>.
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
						<td>${playgroundModel.manager.label}</td>
						<td>
							<a class="btn btn-default" href="/system/console/configMgr/${playgroundModel.manager.class.name}"
								x-cq-linkchecker="skip">
								<i class="glyphicon glyphicon-wrench"></i> Configure
							</a>
						</td>
					</tr>
					<tr>
						<td>Playground Origin Filter</td>
							<td>
								<a class="btn btn-default" href="/system/console/configMgr/com.cognifide.aem.dash.core.playgrounds.PlaygroundOriginFilter"
									x-cq-linkchecker="skip">
									<i class="glyphicon glyphicon-wrench"></i> Configure
								</a>
							</td>
						</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>

<%@include file="/apps/dash/core/renderers/layout/after.jsp" %>

