package com.gmail.chibitopoochan.soqlui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.SceneManager;
import com.gmail.chibitopoochan.soqlui.controller.service.ConnectService;
import com.gmail.chibitopoochan.soqlui.controller.service.FieldProvideService;
import com.gmail.chibitopoochan.soqlui.controller.service.SOQLExecuteService;
import com.gmail.chibitopoochan.soqlui.logic.ConnectionSettingLogic;
import com.gmail.chibitopoochan.soqlui.model.DescribeField;
import com.gmail.chibitopoochan.soqlui.model.DescribeSObject;
import com.gmail.chibitopoochan.soqlui.model.SObjectRecord;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.input.MouseButton;

public class MainController implements Initializable, Controller {
	// クラス共通の参照
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	// 画面上のコンポーネント
	// メニュー
	@FXML private MenuItem menuFileConnection;

	// 左側上段
	@FXML private ComboBox<String> connectOption;
	@FXML private Button connect;
	@FXML private Button disconnect;
	@FXML private ProgressIndicator progressIndicator;

	// 左側中断
	@FXML private TableView<DescribeSObject> sObjectList;
	@FXML private TableColumn<DescribeSObject, String> prefixColumn;
	@FXML private TableColumn<DescribeSObject, String> sObjectColumn;
	@FXML private TextField objectSearch;

	// 左側下段
	@FXML private TableView<DescribeField> fieldList;
	@FXML private TextField columnSearch;

	// 中央
	@FXML private Button execute;
	@FXML private TextArea soqlArea;
	@FXML private TextField batchSize;
	@FXML private CheckBox all;
	@FXML private TableView<SObjectRecord> resultTable;

	// 業務ロジック
	private SceneManager manager;
	private ConnectionSettingLogic setting;

	// 非同期のサービス
	private ConnectService connectService;
	private SOQLExecuteService executionService;
	private FieldProvideService fieldService;

	// 状態管理
	private ObservableList<DescribeSObject> objectMasterList = FXCollections.observableArrayList();
	private ObservableList<DescribeField> fieldMasterList = FXCollections.observableArrayList();
	private Map<String, TableColumn<DescribeField, String>> fieldColumnList = new HashMap<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.manager = SceneManager.getInstance();

		// 画面の初期化
		initializeConnectOption();
		initializeConnection();
		initializeExecution();
		initializeButtons();
		initializeSObjectList();
		initializeFieldList();
		initializeField();
	}

	private void initializeFieldList() {
		// 現在の列とレコードを削除
		fieldList.getColumns().clear();
		fieldList.getItems().clear();

		// 列の追加（追加順で左の列から定義）
		addFieldListColumn(DescribeField.FIELD_LIST_COLUMN_NAME);
		addFieldListColumn(DescribeField.FIELD_LIST_COLUMN_LABEL);
		addFieldListColumn(DescribeField.FIELD_LIST_COLUMN_TYPE);
		addFieldListColumn(DescribeField.FIELD_LIST_COLUMN_LENGTH);
		addFieldListColumn(DescribeField.FIELD_LIST_COLUMN_PICKLIST);
		addFieldListColumn(DescribeField.FIELD_LIST_COLUMN_REF);

		// 列のマッピング
		fieldColumnList.get(DescribeField.FIELD_LIST_COLUMN_NAME).setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
		fieldColumnList.get(DescribeField.FIELD_LIST_COLUMN_LABEL).setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLabel()));
		fieldColumnList.get(DescribeField.FIELD_LIST_COLUMN_LENGTH).setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getLength())));
		fieldColumnList.get(DescribeField.FIELD_LIST_COLUMN_PICKLIST).setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPicklist()));
		fieldColumnList.get(DescribeField.FIELD_LIST_COLUMN_REF).setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReference()));
		fieldColumnList.get(DescribeField.FIELD_LIST_COLUMN_TYPE).setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType()));

		sObjectList.setOnMouseClicked(e -> {
			if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
				fieldService.setSObject(sObjectList.getSelectionModel().getSelectedItem().getName());
				fieldService.start();
			}
		});
	}

	private void addFieldListColumn(String name) {
		fieldColumnList.put(name, new TableColumn<DescribeField, String>());
		fieldColumnList.get(name).setText(name);
		fieldList.getColumns().add(fieldColumnList.get(name));
	}

	/**
	 * オブジェクト一覧の初期化
	 */
	private void initializeSObjectList() {
		// 既存の設定をクリア
		prefixColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getKeyPrefix()));
		sObjectColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));

	}

	/**
	 * 項目一覧の初期化
	 */
	private void initializeField() {
		fieldService = new FieldProvideService();
		fieldService.connectionLogicProperty().bind(connectService.connectionLogicProperty());
		fieldService.setOnSucceeded(e -> {
			Platform.runLater(() -> {
				fieldMasterList.clear();
				fieldList.getItems().clear();

				columnSearch.setText("");
				columnSearch.setDisable(false);
				fieldMasterList = FXCollections.observableArrayList(fieldService.getDescribeFieldList());
				fieldList.setItems(fieldMasterList);

			});
			fieldService.reset();
		});
		fieldService.setOnFailed(e -> {
			Platform.runLater(() -> {
				// 例外を通知
				Throwable exception = e.getSource().getException();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_001, exception.getMessage()));
				alert.showAndWait();
			});
			fieldService.reset();

		});
	}

	/**
	 * 実行処理の初期化
	 * TODO 外部クラスへの委譲
	 */
	private void initializeExecution() {
		executionService = new SOQLExecuteService();
		executionService.soqlProperty().bind(soqlArea.textProperty());
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
				if(result.isEmpty()) {
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

				} else {
					Throwable exception = e.getSource().getException();
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_002, exception.getMessage()));
					alert.showAndWait();
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
	 * TODO 外部クラスへの委譲
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

					// 一覧のクリア
					objectSearch.setText("");
					objectMasterList.clear();
					sObjectList.getItems().clear();
					objectSearch.setDisable(true);

					columnSearch.setText("");
					fieldMasterList.clear();
					fieldList.getItems().clear();
					columnSearch.setDisable(true);
				} else {
					// ボタン等を制御
					connect.setText("Reconnect");
					disconnect.setDisable(false);
					connectOption.setDisable(true);
					logger.debug("Connection Button Disabled");

					// 一覧の表示
					objectSearch.setText("");
					objectSearch.setDisable(false);
					objectMasterList = FXCollections.observableArrayList(connectService.getDescribeSObjectList());
					sObjectList.setItems(objectMasterList);
					logger.debug("sObject List show");

				}
				connectService.reset();
			});
		});
		connectService.setOnFailed(e -> {
			Platform.runLater(() -> {
				// 例外を通知
				Throwable exception = e.getSource().getException();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_001, exception.getMessage()));
				alert.showAndWait();
				connectService.reset();
			});
		});

	}

	/**
	 * ボタンの初期化
	 */
	private void initializeButtons() {
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

		// オブジェクト一覧の絞り込み
		objectSearch.setDisable(true);
		objectSearch.textProperty().addListener(
			(v, o, n) -> sObjectList.setItems(
				objectMasterList.filtered(
					t -> t.getName().toLowerCase().indexOf(n) > -1)
			)
		);

		// 項目一覧の絞り込み
		columnSearch.setDisable(true);
		columnSearch.textProperty().addListener((v, o, n) ->
			fieldList.setItems(
				fieldMasterList.filtered(
					t -> t.getName().toLowerCase().indexOf(n) > -1
						|| t.getLabel().toLowerCase().indexOf(n) > -1)
			)
		);

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
