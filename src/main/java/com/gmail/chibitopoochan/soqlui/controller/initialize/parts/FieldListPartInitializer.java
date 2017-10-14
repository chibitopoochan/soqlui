package com.gmail.chibitopoochan.soqlui.controller.initialize.parts;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.model.DescribeField;
import com.gmail.chibitopoochan.soqlui.parts.custom.DragSelectableTableCell;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class FieldListPartInitializer implements PartsInitializer<MainController> {
	private TableView<DescribeField> fieldList;
	private TextField columnSearch;
	private ObservableList<DescribeField> fieldMasterList;

	@Override
	public void setController(MainController controller) {
		this.fieldList = controller.getFieldList();
		this.columnSearch = controller.getColumnSearch();
		this.fieldList = controller.getFieldList();
		this.fieldMasterList = controller.getFieldMasterList();
	}

	@Override
	public void initialize() {
		// 現在の列とレコードを削除
		fieldList.getColumns().clear();
		fieldList.getItems().clear();

		// 列の追加（追加順で左の列から定義）
		fieldList.getColumns().add(createColumn(DescribeField.FIELD_LIST_COLUMN_NAME, this::getNameCallback));
		fieldList.getColumns().add(createColumn(DescribeField.FIELD_LIST_COLUMN_LABEL, this::getLabelCallback));
		fieldList.getColumns().add(createColumn(DescribeField.FIELD_LIST_COLUMN_TYPE, this::getTypeCallback));
		fieldList.getColumns().add(createColumn(DescribeField.FIELD_LIST_COLUMN_LENGTH, this::getLengthCallback));
		fieldList.getColumns().add(createColumn(DescribeField.FIELD_LIST_COLUMN_PICKLIST, this::getPicklistCallback));
		fieldList.getColumns().add(createColumn(DescribeField.FIELD_LIST_COLUMN_REF, this::getRefCallback));
		fieldList.getSelectionModel().setCellSelectionEnabled(true);
		fieldList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// 項目一覧の絞り込み
		columnSearch.setDisable(true);
		columnSearch.textProperty().addListener((v, o, n) ->
			fieldList.setItems(
				fieldMasterList.filtered(
					t -> t.getName().toLowerCase().indexOf(n) > -1
						|| t.getLabel().toLowerCase().indexOf(n) > -1
						|| n.length() == 0)
			)
		);

	}

	private SimpleStringProperty getNameCallback(TableColumn.CellDataFeatures<DescribeField,String> d) {
		return new SimpleStringProperty(d.getValue().getName());
	}

	private SimpleStringProperty getLabelCallback(TableColumn.CellDataFeatures<DescribeField,String> d) {
		return new SimpleStringProperty(d.getValue().getLabel());
	}

	private SimpleStringProperty getTypeCallback(TableColumn.CellDataFeatures<DescribeField,String> d) {
		return new SimpleStringProperty(d.getValue().getType());
	}

	private SimpleStringProperty getLengthCallback(TableColumn.CellDataFeatures<DescribeField,String> d) {
		return new SimpleStringProperty(String.valueOf(d.getValue().getLength()));
	}

	private SimpleStringProperty getPicklistCallback(TableColumn.CellDataFeatures<DescribeField,String> d) {
		return new SimpleStringProperty(d.getValue().getPicklist());
	}

	private SimpleStringProperty getRefCallback(TableColumn.CellDataFeatures<DescribeField,String> d) {
		return new SimpleStringProperty(d.getValue().getReference());
	}

	private TableColumn<DescribeField, String> createColumn(String name, Callback<TableColumn.CellDataFeatures<DescribeField,String>,ObservableValue<String>> callback) {
		TableColumn<DescribeField, String> column = new TableColumn<DescribeField, String>(name);
		column.setCellValueFactory(callback);
		column.setCellFactory(param -> new DragSelectableTableCell<>());
		return column;
	}

}
