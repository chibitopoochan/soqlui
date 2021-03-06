package com.gmail.chibitopoochan.soqlui.initializer.parts;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.logic.FavoriteLogic;
import com.gmail.chibitopoochan.soqlui.model.SOQLFavorite;

import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class FavoritePartInitializer implements PartsInitializer<MainController> {
	private ListView<SOQLFavorite> list;
	private TextField search;
	private TextArea soqlArea;

	private FavoriteLogic logic;

	@Override
	public void setController(MainController controller) {
		this.list = controller.getFavoriteList();
		this.search = controller.getFavoriteSearch();
		this.logic = controller.getFavoriteLogic();
		this.soqlArea = controller.getSoqlArea();

	}

	@Override
	public void initialize() {
		// お気に入りの設定
		list.setItems(FXCollections.observableArrayList(logic.getFavoriteList()));
		list.setItems(list.getItems().sorted());
		list.setCellFactory(param ->{
			final ListCell<SOQLFavorite> listCells = new ListCell<SOQLFavorite>(){
				@Override
				public void updateItem(SOQLFavorite item, boolean empty) {
					super.updateItem(item, empty);

					if(item == null || empty) {
						setText("");
						setTooltip(null);
					} else {
						setText(item.getName());
						setTooltip(new Tooltip(item.getQuery()));

					}

				}
			};
			return listCells;
		});

		// お気に入りの絞り込み
		search.textProperty().addListener(
			(v, o, n) -> list.setItems(
					FXCollections.observableArrayList(logic.getFavoriteList()).
					filtered(
						t -> t.getName().toLowerCase().indexOf(n.toLowerCase()) > -1
						|| n.length() == 0)
			)
		);

		// お気に入りのクリックイベント
		list.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
				SOQLFavorite favorite = list.getSelectionModel().getSelectedItem();
				if(favorite != null) {
					soqlArea.setText(favorite.getQuery());
				}
			}
		});

	}

}
