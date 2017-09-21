package com.gmail.chibitopoochan.soqlui.controller;

/**
 * FXMLの制御
 */
public interface Controller {

	/**
	 * 子画面が閉じた際のコールバック.
	 * Overrideしない場合、何も行わない
	 */
	default void onCloseChild() {}

}
