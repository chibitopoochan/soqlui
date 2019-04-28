package com.gmail.chibitopoochan.soqlui.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.gmail.chibitopoochan.soqlui.model.ConnectionSetting;

public class ConnectionSettingSetTest {
	// 一時フォルダを作成
	@Rule public TemporaryFolder temp = new TemporaryFolder();

	// 例外検証用のルールを用意
	@Rule public ExpectedException thrown = ExpectedException.none();

	// インスタンスの取得
	@Test public void testInstance() throws Exception {
		ConnectionSettingSet settings = ConnectionSettingSet.getInstance(false);
		assertFalse(settings.getSkipFlag());

	}

	// 設定ファイルのパスを取得するケース
	@Test public void testLoadConfigPath() throws Exception {
		ConnectionSettingSet settings = ConnectionSettingSet.getInstance(false);
		assertThat(settings.getSettingFilePath(), is(not("config/connection.xml")));

		settings.loadSettingFilePath();
		assertThat(settings.getSettingFilePath(), is("config/connection.xml"));

	}

	// 設定ファイルの読み込み
	@Test public void testLoadConfigFile() throws Exception {
		ConnectionSettingSet settings = ConnectionSettingSet.getInstance(false);
		settings.setSettingFilePath("src/test/resources/connection.xml");
		settings.loadConfiguration();

		for(ConnectionSetting setting : settings.getConnectionSettingList()) {
			assertThat(setting.getName(), is("name"));
			assertThat(setting.getUsername(), is("username"));
			assertThat(setting.getPassword(), is("password"));
			assertThat(setting.getToken(), is("token"));
			assertThat(setting.getApiVersion(), is("39.0"));
			assertThat(setting.getEnvironment(), is("Test"));
		}


	}

	// 設定ファイルの読み込み失敗
	@Test public void testLoadConfigFileFailed() throws Exception {
		thrown.expect(NoSuchFileException.class);
		ConnectionSettingSet settings = ConnectionSettingSet.getInstance(false);
		settings.setSettingFilePath("src/test/resources/connection.xml2");
		settings.loadConfiguration();

	}

	// 設定ファイルの書き込み
	@Test public void testStoreConfigFile() throws Exception {
		File file = new File(temp.getRoot(),"connection.xml");
		File expect = new File("src/test/resources/connectionResult.xml");

		ConnectionSettingSet settings = ConnectionSettingSet.getInstance(false);
		settings.setSettingFilePath(file.getAbsolutePath());

		List<ConnectionSetting> list = new LinkedList<>();
		ConnectionSetting setting1 = new ConnectionSetting();
		setting1.setName("name1");
		setting1.setUsername("username1");
		setting1.setPassword("password");
		setting1.setEnvironment(ConnectionSetting.ENV_PROD);
		setting1.setApiVersion("39.0");
		setting1.setToken("token");
		list.add(setting1);

		ConnectionSetting setting2 = new ConnectionSetting();
		setting2.setName("name2");
		setting2.setUsername("username");
		setting2.setPassword("password");
		setting2.setToken("token");
		setting2.setEnvironment(ConnectionSetting.ENV_PROD);
		setting2.setApiVersion("39.0");
		list.add(setting2);

		settings.setConnectionSetting(list);
		settings.storeConfiguration();

		assertThat(Files.readAllLines(file.toPath()), is(Files.readAllLines(expect.toPath())));


	}

}
