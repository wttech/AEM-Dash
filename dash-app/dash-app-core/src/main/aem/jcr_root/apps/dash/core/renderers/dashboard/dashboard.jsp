<%@include file="/apps/dash/core/renderers/layout/before.jsp" %>
<%@include file="/apps/dash/core/renderers/layout/menu.jsp" %>
<c:set var="searchModel" value="<%= com.cognifide.aem.dash.core.finder.SearchModel.fromRequest(slingRequest) %>" />
<div id="dash-container"></div>
<iframe id="dash-frame" name="dash-frame" src="${searchModel.search.startupPath}"/>
<%@include file="/apps/dash/core/renderers/layout/after.jsp" %>