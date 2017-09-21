package com.gmail.chibitopoochan.soqlui.model;

import com.gmail.chibitopoochan.soqlexec.api.Connector;

public class SOQLConnection {
	private Connector connection;

	public Connector getConnection() {
		return connection;
	}

	public void setConnection(Connector connection) {
		this.connection = connection;
	}

}
