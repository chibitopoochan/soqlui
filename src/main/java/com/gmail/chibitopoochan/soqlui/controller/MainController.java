package com.gmail.chibitopoochan.soqlui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.SceneManager;
import com.gmail.chibitopoochan.soqlui.logic.ConnectionLogic;
import com.gmail.chibitopoochan.soqlui.logic.ConnectionSettingLogic;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public class MainController implements Initializable, Controller {
	// クラス共通の参照
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	private static final ResourceBundle config = ResourceBundle.getBundle(Configuration.RESOURCE);

	@FXML
	private ComboBox<String> connectOption;

	@FXML
	private Button connect;

	@FXML
	private Button disconnect;

	private SceneManager manager;

	private ConnectionSettingLogic setting;

	private ConnectionLogic connection;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.manager = SceneManager.getInstance();

		// 接続情報の初期化
		initializeConnectOption();
		initializeButtons();
	}

	/**
	 * ボタンの初期化
	 */
	private void initializeButtons() {
		// 接続情報があるか確認
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

	/**
	 * 接続イベント
	 */
	public void doConnect() {
		// 選択値を取得
		String selected = connectOption.getValue();

		try {
			// Salesforceへ接続
			connection.connect(setting.getConnectionSetting(selected));
			logger.info(String.format("Connected to Salesforce [%s]", selected));

			// ボタン等を制御
			connect.setDisable(true);
			disconnect.setDisable(false);
			connectOption.setDisable(true);
			logger.debug("Connection Button Disabled");

		} catch (Exception e) {
			// 例外を通知
			logger.error("Connection Error", e);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_001, e.getLocalizedMessage()));
			alert.showAndWait();
		}

	}

	/**
	 * 切断イベント
	 */
	public void doDisconnect() {
		// Salesforceへの接続を切断
		connection.disconnect();
		logger.info("Connection Disconnect");

		// ボタン等を制御
		connect.setDisable(false);
		disconnect.setDisable(true);
		connectOption.setDisable(false);
		logger.debug("Connection Button Enabled");

	}

	/**
	 * 接続設定画面の表示
	 */
	public void openConnectSetting() {
		try {
			logger.debug("Open Window [Connection Setting]");
			manager.sceneOpen(Configuration.VIEW_SU02, Configuration.TITLE_SU02);
		} catch (IOException e) {
			logger.error("Open window error", e);
		}
	}

	/**
	 * 選択肢の初期化
	 */
	private void initializeConnectOption() {
		try{
			// 接続情報を取得
			setting = new ConnectionSettingLogic();
			connection = new ConnectionLogic();
		} catch(Exception e) {
			// 例外を通知
			logger.error("Initialize error",e);
		}

		List<String> option = setting.getNameOption();

		// 接続情報を選択肢として追加
		connectOption.getItems().clear();
		if(option.isEmpty()) {
			connectOption.getItems().add("--");
		} else {
			connectOption.getItems().addAll(option);
		}
		connectOption.setValue(connectOption.getItems().get(0));

	}

	/**
	 * 接続情報を更新
	 */
	@Override
	public void onCloseChild() {
		initializeButtons();
		initializeConnectOption();
	}

}
