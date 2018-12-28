package com.gmail.chibitopoochan.soqlui.initializer.parts;

import com.gmail.chibitopoochan.soqlui.controller.MainController;

import javafx.event.Event;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TabContextPartInitializer implements PartsInitializer<MainController> {
	private TabPane tabArea;
	private TabPane fieldTabArea;

	@Override
	public void setController(MainController controller) {
		this.tabArea = controller.getTabArea();
		this.fieldTabArea = controller.getFieldTabArea();
	}

	@Override
	public void initialize() {
		setMenuItems(tabArea);
		setMenuItems(fieldTabArea);
	}

	private void setMenuItems(TabPane tabPane) {
		// メニュー作成
		MenuItem close = new MenuItem("Close");
		MenuItem closeOther = new MenuItem("Close Other");
		MenuItem closeAll = new MenuItem("Close All");

		// メニューのイベント設定
		close.setOnAction(e -> close(tabPane));
		closeOther.setOnAction(e -> closeOther(tabPane));
		closeAll.setOnAction(e -> closeAll(tabPane));

		// メニューの登録
		ContextMenu menu = new ContextMenu();
		menu.getItems().addAll(close, closeOther, closeAll);
		menu.getItems().forEach(m -> m.setDisable(true));

		// イベントの設定
		tabPane.setContextMenu(menu);

	}

	private void close(TabPane tabPane) {
		Tab source = tabPane.getSelectionModel().getSelectedItem();
		source.getOnClosed().handle(new Event(Tab.CLOSED_EVENT));
		source.getTabPane().getTabs().remove(source);
		source.getOnClosed().handle(new Event(Tab.CLOSED_EVENT));
	}

	private void closeOther(TabPane tabPane) {
		Tab source = tabPane.getSelectionModel().getSelectedItem();
		source.getTabPane().getTabs().removeIf(t -> t != source);
		source.getTabPane().getSelectionModel().select(source);
	}

	private void closeAll(TabPane tabPane) {
		Tab source = tabPane.getSelectionModel().getSelectedItem();
		source.getTabPane().getTabs().clear();
		source.getOnClosed().handle(new Event(Tab.CLOSED_EVENT));
	}

}
