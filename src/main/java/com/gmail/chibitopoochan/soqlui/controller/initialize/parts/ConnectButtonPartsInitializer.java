package com.gmail.chibitopoochan.soqlui.controller.initialize.parts;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.SceneManager;
import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.controller.service.ConnectService;
import com.gmail.chibitopoochan.soqlui.controller.service.ExportService;
import com.gmail.chibitopoochan.soqlui.controller.service.SOQLExecuteService;
import com.gmail.chibitopoochan.soqlui.logic.ConnectionSettingLogic;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
		this.executor = controller.getExecutionService();
		this.export = controller.getExport();
		this.exportor = controller.getExportService();
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
		// TODO オプションは後程設定
		executor.start();

	}

	public void doExport() {
		export.setDisable(true);

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Data Export");
		chooser.getExtensionFilters().add(new ExtensionFilter("CSV Format", "csv"));
		exportHistory.ifPresent(value -> chooser.setInitialDirectory(value));

		File saveFile = chooser.showSaveDialog(SceneManager.getInstance().getStageStack().peek().unwrap());
		exportHistory = Optional.of(saveFile.getParentFile());

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

		connect.setDisable(true);

		// 接続を開始
		String selected = connectOption.getValue();
		connector.setConnectionSetting(setting.getConnectionSetting(selected));
		connector.setClosing(false);
		connector.start();

	}

}
