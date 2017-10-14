package com.gmail.chibitopoochan.soqlui.controller.initialize.service;

import java.util.List;
import java.util.Map;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.controller.service.SOQLExecuteService;
import com.gmail.chibitopoochan.soqlui.model.SObjectRecord;
import com.gmail.chibitopoochan.soqlui.parts.custom.DragSelectableTableCell;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ExecuteServiceInitializer implements ServiceInitializer<MainController>{
	private SOQLExecuteService service;
	private TableView<SObjectRecord> resultTable;

	@Override
	public void setController(MainController controller) {
		this.service = controller.getExecutionService();
		this.resultTable = controller.getResultTable();
	}

	@Override
	public void initialize() {
		service.setOnSucceeded(this::succeeded);
		service.setOnFailed(this::failed);
	}

	@Override
	public void succeeded(WorkerStateEvent e) {
		// 実行結果を取得
		@SuppressWarnings("unchecked")
		List<Map<String, String>> result = (List<Map<String, String>>) e.getSource().getValue();

		Platform.runLater(() -> {
			resultTable.getItems().clear();
			resultTable.getColumns().clear();

			// 実行結果をテーブルに追加
			if(!result.isEmpty()) {
				// 列を設定
				for(String key : result.get(0).keySet()) {
					TableColumn<SObjectRecord,String> newColumn = new TableColumn<>(key);
					newColumn.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getRecord().get(key)));
					newColumn.setCellFactory(param -> new DragSelectableTableCell<>());
					resultTable.getColumns().add(newColumn);

				}

				// 行を設定
				for(Map<String,String> record : result) {
					resultTable.getItems().add(new SObjectRecord(record));
				}

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