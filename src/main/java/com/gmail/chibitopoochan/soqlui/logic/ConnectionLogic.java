package com.gmail.chibitopoochan.soqlui.logic;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.gmail.chibitopoochan.soqlexec.api.Connector;
import com.gmail.chibitopoochan.soqlui.config.ApplicationSettingSet;
import com.gmail.chibitopoochan.soqlui.model.ConnectionSetting;
import com.gmail.chibitopoochan.soqlui.model.DescribeField;
import com.gmail.chibitopoochan.soqlui.model.DescribeSObject;
import com.gmail.chibitopoochan.soqlui.model.ProxySetting;
import com.sforce.ws.ConnectionException;

public class ConnectionLogic {
	private static final String SERVER_URL = ApplicationSettingSet.getInstance().getSetting().getConnectionURL();
	private Optional<Connector> connector = Optional.empty();

	/**
	 * Salesforceへの接続
	 * @param selected 使用する接続情報
	 * @throws Exception ログイン時の例外
	 */
	public void connect(ConnectionSetting selectedSetting, boolean reset, boolean useTooling, String local) throws Exception {
		if(reset) {
			Connector.resetProxySetting();
		}
		connect(selectedSetting, useTooling, local);
	}

	/**
	 * Salesforceへの接続
	 * @param selected 使用する接続情報
	 * @param proxy プロキシ接続
	 * @throws Exception ログイン時の例外
	 */
	public void connect(ConnectionSetting selectedSetting, ProxySetting proxy, boolean useTooling, String local) throws Exception {
		Connector.setProxySetting(proxy.getHost(), proxy.getPortNumber(), proxy.getUsername(), proxy.getPassword());
		connect(selectedSetting, useTooling, local);
	}

	private void connect(ConnectionSetting selectedSetting, boolean useTooling, String local) throws Exception {
		String env = selectedSetting.getEnvironmentOfURL();
		String api = selectedSetting.getApiVersion();

		connector = Optional.of(Connector.login(
				selectedSetting.getUsername(),
				selectedSetting.getPassword() + selectedSetting.getToken(),
				String.format(SERVER_URL, env, useTooling ? "T" : "u" ,api),
				useTooling,local));
	}

	/**
	 * SOQLの実行
	 * @param soql SOQL
	 * @param all 削除レコードも取得するならtrue
	 * @param batchSize 取得レコードサイズ
	 * @param join サブクエリの取得方法
	 * @return 実行結果
	 * @throws ConnectionException 接続エラー
	 */
	public List<Map<String,String>> execute(String soql, boolean all, int batchSize, boolean join) throws ConnectionException {
		if(!connector.isPresent()) {
			return new LinkedList<>();
		}

		return connector.get().execute(soql, all, batchSize, join);
	}

	/**
	 * 残りのレコードを取得.
	 * @return 残りのレコード。無ければ０件
	 * @throws ConnectionException 接続エラー
	 */
	public List<Map<String,String>> executeMore() throws ConnectionException {
		if(!connector.isPresent()) {
			return new LinkedList<>();
		}

		return connector.get().executeMore();
	}

	/**
	 * Salesforceへの接続解除
	 */
	public void disconnect() {
		connector.ifPresent(c -> c.logout());
		connector = Optional.empty();
	}

	/**
	 * オブジェクト一覧の取得
	 * @return オブジェクトの一覧
	 * @throws ConnectionException 接続エラー
	 */
	public List<DescribeSObject> getSObjectList() throws ConnectionException {
		List<DescribeSObject> list = new LinkedList<>();

		if(connector.isPresent()) {
			list = connector.get()
			.getDescribeSObjects()
			.stream()
			.map(m -> new DescribeSObject(m))
			.collect(Collectors.toList());
		}

		return list;

	}

	/**
	 * 項目一覧の取得
	 * @param name オブジェクト名
	 * @return 項目一覧
	 * @throws ConnectionException 接続エラー
	 */
	public List<DescribeField> getFieldList(String name) throws ConnectionException {
		List<DescribeField> list = new LinkedList<>();

		if(connector.isPresent()) {
			list = connector.get().getDescribeFields(name).stream()
					.map(m -> new DescribeField(m)).collect(Collectors.toList());
		}

		return list;
	}

	/**
	 * ユーザ情報の取得
	 * @return ユーザ情報
	 * @throws ConnectionException 接続エラー
	 */
	public Map<String, String> getUserInfo() throws ConnectionException {
		Map<String,String> userInfo = new HashMap<>();

		if(connector.isPresent()) {
			userInfo = connector.get().getUserInfo();
		}

		return userInfo;
	}

	/**
	 * レコードの行数を取得
	 * @return 行数
	 */
	public int getSize() {
		int size = 0;
		if(connector.isPresent()) {
			size = connector.get().getSize();
		}

		return size;
	}

	public String getServerURL() {
		String url = "";
		if(connector.isPresent()) {
			url = connector.get().getServerURL();
			url = url.substring(0, url.indexOf("/services"));
			url = url.replace("https://", "");
		}
		return url;
	}

	public String getSessionId() {
		String sessionId = "";
		if(connector.isPresent()) {
			sessionId = connector.get().getSessionId();
		}
		return sessionId;
	}

}
