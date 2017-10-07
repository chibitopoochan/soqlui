package com.gmail.chibitopoochan.soqlui.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.gmail.chibitopoochan.soqlexec.api.Connector;
import com.gmail.chibitopoochan.soqlui.model.ConnectionSetting;
import com.gmail.chibitopoochan.soqlui.model.DescribeField;
import com.gmail.chibitopoochan.soqlui.model.DescribeSObject;
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
			.map(m -> new DescribeSObject(m.getName(), m.getLabel(), m.getKeyPrefix()))
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

}
