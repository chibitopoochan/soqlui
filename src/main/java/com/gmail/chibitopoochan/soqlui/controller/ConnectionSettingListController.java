package com.gmail.chibitopoochan.soqlui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.xml.stream.XMLStreamException;

import com.gmail.chibitopoochan.soqlui.SceneManager;
import com.gmail.chibitopoochan.soqlui.logic.ConnectionSettingLogic;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

public class ConnectionSettingListController implements Initializable, Controller {
	private SceneManager manager;

	private ConnectionSettingLogic setting;

	@FXML
	private ListView<String> settingList;

	@FXML
	private Button newButton;

	@FXML
	private Button editButton;

	@FXML
	private Button deleteButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.manager = SceneManager.getInstance();

		try{
			// 接続情報を取得
			setting = new ConnectionSettingLogic();
			setting.loadSettings();

			// 一覧を画面に設定
			refreshList();

		} catch(Exception ex) {
			// 例外を通知
			ex.printStackTrace();
		}

	}

	/**
	 * 接続設定の追加
	 */
	public void newConfiguration() {
		try {
			manager.sceneOpen(Configuration.VIEW_SU03, Configuration.TITLE_SU03);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * 接続設定の編集
	 */
	public void editConfiguration() {
		try {
			manager.putParameter("configuration", settingList.getSelectionModel().selectedItemProperty().get());
			manager.sceneOpen(Configuration.VIEW_SU03, Configuration.TITLE_SU03);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * 接続設定の削除
	 */
	public void deleteConfiguration() {
		// 削除して良いか確認
		Alert confirm = new Alert(AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
		confirm.showAndWait().ifPresent(this::ifYesDelete);
		refreshList();
	}

	/**
	 * 画面を閉じる
	 */
	public void close() {
		manager.sceneClose();
	}

	/**
	 * No以外が選択された場合、設定を削除
	 * @param button 選択されたボタン
	 */
	private void ifYesDelete(ButtonType button) {
		if(button == ButtonType.NO) return;

		// 削除を実行
		setting.delete(settingList.getSelectionModel().selectedItemProperty().get());

		// 一覧を更新
		refreshList();
	}

	/**
	 * 一覧を画面に設定
	 */
	private void refreshList() {
		// 設定の再読み込み
		try {
			setting.loadSettings();
		} catch (IllegalStateException | IOException | XMLStreamException e) {
			e.printStackTrace();
		}

		// 一覧の更新
		settingList.getItems().clear();
		settingList.getItems().addAll(setting.getNameOption());
		settingList.getSelectionModel().selectedItemProperty().addListener(
			(ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
				editButton.setDisable(false);
				deleteButton.setDisable(false);
			}
		);

		// ボタンを制御
		editButton.setDisable(true);
		deleteButton.setDisable(true);
		settingList.getSelectionModel().clearSelection();

	}

	/**
	 * 子画面のクローズ処理.
	 * 設定一覧を再取得
	 */
	@Override
	public void onCloseChild() {
		refreshList();

	}

}
