package com.gmail.chibitopoochan.soqlui.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.gmail.chibitopoochan.soqlexec.api.Connector;
import com.gmail.chibitopoochan.soqlui.model.ConnectionSetting;
import com.sforce.ws.ConnectionException;

public class ConnectionLogic {
	private Optional<Connector> connector = Optional.empty();

	/**
	 * Salesforceへの接続
	 * @param selected 使用する接続情報
	 * @throws Exception ログイン時の例外
	 */
	public void connect(ConnectionSetting selectedSetting) throws Exception {
		connector = Optional.of(Connector.login(
				selectedSetting.getUsername(),
				selectedSetting.getPassword() + selectedSetting.getToken(),
				selectedSetting.getAuthEndPoint()));

	}

	/**
	 * SOQLの実行
	 * @param soql SOQL
	 * @param all 削除レコードも取得するならtrue
	 * @param batchSize 取得レコードサイズ
	 * @return 実行結果
	 * @throws ConnectionException 接続エラー
	 */
	public List<Map<String,String>> execute(String soql, boolean all, int batchSize) throws ConnectionException {
		if(!connector.isPresent()) {
			return new LinkedList<>();
		}

		return connector.get().execute(soql, all, batchSize);
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

}
