package com.gmail.chibitopoochan.soqlui.initializer.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.service.ExportService;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

public class ExportServiceInitializer implements ServiceInitializer<MainController> {
	private ExportService service;
	private MainController controller;
	private ObservableList<String> soqlList;
	private StringProperty baseFileName;
	private StringProperty actualSOQL;
	private Button export;
	private Button cancel;
	private Button execute;

	@Override
	public void setController(MainController controller) {
		this.service = controller.getExportService();
		this.execute = controller.getExecute();
		this.export = controller.getExport();
		this.cancel = controller.getCancel();
		this.soqlList = controller.getExecSoqlList();
		this.actualSOQL = controller.actualSOQL();
		this.baseFileName = controller.getExecSoqlBaseName();
		this.controller = controller;
	}

	@Override
	public void initialize() {
		service.setOnSucceeded(this::succeeded);
		service.setOnFailed(this::failed);
		service.setOnCancelled(this::cancelled);
		service.soqlProperty().bind(controller.actualSOQL());
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
			service.reset();

			// 連続実行の判定
			if(!soqlList.isEmpty()) {
				executeExport();
			} else {
				Alert confirm = new Alert(AlertType.INFORMATION, "Export finished.");
				confirm.showAndWait();
				execute.setDisable(false);
				export.setDisable(false);
				cancel.setDisable(true);
			}
		});
	}

	/**
	 * エクスポート処理の連続実行
	 */
	private void executeExport() {
		// SOQLの再設定
		actualSOQL.setValue(soqlList.remove(0));

		// ファイル名の再設定
		Path path = service.getExportPath();
		Date currentTime = Calendar.getInstance().getTime();
		String fileName = String.format("%s-%2$tY%2$tm%2$td%2$tH%2$tM%2$tS.csv", baseFileName.getValue(), currentTime);

		// パスの再設定
		File saveFile = path.resolveSibling(fileName).toFile();

		// 既存ファイル削除
		if(saveFile.exists()) {
			saveFile.delete();
		}

		// 抽出の再実行
		try {
			saveFile.createNewFile();
			service.setExportPath(saveFile.toPath());
			service.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
