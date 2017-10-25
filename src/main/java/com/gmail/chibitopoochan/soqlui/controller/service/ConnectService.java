package com.gmail.chibitopoochan.soqlui.controller.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.logic.ConnectionLogic;
import com.gmail.chibitopoochan.soqlui.model.ConnectionSetting;
import com.gmail.chibitopoochan.soqlui.model.DescribeSObject;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ConnectService extends Service<Void> {
	private static final Logger logger = LoggerFactory.getLogger(ConnectService.class);

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

	@Override
	protected Task<Void> createTask() {
		final ConnectionSetting useSetting = getConnectionSetting();
		final ConnectionLogic useLogic = getConnectionLogic();
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

					// Salesforceへ接続
					useLogic.connect(useSetting);
					logger.info(String.format("Connected to Salesforce [%s]", useSetting.getName()));

					// Object一覧を取得
					setDescribeSObjectList(useLogic.getSObjectList());

				}

				return null;
			}

		};
	}

}
