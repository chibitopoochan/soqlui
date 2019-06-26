package com.gmail.chibitopoochan.soqlui.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.gmail.chibitopoochan.soqlui.util.format.FormatDecoration;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class CopyUtils {

	/**
	 * フォーマットに沿った内容をクリップボードにコピー
	 * @param decorator フォーマット
	 * @param table 対象のTableView
	 */
	public static void copyToClipboard(FormatDecoration decorator, TableView<?> table) {
		String formattedContent = FormatUtils.format(decorator,() -> buildFormatValueProvider(table, decorator.isShowHeader()));

		// クリップボードへコピー
		ClipboardContent content = new ClipboardContent();
		content.putString(formattedContent);
		Clipboard.getSystemClipboard().setContent(content);

	}

	/**
	 * 選択された項目をもとにフォーマットを構築します
	 * @param table 対象のTableView
	 * @param withHeader ヘッダを含むならtrue
	 * @return 選択項目の二次元配列
	 */
	@SuppressWarnings("unchecked")
	private static List<List<String>> buildFormatValueProvider(TableView<?> table, boolean withHeader) {
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

		Optional<Integer> leftColumnNumber = positionList.stream().map(p -> p.getColumn()).min(Comparator.naturalOrder());
		positionList.forEach(position -> {
			if(position.getColumn() == leftColumnNumber.orElse(0) && !columnList.isEmpty()) {
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
