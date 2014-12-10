package com.cognifide.aem.dash.core.launchers;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;

import com.cognifide.aem.dash.core.finder.Phrase;
import com.cognifide.aem.dash.core.utils.Dash;
import com.cognifide.aem.dash.core.utils.JSONUtils;

@SlingServlet(paths = { LauncherManagerServlet.PATH }, label = Dash.PREFIX + " Launcher Manager Servlet")
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Launcher servlet for managing registered launchers"),
		@Property(name = Constants.SERVICE_VENDOR, value = Dash.SERVICE_VENDOR) })
public class LauncherManagerServlet extends SlingAllMethodsServlet {

	public static final String PATH = "/bin/dash/launcher/manager";

	@Reference
	private LauncherManager manager;

	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		final String action = StringUtils.trimToEmpty(request.getParameter("action"));

		if (action.equals("execute")) {
			final String launcherName = request.getParameter("launcher");
			final Launcher launcher = manager.getLauncher(launcherName);
			final Phrase phrase = new Phrase(request.getParameter("phrase"));

			if (launcher == null) {
				JSONUtils.writeMessage(response, "error",
						String.format("Launcher '%s' not found", launcherName));
			} else {
				JSONUtils.writeJson(response, manager.launch(launcher, phrase.getOptions()));
			}
		} else {
			JSONUtils
					.writeMessage(response, "error", String.format("Invalid action specified: '%s'", action));
		}
	}
}
