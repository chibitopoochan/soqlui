package com.gmail.chibitopoochan.soqlui.initializer.parts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.gmail.chibitopoochan.soqlexec.soql.QueryAnalyzeUtils;
import com.gmail.chibitopoochan.soqlexec.soql.QueryAnalyzeUtils.TokenException;
import com.gmail.chibitopoochan.soqlexec.soql.SOQL;
import com.gmail.chibitopoochan.soqlexec.soql.SOQLField;
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
import com.gmail.chibitopoochan.soqlui.util.LogUtils;
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

public class ButtonPartsInitializer implements PartsInitializer<MainController>{
	// クラス共通の参照
	private static final boolean USE_ADVANCE_SOQL = ApplicationSettingSet.getInstance().getSetting().isAdvanceQuery();
	private static final boolean USE_EDITOR = ApplicationSettingSet.getInstance().getSetting().isUseEditor();
	private static final Logger logger = LogUtils.getLogger(ButtonPartsInitializer.class);

	private static final Pattern BIND_PATTERN = Pattern.compile(":[a-zA-Z]+");

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
	private StringProperty baseFileName;

	private Optional<File> exportHistory = Optional.empty();
	private ObservableList<DescribeSObject> objectList;
	private ObservableList<String> soqlList;
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
		this.soqlList = controller.getExecSoqlList();
		this.logic = controller.getConnectService().getConnectionLogic();
		this.soqlWebArea = controller.getSoqlWebArea();
		this.useTooling = controller.getUseTooling();
		this.baseFileName = controller.getExecSoqlBaseName();
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
			SOQL query;
			try {
				query = QueryAnalyzeUtils.analyze(soql);
				convertByWildcard(query);
				bindVariable(query);
				soql = query.toString();
			} catch (TokenException | ConnectionException e) {
				soql = convertByID(soql);
			}
		}
		actualSOQL.set(soql);

	}

	private void convertByWildcard(SOQL soql) throws ConnectionException {
		List<SOQLField> fields = soql.getSelectFields();

		// "*"1文字なら継続
		if(fields.size() != 1) return;
		if(!fields.get(0).getLabel().equals("*")) return;

		// "*"を削除し、オブジェクトの項目を追加
		List<DescribeField> fieldList = logic.getFieldList(soql.getFromObject());
		soql.getSelectFields().clear();
		fieldList.stream().map(f -> new SOQLField(f.getName())).forEach(e -> soql.getSelectFields().add(e));

	}

	/**
	 * IDからレコードを取得するSOQLを作成
	 * @param soql ID
	 * @return SOQL
	 * @throws ConnectionException
	 */
	private String convertByID(String soql){
		// 対象とならない場合、処理を終了
		if(soql.toLowerCase().contains("select") || soql.length() < 3) return soql;

		// KeyPrefixからオブジェクトを特定
		String keyPrefix = soql.substring(0,3);
		Optional<DescribeSObject> result = objectList.stream().filter(o -> keyPrefix.equals(o.getKeyPrefix())).findFirst();
		if(!result.isPresent()) return soql;

		// オブジェクトから項目を特定
		DescribeSObject obj = result.get();
		List<DescribeField> fieldList;
		try {
			fieldList = logic.getFieldList(obj.getName());
		} catch (ConnectionException e) {
			e.printStackTrace();
			return soql;
		}

		// SOQLを構築
		SOQL query = new SOQL();
		fieldList.forEach(f -> query.addSelectField(new SOQLField(f.getName())));
		query.setWhere(String.format("id = '%s'",soql.trim()));

		return query.toString();

	}

	private void bindVariable(SOQL query) {
		// SOQLからバインド変数を抽出
		Matcher bindMatcher = BIND_PATTERN.matcher(query.getWhere());

		// SOQLを再構築
		StringBuffer whereCaluse = new StringBuffer();
		while(bindMatcher.find()) {
			// バインド変数の入力
			Optional<String> result = DialogUtils.showMultiLineDialog(bindMatcher.group());

			// SOQLの組み換え
			result.ifPresent(r -> {
				List<String> varList = Arrays.asList(r.split("\n"));
				String replaceText = varList.stream().map(v -> String.format("'%s'",v )).collect(Collectors.joining(","));
				replaceText = varList.size() > 1 ? String.format("(%s)", replaceText) : replaceText;
				bindMatcher.appendReplacement(whereCaluse, replaceText);
			});

		}
		bindMatcher.appendTail(whereCaluse);
		query.setWhere(whereCaluse.toString());

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
		// HTML形式からテキスト形式に変換
		convertToActualSOQL();

		// 変数をバインド
		progressBar.progressProperty().unbind();
		progressBar.visibleProperty().unbind();
		progressText.textProperty().unbind();
		progressBar.progressProperty().bind(exportor.progressProperty());
		progressBar.visibleProperty().bind(exportor.runningProperty());
		progressText.textProperty().bind(exportor.messageProperty());

		// ボタンを制御
		cancel.setDisable(false);
		export.setDisable(true);
		execute.setDisable(true);

		// ダイアログの準備
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Data Export");
		chooser.getExtensionFilters().add(new ExtensionFilter("CSV Format", "csv"));
		exportHistory.ifPresent(value -> {
			chooser.setInitialDirectory(value.getParentFile());
			chooser.setInitialFileName(value.getName());
		});

		// ファイルの取得（キャンセルなら終了）
		File saveFile = chooser.showSaveDialog(SceneManager.getInstance().getStageStack().peek().unwrap());
		exportHistory = Optional.ofNullable(saveFile);
		if(!exportHistory.isPresent()) {
			cancel.setDisable(true);
			export.setDisable(false);
			execute.setDisable(false);
			return;
		}

		// 既存ファイル削除
		if(saveFile.exists()) {
			saveFile.delete();
		}

		// 連続実行の前処理
		saveFile = splitExportSOQL(saveFile);

		// エクスポートの開始
		try {
			saveFile.createNewFile();
			exportor.setExportPath(saveFile.toPath());
			exportor.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 連続実行の前処理
	 */
	private File splitExportSOQL(File saveFile) {
		File newSaveFile = saveFile;
		if(USE_ADVANCE_SOQL) {
			// SOQLの分割
			String soql = actualSOQL.getValue();
			String[] list = soql.split("\n/\n");
			soqlList.clear();
			soqlList.addAll(list);
			soql = soqlList.remove(0);
			actualSOQL.setValue(soql);

			// ファイル名の修正
			if(!soqlList.isEmpty()){
				Path path = saveFile.toPath();
				String fileName = path.getFileName().toString();
				if(fileName.endsWith(".csv")) {
					fileName = fileName.substring(0, fileName.length()-".csv".length());
				}
				baseFileName.setValue(fileName);
				fileName = String.format("%s-%2$tY%2$tm%2$td%2$tH%2$tM%2$tS.csv",fileName, Calendar.getInstance().getTime());
				newSaveFile = path.resolveSibling(fileName).toFile();
			}
		}

		return newSaveFile;
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
