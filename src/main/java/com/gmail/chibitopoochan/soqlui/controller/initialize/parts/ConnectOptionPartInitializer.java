package com.gmail.chibitopoochan.soqlui.controller.initialize.parts;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.controller.MainController;

import javafx.scene.control.ComboBox;

public class ConnectOptionPartInitializer implements PartsInitializer<MainController>{
	// クラス共通の参照
	private static final Logger logger = LoggerFactory.getLogger(ConnectOptionPartInitializer.class);

	private MainController controller;

	@Override
	public void setController(MainController controller) {
		this.controller = controller;
	}

	@Override
	public void initialize() {
		List<String> option = controller.getSetting().getNameOption();

		// 接続情報を選択肢として追加
		ComboBox<String> options = controller.getConnectOption();
		options.getItems().clear();
		if (option.isEmpty()) {
			options.getItems().add("--");
		} else {
			options.getItems().addAll(option);
		}
		options.setValue(controller.getSetting().getSelectedName(options.getItems().get(0)));
		logger.debug(String.format("Default option [%s]", options.getValue()));
		logger.debug(String.format("Load options [%s]", option.size()));
	}

}
