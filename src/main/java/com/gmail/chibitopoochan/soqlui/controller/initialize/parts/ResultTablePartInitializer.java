package com.gmail.chibitopoochan.soqlui.controller.initialize.parts;

import java.util.stream.Collectors;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.model.ResultSet;
import com.gmail.chibitopoochan.soqlui.model.SObjectRecord;

import javafx.scene.control.SelectionMode;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ResultTablePartInitializer implements PartsInitializer<MainController>{
	private TableView<SObjectRecord> resultTable;
	private TextField resultSearch;
	private TabPane tabArea;

	@Override
	public void setController(MainController controller) {
		this.resultTable = controller.getResultTable();
		this.resultSearch = controller.getResultSearch();
		this.tabArea = controller.getTabArea();
	}

	@Override
	public void initialize() {
		resultTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		resultTable.getSelectionModel().setCellSelectionEnabled(true);

		// 項目一覧の絞り込み
		resultSearch.setDisable(true);
		resultSearch.textProperty().addListener((v, o, n) -> {
			ResultSet resultSet =
					(ResultSet) tabArea.getSelectionModel().getSelectedItem().getUserData();
			resultTable.setItems(
				resultSet.getRecords().filtered(
					t -> t.getRecord().values().stream().collect(Collectors.joining()).toLowerCase().indexOf(n) > -1
						|| n.length() == 0)
			);
		});

	}

}
