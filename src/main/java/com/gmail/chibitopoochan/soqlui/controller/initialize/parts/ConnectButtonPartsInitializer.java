package com.gmail.chibitopoochan.soqlui.controller.initialize.parts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.logic.ConnectionSettingLogic;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public class ConnectButtonPartsInitializer implements PartsInitializer<MainController>{
	// クラス共通の参照
	private static final Logger logger = LoggerFactory.getLogger(ConnectButtonPartsInitializer.class);

	private ConnectionSettingLogic setting;
	private Button connect;
	private Button disconnect;
	private ComboBox<String> connectOption;

	@Override
	public void setController(MainController controller) {
		this.setting = controller.getSetting();
		this.connect = controller.getConnect();
		this.disconnect = controller.getDisconnect();
		this.connectOption = controller.getConnectOption();

	}

	public void initialize() {
		// 接続情報関連の設定
		if(setting.hasSetting()) {
			logger.debug("Connection Button Enabled");
			connect.setDisable(false);
			disconnect.setDisable(true);
			connectOption.setDisable(false);
		} else {
			logger.debug("Connection Button Disabled");
			connect.setDisable(true);
			disconnect.setDisable(true);
			connectOption.setDisable(true);
		}

	}

}
