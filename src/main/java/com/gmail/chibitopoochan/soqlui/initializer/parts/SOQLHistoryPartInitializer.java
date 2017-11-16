package com.gmail.chibitopoochan.soqlui.initializer.parts;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.logic.SOQLHistoryLogic;
import com.gmail.chibitopoochan.soqlui.model.SOQLHistory;

import javafx.scene.control.ListView;

public class SOQLHistoryPartInitializer implements PartsInitializer<MainController> {
	private ListView<SOQLHistory> historyList;
	private SOQLHistoryLogic logic;

	@Override
	public void setController(MainController controller) {
		this.historyList = controller.getHistoryList();
		this.logic = controller.getHistory();
	}

	@Override
	public void initialize() {
		historyList.getItems().addAll(logic.getHistoryList());
		historyList.setItems(historyList.getItems().sorted((i, j) -> -i.getCreatedDate().compareTo(j.getCreatedDate())));
	}

}
