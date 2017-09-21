package com.gmail.chibitopoochan.soqlui.wrapper;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class ParentWrapper {
	private Parent parent;

	public ParentWrapper(){}

	public ParentWrapper(Parent parent) {
		this.parent = parent;
	}

	public SceneWrapper getScene() {
		return new SceneWrapper(new Scene(parent));
	}

}
