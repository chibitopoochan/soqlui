package com.gmail.chibitopoochan.soqlui;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;

import com.gmail.chibitopoochan.soqlui.config.mock.ControllerMock;
import com.gmail.chibitopoochan.soqlui.config.mock.FXMLLoaderWrapperMock;
import com.gmail.chibitopoochan.soqlui.config.mock.ParentWrapperMock;
import com.gmail.chibitopoochan.soqlui.config.mock.SceneWrapperMock;
import com.gmail.chibitopoochan.soqlui.config.mock.StageWrapperMock;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;
import com.gmail.chibitopoochan.soqlui.wrapper.StageWrapper;
import com.gmail.chibitopoochan.soqlui.wrapper.WindowWrapper;

import javafx.stage.Modality;

/**
 * {@link com.gmail.chibitopoochan.soqlui.SceneManager}のテストクラス
 */
public class SceneManagerTest {
	private static final ResourceBundle config = ResourceBundle.getBundle(Configuration.RESOURCE);
	private SceneWrapperMock currentScene;
	private ParentWrapperMock currentParent;
	private FXMLLoaderWrapperMock loader;
	private WindowWrapper ownerWindow;
	private SceneWrapperMock ownerScene;
	private StageWrapperMock parentStage;
	private StageWrapperMock newStage;
	private ControllerMock newController;
	private ControllerMock oldController;
	private String title = Configuration.TITLE_SU02;
	private URL location = SceneManager.class.getResource(config.getString(Configuration.VIEW_SU01));
	private SceneManager manager;

	/**
	 * テストデータの準備
	 */
	@Before public void setup() {
		// 現在の画面設定
		currentScene = new SceneWrapperMock();
		currentParent = new ParentWrapperMock();
		currentParent.setScene(currentScene);
		loader = new FXMLLoaderWrapperMock();
		loader.setParent(currentParent);

		// 親画面の設定
		ownerWindow = new WindowWrapper();
		ownerScene = new SceneWrapperMock();
		ownerScene.setWindow(ownerWindow);
		parentStage = new StageWrapperMock();
		parentStage.setScene(ownerScene);

		newStage = new StageWrapperMock();
		StageWrapper.setInstance(newStage);

		// コントローラの設定
		newController = new ControllerMock();
		oldController = new ControllerMock();
		loader.setController(newController);

		// SceneManagerの設定
		manager = SceneManager.getInstance();
		manager.getStageStack().clear();
		manager.getStageStack().push(parentStage);
		manager.setFXMLLoader(loader);
		manager.pushController(oldController);

	}

	/**
	 * シーンの表示
	 * @throws IOException
	 */
	@Test public void testSceneOpen() throws IOException {
		// 実行
		manager.sceneOpen(Configuration.VIEW_SU01, title);

		// 画面構成のロード
		assertThat(loader.getLocation(), is(location));

		// 子画面の設定
		StageWrapperMock currentStage = (StageWrapperMock)manager.getStageStack().peek();
		assertThat(currentStage.getScene(), is(currentScene));
		assertThat(currentStage.getOwner(), is(ownerWindow));
		assertThat(currentStage.getModality(), is(Modality.WINDOW_MODAL));
		assertFalse(currentStage.isResizable());
		assertThat(currentStage.getTitle(), is(config.getString(title)));

		// コントローラの保存
		assertThat(manager.popController(), is(newController));

		// 子画面の表示
		assertTrue(currentStage.isShowing());

	}

	/**
	 * シーンの変更
	 * @throws IOException
	 */
	@Test public void testSceneChange() throws IOException {
		// パラメータの設定
		manager.putParameter("param1", "value1");
		manager.putParameter("param2", "value2");

		// シーンの変更
		manager.sceneInit(Configuration.VIEW_SU01, title);

		// 画面構成のロード
		assertThat(loader.getLocation(),is(location));

		// 画面の設定
		StageWrapperMock stage = (StageWrapperMock) manager.getStageStack().peek();
		assertThat(stage.getScene(),is(currentScene));
		assertThat(stage.getTitle(),is(config.getString(title)));

		// 画面の表示
		assertTrue(stage.isShowing());

		// パラメータの取得
		Map<String, String> param = manager.getParameters();
		assertThat(param.get("param1"),is("value1"));
		assertThat(param.get("param2"), is("value2"));
	}

	/**
	 * シーンの終了
	 */
	@Test public void testSceneClose() {
		// コントローラの設定
		manager.pushController(oldController);
		manager.pushController(newController);

		// シーンの終了
		manager.sceneClose();

		// 子画面のクローズ
		assertTrue(parentStage.isClose());
		assertTrue(oldController.called());
		assertFalse(newController.called());
		assertEquals(manager.popController(), oldController);

	}

}
