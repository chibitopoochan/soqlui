package com.gmail.chibitopoochan.soqlui.controller.initialize.parts;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.controller.service.ConnectService;
import com.gmail.chibitopoochan.soqlui.controller.service.SOQLExecuteService;

import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;

public class SOQLAreaPartInitializer implements PartsInitializer<MainController> {
	private TextArea soqlArea;
	private SOQLExecuteService executor;
	private ConnectService service;

	@Override
	public void setController(MainController controller) {
		this.executor = controller.getExecutionService();
		this.soqlArea = controller.getSoqlArea();
		this.service = controller.getConnectService();
	}

	@Override
	public void initialize() {
		soqlArea.setOnKeyPressed(this::keyPressed);
	}

	private void keyPressed(KeyEvent e) {
		if(service.isClosing()) {
			return;
		}

		KeyCodeCombination executeKey = new KeyCodeCombination(KeyCode.ENTER, KeyCodeCombination.CONTROL_DOWN);
		if(executeKey.match(e)) {
			executor.start();
		}

	}

}
