<%@include file="/apps/dash/core/renderers/layout/before.jsp" %>
<div id="dash-container">
	<div id="dash-delacroix">
		<div class="btn-group">
		  <button type="button" class="btn btn-default export-package"><i class="glyphicon glyphicon-floppy-save"></i> Export as package</button>
		  <button type="button" class="btn btn-default import-package"><i class="glyphicon glyphicon-floppy-open"></i> Import from package</button>
		  <button type="button" class="btn btn-default export-xml"><i class="glyphicon glyphicon-export"></i> Export as XML</button>
		  <button type="button" class="btn btn-default import-xml"><i class="glyphicon glyphicon-import"></i> Import from XML</button>
		</div>
	</div>
</div>
<iframe id="dash-frame" name="dash-frame" src="/crx/de"/>
<%@include file="/apps/dash/core/renderers/layout/after.jsp" %>