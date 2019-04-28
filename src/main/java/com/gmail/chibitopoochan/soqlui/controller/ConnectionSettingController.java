package com.gmail.chibitopoochan.soqlui.controller;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import com.gmail.chibitopoochan.soqlui.SceneManager;
import com.gmail.chibitopoochan.soqlui.logic.ConnectionSettingLogic;
import com.gmail.chibitopoochan.soqlui.model.ConnectionSetting;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ConnectionSettingController implements Initializable, Controller {
	@FXML
	private Label errorMessage;

	@FXML
	private TextField nameField;

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private TextField tokenField;

	@FXML
	private TextField apiVersionField;

	@FXML
	private ComboBox<String> environmentField;

	private ConnectionSettingLogic settingLogic;

	private SceneManager manager;

	private boolean edit;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.manager = SceneManager.getInstance();

		// パラメータを取得
		Map<String, String> parameter = manager.getParameters();

		// 接続情報を取得
		settingLogic = new ConnectionSettingLogic();

		// 接続設定の指定があれば取得
		if(parameter.containsKey("configuration")) {
			String name = parameter.get("configuration");
			ConnectionSetting setting = settingLogic.getConnectionSetting(name);
			nameField.setText(setting.getName());
			nameField.setDisable(true);
			usernameField.setText(setting.getUsername());
			passwordField.setText(setting.getPassword());
			tokenField.setText(setting.getToken());
			environmentField.getItems().clear();
			environmentField.getItems().addAll(ConnectionSetting.ENV_TEST,ConnectionSetting.ENV_PROD);
			environmentField.getSelectionModel().select(setting.isSandbox() ? 0 : 1);
			apiVersionField.setText(setting.getApiVersion());
			edit = true;
		}

	}

	private void requiredCheck(String value, String errMsg) throws Exception {
		if("".equals(value)) {
			throw new Exception(errMsg);
		}
	}

	public void save() {
		// 必須チェック
		errorMessage.setVisible(false);

		try {
			requiredCheck(nameField.getText(), "Required name field");
			requiredCheck(usernameField.getText(), "Required username field");
			requiredCheck(passwordField.getText(), "Required password field");
			requiredCheck(apiVersionField.getText(), "Required API version field");
		} catch (Exception e1) {
			errorMessage.setText(e1.getMessage());
			errorMessage.setVisible(true);
			return;
		}

		// 存在チェック
		if(!edit) {
			if(settingLogic.isExists(nameField.getText())) {
				errorMessage.setText("Already exists name");
				errorMessage.setVisible(true);
				return;
			}
		}

		// 接続設定を作成
		ConnectionSetting setting = new ConnectionSetting();
		setting.setName(nameField.getText());
		setting.setUsername(usernameField.getText());
		setting.setPassword(passwordField.getText());
		setting.setToken(tokenField.getText());
		setting.setEnvironment(environmentField.getSelectionModel().getSelectedItem());
		setting.setApiVersion(apiVersionField.getText());

		// 接続設定を登録
		settingLogic.upsert(setting);

		// 画面を遷移
		manager.sceneClose();

	}

	public void cancel() {
		// 画面を遷移
		manager.sceneClose();

	}

}
