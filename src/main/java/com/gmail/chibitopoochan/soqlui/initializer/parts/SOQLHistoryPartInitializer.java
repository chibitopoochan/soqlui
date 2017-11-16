package com.gmail.chibitopoochan.soqlui.initializer.parts;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.logic.SOQLHistoryLogic;
import com.gmail.chibitopoochan.soqlui.model.SOQLHistory;

import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class SOQLHistoryPartInitializer implements PartsInitializer<MainController> {
	private ListView<SOQLHistory> historyList;
	private SOQLHistoryLogic logic;
	private TextField historySearch;

	@Override
	public void setController(MainController controller) {
		this.historyList = controller.getHistoryList();
		this.logic = controller.getHistory();
		this.historySearch = controller.getHistorySearch();
	}

	@Override
	public void initialize() {
		historyList.getItems().addAll(logic.getHistoryList());
		historyList.setItems(historyList.getItems().sorted((i, j) -> -i.getCreatedDate().compareTo(j.getCreatedDate())));

		// 履歴一覧の絞り込み
		historySearch.textProperty().addListener(
			(v, o, n) -> historyList.setItems(
				FXCollections.observableArrayList(logic.getHistoryList()).
				filtered(
					t -> t.getQuery().toLowerCase().indexOf(n) > -1
					|| n.length() == 0)
			)
		);

	}

}
