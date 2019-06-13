package com.gmail.chibitopoochan.soqlui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;

import com.gmail.chibitopoochan.soqlui.SceneManager;
import com.gmail.chibitopoochan.soqlui.logic.ProxySettingLogic;
import com.gmail.chibitopoochan.soqlui.model.ProxySetting;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ProxySettingController implements Initializable, Controller {
	// クラス共通の参照
	private static final Logger logger = LogUtils.getLogger(ProxySettingController.class);

	// 画面上のコンポーネント
	@FXML private CheckBox useProxy;
	@FXML private TextField host;
	@FXML private TextField port;
	@FXML private TextField username;
	@FXML private PasswordField password;
	@FXML private Label errorMessage;

	private SceneManager manager;

	private ProxySettingLogic logic;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 画面管理用のインスタンス取得
		this.manager = SceneManager.getInstance();

		// 項目制御
		host.disableProperty().bind(useProxy.selectedProperty().not());
		port.disableProperty().bind(useProxy.selectedProperty().not());
		username.disableProperty().bind(useProxy.selectedProperty().not());
		password.disableProperty().bind(useProxy.selectedProperty().not());

		// Proxy情報を設定
		logic = new ProxySettingLogic();
		ProxySetting setting = logic.getProxySetting();
		useProxy.setSelected(setting.useProxy());
		host.setText(setting.getHost());
		port.setText(setting.getPort());
		username.setText(setting.getUsername());
		password.setText(setting.getPassword());

	}

	/**
	 * 保存ボタン押下時の処理
	 */
	public void save() {
		// 入力値チェック
		errorMessage.setVisible(false);

		if(useProxy.isSelected()) {
			try {
				requiredCheck(host.getText(), "Required host field");
				requiredCheck(port.getText(), "Required port field");
				requiredCheck(username.getText(), "Required username field");
				requiredCheck(password.getText(), "Required password field");
			} catch (Exception e1) {
				errorMessage.setText(e1.getMessage());
				errorMessage.setVisible(true);
				logger.error(e1.getMessage());
				return;
			}
		}

		// 保存
		ProxySetting setting = new ProxySetting();
		setting.setUseProxy(useProxy.isSelected());
		setting.setHost(host.getText());
		setting.setPort(port.getText());
		setting.setUsername(username.getText());
		setting.setPassword(password.getText());
		logic.setProxySetting(setting);
		logic.storeSetting();

		// 画面遷移
		manager.sceneClose();

	}

	/**
	 * 必須項目チェック
	 */
	private void requiredCheck(String value, String errMsg) throws Exception {
		if("".equals(value)) {
			throw new Exception(errMsg);
		}
	}

	/**
	 * キャンセル押下時の処理
	 */
	public void cancel() {
		// 画面を遷移
		manager.sceneClose();
	}

}
