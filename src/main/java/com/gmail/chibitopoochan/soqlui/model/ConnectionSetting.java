package com.gmail.chibitopoochan.soqlui.model;

public class ConnectionSetting {
	private String name;
	private String username;
	private String password;
	private String token;
	private String authEndPoint;

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
	 * @return authEndPoint
	 */
	public String getAuthEndPoint() {
		return authEndPoint;
	}
	/**
	 * @param authEndPoint セットする authEndPoint
	 */
	public void setAuthEndPoint(String authEndPoint) {
		this.authEndPoint = authEndPoint;
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

}
