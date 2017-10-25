package com.gmail.chibitopoochan.soqlui.controller.initialize;

import java.util.LinkedList;
import java.util.List;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.controller.initialize.parts.ConnectButtonPartsInitializer;
import com.gmail.chibitopoochan.soqlui.controller.initialize.parts.ConnectOptionPartInitializer;
import com.gmail.chibitopoochan.soqlui.controller.initialize.parts.ContextPartInitializer;
import com.gmail.chibitopoochan.soqlui.controller.initialize.parts.ResultTablePartInitializer;
import com.gmail.chibitopoochan.soqlui.controller.initialize.parts.SOQLAreaPartInitializer;
import com.gmail.chibitopoochan.soqlui.controller.initialize.parts.SObjectListPartInitializer;
import com.gmail.chibitopoochan.soqlui.controller.initialize.service.ConnectServiceInitializer;
import com.gmail.chibitopoochan.soqlui.controller.initialize.service.ExecuteServiceInitializer;
import com.gmail.chibitopoochan.soqlui.controller.initialize.service.ExportServiceInitializer;
import com.gmail.chibitopoochan.soqlui.controller.initialize.service.FieldServiceInitializer;

public class MainControllerInitializer implements Initializer<MainController> {
	private MainController controller;
	private Initializer<MainController> mainButtonsInit;
	private Initializer<MainController> connectOptionInit;

	@Override
	public void setController(MainController controller) {
		this.controller = controller;
	}

	@Override
	public void initialize() {
		List<Initializer<MainController>> initList = new LinkedList<>();

		// UI部品の初期化
		mainButtonsInit = new ConnectButtonPartsInitializer();
		connectOptionInit = new ConnectOptionPartInitializer();
		initList.add(mainButtonsInit);
		initList.add(connectOptionInit);
		initList.add(new ResultTablePartInitializer());
		initList.add(new SObjectListPartInitializer());
		initList.add(new ContextPartInitializer());
		initList.add(new SOQLAreaPartInitializer());

		// サービスの初期化
		initList.add(new ConnectServiceInitializer());
		initList.add(new ExecuteServiceInitializer());
		initList.add(new FieldServiceInitializer());
		initList.add(new ExportServiceInitializer());

		initList.forEach(init -> {
			init.setController(controller);
			init.initialize();
		});

	}

	public void reset() {
		mainButtonsInit.initialize();
		connectOptionInit.initialize();
	}

}
