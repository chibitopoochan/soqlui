package com.gmail.chibitopoochan.soqlui.initializer.parts;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.logic.FavoriteLogic;
import com.gmail.chibitopoochan.soqlui.logic.SOQLHistoryLogic;
import com.gmail.chibitopoochan.soqlui.model.SOQLFavorite;
import com.gmail.chibitopoochan.soqlui.model.SOQLHistory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class HistoryContextPartInitializer implements PartsInitializer<MainController> {
	private ListView<SOQLHistory> historyList;
	private TextArea soqlArea;
	private SOQLHistoryLogic logic;

	private ListView<SOQLFavorite> favoriteList;
	private FavoriteLogic favoriteLogic;

	@Override
	public void setController(MainController controller) {
		this.historyList = controller.getHistoryList();
		this.soqlArea = controller.getSoqlArea();
		this.logic = controller.getHistory();

		this.favoriteList = controller.getFavoriteList();
		this.favoriteLogic = controller.getFavoriteLogic();

	}

	@Override
	public void initialize() {
		// メニュー作成
		MenuItem copyArea = new MenuItem("SOQLに反映");
		MenuItem copyClip = new MenuItem("コピー");
		MenuItem removeHistory = new MenuItem("削除");
		MenuItem addFavorite = new MenuItem("お気に入りに追加");

		// メニューのイベント設定
		copyArea.setOnAction(this::copyArea);
		copyClip.setOnAction(this::copyClip);
		removeHistory.setOnAction(this::removeHistory);
		addFavorite.setOnAction(this::addFavorite);

		// メニューの登録
		ContextMenu menu = new ContextMenu();
		menu.getItems().addAll(copyArea, copyClip, removeHistory, addFavorite);

		// イベントの設定
		historyList.setContextMenu(menu);

	}

	/**
	 * お気に入りに追加
	 * @param e イベント
	 */
	private void addFavorite(ActionEvent e) {
		// お気に入りに値を設定
		SOQLFavorite favorite = buildFavorite(e.getSource());

		// お気に入りの再表示
		ObservableList<SOQLFavorite> observableList = FXCollections.observableArrayList(favoriteLogic.getFavoriteList());
		observableList.add(favorite);
		favoriteList.setItems(observableList);

		// ファイルに書き込み
		favoriteLogic.addFavorite(favorite);

	}

	/**
	 * 履歴からお気に入りに変換
	 * @param source 選択された要素
	 * @return お気に入り情報
	 */
	private SOQLFavorite buildFavorite(Object source) {
		SOQLFavorite favorite = new SOQLFavorite();

		// 選択行を取得
		SOQLHistory history = historyList.getSelectionModel().getSelectedItem();
		favorite.setName(history.getCreatedDate().toString());
		favorite.setQuery(history.getQuery());

		return favorite;
	}

	/**
	 * 履歴の削除
	 * @param e イベント
	 */
	private void removeHistory(ActionEvent e) {
		// 履歴を削除
		SOQLHistory history = historyList.getSelectionModel().getSelectedItem();
		logic.removeHistory(history);

		// ソートして一覧を再設定
		historyList.setItems(
				FXCollections.observableArrayList(logic.getHistoryList())
				.sorted((i, j) -> -i.getCreatedDate().compareTo(j.getCreatedDate())));

	}

	/**
	 * SOQL領域へのコピー
	 * @param e イベント
	 */
	private void copyArea(ActionEvent e) {
		SOQLHistory history = historyList.getSelectionModel().getSelectedItem();
		if(history != null) {
			soqlArea.setText(history.getQuery());
		}
	}

	/**
	 * クリップボードへのコピー
	 * @param e イベント
	 */
	private void copyClip(ActionEvent e) {
		SOQLHistory history = historyList.getSelectionModel().getSelectedItem();

		ClipboardContent content = new ClipboardContent();
		content.putString(history.getQuery());
		Clipboard.getSystemClipboard().setContent(content);

	}

}
