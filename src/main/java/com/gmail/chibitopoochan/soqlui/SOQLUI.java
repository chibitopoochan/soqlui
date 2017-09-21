package com.gmail.chibitopoochan.soqlui;

import java.io.IOException;

import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;
import com.gmail.chibitopoochan.soqlui.wrapper.StageWrapper;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * SOQLUIの起動クラス
 */
public class SOQLUI extends Application {

	/**
	 * アプリケーション起動時の処理
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		internalStart(StageWrapper.newInstance(primaryStage));
	}

	/**
	 * アプリケーション起動時の処理（内部用）
	 * @param primaryStage メインステージ
	 * @throws IOException FXMLの読み込みエラー
	 */
	public void internalStart(StageWrapper primaryStage) throws IOException {
		SceneManager manager = SceneManager.getInstance();
		manager.setPrimaryStage(primaryStage);
		manager.sceneChange(Configuration.VIEW_SU01, Configuration.TITLE_SU01);
	}

	/**
	 * アプリケーションを起動する
	 * @param args 引数
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
