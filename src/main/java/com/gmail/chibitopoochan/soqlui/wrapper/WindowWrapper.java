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

	/**
	 * {@link javafx.stage.Window#getX()}のラッピング
	 * @return x座標
	 */
	public double getX() {
		return window.getX();
	}

	/**
	 * {@link javafx.stage.Window#getY()}のラッピング
	 * @return y座標
	 */
	public double getY() {
		return window.getY();
	}

	/**
	 * {@link javafx.stage.Window#setX(double)}のラッピング
	 * @param value x座標
	 */
	public void setX(double value) {
		this.window.setX(value);
	}

	/**
	 * {@link javafx.stage.Window#setY(double)}のラッピング
	 * @param value y座標
	 */
	public void setY(double value) {
		this.window.setY(value);
	}

}
