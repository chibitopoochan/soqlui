package com.gmail.chibitopoochan.soqlui.model;

public class ConnectionSetting {
	public static final String ENV_PROD = "Prod";
	public static final String ENV_TEST = "Test";
	public static final String ENV_URL_PROD = "login";
	public static final String ENV_URL_TEST = "test";

	private String name;
	private String username;
	private String password;
	private String token;
	private boolean selected;
	private String environment;
	private String apiVersion;

	/**
	 * @return username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username セットする username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password セットする password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param token セットする token
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name セットする name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return selected
	 */
	public boolean isSelected() {
		return selected;
	}
	/**
	 * @param selected セットする selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	/**
	 * @return environment
	 */
	public String getEnvironment() {
		return environment;
	}
	/**
	 * @param environment セットする environment
	 */
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	/**
	 * @return apiVersion
	 */
	public String getApiVersion() {
		return apiVersion;
	}
	/**
	 * @param apiVersion セットする apiVersion
	 */
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}
	/**
	 * @return is sandbox
	 */
	public boolean isSandbox(){
		return ENV_TEST.equals(environment);
	}

	public String getEnvironmentOfURL() {
		return isSandbox() ? ENV_URL_TEST : ENV_URL_PROD;
	}

}
