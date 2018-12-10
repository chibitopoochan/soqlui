package com.gmail.chibitopoochan.soqlui.initializer.parts;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.service.ConnectService;
import com.gmail.chibitopoochan.soqlui.service.SOQLExecuteService;

import javafx.event.Event;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;

public class SOQLAreaPartInitializer implements PartsInitializer<MainController> {
	private TextArea soqlArea;
	private SOQLExecuteService executor;
	private ConnectService service;
	private TabPane tabArea;

	@Override
	public void setController(MainController controller) {
		this.executor = controller.getExecutionService();
		this.soqlArea = controller.getSoqlArea();
		this.service = controller.getConnectService();
		this.tabArea = controller.getQueryTabArea();
	}

	@Override
	public void initialize() {
		soqlArea.setOnKeyPressed(this::keyPressed);

		for(int i=0; i<5; i++) {
			Tab queryTab = new Tab("Query "+i);
			queryTab.setOnSelectionChanged(this::queryTabChanged);
			tabArea.getTabs().add(queryTab);
		}

	}

	private void queryTabChanged(Event e) {
		// 現在のタブの情報を退避
		String soql = soqlArea.getText();
		Tab queryTab = (Tab) e.getTarget();

		if(queryTab.isSelected()) {
			soqlArea.setText((String)queryTab.getUserData());
		} else {
			queryTab.setUserData(soql);
		}

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
