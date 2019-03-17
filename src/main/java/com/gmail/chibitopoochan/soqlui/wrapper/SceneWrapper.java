package com.gmail.chibitopoochan.soqlui.wrapper;

import javafx.collections.ObservableList;
import javafx.scene.Scene;

public class SceneWrapper {
	private Scene scene;

	public SceneWrapper(){}

	public SceneWrapper(Scene scene) {
		this.scene = scene;
	}

	public WindowWrapper getWindow() {
		return new WindowWrapper(scene.getWindow());
	}

	public Scene unwrap() {
		return scene;
	}

	public ObservableList<String> getStylesheets() {
		return scene.getStylesheets();

	}
}
