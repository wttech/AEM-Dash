package com.cognifide.aem.dash.core.launchers;

public interface Launcher {

	/**
	 * Tasks to do
	 */
	public void launch(LauncherProgress progress) throws Exception;

	/**
	 * Get name for finder
	 */
	public String getLabel();

	/**
	 * Get description for finder
	 */
	public String getDescription();
}
