package com.gmail.chibitopoochan.soqlui.controller.initialize.parts;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.model.SObjectRecord;

import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;

public class ResultTablePartInitializer implements PartsInitializer<MainController>{
	private TableView<SObjectRecord> resultTable;

	@Override
	public void setController(MainController controller) {
		this.resultTable = controller.getResultTable();

	}

	@Override
	public void initialize() {
		resultTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		resultTable.getSelectionModel().setCellSelectionEnabled(true);

	}

}
