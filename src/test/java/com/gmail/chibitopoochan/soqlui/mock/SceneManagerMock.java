package com.gmail.chibitopoochan.soqlui.mock;

import java.io.IOException;

import com.gmail.chibitopoochan.soqlui.SceneManager;
import com.gmail.chibitopoochan.soqlui.wrapper.StageWrapper;

public class SceneManagerMock extends SceneManager {
	private StageWrapper primary;
	private String resource;
	private String title;

	public SceneManagerMock() {}

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.SceneManager#setPrimaryStage(com.gmail.chibitopoochan.soqlui.wrapper.StageWrapper)
	 */
	@Override
	public void setPrimaryStage(StageWrapper primary) {
		this.primary = primary;
	}

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.SceneManager#sceneChange(java.lang.String, java.lang.String)
	 */
	@Override
	public void sceneInit(String resource, String title) throws IOException {
		this.resource = resource;
		this.title = title;
	}

	/**
	 * @return primary
	 */
	public StageWrapper getPrimary() {
		return primary;
	}

	/**
	 * @return resource
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

}
