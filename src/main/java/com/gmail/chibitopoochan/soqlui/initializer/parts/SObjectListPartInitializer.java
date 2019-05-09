package com.gmail.chibitopoochan.soqlui.initializer.parts;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.initializer.service.FieldServiceInitializer;
import com.gmail.chibitopoochan.soqlui.model.DescribeSObject;
import com.gmail.chibitopoochan.soqlui.parts.custom.DragSelectableTableCell;
import com.gmail.chibitopoochan.soqlui.service.FieldProvideService;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressIndicator;
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
	private FieldServiceInitializer initService;
	private ProgressIndicator progress;

	@Override
	public void setController(MainController controller) {
		sObjectList = controller.getsObjectList();
		service = controller.getFieldService();
		objectMasterList = controller.getObjectMasterList();
		objectSearch = controller.getObjectSearch();
		progress = controller.getFieldProgressIndicator();

		initService = new FieldServiceInitializer();
		initService.setController(controller);
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
				// 変数をバインド
				progress.progressProperty().unbind();
				progress.visibleProperty().unbind();
				progress.progressProperty().bind(service.progressProperty());
				progress.visibleProperty().bind(service.runningProperty());

				initService.initialize();
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
		column.setPrefWidth(width);

		return column;
	}

	private SimpleStringProperty getPrefixCallback(TableColumn.CellDataFeatures<DescribeSObject,String> d) {
		return new SimpleStringProperty(d.getValue().getKeyPrefix());
	}

	private SimpleStringProperty getSObjectCallback(TableColumn.CellDataFeatures<DescribeSObject,String> d) {
		return new SimpleStringProperty(d.getValue().getName());
	}

}
