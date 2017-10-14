package com.gmail.chibitopoochan.soqlui.controller.initialize.parts;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.controller.service.SOQLExecuteService;
import com.gmail.chibitopoochan.soqlui.model.SObjectRecord;

import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;

public class ResultTablePartInitializer implements PartsInitializer<MainController>{
	private MainController controller;
	private TableView<SObjectRecord> resultTable;
	private SOQLExecuteService service;

	@Override
	public void setController(MainController controller) {
		this.controller = controller;
		this.resultTable = controller.getResultTable();
		this.service = controller.getExecutionService();

	}

	@Override
	public void initialize() {
		service.soqlProperty().bind(controller.getSoqlArea().textProperty());
		service.connectionLogicProperty().bind(controller.getConnectService().connectionLogicProperty());
		service.batchSizeProperty().bind(controller.getBatchSize().textProperty());
		service.allProperty().bind(controller.getAll().selectedProperty());

		resultTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		resultTable.getSelectionModel().setCellSelectionEnabled(true);

	}

}
