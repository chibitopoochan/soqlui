package com.gmail.chibitopoochan.soqlui.controller.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.logic.ConnectionLogic;
import com.gmail.chibitopoochan.soqlui.model.ConnectionSetting;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ConnectService extends Service<Void> {
	private static final Logger logger = LoggerFactory.getLogger(ConnectService.class);
	private ObjectProperty<ConnectionLogic> connectionLogic = new SimpleObjectProperty<ConnectionLogic>(this, "connectionLogic", new ConnectionLogic());
	private ObjectProperty<ConnectionSetting> connectionSetting = new SimpleObjectProperty<ConnectionSetting>(this, "connectionSetting");
	private BooleanProperty closing = new SimpleBooleanProperty(false);

	public void setClosing(boolean close) {
		closing.set(close);
	}

	public boolean isClosing() {
		return closing.get();
	}

	public BooleanProperty closingProperty() {
		return closing;
	}

	public void setConnectionLogic(ConnectionLogic logic) {
		connectionLogic.set(logic);
	}

	public ConnectionLogic getConnectionLogic() {
		return connectionLogic.get();
	}

	public ObjectProperty<ConnectionLogic> connectionLogicProperty() {
		return connectionLogic;
	}

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

				}

				return null;
			}

		};
	}

}
