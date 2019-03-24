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
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.SceneManager;
import com.gmail.chibitopoochan.soqlui.initializer.MainControllerInitializer;
import com.gmail.chibitopoochan.soqlui.logic.ConnectionSettingLogic;
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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainController implements Initializable, Controller {
	// クラス共通の参照
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	// 画面上のコンポーネント
	// メニュー
	@FXML private Menu menuIcon;
	@FXML private MenuItem menuFileProxy;
	@FXML private MenuItem menuFileConnection;

	// 左側上段
	@FXML private ComboBox<String> connectOption;
	@FXML private Button connect;
	@FXML private Button disconnect;
	@FXML private ProgressIndicator progressIndicator;

	// 左側中断
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
	@FXML private TabPane queryTabArea;
	@FXML private TextField batchSize;
	@FXML private CheckBox all;
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

	// 非同期のサービス
	private ConnectService connectService = new ConnectService();
	private SOQLExecuteService executionService = new SOQLExecuteService();
	private FieldProvideService fieldService = new FieldProvideService();
	private ExportService exportService = new ExportService();

	// 状態管理
	private ObservableList<DescribeSObject> objectMasterList = FXCollections.observableArrayList();
	private ObservableList<DescribeField> fieldMasterList = FXCollections.observableArrayList();
	private BooleanProperty withExecute = new SimpleBooleanProperty(false);

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

	}

	/**
	 * 接続設定画面の表示
	 */
	public void openConnectSetting() {
		try {
			logger.debug("Open Window [Connection Setting]");
			manager.sceneOpen(Configuration.VIEW_SU02, Configuration.TITLE_SU02);
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
			manager.sceneOpen(Configuration.VIEW_SU04, Configuration.TITLE_SU04);
		} catch (IOException e) {
			logger.error("Open window error", e);
		}

	}

	/**
	 * ファイルへの保存
	 */
	public void onSave() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("ファイルへの保存");
		chooser.setSelectedExtensionFilter(new ExtensionFilter("soql file", "soql"));
		chooser.setInitialFileName("untitled."+connectOption.getSelectionModel().getSelectedItem()+".soql");

		File saveFile = chooser.showSaveDialog(manager.getStageStack().peek().unwrap());
		if(saveFile == null) return;

		try(FileWriter writer = new FileWriter(saveFile)) {
			writer.write(soqlArea.getText());
			Alert resultDialog = new Alert(AlertType.INFORMATION);
			resultDialog.setContentText("ファイルを保存しました。");
			resultDialog.setTitle("確認");
			resultDialog.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void minimam() {
		manager.minimized();
	}

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

}
