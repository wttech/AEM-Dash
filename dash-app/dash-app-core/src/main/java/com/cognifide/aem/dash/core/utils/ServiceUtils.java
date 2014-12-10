package com.cognifide.aem.dash.core.utils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class ServiceUtils {

	@SuppressWarnings("unchecked")
	public static <T> T getService(SlingHttpServletRequest request, Class<T> type) {
		SlingBindings bindings = (SlingBindings) request.getAttribute(SlingBindings.class.getName());
		if (bindings != null) {
			SlingScriptHelper sling = bindings.getSling();

			return sling.getService(type);
		} else {
			BundleContext bundleContext = FrameworkUtil.getBundle(type).getBundleContext();
			ServiceReference settingsRef = bundleContext.getServiceReference(type.getName());

			return (T) bundleContext.getService(settingsRef);
		}
	}
}
