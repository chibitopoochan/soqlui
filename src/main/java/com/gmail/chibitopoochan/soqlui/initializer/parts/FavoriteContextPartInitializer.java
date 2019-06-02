package com.gmail.chibitopoochan.soqlui.initializer.parts;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.logic.FavoriteLogic;
import com.gmail.chibitopoochan.soqlui.model.SOQLFavorite;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;

public class FavoriteContextPartInitializer implements PartsInitializer<MainController> {
	private ListView<SOQLFavorite> list;
	private FavoriteLogic logic;
	private TextArea soqlArea;

	@Override
	public void setController(MainController controller) {
		list = controller.getFavoriteList();
		logic = controller.getFavoriteLogic();
		soqlArea = controller.getSoqlArea();

	}

	@Override
	public void initialize() {
		// メニュー作成
		MenuItem copyArea = new MenuItem("SOQLを反映");
		MenuItem rename = new MenuItem("名称変更");
		MenuItem remove = new MenuItem("削除");

		// イベントの割り当て
		copyArea.setOnAction(this::copySOQLArea);
		rename.setOnAction(this::rename);
		remove.setOnAction(this::remove);

		// メニューの登録
		ContextMenu menu = new ContextMenu(copyArea, rename, remove);
		list.setContextMenu(menu);

	}

	// SOQLエリアへの反映
	private void copySOQLArea(ActionEvent e) {
		SOQLFavorite favorite = list.getSelectionModel().getSelectedItem();
		if(favorite != null) {
			soqlArea.setText(favorite.getQuery());
		}
	}

	// 名前変更
	private void rename(ActionEvent e) {
		SOQLFavorite favorite = list.getSelectionModel().getSelectedItem();
		if(favorite != null) {
			// 新しい名前を取得
			TextInputDialog dialog = new TextInputDialog(favorite.getName());
			dialog.setContentText(MessageHelper.getMessage(Message.Information.MSG_003));
			dialog.showAndWait().ifPresent(name -> {
				// 入力値チェック
				if(name == null || name.length() == 0) {
					Alert errorDialog = new Alert(AlertType.ERROR, "Error");
					errorDialog.setContentText(MessageHelper.getMessage(Message.Error.ERR_004));
					errorDialog.showAndWait();
					return;
				}

				// 新しい名前を設定
				logic.rename(favorite.getName(), name);

				// お気に入りの再表示
				// TODO 手法的にきれいにしたい
				list.setItems(FXCollections.observableArrayList());
				list.setItems(FXCollections.observableArrayList(logic.getFavoriteList()));
				list.setItems(list.getItems().sorted());

			});

		}

	}

	// 削除
	private void remove(ActionEvent e) {
		SOQLFavorite favorite = list.getSelectionModel().getSelectedItem();
		logic.removeFavorite(favorite);
		list.setItems(FXCollections.observableArrayList(logic.getFavoriteList()));
		list.setItems(list.getItems().sorted());
	}

}
