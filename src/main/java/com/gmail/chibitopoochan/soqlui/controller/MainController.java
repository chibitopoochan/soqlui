package com.gmail.chibitopoochan.soqlui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.SceneManager;
import com.gmail.chibitopoochan.soqlui.controller.task.ConnectService;
import com.gmail.chibitopoochan.soqlui.logic.ConnectionSettingLogic;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;

public class MainController implements Initializable, Controller {
	// クラス共通の参照
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	// 画面上のコンポーネント
	@FXML private ComboBox<String> connectOption;
	@FXML private Button connect;
	@FXML private Button disconnect;
	@FXML private ProgressIndicator progressIndicator;
	@FXML private MenuItem menuFileConnection;

	// 業務ロジック
	private SceneManager manager;
	private ConnectionSettingLogic setting;

	// 状態管理
	private ConnectService connectService;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.manager = SceneManager.getInstance();

		// 接続情報の初期化
		initializeConnectOption();
		initializeConnection();
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
	 * 接続サービスの初期化
	 */
	private void initializeConnection() {
		// 結果ごとのイベントを設定
		connectService = new ConnectService();
		connectService.setOnSucceeded(e -> {
			Platform.runLater(() -> {
				if(connectService.isClosing()) {
					// ボタン等を制御
					connect.setText("Connect");
					disconnect.setDisable(true);
					connectOption.setDisable(false);
					logger.debug("Connection Button Enabled");
				} else {
					// ボタン等を制御
					connect.setText("Reconnect");
					disconnect.setDisable(false);
					connectOption.setDisable(true);
					logger.debug("Connection Button Disabled");
				}
				connectService.reset();
			});
		});
		connectService.setOnFailed(e -> {
			Platform.runLater(() -> {
				// 例外を通知
				Throwable exception = connectService.exceptionProperty().get();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_001, exception.getLocalizedMessage()));
				alert.showAndWait();
				connectService.reset();
			});
		});

	}

	/**
	 * 接続イベント
	 */
	public void doConnect() {
		// 変数をバインド
		progressIndicator.progressProperty().unbind();
		progressIndicator.visibleProperty().unbind();
		progressIndicator.progressProperty().bind(connectService.progressProperty());
		progressIndicator.visibleProperty().bind(connectService.runningProperty());

		// 接続を開始
		String selected = connectOption.getValue();
		connectService.setConnectionSetting(setting.getConnectionSetting(selected));
		connectService.setClosing(false);
		connectService.start();

	}

	/**
	 * 切断イベント
	 */
	public void doDisconnect() {
		// 変数をバインド
		progressIndicator.progressProperty().unbind();
		progressIndicator.visibleProperty().unbind();
		progressIndicator.progressProperty().bind(connectService.progressProperty());
		progressIndicator.visibleProperty().bind(connectService.runningProperty());

		// 切断を開始
		connectService.setClosing(true);
		connectService.start();

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
