package com.gmail.chibitopoochan.soqlui.initializer.parts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.gmail.chibitopoochan.soqlui.util.DialogUtils;
import com.gmail.chibitopoochan.soqlui.util.FormatUtils;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;
import com.gmail.chibitopoochan.soqlui.util.format.QueryFormatDecoration;
import com.sforce.ws.ConnectionException;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ConnectButtonPartsInitializer implements PartsInitializer<MainController>{
	// クラス共通の参照
	private static final boolean USE_ADVANCE_SOQL = ApplicationSettingSet.getInstance().getSetting().isAdvanceQuery();
	private static final boolean USE_EDITOR = ApplicationSettingSet.getInstance().getSetting().isUseEditor();
	private static final Logger logger = LogUtils.getLogger(ConnectButtonPartsInitializer.class);

	private static final Pattern BIND_PATTERN = Pattern.compile(":[a-zA-Z]+");
	private static final Pattern WILDCARD_PATTERN = Pattern.compile("select\\s+\\*\\s+from\\s+([_a-zA-Z0-9]+).*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
	private static final Pattern WILDCARD_WHILE_PATTERN = Pattern.compile(".*\\s+from\\s+[_a-zA-Z0-9]+\\s+(.*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
	private static final Pattern WILDCARD_IGNORE_PATTERN = Pattern.compile("select.*\\(.*\\).*from.*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);

	private static final KeyCodeCombination ZOOM_IN = new KeyCodeCombination(KeyCode.I, KeyCodeCombination.CONTROL_DOWN);
	private static final KeyCodeCombination ZOOM_OUT = new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_DOWN);
	private static final KeyCodeCombination EXECUTE = new KeyCodeCombination(KeyCode.ENTER, KeyCodeCombination.CONTROL_DOWN);
	private static final KeyCodeCombination IMAGE_COPY = new KeyCodeCombination(KeyCode.P, KeyCodeCombination.CONTROL_DOWN);

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
	private CheckBox useTooling;

	private Optional<File> exportHistory = Optional.empty();
	private ObservableList<DescribeSObject> objectList;
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
		this.objectList = controller.getObjectMasterList();
		this.logic = controller.getConnectService().getConnectionLogic();
		this.soqlWebArea = controller.getSoqlWebArea();
		this.useTooling = controller.getUseTooling();
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
			soql = convertByID(soql);
			soql = convertByWildcard(soql);
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

	private String convertByWildcard(String soql) {
		Matcher wildcardMatch = WILDCARD_PATTERN.matcher(soql);
		Matcher ignoreMatch = WILDCARD_IGNORE_PATTERN.matcher(soql);
		if(!wildcardMatch.matches() || ignoreMatch.matches()) {
			return soql;
		}

		String objName = wildcardMatch.group(1);
		String whileCondition = "";

		Matcher whileMatch = WILDCARD_WHILE_PATTERN.matcher(soql);
		if(whileMatch.matches()) {
			whileCondition = whileMatch.group(1);
		}

		// SOQLを構築
		try {
			if(whileCondition.isEmpty()) {
				soql = createSOQL(objName);
			} else {
				soql = String.format("%s %s",createSOQL(objName), whileCondition);
			}
		} catch (ConnectionException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return soql;
	}

	/**
	 * IDからレコードを取得するSOQLを作成
	 * @param soql ID
	 * @return SOQL
	 */
	private String convertByID(String soql) {
		// 対象とならない場合、処理を終了
		if(soql.toLowerCase().contains("select") || soql.length() < 3) {
			return soql;
		}

		// KeyPrefixからオブジェクトを特定
		String keyPrefix = soql.substring(0,3);
		Optional<DescribeSObject> result = objectList.stream().filter(o -> keyPrefix.equals(o.getKeyPrefix())).findFirst();

		if(!result.isPresent()) {
			return soql;
		}

		// SOQLを構築
		try {
			DescribeSObject obj = result.get();
			soql = String.format("%s where id = '%s'",createSOQL(obj.getName()), soql.trim());
		} catch (ConnectionException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return soql;
	}

	/**
	 * オブジェクト名から項目を取得し、SOQL形式に成形
	 * @param objName オブジェクト名
	 * @return SOQL
	 * @throws ConnectionException 項目定義取得時のエラー
	 */
	private String createSOQL(String objName) throws ConnectionException {
		QueryFormatDecoration decoration = new QueryFormatDecoration();
		decoration.setTableAfter(objName);

		List<DescribeField> fieldList = logic.getFieldList(objName);

		return FormatUtils.format(decoration, () ->
			fieldList.stream().map(f -> {
				List<String> list = new ArrayList<>();
				list.add(f.getName());
				return list;
			}).collect(Collectors.toList())
		);

	}

	private Optional<String> bindVariable(String soql) {
		// SOQLからバインド変数を抽出
		Matcher bindMatcher = BIND_PATTERN.matcher(soql);

		// SOQLを再構築
		StringBuffer workSOQL = new StringBuffer();
		while(bindMatcher.find()) {
			// バインド変数の入力
			Optional<String> result = DialogUtils.showMultiLineDialog(bindMatcher.group());

			// SOQLの組み換え
			if(result.isPresent()) {
				List<String> varList = Arrays.asList(result.get().split("\n"));
				String replaceText = varList.stream().map(v -> String.format("'%s'",v )).collect(Collectors.joining(","));
				replaceText = varList.size() > 1 ? String.format("(%s)", replaceText) : replaceText;
				bindMatcher.appendReplacement(workSOQL, replaceText);
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
		if(USE_EDITOR) {
			if(ZOOM_IN.match(e)) soqlWebArea.getEngine().executeScript("zoomIn()");
			if(ZOOM_OUT.match(e)) soqlWebArea.getEngine().executeScript("zoomOut()");
			if(IMAGE_COPY.match(e)) {
				ClipboardContent content = new ClipboardContent();
				content.putImage(soqlWebArea.snapshot(null, null));
				Clipboard.getSystemClipboard().setContent(content);
			}
			if(EXECUTE.match(e) && !connector.isClosing()) 	{
				soqlWebArea.getOnMouseExited().handle(null);
				doExecute();
			}

		} else {
			Font font = soqlArea.getFont();
			if(ZOOM_IN.match(e)) soqlArea.setFont(Font.font(font.getFamily(), font.getSize()+1));
			if(ZOOM_OUT.match(e)) soqlArea.setFont(Font.font(font.getFamily(), font.getSize()-1));
			if(IMAGE_COPY.match(e)) {
				ClipboardContent content = new ClipboardContent();
				content.putImage(soqlArea.snapshot(null, null));
				Clipboard.getSystemClipboard().setContent(content);
			}
			if(EXECUTE.match(e) && !connector.isClosing()) 	doExecute();
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
		connector.useTooling().bind(useTooling.selectedProperty());
		connector.start();
		connect.setDisable(true);

		// 接続名を保存
		setting.setSelectedName(selected);
	}

}
