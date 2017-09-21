package com.gmail.chibitopoochan.soqlui.logic;

import com.gmail.chibitopoochan.soqlexec.api.Connector;
import com.gmail.chibitopoochan.soqlui.model.ConnectionSetting;

public class ConnectionLogic {
	private Connector connector;

	/**
	 * Salesforceへの接続
	 * @param selected 使用する接続情報
	 * @throws Exception ログイン時の例外
	 */
	public void connect(ConnectionSetting selectedSetting) throws Exception {
		connector = Connector.login(
				selectedSetting.getUsername(),
				selectedSetting.getPassword() + selectedSetting.getToken(),
				selectedSetting.getAuthEndPoint());

	}

	/**
	 * Salesforceへの接続解除
	 */
	public void disconnect() {
		connector.logout();
	}

}
