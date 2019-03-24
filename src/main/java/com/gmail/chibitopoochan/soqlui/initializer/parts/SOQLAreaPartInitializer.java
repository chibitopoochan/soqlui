package com.gmail.chibitopoochan.soqlui.initializer.parts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.service.ConnectService;
import com.gmail.chibitopoochan.soqlui.service.SOQLExecuteService;

import javafx.concurrent.Worker.State;
import javafx.event.Event;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;


public class SOQLAreaPartInitializer implements PartsInitializer<MainController> {
	private TextArea soqlArea;
	private WebView soqlWebArea;
	private SOQLExecuteService executor;
	private ConnectService service;
	private TabPane tabArea;

	@Override
	public void setController(MainController controller) {
		this.executor = controller.getExecutionService();
		this.soqlArea = controller.getSoqlArea();
		this.service = controller.getConnectService();
		this.tabArea = controller.getQueryTabArea();
		this.soqlWebArea = controller.getSoqlWebArea();
	}

	@Override
	public void initialize() {
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
		String jsUrl = this.getClass().getResource("/js/ace/src-noconflict/ace.js").getPath();
		if(jsUrl.startsWith("file:/")) {
			jsUrl = "jar:" + jsUrl;
		} else {
			jsUrl = "file://" + jsUrl;
		}
		String htmlText = builder.toString().replace("{js.url}", jsUrl);

		WebEngine engine = soqlWebArea.getEngine();
		engine.setJavaScriptEnabled(true);
		engine.loadContent(htmlText);
		engine.getLoadWorker().stateProperty().addListener((ov, os, ns) -> {
			if(State.SUCCEEDED.equals(ns)) {
				soqlWebArea.setOnKeyPressed(this::keyPressed);
				soqlWebArea.setOnMouseExited(this::applyToText);
				soqlArea.textProperty().addListener((e,o,n) -> applyToWeb());
				applyToWeb();
				soqlWebArea.setVisible(true);
			}
		});

		for(int i=0; i<5; i++) {
			Tab queryTab = new Tab("Query "+i);
			queryTab.setOnSelectionChanged(this::queryTabChanged);
			tabArea.getTabs().add(queryTab);
		}

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

	private void keyPressed(KeyEvent e) {
		if(service.isClosing()) {
			return;
		}

		KeyCodeCombination executeKey = new KeyCodeCombination(KeyCode.ENTER, KeyCodeCombination.CONTROL_DOWN);
		if(executeKey.match(e)) {
			executor.start();
		}

	}

}
