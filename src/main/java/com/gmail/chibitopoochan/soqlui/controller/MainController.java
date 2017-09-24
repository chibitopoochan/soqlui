package com.gmail.chibitopoochan.soqlui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.SceneManager;
import com.gmail.chibitopoochan.soqlui.controller.task.ConnectService;
import com.gmail.chibitopoochan.soqlui.controller.task.SOQLExecuteService;
import com.gmail.chibitopoochan.soqlui.logic.ConnectionSettingLogic;
import com.gmail.chibitopoochan.soqlui.model.SObjectRecord;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MainController implements Initializable, Controller {
	// クラス共通の参照
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	// 画面上のコンポーネント
	@FXML private ComboBox<String> connectOption;
	@FXML private Button connect;
	@FXML private Button disconnect;
	@FXML private ProgressIndicator progressIndicator;
	@FXML private MenuItem menuFileConnection;

	@FXML private Button execute;
	@FXML private TextArea soqlArea;
	@FXML private TextField batchSize;
	@FXML private CheckBox all;

	@FXML private TableView<SObjectRecord> resultTable;

	// 業務ロジック
	private SceneManager manager;
	private ConnectionSettingLogic setting;

	// 状態管理
	private ConnectService connectService;
	private SOQLExecuteService executionService;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.manager = SceneManager.getInstance();

		// 接続情報の初期化
		initializeConnectOption();
		initializeConnection();
		initializeExecution();
		initializeButtons();

	}

	private void initializeExecution() {
		executionService = new SOQLExecuteService();
		executionService.connectionLogicProperty().bind(connectService.connectionLogicProperty());
		executionService.batchSizeProperty().bind(batchSize.textProperty());
		executionService.allProperty().bind(all.selectedProperty());
		executionService.setOnSucceeded(e -> {
			// 実行結果を取得
			@SuppressWarnings("unchecked")
			List<Map<String, String>> result = (List<Map<String, String>>) e.getSource().getValue();

			Platform.runLater(() -> {
				resultTable.getItems().clear();
				resultTable.getColumns().clear();

				// 実行結果０件なら終了
				if(!result.isEmpty()) {
					// 列を設定
					for(String key : result.get(0).keySet()) {
						TableColumn<SObjectRecord,String> newColumn = new TableColumn<>(key);
						newColumn.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getRecord().get(key)));
						resultTable.getColumns().add(newColumn);

					}

					// 行を設定
					for(Map<String,String> record : result) {
						resultTable.getItems().add(new SObjectRecord(record));
					}

				}

				executionService.reset();

			});
		});
		executionService.setOnFailed(e -> {
			Platform.runLater(() -> {
				// 例外を通知
				Throwable exception = e.getSource().getException();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_001, exception.getMessage()));
				alert.showAndWait();
			});
			executionService.reset();
		});

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
				Throwable exception = e.getSource().getException();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_001, exception.getLocalizedMessage()));
				alert.showAndWait();
				connectService.reset();
			});
		});

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
	 * SOQLを実行
	 */
	public void doExecute() {
		// TODO オプションは後程設定
		String soql = soqlArea.getText();
		executionService.setSOQL(soql);
		executionService.start();

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
	 * 接続情報を更新
	 */
	@Override
	public void onCloseChild() {
		initializeButtons();
		initializeConnectOption();
	}

}
