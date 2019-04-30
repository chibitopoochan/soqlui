package com.gmail.chibitopoochan.soqlui.initializer.parts;

import java.util.List;

import org.slf4j.Logger;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.logic.ConnectionSettingLogic;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;

import javafx.scene.control.ComboBox;

public class ConnectOptionPartInitializer implements PartsInitializer<MainController>{
	// クラス共通の参照
	private static final Logger logger = LogUtils.getLogger(ConnectOptionPartInitializer.class);

	private ConnectionSettingLogic setting;
	private ComboBox<String> connectOption;

	@Override
	public void setController(MainController controller) {
		this.setting = controller.getSetting();
		this.connectOption = controller.getConnectOption();
	}

	@Override
	public void initialize() {
		List<String> option = setting.getNameOption();

		// 接続情報を選択肢として追加
		connectOption.getItems().clear();
		if (option.isEmpty()) {
			connectOption.getItems().add(ConnectionSettingLogic.DEFAULT_NAME);
		} else {
			connectOption.getItems().addAll(option);
		}
		connectOption.setValue(setting.getSelectedName(connectOption.getItems().get(0)));
		logger.debug(String.format("Default option [%s]", connectOption.getValue()));
		logger.debug(String.format("Load options [%s]", option.size()));
	}

}
