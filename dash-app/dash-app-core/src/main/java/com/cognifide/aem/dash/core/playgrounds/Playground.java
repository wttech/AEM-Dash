package com.cognifide.aem.dash.core.playgrounds;

public class Playground {

	public static final String INSTANCE_AUTHOR = "author";

	private String name;

	private String url;

	private String username;

	private String password;

	private boolean author;

	private String loginUrl;

	public Playground(String name, String url) {
		this.name = name;
		this.url = url;
		this.author = name.toLowerCase().contains(INSTANCE_AUTHOR);
	}

	public Playground(String name, String url, String username, String password) {
		this(name, url);
		this.username = username;
		this.password = password;
		this.loginUrl = String.format("%s:%s@%s", username, password, url);
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public boolean isAuthor() {
		return author;
	}
}
