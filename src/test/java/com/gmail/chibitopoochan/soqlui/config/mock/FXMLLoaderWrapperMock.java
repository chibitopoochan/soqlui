package com.gmail.chibitopoochan.soqlui.config.mock;

import java.io.IOException;
import java.net.URL;

import com.gmail.chibitopoochan.soqlui.controller.Controller;
import com.gmail.chibitopoochan.soqlui.wrapper.FXMLLoaderWrapper;
import com.gmail.chibitopoochan.soqlui.wrapper.ParentWrapper;

public class FXMLLoaderWrapperMock extends FXMLLoaderWrapper {
	private URL location;
	private ParentWrapper parent;
	private Controller controller;

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.wrapper.FXMLLoaderWrapper#setLocation(java.net.URL)
	 */
	@Override
	public void setLocation(URL location) {
		this.location = location;
	}

	public URL getLocation() {
		return location;
	}

	public void setParent(ParentWrapper parent) {
		this.parent = parent;
	}

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.wrapper.FXMLLoaderWrapper#load()
	 */
	@Override
	public ParentWrapper load() throws IOException {
		return parent;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.wrapper.FXMLLoaderWrapper#getController()
	 */
	@Override
	public Controller getController() {
		return controller;
	}

}
