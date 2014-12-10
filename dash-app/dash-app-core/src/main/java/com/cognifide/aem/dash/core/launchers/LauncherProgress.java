package com.cognifide.aem.dash.core.launchers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ocpsoft.prettytime.PrettyTime;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class LauncherProgress {

	private String launcher;

	private Exception exception;

	private Date start;

	private Date stop;

	private long duration;

	private String elapsed;

	private Map<String, Object> context = Maps.newHashMap();

	private final Map<String, String> options;

	private List<Object> steps = Lists.newArrayList();

	public LauncherProgress(Launcher launcher, Map<String, String> options) {
		this.launcher = LauncherUtils.parseLabel(launcher.getLabel());
		this.options = options;
	}

	public void start() {
		this.start = new Date();
	}

	public void stop() {
		this.stop = new Date();
		this.duration = stop.getTime() - start.getTime();
		this.elapsed = new PrettyTime(start).format(stop);
	}

	public String getLauncher() {
		return launcher;
	}

	public Date getStart() {
		return start;
	}

	public Date getStop() {
		return stop;
	}

	public long getDuration() {
		return duration;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public void step(String message) {
		steps.add(message);
	}

	public List<Object> getSteps() {
		return steps;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	public String getElapsed() {
		return elapsed;
	}

	public Map<String, String> getOptions() {
		return options;
	}
}
