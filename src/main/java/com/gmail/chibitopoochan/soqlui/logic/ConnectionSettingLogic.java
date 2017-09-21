package com.gmail.chibitopoochan.soqlui.logic;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import com.gmail.chibitopoochan.soqlui.config.ConnectionSettingSet;
import com.gmail.chibitopoochan.soqlui.model.ConnectionSetting;

/**
 * 接続設定のロジック.
 * API側とUI側の依存性を下げるために作成
 */
public class ConnectionSettingLogic {
	private ConnectionSettingSet settings;
	private Map<String, ConnectionSetting> cachedSettings = new HashMap<>();

	/**
	 * 設定ファイルの読み込み
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public ConnectionSettingLogic() throws IllegalStateException, IOException, XMLStreamException {
		settings = ConnectionSettingSet.getInstance(true);
		loadSettings();
	}

	/**
	 * 接続設定の名称一覧を取得
	 * @return 名称一覧
	 */
	public List<String> getNameOption() {
		List<String> list = new LinkedList<>();
		cachedSettings.keySet().forEach(key -> list.add(key));
		return list;
	}

	/**
	 * 設定ファイルの設定有無
	 * @return 設定があればtrue
	 */
	public boolean hasSetting() {
		return !cachedSettings.isEmpty();
	}

	/**
	 * 接続設定の名前が既に登録されているか確認
	 * @param name 接続設定名
	 * @return 存在するならtrue
	 */
	public boolean isExists(String name) {
		return cachedSettings.containsKey(name);
	}

	/**
	 * 接続設定を持っていなければ、設定ファイルから取得
	 */
	public void loadSettings() {
		cachedSettings = settings
				.getConnectionSettingList()
				.stream()
				.collect(Collectors.toMap(ConnectionSetting::getName, setting -> setting));
	}

	/**
	 * 接続設定を取得
	 * @param name 接続設定の名称
	 * @return 接続設定（該当する設定が無ければ空の接続設定）
	 */
	public ConnectionSetting getConnectionSetting(String name) {
		return cachedSettings.getOrDefault(name, new ConnectionSetting());
	}

	/**
	 * 設定の更新（または追加）
	 * @param setting 登録する設定
	 */
	public void upsert(ConnectionSetting setting) {
		cachedSettings.put(setting.getName(), setting);
		storeSettings();
	}

	/**
	 * 接続設定の削除
	 * @param string
	 */
	public void delete(String key) {
		cachedSettings.remove(key);
		storeSettings();
	}

	/**
	 * ファイルへの永続化
	 */
	private void storeSettings() {
		// ファイルに書き出す
		settings.setConnectionSetting(new LinkedList<>(cachedSettings.values()));
		try {
			settings.storeConfiguration();
		} catch (IllegalStateException | XMLStreamException | IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

}
