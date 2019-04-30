package com.gmail.chibitopoochan.soqlui.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.gmail.chibitopoochan.soqlui.logic.ConnectionLogic;
import com.gmail.chibitopoochan.soqlui.logic.ProxySettingLogic;
import com.gmail.chibitopoochan.soqlui.model.ConnectionSetting;
import com.gmail.chibitopoochan.soqlui.model.DescribeSObject;
import com.gmail.chibitopoochan.soqlui.model.ProxySetting;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ConnectService extends Service<Void> {
	private static final Logger logger = LogUtils.getLogger(ConnectService.class);

	/**
	 * メタ情報のプロパティ
	 */
	private ListProperty<DescribeSObject> describeSObjectList = new SimpleListProperty<>(this, "describeSObjectList");
	public void setDescribeSObjectList(List<DescribeSObject> elements) {
		describeSObjectList.setValue(FXCollections.observableArrayList(elements));
	}

	public List<DescribeSObject> getDescribeSObjectList() {
		return describeSObjectList.getValue();
	}

	public ListProperty<DescribeSObject> describeSObjectListProperty() {
		return describeSObjectList;
	}

	/**
	 * ユーザ情報のプロパティ
	 */
	private MapProperty<String, String> userInfoMap = new SimpleMapProperty<>(this, "userInfoMap");
	public void setUserInfoMap(Map<String, String> elements) {
		userInfoMap.setValue(FXCollections.observableMap(elements));
	}

	public Map<String, String> getUserInfoMap() {
		return userInfoMap.getValue();
	}

	public MapProperty<String, String> userInfoMap() {
		return userInfoMap;
	}

	/**
	 * 接続切断状態のプロパティ
	 */
	private BooleanProperty closing = new SimpleBooleanProperty(true);
	public void setClosing(boolean close) {
		closing.set(close);
	}

	public boolean isClosing() {
		return closing.get();
	}

	public BooleanProperty closingProperty() {
		return closing;
	}

	/**
	 * 接続のプロパティ
	 */
	private ObjectProperty<ConnectionLogic> connectionLogic = new SimpleObjectProperty<ConnectionLogic>(this, "connectionLogic", new ConnectionLogic());
	public void setConnectionLogic(ConnectionLogic logic) {
		connectionLogic.set(logic);
	}

	public ConnectionLogic getConnectionLogic() {
		return connectionLogic.get();
	}

	public ObjectProperty<ConnectionLogic> connectionLogicProperty() {
		return connectionLogic;
	}

	/**
	 * 接続情報のプロパティ
	 */
	private ObjectProperty<ConnectionSetting> connectionSetting = new SimpleObjectProperty<ConnectionSetting>(this, "connectionSetting");
	public void setConnectionSetting(ConnectionSetting setting) {
		connectionSetting.set(setting);
	}

	public ConnectionSetting getConnectionSetting() {
		return connectionSetting.get();
	}

	public ObjectProperty<ConnectionSetting> connectionSettingProperty() {
		return connectionSetting;
	}

	/**
	 * プロキシ情報のプロパティ
	 */
	private ObjectProperty<ProxySettingLogic> proxyLogic = new SimpleObjectProperty<ProxySettingLogic>(this, "proxyLogic",new ProxySettingLogic());
	public void setProxyLogic(ProxySettingLogic logic) {
		proxyLogic.set(logic);
	}

	public ProxySettingLogic getProxyLogic() {
		return proxyLogic.get();
	}

	public ObjectProperty<ProxySettingLogic> proxyLogicProperty() {
		return proxyLogic;
	}

	@Override
	protected Task<Void> createTask() {
		final ConnectionSetting useSetting = getConnectionSetting();
		final ConnectionLogic useLogic = getConnectionLogic();
		final ProxySettingLogic useProxy = getProxyLogic();
		final boolean closeOnly = isClosing();

		return new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				if(closeOnly) {
					// Salesforceへの接続を切断
					useLogic.disconnect();
					logger.info("Connection Disconnect");

				} else {
					// Salesforceへの接続を切断
					useLogic.disconnect();
					logger.info("Connection Disconnect");

					ProxySetting proxy = useProxy.getProxySetting();
					if(Boolean.valueOf(proxy.getUseProxy())) {
						// Proxyが有効ならProxy経由でSalesforceへ接続
						useLogic.connect(useSetting, proxy);
					} else {
						// Proxyが無効なら、そのまま接続
						useLogic.connect(useSetting, true);

					}
					logger.info(String.format("Connected to Salesforce [%s]", useSetting.getName()));

					// Object一覧を取得
					setDescribeSObjectList(useLogic.getSObjectList());

					// ユーザ情報を取得
					setUserInfoMap(useLogic.getUserInfo());

				}

				return null;
			}

		};
	}

}
