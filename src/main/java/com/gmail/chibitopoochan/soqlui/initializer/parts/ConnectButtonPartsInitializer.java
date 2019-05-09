package com.gmail.chibitopoochan.soqlui.initializer.parts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.gmail.chibitopoochan.soqlui.SceneManager;
import com.gmail.chibitopoochan.soqlui.config.ApplicationSettingSet;
import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.logic.ConnectionLogic;
import com.gmail.chibitopoochan.soqlui.logic.ConnectionSettingLogic;
import com.gmail.chibitopoochan.soqlui.model.DescribeField;
import com.gmail.chibitopoochan.soqlui.model.DescribeSObject;
import com.gmail.chibitopoochan.soqlui.service.ConnectService;
import com.gmail.chibitopoochan.soqlui.service.ExportService;
import com.gmail.chibitopoochan.soqlui.service.SOQLExecuteService;
import com.gmail.chibitopoochan.soqlui.util.FormatUtils;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;
import com.gmail.chibitopoochan.soqlui.util.format.QueryFormatDecoration;
import com.sforce.ws.ConnectionException;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ConnectButtonPartsInitializer implements PartsInitializer<MainController>{
	// クラス共通の参照
	private static final boolean USE_ADVANCE_SOQL = ApplicationSettingSet.getInstance().getSetting().isAdvanceQuery();
	private static final Logger logger = LogUtils.getLogger(ConnectButtonPartsInitializer.class);
	private static final Pattern bindPattern = Pattern.compile(":[a-zA-Z]+");

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
	private TextArea soqlArea;
	private StringProperty actualSOQL;
	private WebView soqlWebArea;

	private Optional<File> exportHistory = Optional.empty();
	private TableView<DescribeSObject> objectList;
	private ConnectionLogic logic;

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
		this.soqlArea = controller.getSoqlArea();
		this.actualSOQL = controller.actualSOQL();
		this.objectList = controller.getsObjectList();
		this.logic = controller.getConnectService().getConnectionLogic();
		this.soqlWebArea = controller.getSoqlWebArea();
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

		soqlArea.setOnKeyPressed(this::keyPressed);
		soqlWebArea.setOnKeyPressed(this::keyPressed);

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
	 * 拡張SOQLを実際のSOQLに変換
	 */
	private void convertToActualSOQL() {
		String soql = soqlArea.getText();
		if(USE_ADVANCE_SOQL) {
			soql = converToSOQL(soql);
			Optional<String> workSOQL = bindVariable(soql);
			if(workSOQL.isPresent()) {
				actualSOQL.set(workSOQL.get());
			} else {
				actualSOQL.set(soql);
			}
		} else {
			actualSOQL.set(soql);
		}

	}

	private String converToSOQL(String soql) {
		// 対象とならない場合、処理を終了
		if(soql.toLowerCase().contains("select") || soql.length() < 3) {
			return soql;
		}

		// KeyPrefixからオブジェクトを特定
		String keyPrefix = soql.substring(0,3);
		Optional<DescribeSObject> result = objectList.getItems().stream().filter(o -> keyPrefix.equals(o.getKeyPrefix())).findFirst();

		if(!result.isPresent()) {
			return soql;
		}

		// SOQLを構築
		try {
			QueryFormatDecoration decoration = new QueryFormatDecoration();
			DescribeSObject obj = result.get();
			decoration.setTableAfter(obj.getName());

			String id = soql;
			List<DescribeField> fieldList = logic.getFieldList(obj.getName());

			soql = FormatUtils.format(decoration, () ->
				fieldList.stream().map(f -> {
					List<String> list = new ArrayList<>();
					list.add(f.getName());
					return list;
				}).collect(Collectors.toList())
			);

			soql = String.format("%s where id = '%s'",soql, id.trim());

		} catch (ConnectionException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return soql;
	}

	private Optional<String> bindVariable(String soql) {
		// SOQLからバインド変数を抽出
		Matcher bindMatcher = bindPattern.matcher(soql);

		// SOQLを再構築
		StringBuffer workSOQL = new StringBuffer();
		while(bindMatcher.find()) {
			// バインド変数の入力
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("バインド変数");
			dialog.setHeaderText("バインド変数に代入する値を入力してください");
			dialog.setContentText("変数"+bindMatcher.group());
			Optional<String> result = dialog.showAndWait();

			// SOQLの組み換え
			if(result.isPresent()) {
				bindMatcher.appendReplacement(workSOQL, String.format("'%s'", result.get()));
			} else {
				return Optional.empty();
			}

		}
		bindMatcher.appendTail(workSOQL);

		return Optional.of(workSOQL.toString());
	}

	/**
	 * SOQLを実行
	 */
	public void doExecute() {
		convertToActualSOQL();

		progressBar.progressProperty().unbind();
		progressBar.progressProperty().bind(executor.progressProperty());
		progressBar.visibleProperty().unbind();
		progressBar.visibleProperty().bind(executor.runningProperty());
		progressText.textProperty().unbind();
		progressText.textProperty().bind(executor.messageProperty());

		cancel.setDisable(false);
		export.setDisable(true);
		execute.setDisable(true);
		executor.start();

	}

	/**
	 * ショートカットキー
	 * @param e キーイベント
	 */
	private void keyPressed(KeyEvent e) {
		if(connector.isClosing()) {
			return;
		}

		KeyCodeCombination executeKey = new KeyCodeCombination(KeyCode.ENTER, KeyCodeCombination.CONTROL_DOWN);
		if(executeKey.match(e)) {
			doExecute();
		}

	}

	public void doExport() {
		convertToActualSOQL();

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
