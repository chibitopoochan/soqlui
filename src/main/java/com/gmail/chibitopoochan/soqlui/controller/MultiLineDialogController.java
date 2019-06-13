package com.gmail.chibitopoochan.soqlui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.gmail.chibitopoochan.soqlui.SceneManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class MultiLineDialogController implements Initializable, Controller {
	public static final String RESULT = "result";
	public static final String MESSAGE = "msg";

	@FXML private TextArea multiText;
	@FXML private Label message;

	private SceneManager manager;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		manager = SceneManager.getInstance();
		message.setText(manager.getParameters().get(MESSAGE));

	}

	public void save() {
		manager.putParameter(RESULT, multiText.getText());

		// 画面を遷移
		manager.sceneClose();

	}

	public void cancel() {
		// 画面を遷移
		manager.sceneClose();

	}

}
