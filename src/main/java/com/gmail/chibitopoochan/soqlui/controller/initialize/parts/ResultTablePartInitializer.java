package com.gmail.chibitopoochan.soqlui.controller.initialize.parts;

import java.util.stream.Collectors;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.model.SObjectRecord;

import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ResultTablePartInitializer implements PartsInitializer<MainController>{
	private TableView<SObjectRecord> resultTable;
	private ObservableList<SObjectRecord> resultMasterTable;
	private TextField resultSearch;

	@Override
	public void setController(MainController controller) {
		this.resultTable = controller.getResultTable();
		this.resultSearch = controller.getResultSearch();
		this.resultMasterTable = controller.getResultMasterList();
	}

	@Override
	public void initialize() {
		resultTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		resultTable.getSelectionModel().setCellSelectionEnabled(true);

		// 項目一覧の絞り込み
		resultSearch.setDisable(true);
		resultSearch.textProperty().addListener((v, o, n) ->
			resultTable.setItems(
				resultMasterTable.filtered(
					t -> t.getRecord().values().stream().collect(Collectors.joining()).toLowerCase().indexOf(n) > -1
						|| n.length() == 0)
			)
		);

	}

}
