package com.gmail.chibitopoochan.soqlui.controller.initialize.parts;

import java.util.stream.Collectors;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.model.DescribeField;
import com.gmail.chibitopoochan.soqlui.model.DescribeSObject;
import com.gmail.chibitopoochan.soqlui.model.SObjectRecord;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.scene.Node;
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
		copyNormal.setOnAction(this::copyNormal);
		copyWithExcel.setOnAction(this::copyWithExcel);
		copyWithCSV.setOnAction(this::copyWithCSV);
		copyNoHead.setOnAction(this::copyNoHead);

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
			copyNormal(new ActionEvent(e.getSource(), e.getTarget()));
		}
	}

	public void copyNoHead(ActionEvent e) {
		FormatDecorator decorator = new FormatDecorator();
		decorator.tableBefore = "<table border=\"1\">";
		decorator.headerBefore = "<tr>";
		decorator.headerValueBefore = "<td>";
		decorator.headerValueAfter = "</td>";
		decorator.headerAfter = "</tr>";
		decorator.rowBefore = "<tr>";
		decorator.valueBefore = "<td>";
		decorator.valueAfter = "</td>";
		decorator.rowAfter = "</tr>";
		decorator.tableAfter = "</table>";
		decorator.showHeader = false;

		copy(decorator, e.getSource());

		e.consume();

	}

	public void copyWithCSV(ActionEvent e) {
		FormatDecorator decorator = new FormatDecorator();
		decorator.headerAfter = System.lineSeparator();
		decorator.headerValueBefore = "\"";
		decorator.headerValueAfter = "\"";
		decorator.valueBefore = "\"";
		decorator.valueAfter = "\"";
		decorator.rowAfter = System.lineSeparator();
		decorator.valueBetween = ",";

		copy(decorator, e.getSource());

		e.consume();

	}

	public void copyWithExcel(ActionEvent e) {
		FormatDecorator decorator = new FormatDecorator();
		decorator.tableBefore = "<table border=\"1\">";
		decorator.headerBefore = "<tr bgcolor=\"#5BA5DC\">";
		decorator.headerValueBefore = "<th>";
		decorator.headerValueAfter = "</th>";
		decorator.headerAfter = "</tr>";
		decorator.rowBefore = "<tr>";
		decorator.valueBefore = "<td>";
		decorator.valueAfter = "</td>";
		decorator.rowAfter = "</tr>";
		decorator.tableAfter = "</table>";

		copy(decorator, e.getSource());

		e.consume();

	}

	public void copyNormal(ActionEvent e) {
		FormatDecorator decorator = new FormatDecorator();
		decorator.headerAfter = System.lineSeparator();
		decorator.rowAfter = System.lineSeparator();
		decorator.valueBetween = "\t";
		decorator.madatoryLastRowDecoration = false;

		copy(decorator, e.getSource());
		e.consume();
	}

	public void copy(FormatDecorator decorator, Object source) {
		TableView<?> table;

		if(source instanceof MenuItem) {
			MenuItem item = (MenuItem) source;
			table = (TableView<?>) item.getParentPopup().getUserData();
		} else if(source instanceof TableView) {
			table = (TableView<?>) source;
		} else {
			throw new IllegalArgumentException("cannot cast to TableView/MenuItem from " + source);
		}

		selectedCopyToClipboard(decorator, table);

	}

	private class FormatDecorator {
		private String tableBefore = "";
		private String headerBefore = "";
		private String headerValueBefore = "";
		private String headerValueAfter = "";
		private String headerAfter = "";
		private String rowBefore = "";
		private String valueBefore = "";
		private String valueAfter = "";
		private String valueBetween = "";
		private String rowAfter = "";
		private String tableAfter = "";

		private boolean madatoryLastRowDecoration = true;
		private boolean showHeader = true;

	}

	public void selectedCopyToClipboard(FormatDecorator decorator, TableView<?> table) {
		// 選択範囲を取得
		ObservableList<TablePosition> positionList =
				 (ObservableList<TablePosition>) table.getSelectionModel().getSelectedCells();

		StringBuilder tempText = new StringBuilder();

		final int firstRow = positionList.stream().map(t -> t.getRow()).collect(Collectors.minBy((T1,T2) -> T1-T2)).orElse(0);
		final int firstColumn = positionList.stream().map(t -> t.getColumn()).collect(Collectors.minBy((T1,T2) -> T1-T2)).orElse(0);
		final int lastColumn = positionList.stream().map(t -> t.getColumn()).collect(Collectors.maxBy((T1,T2) -> T1-T2)).orElse(0);

		tempText.append(decorator.tableBefore);

		// ヘッダーを構築
		if(decorator.showHeader) {
			tempText.append(decorator.headerBefore);
			for(int i = firstColumn; i <= lastColumn; i++) {
				if(i != firstColumn) {
					tempText.append(decorator.valueBetween);
				}
				tempText.append(decorator.headerValueBefore);
				tempText.append(table.getVisibleLeafColumn(i).getText());
				tempText.append(decorator.headerValueAfter);
			}
			tempText.append(decorator.headerAfter);
		}

		// 本体を構築
		positionList.forEach(position -> {
			if(position.getColumn()-firstColumn == 0) {
				// 行の終了と開始
				if (position.getRow()-firstRow == 0) {
					tempText.append(decorator.rowBefore);
				} else {
					tempText.append(decorator.rowAfter);
					tempText.append(decorator.rowBefore);
				}
			} else {
				// 項目間
				tempText.append(decorator.valueBetween);
			}

			// 項目
			tempText.append(decorator.valueBefore);
			Object column = table.getColumns().get(position.getColumn());
			if (column instanceof TableColumn) {
				tempText.append(((TableColumn<?,?>)column).getCellData(position.getRow()));
			}
			tempText.append(decorator.valueAfter);

		});
		if(decorator.madatoryLastRowDecoration) {
			tempText.append(decorator.rowAfter);
		}
		tempText.append(decorator.tableAfter);

		// クリップボードへコピー
		ClipboardContent content = new ClipboardContent();
		content.putString(tempText.toString());
		Clipboard.getSystemClipboard().setContent(content);

	}

}
