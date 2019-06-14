package com.gmail.chibitopoochan.soqlui.initializer.parts;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellUtil;

import com.gmail.chibitopoochan.soqlexec.model.FieldMetaInfo;
import com.gmail.chibitopoochan.soqlui.config.ApplicationSettingSet;
import com.gmail.chibitopoochan.soqlui.config.Format;
import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.initializer.service.FieldServiceInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.service.GenerateSOQLServiceInitializer;
import com.gmail.chibitopoochan.soqlui.logic.ConnectionSettingLogic;
import com.gmail.chibitopoochan.soqlui.model.ConnectionSetting;
import com.gmail.chibitopoochan.soqlui.model.DescribeField;
import com.gmail.chibitopoochan.soqlui.model.DescribeSObject;
import com.gmail.chibitopoochan.soqlui.model.ResultSet;
import com.gmail.chibitopoochan.soqlui.model.SObjectRecord;
import com.gmail.chibitopoochan.soqlui.service.ConnectService;
import com.gmail.chibitopoochan.soqlui.service.FieldProvideService;
import com.gmail.chibitopoochan.soqlui.util.DialogUtils;
import com.gmail.chibitopoochan.soqlui.util.FormatUtils;
import com.gmail.chibitopoochan.soqlui.util.format.CSVFormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.ExcelFormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.FormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.QueryFormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.SimpleFormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.WithoutHeaderExcelFormatDecoration;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * コンテキストメニューの初期設定
 * @author mamet
 */
public class ContextPartInitializer implements PartsInitializer<MainController> {
	private static final String URL_ENCODE = "UTF-8";
	private static final String RECORD_ID = "id";
	public static final String LOGIN_URL = ApplicationSettingSet.getInstance().getSetting().getLoginURL();
	public static final String OBJECT_URL = ApplicationSettingSet.getInstance().getSetting().getObjectURL();
	public static final String PROXY_URL = ApplicationSettingSet.getInstance().getSetting().getProxyLoginURL();
	public static final String PROXY_BACK_URL = ApplicationSettingSet.getInstance().getSetting().getProxyBackURL();
	public static final String PROXY_TARGET_URL = ApplicationSettingSet.getInstance().getSetting().getProxyTargetURL();

	private TableView<SObjectRecord> resultTable;
	private TableView<DescribeSObject> sObjectList;
	private TableView<DescribeField> fieldList;

	private Label objectName;
	private TextArea soqlArea;
	private MenuItem selectRecordCount;
	private MenuItem selectAllColumns;
	private MenuItem copyNormal;
	private MenuItem copyWithExcel;
	private MenuItem copyWithCSV;
	private MenuItem copyNoHead;
	private MenuItem createSOQL;
	private MenuItem createWithSelect;
	private MenuItem exportExcelFormat;
	private MenuItem browseRecord;
	private MenuItem browseProxy;
	private MenuItem openFieldList;

	private GenerateSOQLServiceInitializer initService;
	private FieldServiceInitializer initFieldService;
	private FieldProvideService service;
	private ConnectionSettingLogic settingLogic;
	private ConnectService connectService;
	private ProgressIndicator progress;

	@Override
	public void setController(MainController controller) {
		this.resultTable = controller.getResultTable();
		this.sObjectList = controller.getsObjectList();
		this.fieldList = controller.getFieldList();

		this.objectName = controller.getObjectName();
		this.soqlArea = controller.getSoqlArea();
		this.service = controller.getFieldService();

		this.initService = new GenerateSOQLServiceInitializer();
		initService.setController(controller);
		this.initFieldService = new FieldServiceInitializer();
		initFieldService.setController(controller);

		this.settingLogic = controller.getSetting();
		this.connectService = controller.getConnectService();
		this.progress = controller.getFieldProgressIndicator();

	}

	@Override
	public void initialize() {
		copyNormal			= new MenuItem("選択領域のコピー");
		copyWithExcel		= new MenuItem("選択領域のコピー（Excel用）");
		copyWithCSV			= new MenuItem("選択領域のコピー（CSV用）");
		copyNoHead			= new MenuItem("選択領域のコピー（Excel用ヘッダ無し）");
		selectRecordCount	= new MenuItem("SOQL作成（件数）");
		selectAllColumns	= new MenuItem("SOQL作成（全項目）");
		createSOQL			= new MenuItem("SOQL作成（選択範囲）");
		createWithSelect    = new MenuItem("SOQL作成（条件指定）");
		exportExcelFormat	= new MenuItem("オブジェクト定義出力");
		browseRecord		= new MenuItem("ブラウザで表示");
		browseProxy			= new MenuItem("代理ログイン");
		openFieldList		= new MenuItem("項目定義の表示");

		// メニューのイベント設定
		copyNormal.setOnAction(e -> copy(new SimpleFormatDecoration(), e.getSource()));
		copyWithExcel.setOnAction(e -> copy(new ExcelFormatDecoration(), e.getSource()));
		copyWithCSV.setOnAction(e -> copy(new CSVFormatDecoration(), e.getSource()));
		copyNoHead.setOnAction(e -> copy(new WithoutHeaderExcelFormatDecoration(), e.getSource()));
		selectRecordCount.setOnAction(e -> query(new QueryFormatDecoration(), e.getSource()));
		selectAllColumns.setOnAction(e -> queryWithAllColumns(new QueryFormatDecoration(), e.getSource()));
		createSOQL.setOnAction(e -> querySelected(new QueryFormatDecoration(), e.getSource()));
		createWithSelect.setOnAction(e -> queryWithCondition(new QueryFormatDecoration(), e.getSource()));
		exportExcelFormat.setOnAction(e -> exportExcelFormat(e.getSource()));
		browseRecord.setOnAction(e -> browse(e.getSource()));
		browseProxy.setOnAction(e -> browseProxyLogin(e.getSource()));
		openFieldList.setOnAction(e -> openFieldList());

		// コンテキストメニューの登録
		ContextMenu contextMenu = new ContextMenu();
		resultTable.setContextMenu(contextMenu);
		sObjectList.setContextMenu(contextMenu);
		fieldList.setContextMenu(contextMenu);

		// キータイプイベントの登録
		resultTable.setOnKeyPressed(this::keyTyped);
		sObjectList.setOnKeyPressed(this::keyTyped);
		fieldList.setOnKeyPressed(this::keyTyped);

		// マウスイベントの登録
		resultTable.setOnMouseClicked(this::showContextMenu);
		sObjectList.addEventHandler(MouseEvent.MOUSE_CLICKED, this::showContextMenu);
		fieldList.addEventHandler(MouseEvent.MOUSE_CLICKED, this::showContextMenu);

	}

	private void openFieldList() {
		// 変数をバインド
		progress.progressProperty().unbind();
		progress.visibleProperty().unbind();
		progress.progressProperty().bind(service.progressProperty());
		progress.visibleProperty().bind(service.runningProperty());

		initFieldService.initialize();
		service.setSObject(sObjectList.getSelectionModel().getSelectedItem().getName());
		service.start();

	}

	private void exportExcelFormat(Object source) {
		// 項目一覧を取得
		List<DescribeField> list = fieldList.getItems();

		// フォーマットを読み込み
		Format format = Format.getInstance();

		// ダイアログの生成
		File dir = DialogUtils.showDirectoryChooser();

		if(dir == null) return;

		// 項目のメタ情報を加工
		Set<String> keySet = new HashSet<>();
		Stream.of(FieldMetaInfo.Type.values()).forEach(t -> keySet.add("$"+t.name()));

		Workbook book = null;
		OutputStream out = null;
		InputStream input = null;
		try{
			input = new FileInputStream(new File(format.getFilePath()));
			book = WorkbookFactory.create(input);
			out = new FileOutputStream(Paths.get(dir.getAbsolutePath(), getObjectName(source)+".xlsx").toFile());
			// 開始地点のセルを走査
			Map<String,CellAddress> cellMap = new HashMap<>();
			Map<String,Sheet> sheetMap = new HashMap<>();
			for(Sheet s : book) {
				for(Row r : s) {
					for(Cell c : r) {
						if(c.getCellType() == CellType.STRING && keySet.contains(c.getStringCellValue())) {
							cellMap.put(c.getStringCellValue(), c.getAddress());
							sheetMap.put(c.getStringCellValue(), c.getSheet());
						}
					}
				}
			}

			// セルに値を埋め込む
			for(DescribeField field : list) {
				for(String key : keySet) {
					if(cellMap.containsKey(key)) {
						// 必要なデータを準備
						CellAddress a = cellMap.get(key);
						Sheet s = sheetMap.get(key);
						Row r = CellUtil.getRow(a.getRow(), s);
						Cell c = CellUtil.getCell(r, a.getColumn());

						// 値を設定
						c.setCellValue(field.getMetaInfo().get(FieldMetaInfo.Type.valueOf(key.substring(1))));

						// セルの位置を移動
						cellMap.put(key, new CellAddress(a.getRow()+1, a.getColumn()));
					}
				}
			}

			// ファイルを書き込み
			book.write(out);

			Alert confirm = new Alert(AlertType.INFORMATION, "Export finished.");
			confirm.showAndWait();

		} catch (Exception e) {
			e.printStackTrace();
			Alert confirm = new Alert(AlertType.INFORMATION, "Export failed.\n" + e.getMessage());
			confirm.showAndWait();
			return;
		} finally {
			try {
				input.close();
				out.close();
				book.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

	}

	private void showContextMenu(MouseEvent e) {
		if(e.getButton() != MouseButton.SECONDARY) 	return;
		if(!(e.getSource() instanceof TableView)) return;

		TableView<?> table  = (TableView<?>) e.getSource();
		table.getContextMenu().setUserData(table);

		if(table.getItems().isEmpty()) return;

		ContextMenu context = table.getContextMenu();
		context.getItems().clear();
		if(table == sObjectList) {
			context.getItems().addAll(openFieldList, selectRecordCount, selectAllColumns, copyNormal, copyNoHead, browseRecord);
		} else if(table == fieldList) {
			context.getItems().addAll(createSOQL, copyNormal, copyWithCSV, copyWithExcel, copyNoHead, exportExcelFormat);
		} else {
			context.getItems().addAll(createWithSelect, copyNormal, copyWithCSV, copyWithExcel, copyNoHead, browseRecord, browseProxy);
		}
		context.show(table, 0, 0);

	}

	private void keyTyped(KeyEvent e) {
		KeyCodeCombination copyKey = new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_ANY);
		if(copyKey.match(e)) {
			copy(new SimpleFormatDecoration(), e.getSource());
		}
	}

	private void query(FormatDecoration decorator, Object source) {

		// 項目を構築
		String count = "count(id)";
		List<String> columnList = new ArrayList<>();
		columnList.add(count);
		List<List<String>> rowList = new ArrayList<>();
		rowList.add(columnList);

		// SOQLの設定
		decorator.setTableAfter(getObjectName(source));
		String formattedContent = FormatUtils.format(decorator, () -> rowList);
		soqlArea.setText(formattedContent);

	}

	private void querySelected(FormatDecoration decorator, Object source) {
		List<String> selectedItems = fieldList.getSelectionModel().getSelectedItems().stream().map(i -> i.getName()).collect(Collectors.toList());

		decorator.setTableAfter(getObjectName(source));
		String soql = FormatUtils.format(decorator, () ->
			selectedItems.stream().map(i -> {
				List<String> list = new ArrayList<>();
				list.add(i);
				return list;
			}).collect(Collectors.toList())
		);

		soqlArea.setText(soql);

	}

	private void queryWithCondition(FormatDecoration decorator, Object source) {
		// 列毎にセルを集計
		Map<String, List<String>> conditions = resultTable
				.getSelectionModel()
				.getSelectedCells()
				.stream()
				.collect(Collectors.groupingBy(
						k -> k.getTableColumn().getText()
						,Collectors.mapping(
								v -> (String) v.getTableColumn().getCellData(v.getRow())
								,Collectors.toList()
						)
				));

		ResultSet resultSet = (ResultSet) resultTable.getUserData();
		StringBuilder soql = new StringBuilder(resultSet.getSOQL());

		// 条件を構築
		// TODO 型によって囲み文字が異なる
		conditions.forEach((k,v) ->
			soql
			.append(" or ")
			.append(k)
			.append(" in (")
			.append(v.stream().map(i -> String.format("'%s'", i)).collect(Collectors.joining(",")))
			.append(")")
			.append(System.lineSeparator())
		);

		String soqlText = soql.toString();

		if(!soqlText.matches("(?s).+\\s+where\\s+.+")) {
			soqlText = soqlText.replaceFirst(" or ", " where ");
		}

		soqlArea.setText(soqlText);
	}

	private void queryWithAllColumns(FormatDecoration decorator, Object source) {
		initService.initialize();
		initService.setFormatDecoration(decorator);
		service.setSObject(getObjectName(source));
		service.start();
	}

	private String getObjectName(Object source) {
		// オブジェクト名の取得
		String objectNameText = "";
		if(source instanceof MenuItem) {
			MenuItem item = (MenuItem) source;
			TableView<?> table = (TableView<?>) item.getParentPopup().getUserData();
			if(table == sObjectList) {
				DescribeSObject object = sObjectList.getSelectionModel().getSelectedItem();
				objectNameText = object.getName();
			} else if(table == fieldList) {
				objectNameText = objectName.getText();
			} else {
				throw new IllegalArgumentException("cannot cast to sObjectList/FieldList from " + source);
			}
		} else {
			throw new IllegalArgumentException("cannot cast to TableView/MenuItem from " + source);
		}

		return objectNameText;
	}

	private void copy(FormatDecoration decorator, Object source) {
		String formattedContent = buildFormat(decorator, source);

		// クリップボードへコピー
		ClipboardContent content = new ClipboardContent();
		content.putString(formattedContent);
		Clipboard.getSystemClipboard().setContent(content);

	}

	private void browse(Object source) {
		try {
			if(source instanceof MenuItem) {
				MenuItem item = (MenuItem) source;
				TableView<?> table = (TableView<?>) item.getParentPopup().getUserData();
				if(table == sObjectList) {
					DescribeSObject object = sObjectList.getSelectionModel().getSelectedItem();
					if("".equals(object.getKeyPrefix())) {
						throw new IllegalArgumentException("please include key prefix with sObject");
					}
					openBrowser(String.format(OBJECT_URL, object.getKeyPrefix()));
				} else if(table == resultTable) {
					SObjectRecord record = resultTable.getSelectionModel().getSelectedItem();
					Map<String, String> fieldMap = record.getRecord();

					Optional<String> fieldName = fieldMap.keySet().stream().filter(key -> key.toLowerCase().equals(RECORD_ID)).findFirst();

					if(fieldName.isPresent()){
						openBrowser(fieldMap.get(fieldName.get()));
					} else {
						throw new IllegalArgumentException("please include id field with record");
					}
				} else {
					throw new IllegalArgumentException("cannot cast to sObjectList/FieldList from " + source);
				}
			} else {
				throw new IllegalArgumentException("cannot cast to TableView/MenuItem from " + source);
			}
		} catch(Exception e) {
			Alert confirm = new Alert(AlertType.ERROR, e.getMessage());
			confirm.showAndWait();
		}
	}

	private void browseProxyLogin(Object source) {
		try {
			String userId = "";

			if(source instanceof MenuItem) {
				SObjectRecord record = resultTable.getSelectionModel().getSelectedItem();
				Map<String, String> fieldMap = record.getRecord();
				Optional<String> fieldName = fieldMap.keySet().stream().filter(key -> key.toLowerCase().equals(RECORD_ID)).findFirst();
				if(fieldName.isPresent()){
					userId = fieldMap.get(fieldName.get());
				} else {
					throw new IllegalArgumentException("please include id field with record");
				}
			} else {
				throw new IllegalArgumentException("cannot cast to sObjectList/FieldList from " + source);
			}

			String orgId = connectService.getUserInfoMap().get("OrganizationId");
			String targetURL = URLEncoder.encode(PROXY_TARGET_URL,URL_ENCODE);
			String retURL = URLEncoder.encode(String.format(PROXY_BACK_URL, userId),URL_ENCODE);
			String startURL = String.format(PROXY_URL, orgId, userId, retURL, targetURL);

			// ブラウザで表示
			openBrowser(startURL);

		} catch (Exception e) {
			Alert confirm = new Alert(AlertType.ERROR, e.getMessage());
			confirm.showAndWait();
		}
	}

	private void openBrowser(String startURL) {
		ConnectionSetting setting = this.settingLogic.getConnectionSetting(settingLogic.getSelectedName(ConnectionSettingLogic.DEFAULT_NAME));
		String username = setting.getUsername();
		String password = setting.getPassword();
		String env = setting.getEnvironmentOfURL();

		// ブラウザで表示
		Desktop desktop = Desktop.getDesktop();
		try {
			String url = String.format(LOGIN_URL,env, username, password, URLEncoder.encode(startURL,URL_ENCODE));
			desktop.browse(URI.create(url));
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("cannot open a browser");
		}

	}

	private String buildFormat(FormatDecoration decorator, Object source) {
		TableView<?> table;

		if(source instanceof MenuItem) {
			MenuItem item = (MenuItem) source;
			table = (TableView<?>) item.getParentPopup().getUserData();
		} else if(source instanceof TableView) {
			table = (TableView<?>) source;
		} else {
			throw new IllegalArgumentException("cannot cast to TableView/MenuItem from " + source);
		}

		String formattedContent = FormatUtils.format(decorator,() -> buildFormatValueProvider(table, decorator.isShowHeader()));

		return formattedContent;
	}

	/**
	 * 選択された項目をもとにフォーマットを構築します
	 * @param table 対象のTableView
	 * @param withHeader ヘッダを含むならtrue
	 * @return 選択項目の二次元配列
	 */
	@SuppressWarnings("unchecked")
	private List<List<String>> buildFormatValueProvider(TableView<?> table, boolean withHeader) {
		// 選択範囲を取得
		@SuppressWarnings("rawtypes")
		ObservableList<TablePosition> positionList =
				 (ObservableList<TablePosition>) table.getSelectionModel().getSelectedCells();

		List<List<String>> rowList = new LinkedList<>();
		List<String> columnList = new LinkedList<>();

		if(withHeader) {
			table.getVisibleLeafColumns()
				.stream()
				.filter(column ->
					positionList.stream().anyMatch(
						position -> position.getTableColumn() == column
					)
				).forEach(column -> {
				columnList.add(column.getText());
			});
			rowList.add(new ArrayList<>(columnList));
			columnList.clear();
		}
		positionList.forEach(position -> {
			if(position.getColumn() == 0 && !columnList.isEmpty()) {
				rowList.add(new ArrayList<>(columnList));
				columnList.clear();
			}

			Object column = table.getColumns().get(position.getColumn());
			if (column instanceof TableColumn) {
				columnList.add(((TableColumn<?,String>)column).getCellData(position.getRow()));
			}

		});
		rowList.add(columnList);

		return rowList;

	}

}
