package com.gmail.chibitopoochan.soqlui.config.mock;

import com.gmail.chibitopoochan.soqlui.controller.Controller;

public class ControllerMock implements Controller {
	private boolean called = false;

	/* (非 Javadoc)
	 * @see com.gmail.chibitopoochan.soqlui.controller.Controller#onCloseChild()
	 */
	@Override
	public void onCloseChild() {
		called = true;
	}

	public boolean called() {
		return called;
	}

}
