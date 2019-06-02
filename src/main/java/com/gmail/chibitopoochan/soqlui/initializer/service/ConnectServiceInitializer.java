package com.gmail.chibitopoochan.soqlui.initializer.service;

import org.slf4j.Logger;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.model.DescribeField;
import com.gmail.chibitopoochan.soqlui.model.DescribeSObject;
import com.gmail.chibitopoochan.soqlui.service.ConnectService;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ConnectServiceInitializer implements ServiceInitializer<MainController> {
	// クラス共通の参照
	private static final Logger logger = LogUtils.getLogger(ConnectServiceInitializer.class);

	private BooleanProperty withExecute = new SimpleBooleanProperty();

	private ConnectService service;

	private Button connect;

	private Button disconnect;

	private Button execute;

	private Button export;

	private Button cancel;

	private ComboBox<String> connectOption;

	private TextField objectSearch;

	private ObservableList<DescribeSObject> objectMasterList;

	private TableView<DescribeSObject> sObjectList;

	private TextField columnSearch;

	private ObservableList<DescribeField> fieldMasterList;

	private TableView<DescribeField> fieldList;

	private Label objectName;

	@Override
	public void setController(MainController controller) {
		this.service = controller.getConnectService();
		this.connect = controller.getConnect();
		this.disconnect = controller.getDisconnect();
		this.execute = controller.getExecute();
		this.connectOption = controller.getConnectOption();
		this.objectSearch = controller.getObjectSearch();
		this.objectMasterList = controller.getObjectMasterList();
		this.sObjectList = controller.getsObjectList();
		this.columnSearch = controller.getColumnSearch();
		this.fieldMasterList = controller.getFieldMasterList();
		this.fieldList = controller.getFieldList();
		this.objectName = controller.getObjectName();
		this.export = controller.getExport();
		this.cancel = controller.getCancel();
		this.withExecute.bind(controller.withExecuteProperty());
	}

	@Override
	public void initialize() {
		service.setOnSucceeded(this::succeeded);
		service.setOnFailed(this::failed);

	}

	@Override
	public void succeeded(WorkerStateEvent e) {
		Platform.runLater(() -> {
			if(service.isClosing()) {
				// ボタン等を制御
				connect.setText("接続");
				connect.setDisable(false);
				disconnect.setDisable(true);
				connectOption.setDisable(false);
				execute.setDisable(true);
				export.setDisable(true);
				cancel.setDisable(true);
				logger.debug("Connection Button Enabled");

				// 一覧のクリア
				objectSearch.setText("");
				objectMasterList.clear();
				sObjectList.getItems().clear();
				objectSearch.setDisable(true);

				columnSearch.setText("");
				fieldMasterList.clear();
				fieldList.getItems().clear();
				objectName.setText("None");
				columnSearch.setDisable(true);

			} else {
				// ボタン等を制御
				connect.setText("再接続");
				connect.setDisable(false);
				disconnect.setDisable(false);
				connectOption.setDisable(true);
				execute.setDisable(false);
				export.setDisable(false);
				cancel.setDisable(true);
				logger.debug("Connection Button Disabled");

				// 一覧の表示
				objectSearch.setText("");
				objectSearch.setDisable(false);
				objectMasterList.setAll(FXCollections.observableArrayList(service.getDescribeSObjectList()));
				sObjectList.setItems(objectMasterList);
				logger.debug("sObject List show");

				// SOQL実行も行う場合、イベントハンドラを呼び出す
				// TODO nullで問題ないか？
				if(withExecute.get()) {
					execute.getOnAction().handle(null);
				}

			}
			service.reset();
		});
	}

	@Override
	public void failed(WorkerStateEvent e) {
		Platform.runLater(() -> {
			// 例外を通知
			Throwable exception = e.getSource().getException();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_005
					,exception.getMessage()
					,exception.getLocalizedMessage()
					,exception.getStackTrace()[0].toString()));
			alert.showAndWait();
			service.reset();

			connect.setDisable(false);
		});
	}

}
