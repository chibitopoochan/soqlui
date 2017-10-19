package com.gmail.chibitopoochan.soqlui.controller.initialize.service;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.controller.service.ExportService;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

public class ExportServiceInitializer implements ServiceInitializer<MainController> {
	private ExportService service;
	private MainController controller;
	private Button export;

	@Override
	public void setController(MainController controller) {
		this.service = controller.getExportService();
		this.export = controller.getExport();
		this.controller = controller;
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
	public void failed(WorkerStateEvent e) {
		Platform.runLater(() -> {
			// 例外を通知
			Throwable exception = e.getSource().getException();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_001, exception.toString()));
			alert.showAndWait();
			service.reset();
			export.setDisable(false);
		});
	}

	@Override
	public void succeeded(WorkerStateEvent e) {
		Platform.runLater(() -> {
			Alert confirm = new Alert(AlertType.INFORMATION, "Export finished.");
			confirm.showAndWait();
			service.reset();
			export.setDisable(false);
		});
	}

}
