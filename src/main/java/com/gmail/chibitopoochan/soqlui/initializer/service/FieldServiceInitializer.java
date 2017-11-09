package com.gmail.chibitopoochan.soqlui.initializer.service;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.model.DescribeField;
import com.gmail.chibitopoochan.soqlui.service.ConnectService;
import com.gmail.chibitopoochan.soqlui.service.FieldProvideService;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class FieldServiceInitializer implements ServiceInitializer<MainController> {
	private ConnectService connectService;
	private ObservableList<DescribeField> fieldMasterList;
	private TableView<DescribeField> fieldList;
	private TextField columnSearch;
	private FieldProvideService fieldService;
	private Label objectName;

	@Override
	public void setController(MainController controller) {
		this.connectService = controller.getConnectService();
		this.fieldMasterList = controller.getFieldMasterList();
		this.fieldList = controller.getFieldList();
		this.columnSearch = controller.getColumnSearch();
		this.fieldService = controller.getFieldService();
		this.objectName = controller.getObjectName();
	}

	@Override
	public void initialize() {
		fieldService.connectionLogicProperty().bind(connectService.connectionLogicProperty());
		fieldService.setOnSucceeded(this::succeeded);
		fieldService.setOnFailed(this::failed);
	}

	@Override
	public void succeeded(WorkerStateEvent e) {
		Platform.runLater(() -> {
			fieldMasterList.clear();
			fieldList.getItems().clear();

			columnSearch.setText("");
			columnSearch.setDisable(false);
			objectName.setText(fieldService.getSObject());
			fieldMasterList.setAll(FXCollections.observableArrayList(fieldService.getDescribeFieldList()));
			fieldList.setItems(fieldMasterList);

		});
		fieldService.reset();
	}

	@Override
	public void failed(WorkerStateEvent e) {
		Platform.runLater(() -> {
			// 例外を通知
			Throwable exception = e.getSource().getException();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_001, exception.toString()));
			alert.showAndWait();
			fieldService.reset();
		});

	}

}
