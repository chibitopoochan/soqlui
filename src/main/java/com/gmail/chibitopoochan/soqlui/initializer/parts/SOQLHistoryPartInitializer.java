package com.gmail.chibitopoochan.soqlui.initializer.parts;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.logic.SOQLHistoryLogic;
import com.gmail.chibitopoochan.soqlui.model.SOQLHistory;

import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class SOQLHistoryPartInitializer implements PartsInitializer<MainController> {
	private ListView<SOQLHistory> historyList;
	private SOQLHistoryLogic logic;
	private TextArea soqlArea;
	private TextField historySearch;

	@Override
	public void setController(MainController controller) {
		this.historyList = controller.getHistoryList();
		this.logic = controller.getHistory();
		this.historySearch = controller.getHistorySearch();
		this.soqlArea = controller.getSoqlArea();
	}

	@Override
	public void initialize() {
		historyList.getItems().addAll(logic.getHistoryList());
		historyList.setItems(historyList.getItems().sorted((i, j) -> -i.getCreatedDate().compareTo(j.getCreatedDate())));
		historyList.setCellFactory(param ->{
			final ListCell<SOQLHistory> listCells = new ListCell<SOQLHistory>(){
				@Override
				public void updateItem(SOQLHistory item, boolean empty) {
					super.updateItem(item, empty);

					if(item == null || empty) {
						setText("");
						setTooltip(null);
					} else {
						setText(item.toString());
						setTooltip(new Tooltip(item.getQuery()));
					}

				}
			};
			return listCells;
		});

		// 履歴一覧の絞り込み
		historySearch.textProperty().addListener(
			(v, o, n) -> historyList.setItems(
				FXCollections.observableArrayList(logic.getHistoryList()).
				sorted((i, j) -> -i.getCreatedDate().compareTo(j.getCreatedDate())).
				filtered(
					t -> t.getQuery().toLowerCase().indexOf(n.toLowerCase()) > -1
					|| n.length() == 0)
			)
		);

		// 履歴のクリックイベント
		historyList.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
				SOQLHistory history = historyList.getSelectionModel().getSelectedItem();
				if(history != null) {
					soqlArea.setText(history.getQuery());
				}
			}
		});

	}

}
