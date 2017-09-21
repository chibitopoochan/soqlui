package com.gmail.chibitopoochan.soqlui;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.gmail.chibitopoochan.soqlui.config.mock.StageWrapperMock;
import com.gmail.chibitopoochan.soqlui.mock.SceneManagerMock;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;

/**
 * {@link com.gmail.chibitopoochan.soqlui.SOQLUI}のテストクラス
 */
public class SOQLUITest {
	/**
	 * アプリケーション起動
	 * @throws Exception
	 */
	@Test public void testStart() throws Exception {
		// パラメータの用意
		SOQLUI ui = new SOQLUI();
		SceneManagerMock manager = new SceneManagerMock();
		StageWrapperMock primaryStage = new StageWrapperMock();
		SceneManager.setInstance(manager);

		// 処理の実行
		ui.internalStart(primaryStage);

		// 設定値の確認
		assertThat(manager.getPrimary(), is(primaryStage));
		assertThat(manager.getResource(), is(Configuration.VIEW_SU01));
		assertThat(manager.getTitle(), is(Configuration.TITLE_SU01));

	}

}
