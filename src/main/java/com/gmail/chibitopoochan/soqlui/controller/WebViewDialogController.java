package com.gmail.chibitopoochan.soqlui.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import com.gmail.chibitopoochan.soqlui.SceneManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

public class WebViewDialogController implements Controller, Initializable {
	public static final String URL = "url";
	private SceneManager manager;

	@FXML private WebView view;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		manager = SceneManager.getInstance();
		String resource = manager.getParameters().get(WebViewDialogController.URL);

		// HTML読み込み
		StringBuilder builder = new StringBuilder();
		try(BufferedReader reader = Files.newBufferedReader(Paths.get(resource))) {
			while(reader.ready()) {
				builder.append(reader.readLine()).append(System.lineSeparator());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		view.getEngine().loadContent(builder.toString());

	}

	public void close() {
		manager.sceneClose();
	}

}
