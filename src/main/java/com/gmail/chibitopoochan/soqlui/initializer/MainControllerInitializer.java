package com.gmail.chibitopoochan.soqlui.initializer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.initializer.parts.ConnectButtonPartsInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.parts.ConnectOptionPartInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.parts.ContextPartInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.parts.FavoriteContextPartInitializer;
import com.gmail.chibitopoochan.soqlui.initializer.parts.FavoritePartInitializer;
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

		initList.addAll(Arrays.asList(
				 mainButtonsInit
				,connectOptionInit
				,new ResultTablePartInitializer()
				,new SObjectListPartInitializer()
				,new ContextPartInitializer()
				,new SOQLAreaPartInitializer()
				,new FieldListPartInitializer()
				,new TabContextPartInitializer()
				,new SOQLHistoryPartInitializer()
				,new HistoryContextPartInitializer()
				,new FavoritePartInitializer()
				,new FavoriteContextPartInitializer()
		));

		// サービスの初期化
		initList.addAll(Arrays.asList(
			 new ConnectServiceInitializer()
			,new ExecuteServiceInitializer()
			,new FieldServiceInitializer()
			,new ExportServiceInitializer()
		));

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
