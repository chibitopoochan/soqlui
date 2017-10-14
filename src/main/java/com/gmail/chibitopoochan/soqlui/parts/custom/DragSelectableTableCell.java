package com.gmail.chibitopoochan.soqlui.parts.custom;

import com.sun.javafx.scene.control.behavior.TableCellBehavior;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;

@SuppressWarnings("restriction")
public class DragSelectableTableCell<S> extends TableCell<S, String> {
	public DragSelectableTableCell() {
		setOnDragDetected(e -> startFullDrag());
		setOnMouseDragEntered(e -> {
			TableView<S> table = getTableView();
			@SuppressWarnings("unchecked")
			TablePosition<S, ?> position = TableCellBehavior.getAnchor(table, table.getFocusModel().getFocusedCell());

			int row = getIndex();
			int column = table.getVisibleLeafIndex(getTableColumn());

			int minRow = Math.min(position.getRow(), row);
			int maxRow = Math.max(position.getRow(), row);

			int minColumn = Math.min(position.getColumn(), column);
			int maxColumn = Math.max(position.getColumn(), column);

			TableColumn<S, ?> minTableColumn = table.getVisibleLeafColumn(minColumn);
			TableColumn<S, ?> maxTableColumn = table.getVisibleLeafColumn(maxColumn);

			table.getSelectionModel().clearSelection();
			table.getSelectionModel().selectRange(minRow, minTableColumn, maxRow, maxTableColumn);
			table.getFocusModel().focus(row, getTableColumn());

		});

	}

	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if(empty) {
			setText(null);
		} else {
			setText(item);
		}
	}

}
