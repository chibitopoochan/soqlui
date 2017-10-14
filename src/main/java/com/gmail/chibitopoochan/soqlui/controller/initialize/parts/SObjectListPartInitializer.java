package com.gmail.chibitopoochan.soqlui.controller.initialize.parts;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.controller.service.FieldProvideService;
import com.gmail.chibitopoochan.soqlui.model.DescribeSObject;
import com.gmail.chibitopoochan.soqlui.parts.custom.DragSelectableTableCell;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class SObjectListPartInitializer implements PartsInitializer<MainController>{
	private TableView<DescribeSObject> sObjectList;
	private FieldProvideService service;
	private TextField objectSearch;
	private ObservableList<DescribeSObject> objectMasterList;

	@Override
	public void setController(MainController controller) {
		sObjectList = controller.getsObjectList();
		service = controller.getFieldService();
		objectMasterList = controller.getObjectMasterList();
		objectSearch = controller.getObjectSearch();
	}

	@Override
	public void initialize() {
		sObjectList.getColumns().clear();

		sObjectList.getColumns().add(createColumn("sObject", this::getSObjectCallback, 180));
		sObjectList.getColumns().add(createColumn("Prefix", this::getPrefixCallback, 60));

		sObjectList.getSelectionModel().setCellSelectionEnabled(true);
		sObjectList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		sObjectList.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
				service.setSObject(sObjectList.getSelectionModel().getSelectedItem().getName());
				service.start();
			}
		});

		// オブジェクト一覧の絞り込み
		objectSearch.setDisable(true);
		objectSearch.textProperty().addListener(
			(v, o, n) -> sObjectList.setItems(
				objectMasterList.filtered(
					t -> t.getName().toLowerCase().indexOf(n) > -1
					|| n.length() == 0)
			)
		);


	}

	private TableColumn<DescribeSObject, String> createColumn(
			 String name
			,Callback<TableColumn.CellDataFeatures<DescribeSObject,String>,ObservableValue<String>> callback
			,int width) {

		TableColumn<DescribeSObject, String> column = new TableColumn<DescribeSObject, String>(name);
		column.setCellValueFactory(callback);
		column.setCellFactory(param -> new DragSelectableTableCell<>());
		column.setResizable(true);
		column.setSortable(true);
		column.setMinWidth(width);

		return column;
	}

	private SimpleStringProperty getPrefixCallback(TableColumn.CellDataFeatures<DescribeSObject,String> d) {
		return new SimpleStringProperty(d.getValue().getKeyPrefix());
	}

	private SimpleStringProperty getSObjectCallback(TableColumn.CellDataFeatures<DescribeSObject,String> d) {
		return new SimpleStringProperty(d.getValue().getName());
	}

}
