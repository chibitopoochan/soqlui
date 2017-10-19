package com.gmail.chibitopoochan.soqlui.controller.initialize.parts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.model.DescribeField;
import com.gmail.chibitopoochan.soqlui.model.DescribeSObject;
import com.gmail.chibitopoochan.soqlui.model.SObjectRecord;
import com.gmail.chibitopoochan.soqlui.util.FormatUtils;
import com.gmail.chibitopoochan.soqlui.util.format.CSVFormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.ExcelFormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.FormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.SimpleFormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.WithoutHeaderExcelFormatDecoration;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
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

	@Override
	public void setController(MainController controller) {
		this.resultTable = controller.getResultTable();
		this.sObjectList = controller.getsObjectList();
		this.fieldList = controller.getFieldList();
	}

	@Override
	public void initialize() {
		MenuItem copyNormal		= new MenuItem("Copy select area");
		MenuItem copyWithExcel	= new MenuItem("Copy select area(Excel)");
		MenuItem copyWithCSV	= new MenuItem("Copy select area(CSV)");
		MenuItem copyNoHead		= new MenuItem("Copy select area(Excel No Head)");

		// メニューのイベント設定
		copyNormal.setOnAction(e -> copy(new SimpleFormatDecoration(), e.getSource()));
		copyWithExcel.setOnAction(e -> copy(new ExcelFormatDecoration(), e.getSource()));
		copyWithCSV.setOnAction(e -> copy(new CSVFormatDecoration(), e.getSource()));
		copyNoHead.setOnAction(e -> copy(new WithoutHeaderExcelFormatDecoration(), e.getSource()));

		// コンテキストメニューの登録
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.getItems().addAll(copyNormal, copyWithExcel, copyWithCSV, copyNoHead);
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
		if(e.getButton() == MouseButton.SECONDARY) {
			Object souce = e.getSource();
			if(souce instanceof TableView) {
				TableView<?> table  = (TableView<?>) souce;
				table.getContextMenu().setUserData(table);
				table.getContextMenu().show(table, 0, 0);
			}
		}
	}

	private void keyTyped(KeyEvent e) {
		KeyCodeCombination copyKey = new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_ANY);
		if(copyKey.match(e)) {
			copy(new SimpleFormatDecoration(), e.getSource());
		}
	}

	private void copy(FormatDecoration decorator, Object source) {
		TableView<?> table;

		if(source instanceof MenuItem) {
			MenuItem item = (MenuItem) source;
			table = (TableView<?>) item.getParentPopup().getUserData();
		} else if(source instanceof TableView) {
			table = (TableView<?>) source;
		} else {
			throw new IllegalArgumentException("cannot cast to TableView/MenuItem from " + source);
		}

		String formattedContent = FormatUtils.format(decorator,
				() -> buildFormatValueProvider(table, decorator.isShowHeader()));

		// クリップボードへコピー
		ClipboardContent content = new ClipboardContent();
		content.putString(formattedContent);
		Clipboard.getSystemClipboard().setContent(content);

	}

	private List<List<String>> buildFormatValueProvider(TableView<?> table, boolean withHeader) {
		// 選択範囲を取得
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
