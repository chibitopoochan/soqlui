package com.gmail.chibitopoochan.soqlui.util;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.gmail.chibitopoochan.soqlui.SceneManager;
import com.gmail.chibitopoochan.soqlui.controller.MultiLineDialogController;
import com.gmail.chibitopoochan.soqlui.controller.WebViewDialogController;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message.Information;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class DialogUtils {
	/**
	 * 複数行入力のダイアログ
	 * @param msg メッセージ
	 * @return 入力内容
	 */
	public static Optional<String> showMultiLineDialog(String msg) {
		SceneManager manager = SceneManager.getInstance();
		try {
			manager.putParameter(MultiLineDialogController.MESSAGE, MessageHelper.getMessage(Information.MSG_004, msg));
			manager.sceneOpen(Configuration.VIEW_SU06, Configuration.TITLE_SU06, true);
		} catch (IOException e) {
			e.printStackTrace();
			// TODO ユーザに通知
		}

		return Optional.ofNullable(manager.getParameters().get(MultiLineDialogController.RESULT));
	}

	/**
	 * Web用のダイアログ
	 * @param url HTMLファイルのURLO
	 */
	public static void showWebDialog(String url) {
		SceneManager manager = SceneManager.getInstance();
		try {
			manager.putParameter(WebViewDialogController.URL, url);
			manager.sceneOpen(Configuration.VIEW_SU07, Configuration.TITLE_SU07, true);
		} catch (IOException e) {
			e.printStackTrace();
			// TODO ユーザに通知
		}

	}

	/**
	 * 通知用のダイアログ
	 * @param msg メッセージ
	 */
	public static void showAlertDialog(String msg) {
		Alert resultDialog = new Alert(AlertType.INFORMATION);
		resultDialog.setContentText(msg);
		resultDialog.setTitle("確認");
		resultDialog.showAndWait();
	}

	/**
	 * ファイル保存のダイアログ
	 * @param initName 初期ファイル名
	 * @param extention 拡張子
	 * @return 保存先
	 */
	public static File showSaveDialog(String initName, ExtensionFilter extention) {
		SceneManager manager = SceneManager.getInstance();

		FileChooser chooser = new FileChooser();
		chooser.setTitle("ファイルへの保存");
		chooser.setSelectedExtensionFilter(extention);
		chooser.setInitialFileName(initName);

		return chooser.showSaveDialog(manager.getStageStack().peek().unwrap());

	}

	/**
	 * フォルダ選択のダイアログ
	 * @return フォルダ
	 */
	public static File showDirectoryChooser() {
		SceneManager manager = SceneManager.getInstance();
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("フォルダへの保存");
		return chooser.showDialog(manager.getStageStack().peek().unwrap());
	}
}
