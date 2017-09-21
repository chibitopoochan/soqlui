package com.gmail.chibitopoochan.soqlui.config.mock;

import com.gmail.chibitopoochan.soqlui.wrapper.SceneWrapper;
import com.gmail.chibitopoochan.soqlui.wrapper.StageWrapper;
import com.gmail.chibitopoochan.soqlui.wrapper.WindowWrapper;

import javafx.stage.Modality;

public class StageWrapperMock extends StageWrapper {
	private SceneWrapper scene;
	private WindowWrapper window;
	private Modality modality;
	private String title;
	private boolean close;
	private boolean show;
	private boolean resizable;

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.wrapper.StageWrapper#setScene(javafx.scene.Scene)
	 */
	@Override
	public void setScene(SceneWrapper scene) {
		this.scene = scene;
	}

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.wrapper.StageWrapper#initOwner(javafx.stage.Window)
	 */
	@Override
	public void initOwner(WindowWrapper window) {
		this.window = window;
	}

	public WindowWrapper getOwner() {
		return window;
	}

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.wrapper.StageWrapper#initModality(javafx.stage.Modality)
	 */
	@Override
	public void initModality(Modality windowModal) {
		this.modality = windowModal;
	}

	public Modality getModality() {
		return modality;
	}

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.wrapper.StageWrapper#setResizable(boolean)
	 */
	@Override
	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	public boolean isResizable() {
		return resizable;
	}

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.wrapper.StageWrapper#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.wrapper.StageWrapper#show()
	 */
	@Override
	public void show() {
		show = true;
	}

	public boolean isShowing() {
		return show;
	}

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.wrapper.StageWrapper#getScene()
	 */
	@Override
	public SceneWrapper getScene() {
		return scene;
	}

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.wrapper.StageWrapper#close()
	 */
	@Override
	public void close() {
		this.close = true;
	}

	public boolean isClose() {
		return close;
	}

}
