package com.gmail.chibitopoochan.soqlui.logic;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;

import com.gmail.chibitopoochan.soqlui.config.ConnectionSettingSet;
import com.gmail.chibitopoochan.soqlui.model.ConnectionSetting;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;

/**
 * 接続設定のロジック.
 * API側とUI側の依存性を下げるために作成
 */
public class ConnectionSettingLogic {
	// クラス共通の参照
	private static final Logger logger = LogUtils.getLogger(ConnectionSettingLogic.class);

	public static final String DEFAULT_NAME = "--";

	private ConnectionSettingSet settings;
	private Map<String, ConnectionSetting> cachedSettings = new HashMap<>();

	public ConnectionSettingLogic() {
		try {
			// 接続情報を取得
			loadSettings();
		} catch (Exception e) {
			// 例外を通知
			logger.error("Initialize error", e);
		}
	}

	/**
	 * 設定ファイルの読み込み
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public void loadSettings() throws IllegalStateException, IOException, XMLStreamException {
		settings = ConnectionSettingSet.getInstance(true);
		cachedSettings = settings
				.getConnectionSettingList()
				.stream()
				.collect(Collectors.toMap(ConnectionSetting::getName, setting -> setting));
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
	 * 接続設定を取得
	 * @param name 接続設定の名称
	 * @return 接続設定（該当する設定が無ければ空の接続設定）
	 */
	public ConnectionSetting getConnectionSetting(String name) {
		return cachedSettings.getOrDefault(name, new ConnectionSetting());
	}

	/**
	 * 前回選択された接続設定を取得
	 * @param string 設定値が存在しない場合の初期値
	 * @return 前回選択した接続設定名
	 */
	public String getSelectedName(String string) {
		return cachedSettings.values()
				.stream()
				.filter(s -> s.isSelected())
				.map(s -> s.getName())
				.findFirst()
				.orElse(string);
	}

	/**
	 * 選択した接続設定を保存
	 * @param name 接続した接続設定名
	 */
	public void setSelectedName(String name) {
		cachedSettings.values().stream().forEach(s -> {
			s.setSelected(name.equals(s.getName()));
		});
		storeSettings();

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
