package com.gmail.chibitopoochan.soqlui.initializer.service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.logic.SOQLHistoryLogic;
import com.gmail.chibitopoochan.soqlui.model.ResultSet;
import com.gmail.chibitopoochan.soqlui.model.SOQLHistory;
import com.gmail.chibitopoochan.soqlui.model.SObjectRecord;
import com.gmail.chibitopoochan.soqlui.parts.custom.DragSelectableTableCell;
import com.gmail.chibitopoochan.soqlui.service.SOQLExecuteService;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ExecuteServiceInitializer implements ServiceInitializer<MainController>{
	private SOQLExecuteService service;
	private TableView<SObjectRecord> resultTable;
	private MainController controller;
	private TextField resultSearch;
	private TabPane tabArea;
	private SOQLHistoryLogic history;
	private ListView<SOQLHistory> historyListView;
	private Button execute;
	private Button export;
	private Button cancel;

	@Override
	public void setController(MainController controller) {
		this.service = controller.getExecutionService();
		this.resultTable = controller.getResultTable();
		this.controller = controller;
		this.resultSearch = controller.getResultSearch();
		this.tabArea = controller.getTabArea();
		this.history = controller.getHistory();
		this.historyListView = controller.getHistoryList();
		this.execute = controller.getExecute();
		this.export = controller.getExport();
		this.cancel = controller.getCancel();
	}

	@Override
	public void initialize() {
		service.setOnSucceeded(this::succeeded);
		service.setOnFailed(this::failed);
		service.setOnCancelled(this::cancelled);
		service.soqlProperty().bind(controller.getSoqlArea().textProperty());
		service.actualSOQL().bind(controller.actualSOQL());
		service.connectionLogicProperty().bind(controller.getConnectService().connectionLogicProperty());
		service.batchSizeProperty().bind(controller.getBatchSize().textProperty());
		service.allProperty().bind(controller.getAll().selectedProperty());
		service.join().bind(controller.getJoin().selectedProperty());
	}

	@Override
	public void succeeded(WorkerStateEvent e) {
		// 実行結果を取得
		@SuppressWarnings("unchecked")
		List<Map<String, String>> result = (List<Map<String, String>>) e.getSource().getValue();

		Platform.runLater(() -> {

			// 実行結果をテーブルに追加
			if(!result.isEmpty()) {
				ResultSet resultSet = new ResultSet();
				result.get(0).keySet().forEach(key -> {
					TableColumn<SObjectRecord,String> newColumn = new TableColumn<>(key);
					newColumn.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getRecord().get(key)));
					newColumn.setCellFactory(param -> new DragSelectableTableCell<>());
					resultSet.getColumns().add(newColumn);
				});

				resultSet.setSearchText("");
				resultSet.setSOQL(service.getActualSOQL());

				ObservableList<SObjectRecord> resultMasterList = FXCollections.observableArrayList();
				resultMasterList.addAll(result.stream().map(SObjectRecord::new).collect(Collectors.toList()));
				resultSet.setRecords(resultMasterList);

				Tab resultTab = new Tab("実行結果" + tabArea.getTabs().size());
				resultTab.setUserData(resultSet);
				resultTab.setOnSelectionChanged(this::tabChanged);
				resultTab.setOnClosed(this::closedTab);
				tabArea.getTabs().add(resultTab);
				tabArea.getContextMenu().getItems().forEach(m -> m.setDisable(false));
				tabArea.getSelectionModel().select(resultTab);

				// 履歴に追加
				SOQLHistory soqlHistory = new SOQLHistory(Calendar.getInstance().getTime(), service.getSOQL());
				ObservableList<SOQLHistory> historyList = FXCollections.observableArrayList(historyListView.getItems());
				historyList.add(soqlHistory);
				historyListView.setItems(historyList.sorted((i, j) -> -i.getCreatedDate().compareTo(j.getCreatedDate())));
				history.addHistory(soqlHistory);

			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_002));
				alert.showAndWait();
			}

			service.reset();
			execute.setDisable(false);
			export.setDisable(false);
			cancel.setDisable(true);

		});
	}

	private void closedTab(Event e) {
		ObservableList<Tab> tabList = tabArea.getTabs();
		if(tabList.isEmpty()) {
			resultTable.getColumns().clear();
			resultTable.setItems(FXCollections.emptyObservableList());
			resultSearch.setDisable(true);
			resultSearch.setText("");
			tabArea.getContextMenu().getItems().forEach(m -> m.setDisable(true));
		} else {
			tabArea.getSelectionModel().selectNext();
		}
	}

	private void setupTable(ResultSet result) {
		// 列を設定
		resultTable.getColumns().clear();
		result.getColumns().forEach(c -> resultTable.getColumns().add(c));

		// 行を設定
		resultTable.setItems(result.getRecords());
		resultTable.setUserData(result);

		// 検索文字を設定
		resultSearch.setDisable(false);
		resultSearch.setText(result.getSearchText());

	}

	private void tabChanged(Event e) {
		Tab resultTab = (Tab) e.getSource();
		ResultSet result = (ResultSet) resultTab.getUserData();
		setupTable(result);

	}

	public void cancelled(WorkerStateEvent e) {
		Platform.runLater(() -> {
			// 例外を通知
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_001, "Cancelled"));
			alert.showAndWait();
			service.reset();

			execute.setDisable(false);
			export.setDisable(false);
			cancel.setDisable(true);

		});
	}

	@Override
	public void failed(WorkerStateEvent e) {
		Platform.runLater(() -> {
			// 例外を通知
			Throwable exception = e.getSource().getException();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_001, exception.toString()));
			alert.showAndWait();
			service.reset();

			execute.setDisable(false);
			export.setDisable(false);
			cancel.setDisable(true);

		});
	}

}