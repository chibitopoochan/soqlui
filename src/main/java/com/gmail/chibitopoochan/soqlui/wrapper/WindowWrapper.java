package com.gmail.chibitopoochan.soqlui.wrapper;

import javafx.stage.Window;

public class WindowWrapper {
	private Window window;

	public WindowWrapper() {}

	public WindowWrapper(Window window) {
		this.window = window;
	}

	public Window unwrap() {
		return window;
	}

}
