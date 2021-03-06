package com.gmail.chibitopoochan.soqlui.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;

import com.gmail.chibitopoochan.soqlui.SceneManager;
import com.gmail.chibitopoochan.soqlui.config.ApplicationSettingSet;
import com.gmail.chibitopoochan.soqlui.initializer.MainControllerInitializer;
import com.gmail.chibitopoochan.soqlui.logic.ConnectionSettingLogic;
import com.gmail.chibitopoochan.soqlui.logic.ExtractFileLogic;
import com.gmail.chibitopoochan.soqlui.logic.FavoriteLogic;
import com.gmail.chibitopoochan.soqlui.logic.SOQLHistoryLogic;
import com.gmail.chibitopoochan.soqlui.model.DescribeField;
import com.gmail.chibitopoochan.soqlui.model.DescribeSObject;
import com.gmail.chibitopoochan.soqlui.model.SOQLFavorite;
import com.gmail.chibitopoochan.soqlui.model.SOQLHistory;
import com.gmail.chibitopoochan.soqlui.model.SObjectRecord;
import com.gmail.chibitopoochan.soqlui.service.ConnectService;
import com.gmail.chibitopoochan.soqlui.service.ExportService;
import com.gmail.chibitopoochan.soqlui.service.FieldProvideService;
import com.gmail.chibitopoochan.soqlui.service.SOQLExecuteService;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;
import com.gmail.chibitopoochan.soqlui.util.DialogUtils;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainController implements Initializable, Controller {
	// クラス共通の参照
	private static final Logger logger = LogUtils.getLogger(MainController.class);
	private static final ResourceBundle config = ResourceBundle.getBundle(Configuration.RESOURCE);
	private static final boolean USE_DECORATOR = ApplicationSettingSet.getInstance().getSetting().isUseDecorator();

	// 画面上のコンポーネント
	// メニュー
	@FXML private Menu menuIcon;
	@FXML private MenuItem menuFileProxy;
	@FXML private MenuItem menuFileConnection;
	@FXML private Label titleLabel;
	@FXML private Button minimam;
	@FXML private Button maximam;
	@FXML private Button close;

	// 左側上段
	@FXML private ComboBox<String> connectOption;
	@FXML private Button connect;
	@FXML private Button disconnect;
	@FXML private CheckBox useTooling;

	// 左側中断
	@FXML private ProgressIndicator progressIndicator;
	@FXML private TableView<DescribeSObject> sObjectList;
	@FXML private TableColumn<DescribeSObject, String> prefixColumn;
	@FXML private TableColumn<DescribeSObject, String> sObjectColumn;
	@FXML private TextField objectSearch;

	// 左側下段
	@FXML private TabPane fieldTabArea;
	@FXML private TableView<DescribeField> fieldList;
	@FXML private TextField columnSearch;
	@FXML private Label objectName;
	@FXML private ProgressIndicator fieldProgressIndicator;

	// 中央
	@FXML private Button execute;
	@FXML private Button export;
	@FXML private Button cancel;
	@FXML private TextArea soqlArea;
	@FXML private WebView soqlWebArea;
	private StringProperty actualSOQL = new SimpleStringProperty(this, "actualSOQL");
	@FXML private TabPane queryTabArea;
	@FXML private TextField batchSize;
	@FXML private CheckBox all;
	@FXML private CheckBox join;
	@FXML private TableView<SObjectRecord> resultTable;
	@FXML private TextField resultSearch;
	@FXML private TabPane tabArea;

	// 右側上段
	@FXML private TextField favoriteSearch;
	@FXML private ListView<SOQLFavorite> favoriteList;

	// 右側下段
	@FXML private TextField historySearch;
	@FXML private ListView<SOQLHistory> historyList;

	// 下段
	@FXML private ProgressBar progressBar;
	@FXML private Label progressText;

	// 業務ロジック
	private SceneManager manager;
	private ConnectionSettingLogic setting = new ConnectionSettingLogic();
	private SOQLHistoryLogic history = new SOQLHistoryLogic();
	private FavoriteLogic favorite = new FavoriteLogic();
	private ExtractFileLogic extract = new ExtractFileLogic();

	// 非同期のサービス
	private ConnectService connectService = new ConnectService();
	private SOQLExecuteService executionService = new SOQLExecuteService();
	private FieldProvideService fieldService = new FieldProvideService();
	private ExportService exportService = new ExportService();

	// 状態管理
	private ObservableList<DescribeSObject> objectMasterList = FXCollections.observableArrayList();
	private ObservableList<DescribeField> fieldMasterList = FXCollections.observableArrayList();
	private BooleanProperty withExecute = new SimpleBooleanProperty(false);
	private ObservableList<String> execSoqlList = FXCollections.observableArrayList();
	private StringProperty execSoqlBaseName = new SimpleStringProperty();

	// 初期化
	private MainControllerInitializer init;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.manager = SceneManager.getInstance();

		// 画面の初期化
		init = new MainControllerInitializer();
		init.setController(this);
		init.initialize();

		// 初期値を設定
		Map<String, String> parameters = manager.getParameters();
		if(parameters.containsKey("param")) {
			loadSOQLFile(parameters);
		}

		// デコレーション無しの場合
		if(USE_DECORATOR) {
			titleLabel.setVisible(false);
			minimam.setVisible(false);
			maximam.setVisible(false);
			close.setVisible(false);

		} else {
			// ウィンドウの移動を設定
			titleLabel.setOnMousePressed(event -> {
				manager.saveLocation(event.getScreenX(),event.getScreenY());
			});
			titleLabel.setOnMouseDragged(event -> {
				manager.moveLocation(event.getScreenX(), event.getScreenY());
			});
		}
	}

	private void loadSOQLFile(Map<String, String> parameters) {
		String fileName = parameters.get("param");
		Optional<String> envName = Optional.empty();

		// ファイル名のチェック
		if(fileName.endsWith(".soql")) {
			String fileNameExcludeExtention = fileName.substring(0, fileName.lastIndexOf(".soql"));
			int indexOfLastPrefix = fileNameExcludeExtention.lastIndexOf(".");
			if(indexOfLastPrefix > -1) {
				envName = Optional.of(fileNameExcludeExtention.substring(indexOfLastPrefix+1));
			}

			// ファイルの存在チェック
			File file = new File(fileName);
			if(file.exists()) {
				// ファイルの読み込み
				try(BufferedReader br = Files.newBufferedReader(file.toPath())) {
					soqlArea.setText(br.lines().collect(Collectors.joining(System.lineSeparator())));
				} catch (IOException e) {
					e.printStackTrace();
				}

				// 初期化
				envName.ifPresent(env -> connectOption.getSelectionModel().select(env));

				// 接続の実行
				// TODO nullで問題ないか？
				setWithExecute(true);
				connect.getOnAction().handle(null);

			}

		}

		logger.debug(fileName);

	}

	/**
	 * アプリケーション設定画面の表示
	 */
	public void openApplicationSetting() {
		try {
			logger.debug("Open Window [Application Setting]");
			manager.sceneOpen(Configuration.VIEW_SU05, Configuration.TITLE_SU05, false);
		} catch (IOException e) {
			logger.error("Open window error", e);
		}
	}

	/**
	 * 接続設定画面の表示
	 */
	public void openConnectSetting() {
		try {
			logger.debug("Open Window [Connection Setting]");
			manager.sceneOpen(Configuration.VIEW_SU02, Configuration.TITLE_SU02, false);
		} catch (IOException e) {
			logger.error("Open window error", e);
		}
	}

	/**
	 * プロキシ設定画面の表示
	 */
	public void openProxySetting() {
		try {
			logger.debug("Open Window [Proxy Setting]");
			manager.sceneOpen(Configuration.VIEW_SU04, Configuration.TITLE_SU04, false);
		} catch (IOException e) {
			logger.error("Open window error", e);
		}

	}

	/**
	 * ファイルへの保存
	 */
	public void onSave() {
		// 保存先の取得
		File saveFile = DialogUtils.showSaveDialog(
				 "untitled."+connectOption.getSelectionModel().getSelectedItem()+".soql"
				,new ExtensionFilter("soql file", "soql"));
		if(saveFile == null) return;

		// ファイルの保存
		logger.debug("Save to " + saveFile.getAbsolutePath());
		try(FileWriter writer = new FileWriter(saveFile)) {
			writer.write(soqlArea.getText());
			DialogUtils.showAlertDialog("ファイルを保存しました。");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * ヘルプの表示
	 */
	public void onHelp() {
		DialogUtils.showWebDialog(config.getString(Configuration.URL_HELP));
	}

	/**
	 * 概要の表示
	 */
	public void onAbout() {
		DialogUtils.showWebDialog(config.getString(Configuration.URL_ABOUT));
	}

	/**
	 * 変更履歴の表示
	 */
	public void onChangeLog() {
		DialogUtils.showWebDialog(config.getString(Configuration.URL_CHANGELOG));
	}

	/**
	 * ライセンスの表示
	 */
	public void onLicense() {
		DialogUtils.showWebDialog(config.getString(Configuration.URL_LICENSE));
	}

	/**
	 * Windowの最小化
	 */
	public void minimam() {
		manager.minimized();
	}

	/**
	 * Windowの最大化
	 */
	public void onChangeSize() {
		manager.sizeChange();
	}

	/**
	 * アプリのクローズ
	 */
	public void onClose() {
		manager.sceneClose();
	}

	/**
	 * 接続情報を更新
	 */
	@Override
	public void onCloseChild() {
		try {
			setting.loadSettings();
		} catch (IllegalStateException | IOException | XMLStreamException e) {
			logger.error("Load settings error", e);
		}
		init.reset();
	}

	/**
	 *
	 * @return
	 */
	public TextField getResultSearch() {
		return resultSearch;
	}

	/**
	 *
	 * @param resultSearch
	 */
	public void setResultSearch(TextField resultSearch) {
		this.resultSearch = resultSearch;
	}

	/**
	 * @return progressBar
	 */
	public ProgressBar getProgressBar() {
		return progressBar;
	}

	/**
	 * @param progress
	 */
	public void setProgressBar(ProgressBar progress) {
		this.progressBar = progress;
	}

	/**
	 * @return exportService
	 */
	public ExportService getExportService() {
		return exportService;
	}

	/**
	 * @param exportService セットする exportService
	 */
	public void setExportService(ExportService exportService) {
		this.exportService = exportService;
	}

	/**
	 * @return export
	 */
	public Button getExport() {
		return export;
	}

	/**
	 * @param export セットする export
	 */
	public void setExport(Button export) {
		this.export = export;
	}

	/**
	 * @return connectOption
	 */
	public ComboBox<String> getConnectOption() {
		return connectOption;
	}

	/**
	 * @return connect
	 */
	public Button getConnect() {
		return connect;
	}

	/**
	 * @return disconnect
	 */
	public Button getDisconnect() {
		return disconnect;
	}

	/**
	 * @return progressIndicator
	 */
	public ProgressIndicator getProgressIndicator() {
		return progressIndicator;
	}

	/**
	 * @return sObjectList
	 */
	public TableView<DescribeSObject> getsObjectList() {
		return sObjectList;
	}

	/**
	 * @return prefixColumn
	 */
	public TableColumn<DescribeSObject, String> getPrefixColumn() {
		return prefixColumn;
	}

	/**
	 * @return sObjectColumn
	 */
	public TableColumn<DescribeSObject, String> getsObjectColumn() {
		return sObjectColumn;
	}

	/**
	 * @return objectSearch
	 */
	public TextField getObjectSearch() {
		return objectSearch;
	}

	/**
	 * @return fieldList
	 */
	public TableView<DescribeField> getFieldList() {
		return fieldList;
	}

	/**
	 * @return progressIndicator
	 */
	public ProgressIndicator getFieldProgressIndicator() {
		return fieldProgressIndicator;
	}

	/**
	 * @return columnSearch
	 */
	public TextField getColumnSearch() {
		return columnSearch;
	}

	/**
	 * @return execute
	 */
	public Button getExecute() {
		return execute;
	}

	/**
	 * @return soqlArea
	 */
	public TextArea getSoqlArea() {
		return soqlArea;
	}

	/**
	 * @return batchSize
	 */
	public TextField getBatchSize() {
		return batchSize;
	}

	/**
	 * @return all
	 */
	public CheckBox getAll() {
		return all;
	}

	public CheckBox getJoin() {
		return join;
	}

	/**
	 * @return resultTable
	 */
	public TableView<SObjectRecord> getResultTable() {
		return resultTable;
	}

	/**
	 * @return setting
	 */
	public ConnectionSettingLogic getSetting() {
		return setting;
	}

	/**
	 * @return connectService
	 */
	public ConnectService getConnectService() {
		return connectService;
	}

	/**
	 * @return executionService
	 */
	public SOQLExecuteService getExecutionService() {
		return executionService;
	}

	/**
	 * @return fieldService
	 */
	public FieldProvideService getFieldService() {
		return fieldService;
	}

	/**
	 * @return objectMasterList
	 */
	public ObservableList<DescribeSObject> getObjectMasterList() {
		return objectMasterList;
	}

	/**
	 * @return fieldMasterList
	 */
	public ObservableList<DescribeField> getFieldMasterList() {
		return fieldMasterList;
	}

	/**
	 * @return progressText
	 */
	public Label getProgressText() {
		return progressText;
	}

	/**
	 * @param progressText セットする progressText
	 */
	public void setProgressText(Label progressText) {
		this.progressText = progressText;
	}

	/**
	 * @return objectName
	 */
	public Label getObjectName() {
		return objectName;
	}

	/**
	 * @param objectName セットする objectName
	 */
	public void setObjectName(Label objectName) {
		this.objectName = objectName;
	}

	/**
	 * @return tabArea
	 */
	public TabPane getTabArea() {
		return tabArea;
	}

	/**
	 * @param tabArea セットする tabArea
	 */
	public void setTabArea(TabPane tabArea) {
		this.tabArea = tabArea;
	}
	/**
	 * @return historyList
	 */
	public ListView<SOQLHistory> getHistoryList() {
		return historyList;
	}

	/**
	 * @return historySearch
	 */
	public TextField getHistorySearch() {
		return historySearch;
	}

	/**
	 * @return history
	 */
	public SOQLHistoryLogic getHistory() {
		return history;
	}

	/**
	 * @param history セットする history
	 */
	public void setHistory(SOQLHistoryLogic history) {
		this.history = history;
	}

	/**
	 * @return favoriteList
	 */
	public ListView<SOQLFavorite> getFavoriteList() {
		return favoriteList;
	}

	/**
	 * @return favorite
	 */
	public FavoriteLogic getFavoriteLogic() {
		return favorite;
	}

	/**
	 * @param favorite セットする favorite
	 */
	public void setFavorite(FavoriteLogic favorite) {
		this.favorite = favorite;
	}

	/**
	 * @return cancel
	 */
	public Button getCancel() {
		return cancel;
	}

	/**
	 * @param cancel セットする cancel
	 */
	public void setCancel(Button cancel) {
		this.cancel = cancel;
	}

	/**
	 * @return favoriteSearch
	 */
	public TextField getFavoriteSearch() {
		return favoriteSearch;
	}

	/**
	 * @return queryTabArea
	 */
	public TabPane getQueryTabArea() {
		return queryTabArea;
	}

	/**
	 * @param queryTabArea セットする queryTabArea
	 */
	public void setQueryTabArea(TabPane queryTabArea) {
		this.queryTabArea = queryTabArea;
	}

	/**
	 * @return fieldTabArea
	 */
	public TabPane getFieldTabArea() {
		return fieldTabArea;
	}

	/**
	 * @param fieldTabArea セットする fieldTabArea
	 */
	public void setFieldTabArea(TabPane fieldTabArea) {
		this.fieldTabArea = fieldTabArea;
	}

	/**
	 * @param fieldProgressIndicator セットする fieldProgressIndicator
	 */
	public void setFieldProgressIndicator(ProgressIndicator fieldProgressIndicator) {
		this.fieldProgressIndicator = fieldProgressIndicator;
	}

	/**
	 * @return withExecute
	 */
	public boolean withExecute() {
		return withExecute.get();
	}

	/**
	 * @param withExecute セットする withExecute
	 */
	public void setWithExecute(boolean withExecute) {
		this.withExecute.set(withExecute);
	}

	/**
	 * 接続後、SOQL実行有無のプロパティ
	 */
	public BooleanProperty withExecuteProperty() {
		return withExecute;
	}

	public WebView getSoqlWebArea() {
		return soqlWebArea;
	}

	/**
	 * @return titleLabel
	 */
	public Label getTitleLabel() {
		return titleLabel;
	}

	/**
	 * @param titleLabel セットする titleLabel
	 */
	public void setTitleLabel(Label titleLabel) {
		this.titleLabel = titleLabel;
	}

	public StringProperty actualSOQL() {
		return actualSOQL;
	}

	/**
	 * @return useTooling
	 */
	public CheckBox getUseTooling() {
		return useTooling;
	}

	/**
	 * @param useTooling セットする useTooling
	 */
	public void setUseTooling(CheckBox useTooling) {
		this.useTooling = useTooling;
	}

	/**
	 * @return extract
	 */
	public ExtractFileLogic getExtract() {
		return extract;
	}

	/**
	 * @param extract セットする extract
	 */
	public void setExtract(ExtractFileLogic extract) {
		this.extract = extract;
	}

	/**
	 * @return execSoqlList
	 */
	public ObservableList<String> getExecSoqlList() {
		return execSoqlList;
	}

	/**
	 * @param execSoqlList セットする execSoqlList
	 */
	public void setExecSoqlList(ObservableList<String> execSoqlList) {
		this.execSoqlList = execSoqlList;
	}

	/**
	 * @return execSoqlBaseName
	 */
	public StringProperty getExecSoqlBaseName() {
		return execSoqlBaseName;
	}

	/**
	 * @param execSoqlBaseName セットする execSoqlBaseName
	 */
	public void setExecSoqlBaseName(StringProperty execSoqlBaseName) {
		this.execSoqlBaseName = execSoqlBaseName;
	}

}
