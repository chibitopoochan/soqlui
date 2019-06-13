package com.gmail.chibitopoochan.soqlui.model;

public class ProxySetting {
	// Proxyサーバへの接続情報
	private String username;
	private String password;
	private String host;
	private int port;
	private boolean useProxy;

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
	 * @return host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host セットする host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return port
	 */
	public String getPort() {
		return String.valueOf(port);
	}

	public int getPortNumber() {
		return port;
	}

	/**
	 * @param port セットする port
	 */
	public void setPort(String port) {
		this.port = Integer.parseInt(port);
	}

	public void setPortNumber(int port) {
		this.port = port;
	}

	/**
	 * @return useProxy
	 */
	public boolean useProxy() {
		return useProxy;
	}

	/**
	 * @param useProxy セットする useProxy
	 */
	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}

}
