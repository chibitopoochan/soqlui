package com.gmail.chibitopoochan.soqlui.wrapper;

import java.io.IOException;
import java.net.URL;

import com.gmail.chibitopoochan.soqlui.controller.Controller;

import javafx.fxml.FXMLLoader;

/**
 * {@code FXMLLoader}のWrapperクラス
 * 実API呼び出しを分離して依存を下げます。
 */
public class FXMLLoaderWrapper {
	private FXMLLoader loader;

	/**
	 * FXMLLoaderのインスタンスを作成
	 * @param location FXMLファイルのパス
	 */
	public FXMLLoaderWrapper() {
		recreate();
	}

	/**
	 * FXMLLoaderのインスタンスを再作成
	 */
	public void recreate() {
		loader = new FXMLLoader();
	}

	/**
	 * {@link javafx.fxml.FXMLLoader#setLocation(URL)}をラップ
	 * @param location FXMLのパス
	 */
	public void setLocation(URL location) {
		loader.setLocation(location);
	}

	/**
	 * {@link javafx.fxml.FXMLLoader#load()}をラップ
	 * @param result 読み込んだオブジェクト階層
	 */
	public ParentWrapper load() throws IOException {
		return new ParentWrapper(loader.load());
	}

	/**
	 * {@link javafx.fxml.FXMLLoader#getController()}をラップ
	 * @return ルートのコントローラ
	 */
	public Controller getController() {
		return loader.getController();
	}

}
