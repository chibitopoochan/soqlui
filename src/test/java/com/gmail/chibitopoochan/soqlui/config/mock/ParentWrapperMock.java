package com.gmail.chibitopoochan.soqlui.config.mock;

import com.gmail.chibitopoochan.soqlui.wrapper.ParentWrapper;
import com.gmail.chibitopoochan.soqlui.wrapper.SceneWrapper;

public class ParentWrapperMock extends ParentWrapper {

	private SceneWrapper scene;

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.wrapper.ParentWrapper#getScene()
	 */
	@Override
	public SceneWrapper getScene() {
		return scene;
	}

	public void setScene(SceneWrapper scene) {
		this.scene = scene;
	}

}
