package com.gmail.chibitopoochan.soqlui.initializer.parts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.gmail.chibitopoochan.soqlui.config.ApplicationSettingSet;
import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.util.ResourceUtils;

import javafx.concurrent.Worker.State;
import javafx.event.Event;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

public class SOQLAreaPartInitializer implements PartsInitializer<MainController> {
	public static final int TAB_COUNT = ApplicationSettingSet.getInstance().getSetting().getTabCount();
	public static final boolean USE_EDITOR = ApplicationSettingSet.getInstance().getSetting().isUseEditor();
	public static final int RECORD_COUNT = ApplicationSettingSet.getInstance().getSetting().getRecordSize();

	private TextArea soqlArea;
	private WebView soqlWebArea;
	private TabPane tabArea;
	private TextField batchSize;

	@Override
	public void setController(MainController controller) {
		this.soqlArea = controller.getSoqlArea();
		this.tabArea = controller.getQueryTabArea();
		this.soqlWebArea = controller.getSoqlWebArea();
		this.batchSize = controller.getBatchSize();
	}

	@Override
	public void initialize() {
		if(USE_EDITOR) {
			loadEditor();
		} else {
			soqlWebArea.setDisable(true);
			soqlWebArea.setVisible(false);
			soqlArea.setDisable(false);
			soqlArea.setVisible(true);
		}

		for(int i=0; i<TAB_COUNT; i++) {
			Tab queryTab = new Tab("クエリ "+i);
			queryTab.setOnSelectionChanged(this::queryTabChanged);
			tabArea.getTabs().add(queryTab);
		}

		batchSize.setText(String.valueOf(RECORD_COUNT));

	}

	private void loadEditor() {
		// HTML読み込み
		StringBuilder builder = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/html/editor.html")))){
			while(reader.ready()) {
				builder.append(reader.readLine()).append(System.lineSeparator());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// JS読み込み
		String jsUrl = ResourceUtils.getURL(this, "/js/ace/src-noconflict/ace.js");
		String htmlText = builder.toString().replace("{js.url}", jsUrl);

		WebEngine engine = soqlWebArea.getEngine();
		engine.setJavaScriptEnabled(true);
		engine.loadContent(htmlText);
		engine.getLoadWorker().stateProperty().addListener((ov, os, ns) -> {
			if(State.SUCCEEDED.equals(ns)) {
				soqlWebArea.setOnMouseExited(this::applyToText);
				soqlArea.textProperty().addListener((e,o,n) -> applyToWeb());
				applyToWeb();
				soqlWebArea.setVisible(true);
			}
		});

	}

	/**
	 * WebViewの値を反映
	 * @param e
	 */
	private void applyToText(Event e) {
		String text = (String) soqlWebArea.getEngine().executeScript("editor.getValue()");
		soqlArea.setText(text);
	}

	private void applyToWeb() {
		String text = soqlArea.getText();

		try {
			text = text == null ? "" : text;
			JSObject obj = (JSObject) soqlWebArea.getEngine().executeScript("editor");
			obj.call("setValue", text);
		} catch (JSException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	private void queryTabChanged(Event e) {
		// 現在のタブの情報を退避
		String soql = soqlArea.getText();
		Tab queryTab = (Tab) e.getTarget();

		if(queryTab.isSelected()) {
			soqlArea.setText((String)queryTab.getUserData());
		} else {
			queryTab.setUserData(soql);
		}

	}

}
