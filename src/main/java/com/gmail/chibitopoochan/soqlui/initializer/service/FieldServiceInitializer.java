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
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class FieldServiceInitializer implements ServiceInitializer<MainController> {
	private ConnectService connectService;
	private ObservableList<DescribeField> fieldMasterList;
	private TableView<DescribeField> fieldList;
	private TextField columnSearch;
	private FieldProvideService fieldService;
	private Label objectName;
	private TabPane tabArea;

	@Override
	public void setController(MainController controller) {
		this.connectService = controller.getConnectService();
		this.fieldMasterList = controller.getFieldMasterList();
		this.fieldList = controller.getFieldList();
		this.columnSearch = controller.getColumnSearch();
		this.fieldService = controller.getFieldService();
		this.objectName = controller.getObjectName();
		this.tabArea = controller.getFieldTabArea();
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
			Tab fieldTab = new Tab(fieldService.getSObject());
			fieldTab.setUserData(FXCollections.observableArrayList(fieldService.getDescribeFieldList()));
			fieldTab.setOnClosed(this::tabClosed);
			fieldTab.setOnSelectionChanged(this::tabChanged);
			tabArea.getTabs().add(fieldTab);
			tabArea.getSelectionModel().select(fieldTab);
			tabArea.getContextMenu().getItems().forEach(m -> m.setDisable(false));
			setFieldList(fieldTab);

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

	private void tabClosed(Event e) {
		ObservableList<Tab> tabList = tabArea.getTabs();
		if(tabList.isEmpty()) {
			columnSearch.setText("");
			columnSearch.setDisable(true);
			objectName.setText("");
			fieldMasterList.clear();
			fieldList.setItems(fieldMasterList);
			tabArea.getContextMenu().getItems().forEach(m -> m.setDisable(true));
		} else {
			tabArea.getSelectionModel().selectNext();
		}
	}

	private void tabChanged(Event e){
		setFieldList((Tab) e.getSource());
	}

	@SuppressWarnings("unchecked")
	private void setFieldList(Tab tab) {
		columnSearch.setText("");
		columnSearch.setDisable(false);
		objectName.setText(tab.getText());
		fieldMasterList.setAll((ObservableList<DescribeField>)tab.getUserData());
		fieldList.setItems(fieldMasterList);
	}

}
