/**
 *
 */
package com.gmail.chibitopoochan.soqlui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import javax.xml.stream.XMLStreamException;

import com.gmail.chibitopoochan.soqlexec.model.FieldMetaInfo;
import com.gmail.chibitopoochan.soqlui.SceneManager;
import com.gmail.chibitopoochan.soqlui.config.ApplicationSettingSet;
import com.gmail.chibitopoochan.soqlui.model.ApplicationSetting;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

/**
 * @author mamet
 *
 */
public class ApplicationSettingController implements Initializable, Controller {
	// 全般
	@FXML private CheckBox useDecorator;
	@FXML private CheckBox useCSS;
	@FXML private CheckBox useEditor;
	@FXML private CheckBox advanceSOQL;
	@FXML private CheckBox debugMode;

	// フォーマット
	@FXML private ListView<String> formatColumns;

	// 接続
	@FXML private TextField connectionURL;
	@FXML private TextField loginURL;
	@FXML private TextField objectURL;
	@FXML private TextField recordURL;
	@FXML private TextField proxyURL;
	@FXML private TextField proxyBackURL;
	@FXML private TextField proxyTargetURL;
	@FXML private TextField local;

	// SOQL
	@FXML private Slider tabCount;
	@FXML private Slider historySize;
	@FXML private Slider recordSize;

	private SceneManager manager;
	private ApplicationSettingSet logic = ApplicationSettingSet.getInstance();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.manager = SceneManager.getInstance();
		ApplicationSetting setting = logic.getSetting();

		// 値を設定
		useDecorator.setSelected(setting.isUseDecorator());
		useCSS.setSelected(setting.isUseCSS());
		useEditor.setSelected(setting.isUseEditor());
		advanceSOQL.setSelected(setting.isAdvanceQuery());
		debugMode.setSelected(setting.isDebugMode());

		Stream.of(FieldMetaInfo.Type.values()).forEach(t -> formatColumns.getItems().add(t.name()));

		connectionURL.setText(setting.getConnectionURL());
		loginURL.setText(setting.getLoginURL());
		objectURL.setText(setting.getObjectURL());
		recordURL.setText(setting.getRecordURL());
		proxyURL.setText(setting.getProxyLoginURL());
		proxyBackURL.setText(setting.getProxyBackURL());
		proxyTargetURL.setText(setting.getProxyTargetURL());
		local.setText(setting.getLocal());

		tabCount.setValue(setting.getTabCount());
		historySize.setValue(setting.getHistorySize());
		recordSize.setValue(setting.getRecordSize());

	}

	public void save() {
		ApplicationSetting setting = new ApplicationSetting();
		setting.setUseDecorator(useDecorator.isSelected());
		setting.setUseCSS(useCSS.isSelected());
		setting.setUseEditor(useEditor.isSelected());
		setting.setAdvanceQuery(advanceSOQL.isSelected());
		setting.setDebugMode(debugMode.isSelected());

		setting.setConnectionURL(connectionURL.getText());
		setting.setLoginURL(loginURL.getText());
		setting.setObjectURL(objectURL.getText());
		setting.setRecordURL(recordURL.getText());
		setting.setProxyLoginURL(proxyURL.getText());
		setting.setProxyBackURL(proxyBackURL.getText());
		setting.setProxyTargetURL(proxyTargetURL.getText());
		setting.setLocal(local.getText());

		setting.setTabCount((int) tabCount.getValue());
		setting.setHistorySize((int) historySize.getValue());
		setting.setRecordSize((int) recordSize.getValue());

		try {
			logic.setSetting(setting);
			logic.storeSetting();
		} catch (IllegalStateException | XMLStreamException | IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_001, e.toString()));
			alert.showAndWait();
			e.printStackTrace();
		}

		// 画面を遷移
		manager.sceneClose();

	}

	public void cancel(){
		// 画面を遷移
		manager.sceneClose();

	}

}
