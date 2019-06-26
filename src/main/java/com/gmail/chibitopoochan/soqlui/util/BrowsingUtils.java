package com.gmail.chibitopoochan.soqlui.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;

import com.gmail.chibitopoochan.soqlui.config.ApplicationSettingSet;
import com.gmail.chibitopoochan.soqlui.model.ConnectionSetting;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class BrowsingUtils {
	private static final String URL_ENCODE = "UTF-8";
	public static final String PROXY_URL = ApplicationSettingSet.getInstance().getSetting().getProxyLoginURL();
	public static final String PROXY_BACK_URL = ApplicationSettingSet.getInstance().getSetting().getProxyBackURL();
	public static final String PROXY_TARGET_URL = ApplicationSettingSet.getInstance().getSetting().getProxyTargetURL();
	public static final String LOGIN_URL = ApplicationSettingSet.getInstance().getSetting().getLoginURL();
	public static final String OBJECT_URL = ApplicationSettingSet.getInstance().getSetting().getObjectURL();

	/**
	 * 代理ログインしてブラウザを開く
	 * @param userId ユーザID
	 * @param orgId 組織ID
	 * @param setting 設定
	 */
	public static void browseProxyLogin(String userId, String orgId, ConnectionSetting setting) {
		try {
			String targetURL = URLEncoder.encode(PROXY_TARGET_URL,URL_ENCODE);
			String retURL = URLEncoder.encode(String.format(PROXY_BACK_URL, userId),URL_ENCODE);
			String startURL = String.format(PROXY_URL, orgId, userId, retURL, targetURL);

			// ブラウザで表示
			openBrowser(startURL, setting);

		} catch (Exception e) {
			Alert confirm = new Alert(AlertType.ERROR, e.getMessage());
			confirm.showAndWait();
		}
	}

	/**
	 * レコードをブラウザで表示
	 * @param id レコードのID
	 * @param setting 接続設定
	 */
	public static void browseRecord(String id, ConnectionSetting setting) {
		openBrowser(id, setting);
	}

	/**
	 * オブジェクトをブラウザで表示
	 * @param keyPrefix オブジェクトのKeyPrefix
	 * @param setting 接続設定
	 */
	public static void browseObject(String keyPrefix, ConnectionSetting setting) {
		if(keyPrefix.isEmpty()) {
			throw new IllegalArgumentException("please include key prefix with sObject");
		}
		openBrowser(String.format(OBJECT_URL, keyPrefix), setting);
	}

	/**
	 * ブラウザを開く
	 * @param startURL ログイン後のURL
	 * @param setting 接続設定
	 */
	private static void openBrowser(String startURL, ConnectionSetting setting) {
		String username = setting.getUsername();
		String password = setting.getPassword();
		String env = setting.getEnvironmentOfURL();

		// ブラウザで表示
		Desktop desktop = Desktop.getDesktop();
		try {
			String url = String.format(LOGIN_URL,env, username, password, URLEncoder.encode(startURL,URL_ENCODE));
			desktop.browse(URI.create(url));
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("cannot open a browser");
		}

	}

}
