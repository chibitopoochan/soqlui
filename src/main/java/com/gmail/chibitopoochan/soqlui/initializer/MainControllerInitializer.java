package com.gmail.chibitopoochan.soqlui.initializer;

import java.util.LinkedList;
import java.util.List;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.initializer.parts.ConnectButtonPartsInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.parts.ConnectOptionPartInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.parts.ContextPartInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.parts.FieldListPartInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.parts.HistoryContextPartInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.parts.ResultTablePartInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.parts.SOQLAreaPartInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.parts.SOQLHistoryPartInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.parts.SObjectListPartInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.parts.TabContextPartInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.service.ConnectServiceInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.service.ExecuteServiceInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.service.ExportServiceInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.service.FieldServiceInitializer;

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
		initList.add(new FieldListPartInitializer());
		initList.add(new TabContextPartInitializer());
		initList.add(new SOQLHistoryPartInitializer());
		initList.add(new HistoryContextPartInitializer());

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
