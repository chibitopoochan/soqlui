package com.gmail.chibitopoochan.soqlui.initializer.parts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.initializer.service.GenerateSOQLServiceInitializer;
import com.gmail.chibitopoochan.soqlui.model.DescribeField;
import com.gmail.chibitopoochan.soqlui.model.DescribeSObject;
import com.gmail.chibitopoochan.soqlui.model.SObjectRecord;
import com.gmail.chibitopoochan.soqlui.service.FieldProvideService;
import com.gmail.chibitopoochan.soqlui.util.FormatUtils;
import com.gmail.chibitopoochan.soqlui.util.format.CSVFormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.ExcelFormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.FormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.QueryFormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.SimpleFormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.WithoutHeaderExcelFormatDecoration;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
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

public class ContextPartInitializer implements PartsInitializer<MainController> {
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

	private GenerateSOQLServiceInitializer initService;
	private FieldProvideService service;

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
	}

	@Override
	public void initialize() {
		copyNormal		= new MenuItem("Copy select area");
		copyWithExcel	= new MenuItem("Copy select area(Excel)");
		copyWithCSV	= new MenuItem("Copy select area(CSV)");
		copyNoHead		= new MenuItem("Copy select area(Excel No Head)");
		selectRecordCount = new MenuItem("Select record count");
		selectAllColumns = new MenuItem("Select all columns");

		// メニューのイベント設定
		copyNormal.setOnAction(e -> copy(new SimpleFormatDecoration(), e.getSource()));
		copyWithExcel.setOnAction(e -> copy(new ExcelFormatDecoration(), e.getSource()));
		copyWithCSV.setOnAction(e -> copy(new CSVFormatDecoration(), e.getSource()));
		copyNoHead.setOnAction(e -> copy(new WithoutHeaderExcelFormatDecoration(), e.getSource()));
		selectRecordCount.setOnAction(e -> query(new QueryFormatDecoration(), e.getSource()));
		selectAllColumns.setOnAction(e -> queryWithAllColumns(new QueryFormatDecoration(), e.getSource()));

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
		fieldList.setOnMouseClicked(this::showContextMenu);

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
			context.getItems().addAll(selectRecordCount, selectAllColumns, copyNormal, copyNoHead);
		} else {
			context.getItems().addAll(copyNormal, copyWithCSV, copyWithExcel, copyNoHead);
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
		decorator.setTableAfter("from " + getObjectName(source));
		String formattedContent = FormatUtils.format(decorator, () -> rowList);
		soqlArea.setText(formattedContent);

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
