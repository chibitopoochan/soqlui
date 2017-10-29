package com.gmail.chibitopoochan.soqlui.wrapper;

import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * {@code Stage}のWrapperクラス
 * 実API呼び出しを分離して依存を下げます。
 */
public class StageWrapper {
	private static Optional<StageWrapper> specificWrapper = Optional.empty();
	private Stage stage;

	/**
	 * Stageのインスタンスを持たずに作成.
	 * テストクラス用
	 */
	protected StageWrapper() {}

	/**
	 * Stageのインスタンスを持ったWrapperを作成.
	 * テストクラス用
	 * @param stage Stageのインスタンス
	 */
	protected StageWrapper(Stage stage) {
		this.stage = stage;
	}

	/**
	 * 生成時に返すインスタンスの指定
	 * @param specificWrapper インスタンス
	 */
	public static void setInstance(StageWrapper specificWrapper) {
		StageWrapper.specificWrapper = Optional.of(specificWrapper);
	}

	/**
	 * インスタンスの生成.
	 * インスタンスの指定が無ければ、都度インスタンスを生成
	 * @param style StageStyleの指定
	 * @return StageWrapperのインスタンス
	 */
	public static StageWrapper newInstance(StageStyle style) {
		return specificWrapper.isPresent()
				? specificWrapper.get()
				: new StageWrapper(new Stage(style));
	}

	/**
	 * インスタンスの生成.
	 * インスタンスの指定が無ければ、都度インスタンスを生成
	 * @param stage ラップするStageの指定
	 * @return StageWrapperのインスタンス
	 */
	public static StageWrapper newInstance(Stage stage) {
		return specificWrapper.isPresent()
				? specificWrapper.get()
				: new StageWrapper(stage);
	}

	/**
	 * {@link javafx.stage.Stage#setScene(Scene)}をラップ
	 * @param scene シーン
	 */
	public void setScene(SceneWrapper scene) {
		stage.setScene(scene.unwrap());
	}

	/**
	 * ラップ対象の取得
	 * @return ラップしているStage
	 */
	public Stage unwrap() {
		return stage;
	}

	/**
	 * {@link javafx.stage.Stage#initOwner(Window)}をラップ
	 * @param window 所有Window
	 */
	public void initOwner(WindowWrapper window) {
		stage.initOwner(window.unwrap());
	}

	/**
	 * {@link javafx.stage.Stage#initModality(Modality)}をラップ
	 * @param windowModal モーダルの設定
	 */
	public void initModality(Modality windowModal) {
		stage.initModality(windowModal);
	}

	/**
	 * {@link javafx.stage.Stage#setResizable(boolean)}をラップ
	 * @param resaizable サイズ変更可否
	 */
	public void setResizable(boolean resizable) {
		stage.setResizable(resizable);
	}

	/**
	 * {@link javafx.stage.Stage#setTitle(String)}をラップ
	 * @param title タイトル
	 */
	public void setTitle(String title) {
		stage.setTitle(title);
	}

	/**
	 * {@link javafx.stage.Stage#show()}をラップ
	 */
	public void show() {
		stage.show();
	}

	/**
	 * {@link javafx.stage.Stage#getIcons()}をラップ
	 * @param Image一覧
	 */
	public ObservableList<Image> getIcons() {
		return stage.getIcons();
	}

	/**
	 * {@link javafx.stage.Stage#getScene()}をラップ
	 * @return シーン
	 */
	public SceneWrapper getScene() {
		return new SceneWrapper(stage.getScene());
	}

	/**
	 * {@link javafx.stage.Stage#close()}をラップ
	 */
	public void close() {
		stage.close();
	}

	/**
	 * {@link javafx.stage.Stage#getTitle()}をラップ
	 * @return タイトル
	 */
	public String getTitle() {
		return stage.getTitle();
	}

}
