package com.gmail.chibitopoochan.soqlui.initializer.service;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.service.ExportService;
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
	private Button cancel;
	private Button execute;

	@Override
	public void setController(MainController controller) {
		this.service = controller.getExportService();
		this.execute = controller.getExecute();
		this.export = controller.getExport();
		this.cancel = controller.getCancel();
		this.controller = controller;
	}

	@Override
	public void initialize() {
		service.setOnSucceeded(this::succeeded);
		service.setOnFailed(this::failed);
		service.setOnCancelled(this::cancelled);
		service.soqlProperty().bind(controller.getSoqlArea().textProperty());
		service.connectionLogicProperty().bind(controller.getConnectService().connectionLogicProperty());
		service.setBatchSize("200");
		service.allProperty().bind(controller.getAll().selectedProperty());
		service.join().bind(controller.getJoin().selectedProperty());
		service.setExtractLogic(controller.getExtract());
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

	@Override
	public void succeeded(WorkerStateEvent e) {
		Platform.runLater(() -> {
			Alert confirm = new Alert(AlertType.INFORMATION, "Export finished.");
			confirm.showAndWait();
			service.reset();
			execute.setDisable(false);
			export.setDisable(false);
			cancel.setDisable(true);
		});
	}

}
