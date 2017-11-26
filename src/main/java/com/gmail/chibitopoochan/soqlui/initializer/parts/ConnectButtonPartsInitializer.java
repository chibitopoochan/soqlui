package com.gmail.chibitopoochan.soqlui.initializer.parts;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.SceneManager;
import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.logic.ConnectionSettingLogic;
import com.gmail.chibitopoochan.soqlui.service.ConnectService;
import com.gmail.chibitopoochan.soqlui.service.ExportService;
import com.gmail.chibitopoochan.soqlui.service.SOQLExecuteService;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ConnectButtonPartsInitializer implements PartsInitializer<MainController>{
	// クラス共通の参照
	private static final Logger logger = LoggerFactory.getLogger(ConnectButtonPartsInitializer.class);

	private ConnectionSettingLogic setting;
	private Button connect;
	private Button disconnect;
	private Button execute;
	private Button export;
	private ComboBox<String> connectOption;
	private ConnectService connector;
	private SOQLExecuteService executor;
	private ProgressIndicator progressIndicator;
	private ExportService exportor;
	private ProgressBar progressBar;
	private Label progressText;
	private Button cancel;

	private Optional<File> exportHistory = Optional.empty();

	@Override
	public void setController(MainController controller) {
		this.setting = controller.getSetting();
		this.connect = controller.getConnect();
		this.disconnect = controller.getDisconnect();
		this.connectOption = controller.getConnectOption();
		this.connector = controller.getConnectService();
		this.executor = controller.getExecutionService();
		this.progressIndicator = controller.getProgressIndicator();
		this.execute = controller.getExecute();
		this.export = controller.getExport();
		this.exportor = controller.getExportService();
		this.progressBar = controller.getProgressBar();
		this.progressText = controller.getProgressText();
		this.cancel = controller.getCancel();

	}

	public void initialize() {
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

		connect.setOnAction(e -> doConnect());
		disconnect.setOnAction(e -> doDisconnect());
		execute.setOnAction(e -> doExecute());
		export.setOnAction(e -> doExport());
		cancel.setOnAction(e -> doCancel());

	}

	/**
	 * 処理のキャンセル
	 */
	public void doCancel() {
		if(executor.isRunning()) {
			executor.cancel();
		} else if (exportor.isRunning()) {
			exportor.cancel();
		}

	}

	/**
	 * 切断イベント
	 */
	public void doDisconnect() {
		// 変数をバインド
		progressIndicator.progressProperty().unbind();
		progressIndicator.visibleProperty().unbind();
		progressIndicator.progressProperty().bind(connector.progressProperty());
		progressIndicator.visibleProperty().bind(connector.runningProperty());

		// 切断を開始
		connector.setClosing(true);
		connector.start();

	}

	/**
	 * SOQLを実行
	 */
	public void doExecute() {
		progressBar.progressProperty().unbind();
		progressBar.progressProperty().bind(executor.progressProperty());
		progressBar.visibleProperty().unbind();
		progressBar.visibleProperty().bind(executor.runningProperty());
		progressText.textProperty().unbind();
		progressText.textProperty().bind(executor.messageProperty());

		// TODO オプションは後程設定
		cancel.setDisable(false);
		export.setDisable(true);
		execute.setDisable(true);
		executor.start();

	}

	public void doExport() {
		// 変数をバインド
		progressBar.progressProperty().unbind();
		progressBar.visibleProperty().unbind();
		progressText.textProperty().unbind();
		progressBar.progressProperty().bind(exportor.progressProperty());
		progressBar.visibleProperty().bind(exportor.runningProperty());
		progressText.textProperty().bind(exportor.messageProperty());

		cancel.setDisable(false);
		export.setDisable(true);
		execute.setDisable(true);

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Data Export");
		chooser.getExtensionFilters().add(new ExtensionFilter("CSV Format", "csv"));
		exportHistory.ifPresent(value -> {
			chooser.setInitialDirectory(value.getParentFile());
			chooser.setInitialFileName(value.getName());
		});

		File saveFile = chooser.showSaveDialog(SceneManager.getInstance().getStageStack().peek().unwrap());
		exportHistory = Optional.ofNullable(saveFile);
		if(!exportHistory.isPresent()) {
			cancel.setDisable(true);
			export.setDisable(false);
			execute.setDisable(false);
			return;
		}

		if(saveFile.exists()) {
			saveFile.delete();
		}
		try {
			saveFile.createNewFile();
			exportor.setExportPath(saveFile.toPath());
			exportor.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 接続イベント
	 */
	public void doConnect() {
		// 変数をバインド
		progressIndicator.progressProperty().unbind();
		progressIndicator.visibleProperty().unbind();
		progressIndicator.progressProperty().bind(connector.progressProperty());
		progressIndicator.visibleProperty().bind(connector.runningProperty());

		// 接続を開始
		String selected = connectOption.getValue();
		connector.setConnectionSetting(setting.getConnectionSetting(selected));
		connector.setClosing(false);
		connector.start();
		connect.setDisable(true);

		// 接続名を保存
		setting.setSelectedName(selected);
	}

}
