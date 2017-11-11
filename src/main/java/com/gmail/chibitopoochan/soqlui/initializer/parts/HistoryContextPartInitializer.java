package com.gmail.chibitopoochan.soqlui.initializer.parts;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.model.SOQLHistory;

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

	@Override
	public void setController(MainController controller) {
		this.historyList = controller.getHistoryList();
		this.soqlArea = controller.getSoqlArea();
	}

	@Override
	public void initialize() {
		// メニュー作成
		MenuItem copyArea = new MenuItem("Copy SOQL Area");
		MenuItem copyClip = new MenuItem("Copy Clipboard");

		// メニューのイベント設定
		copyArea.setOnAction(this::copyArea);
		copyClip.setOnAction(this::copyClip);

		// メニューの登録
		ContextMenu menu = new ContextMenu();
		menu.getItems().addAll(copyArea, copyClip);

		// イベントの設定
		historyList.setContextMenu(menu);

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
