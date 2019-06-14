package com.gmail.chibitopoochan.soqlui;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;

import com.gmail.chibitopoochan.soqlui.config.ApplicationSettingSet;
import com.gmail.chibitopoochan.soqlui.controller.Controller;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;
import com.gmail.chibitopoochan.soqlui.wrapper.FXMLLoaderWrapper;
import com.gmail.chibitopoochan.soqlui.wrapper.StageWrapper;

import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

/**
 * 画面の表示、切り替え、終了を行うクラス.
 * 画面間のパラメータの橋渡しも行う。
 */
public class SceneManager {
	// クラス共通の変数
	private static final Logger logger = LogUtils.getLogger(SceneManager.class);
	private static final ResourceBundle config = ResourceBundle.getBundle(Configuration.RESOURCE);
	private static final boolean USE_DECORATOR = ApplicationSettingSet.getInstance().getSetting().isUseDecorator();
	private static final boolean USE_CSS = ApplicationSettingSet.getInstance().getSetting().isUseCSS();

	// インスタンス（Singleton）
	private static SceneManager instance;

	// 画面間のパラメータ
	private Map<String, String> parameter = new HashMap<>();

	// Stageの階層
	private Deque<StageWrapper> stageStack = new ArrayDeque<>();

	private Deque<Controller> controllerStack = new ArrayDeque<>();

	// FXMLのローダー
	private FXMLLoaderWrapper loader = new FXMLLoaderWrapper();
	private double savedLocationX;
	private double savedLocationY;

	/**
	 * 非公開のコンストラクタ
	 */
	protected SceneManager() {}

	/**
	 * インスタンスの取得
	 * @return このクラスのインスタンス
	 */
	public static SceneManager getInstance() {
		if(instance == null) {
			instance = new SceneManager();
		}
		return instance;
	}

	/**
	 * インスタンスの指定
	 * @param instance このクラスのインスタンス
	 */
	public static void setInstance(SceneManager instance) {
		SceneManager.instance = instance;
	}

	/**
	 * FXMLLoaderWrapperの指定
	 * @param newLoader 新しいFXMLLoaderWrapperの指定
	 */
	public void setFXMLLoader(FXMLLoaderWrapper newLoader) {
		this.loader = newLoader;
	}

	/**
	 * ルートのステージを設定.
	 * 記録しているステージ階層は破棄されます。
	 * @param primary ルートのステージ
	 */
	public void setPrimaryStage(StageWrapper primary) {
		stageStack.clear();
		stageStack.push(primary);
	}

	/**
	 * パラメータの追加
	 * @param key キー。キーは使用側で定義
	 * @param value 値
	 */
	public void putParameter(String key, String value) {
		parameter.put(key, value);
	}

	/**
	 * パラメータの取得.
	 * 取得後、パラメータはクリアする。
	 * @return パラメータ
	 */
	public Map<String, String> getParameters() {
		Map<String, String> copyParameter = new HashMap<>(parameter);
		parameter.clear();

		return copyParameter;
	}

	/**
	 * 現在のコントローラを設定
	 * @param current 現在のコントローラ
	 */
	public void pushController(Controller current) {
		controllerStack.push(current);
	}

	/**
	 * 現在のコントローラを取得
	 * @return 現在のコントローラ
	 */
	public Controller popController() {
		return controllerStack.pop();
	}

	/**
	 * ステージの階層を取得.
	 * @return ステージの階層
	 */
	public Deque<StageWrapper> getStageStack() {
		return stageStack;
	}

	/**
	 * 画面の表示.
	 * @param resource FXMLを定義したリソース名
	 * @param title 画面タイトルのリソース名
	 * @throws IOException リソース取得時のエラー
	 */
	public void sceneOpen(String resource, String title, boolean wait) throws IOException {
		// 画面構成のロード
		logger.debug(String.format("FXML loading... [%s]", resource));
		loader.recreate();
		loader.setLocation(getClass().getResource(config.getString(resource)));

		// 子画面の設定
		StageWrapper parentStage = stageStack.peek();
		StageWrapper currentStage = StageWrapper.newInstance(StageStyle.DECORATED);
		if(USE_DECORATOR) {
			currentStage = StageWrapper.newInstance(StageStyle.DECORATED);
		} else {
			currentStage = StageWrapper.newInstance(StageStyle.UNDECORATED);
		}
		currentStage.setScene(loader.load().getScene());
		currentStage.initOwner(parentStage.getScene().getWindow());
		currentStage.initModality(Modality.WINDOW_MODAL);
		currentStage.setResizable(false);
		currentStage.setTitle(config.getString(title));
		stageStack.push(currentStage);
		logger.debug(String.format("New stage[%s]",title));

		// 子画面のスタイルを設定
		final ObservableList<String> style = currentStage.getScene().getStylesheets();
		if(style != null) {
			style.clear();
		}
		if(USE_CSS) {
			style.addAll("/view/css/main.css");
		}

		// アイコンの設定
		for(String icon : config.getString(Configuration.ICON).split(";")) {
			currentStage.getIcons().add(new Image(icon));
		}

		// コントローラを設定
		controllerStack.push(loader.getController());

		// 子画面の表示
		logger.debug("Show window");
		if(wait) {
			currentStage.showAndWait();
		} else {
			currentStage.show();
		}

	}

	/**
	 * 画面の初期化
	 * @param resource FXMLを定義したリソース名
	 * @param title 画面タイトルのリソース名
	 * @throws IOException リソース取得時のエラー
	 */
	public void sceneInit(String resource, String title) throws IOException {

		// 画面構成のロード
		logger.debug(String.format("FXML loading... [%s][%s]", resource, config.getString(resource)));
		loader.recreate();
		loader.setLocation(getClass().getResource(config.getString(resource)));

		// 画面の設定
		StageWrapper currentStage;
		if(USE_DECORATOR) {
			currentStage = StageWrapper.newInstance(StageStyle.DECORATED);
		} else {
			currentStage = StageWrapper.newInstance(StageStyle.UNDECORATED);
		}
		currentStage.setScene(loader.load().getScene());
		currentStage.setTitle(config.getString(title));
		logger.debug(String.format("Change to new stage[%s]", title));

		// 子画面のスタイルを設定
		final ObservableList<String> style = currentStage.getScene().getStylesheets();
		if(style != null) {
			style.clear();
		}
		if(USE_CSS) {
			style.addAll("/view/css/main.css");
		}

		// アイコンの設定
		Arrays.stream(config.getString(Configuration.ICON).split(";")).forEach(icon -> {
			currentStage.getIcons().add(new Image(icon));
		});

		// シーン変更の通知先を設定
		setPrimaryStage(currentStage);
		if(!controllerStack.isEmpty()) {
			controllerStack.pop();
		}
		controllerStack.push(loader.getController());

		// 画面の表示
		logger.debug("Show window");
		currentStage.show();

	}

	/**
	 * 画面のクローズ.
	 */
	public void sceneClose() {
		// 子画面をクローズ
		StageWrapper currentStage = stageStack.pop();
		logger.debug(String.format("Close window[%s]", currentStage.getTitle()));
		currentStage.close();

		controllerStack.pop();
		if(!controllerStack.isEmpty()) {
			Controller currentController = controllerStack.peek();
			currentController.onCloseChild();
		}

	}

	public void sizeChange() {
		stageStack.peek().setMaximized(!stageStack.peek().isMaximized());
	}

	public void minimized() {
		stageStack.peek().setIconified(true);
	}

	public void saveLocation(double x, double y) {
		this.savedLocationX = x - stageStack.peek().getScene().getWindow().getX();
		this.savedLocationY = y - stageStack.peek().getScene().getWindow().getY();
	}

	public void moveLocation(double x, double y) {
		stageStack.peek().getScene().getWindow().setX(x - savedLocationX);
		stageStack.peek().getScene().getWindow().setY(y - savedLocationY);
	}

}
