package com.gmail.chibitopoochan.soqlui.initializer.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.gmail.chibitopoochan.soqlexec.soql.QueryAnalyzeUtils;
import com.gmail.chibitopoochan.soqlexec.soql.QueryAnalyzeUtils.TokenException;
import com.gmail.chibitopoochan.soqlexec.soql.SOQL;
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
import com.gmail.chibitopoochan.soqlui.util.BrowsingUtils;
import com.gmail.chibitopoochan.soqlui.util.CopyUtils;
import com.gmail.chibitopoochan.soqlui.util.ExcelExportUtils;
import com.gmail.chibitopoochan.soqlui.util.FormatUtils;
import com.gmail.chibitopoochan.soqlui.util.format.CSVFormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.ExcelFormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.FormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.QueryFormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.SimpleFormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.WithoutHeaderExcelFormatDecoration;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
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
	private static final String RECORD_ID = "id";

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
		exportExcelFormat.setOnAction(e -> ExcelExportUtils.exportExcelFormat(fieldList.getItems(), getObjectName(e.getSource())));
		browseRecord.setOnAction(e -> browse(e.getSource()));
		browseProxy.setOnAction(e -> browseProxyLogin(e.getSource()));
		openFieldList.setOnAction(e -> openFieldList(e.getSource()));

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

	private void browseProxyLogin(Object source) {
		ConnectionSetting setting = this.settingLogic.getConnectionSetting(settingLogic.getSelectedName(ConnectionSettingLogic.DEFAULT_NAME));
		String orgId = connectService.getUserInfoMap().get("OrganizationId");
		String userId;
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

		BrowsingUtils.browseProxyLogin(userId, orgId, setting);
	}

	public void browse(Object source) {
		try {
			if(source instanceof MenuItem) {
				ConnectionSetting setting = this.settingLogic.getConnectionSetting(settingLogic.getSelectedName(ConnectionSettingLogic.DEFAULT_NAME));
				MenuItem item = (MenuItem) source;
				TableView<?> table = (TableView<?>) item.getParentPopup().getUserData();
				if(table == sObjectList) {
					DescribeSObject object = sObjectList.getSelectionModel().getSelectedItem();
					BrowsingUtils.browseObject(object.getKeyPrefix(), setting);
				} else if(table == resultTable) {
					SObjectRecord record = resultTable.getSelectionModel().getSelectedItem();
					Map<String, String> fieldMap = record.getRecord();

					Optional<String> fieldName = fieldMap.keySet().stream().filter(key -> key.toLowerCase().equals(RECORD_ID)).findFirst();

					if(fieldName.isPresent()){
						BrowsingUtils.browseRecord(fieldMap.get(fieldName.get()), setting);
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

	private void openFieldList(Object source) {
		// 変数をバインド
		progress.progressProperty().unbind();
		progress.visibleProperty().unbind();
		progress.progressProperty().bind(service.progressProperty());
		progress.visibleProperty().bind(service.runningProperty());

		initFieldService.initialize();

		MenuItem item = (MenuItem) source;
		TableView<?> table = (TableView<?>) item.getParentPopup().getUserData();

		try {
			if(table == sObjectList) {
				service.setSObject(sObjectList.getSelectionModel().getSelectedItem().getName());
			} else if(table == resultTable){
				ResultSet resultSet = (ResultSet) resultTable.getUserData();
				SOQL soql = QueryAnalyzeUtils.analyze(resultSet.getSOQL());
				service.setSObject(soql.getFromObject());
			}

			if(service.getSObject() == null) {
				throw new IllegalArgumentException("オブジェクトが見つかりません。");
			}

			service.start();

		} catch (TokenException e) {
			Alert confirm = new Alert(AlertType.ERROR, e.getMessage());
			confirm.showAndWait();
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
			context.getItems().addAll(openFieldList, createWithSelect, copyNormal, copyWithCSV, copyWithExcel, copyNoHead, browseRecord, browseProxy);
		}
		context.show(table, 0, 0);

	}

	private void keyTyped(KeyEvent e) {
		KeyCodeCombination copyKey = new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_ANY);
		if(copyKey.match(e)) {
			copy(new SimpleFormatDecoration(), e.getSource());
		}
	}

	private void copy(FormatDecoration format, Object source) {
		TableView<?> table;

		if(source instanceof MenuItem) {
			MenuItem item = (MenuItem) source;
			table = (TableView<?>) item.getParentPopup().getUserData();
		} else if(source instanceof TableView) {
			table = (TableView<?>) source;
		} else {
			throw new IllegalArgumentException("cannot cast to TableView/MenuItem from " + source);
		}
		CopyUtils.copyToClipboard(format, table);

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


}
