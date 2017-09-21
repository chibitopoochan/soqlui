package com.gmail.chibitopoochan.soqlui.config.mock;

import com.gmail.chibitopoochan.soqlui.wrapper.SceneWrapper;
import com.gmail.chibitopoochan.soqlui.wrapper.WindowWrapper;

import javafx.scene.Scene;

public class SceneWrapperMock extends SceneWrapper {

	private WindowWrapper window;
	private Scene scene;

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.wrapper.SceneWrapper#getWindow()
	 */
	@Override
	public WindowWrapper getWindow() {
		return window;
	}

	public void setWindow(WindowWrapper window) {
		this.window = window;
	}

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.wrapper.SceneWrapper#unwrap()
	 */
	@Override
	public Scene unwrap() {
		return scene;
	}

	public void wrap(Scene scene) {
		this.scene = scene;
	}

}
