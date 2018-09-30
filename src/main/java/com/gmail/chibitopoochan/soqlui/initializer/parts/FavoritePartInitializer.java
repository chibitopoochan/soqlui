package com.gmail.chibitopoochan.soqlui.initializer.parts;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.logic.FavoriteLogic;
import com.gmail.chibitopoochan.soqlui.model.SOQLFavorite;

import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

public class FavoritePartInitializer implements PartsInitializer<MainController> {
	private ListView<SOQLFavorite> list;
	private TextField search;

	private FavoriteLogic logic;

	@Override
	public void setController(MainController controller) {
		this.list = controller.getFavoriteList();
		this.search = controller.getFavoriteSearch();
		this.logic = controller.getFavoriteLogic();

	}

	@Override
	public void initialize() {
		// お気に入りの設定
		list.getItems().addAll(logic.getFavoriteList());
		list.setCellFactory(param ->{
			final ListCell<SOQLFavorite> listCells = new ListCell<SOQLFavorite>(){
				@Override
				public void updateItem(SOQLFavorite item, boolean empty) {
					super.updateItem(item, empty);

					if(!empty) {
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
						t -> t.getName().toLowerCase().indexOf(n) > -1
						|| n.length() == 0)
			)
		);

	}

}
