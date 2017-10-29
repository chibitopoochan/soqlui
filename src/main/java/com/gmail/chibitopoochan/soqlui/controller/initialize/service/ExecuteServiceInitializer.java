package com.gmail.chibitopoochan.soqlui.controller.initialize.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.controller.service.SOQLExecuteService;
import com.gmail.chibitopoochan.soqlui.model.SObjectRecord;
import com.gmail.chibitopoochan.soqlui.parts.custom.DragSelectableTableCell;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ExecuteServiceInitializer implements ServiceInitializer<MainController>{
	private SOQLExecuteService service;
	private TableView<SObjectRecord> resultTable;
	private MainController controller;
	private ObservableList<SObjectRecord> resultMasterList;
	private TextField resultSearch;

	@Override
	public void setController(MainController controller) {
		this.service = controller.getExecutionService();
		this.resultTable = controller.getResultTable();
		this.controller = controller;
		this.resultMasterList = controller.getResultMasterList();
		this.resultSearch = controller.getResultSearch();
	}

	@Override
	public void initialize() {
		service.setOnSucceeded(this::succeeded);
		service.setOnFailed(this::failed);
		service.soqlProperty().bind(controller.getSoqlArea().textProperty());
		service.connectionLogicProperty().bind(controller.getConnectService().connectionLogicProperty());
		service.batchSizeProperty().bind(controller.getBatchSize().textProperty());
		service.allProperty().bind(controller.getAll().selectedProperty());
	}

	@Override
	public void succeeded(WorkerStateEvent e) {
		// 実行結果を取得
		@SuppressWarnings("unchecked")
		List<Map<String, String>> result = (List<Map<String, String>>) e.getSource().getValue();

		Platform.runLater(() -> {
			resultTable.getItems().clear();
			resultTable.getColumns().clear();
			resultMasterList.clear();

			// 実行結果をテーブルに追加
			if(!result.isEmpty()) {
				resultSearch.setText("");
				resultSearch.setDisable(false);

				// 列を設定
				result.get(0).keySet().forEach(key -> {
					TableColumn<SObjectRecord,String> newColumn = new TableColumn<>(key);
					newColumn.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getRecord().get(key)));
					newColumn.setCellFactory(param -> new DragSelectableTableCell<>());
					resultTable.getColumns().add(newColumn);
				});

				// 行を設定
				resultMasterList.addAll(result.stream().map(SObjectRecord::new).collect(Collectors.toList()));
				resultTable.setItems(resultMasterList);

			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_002));
				alert.showAndWait();
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
			alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_001, exception.toString()));
			alert.showAndWait();
			service.reset();
		});
	}

}