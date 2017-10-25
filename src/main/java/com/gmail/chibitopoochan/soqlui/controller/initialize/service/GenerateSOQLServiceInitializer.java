package com.gmail.chibitopoochan.soqlui.controller.initialize.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gmail.chibitopoochan.soqlui.controller.MainController;
import com.gmail.chibitopoochan.soqlui.controller.service.ConnectService;
import com.gmail.chibitopoochan.soqlui.controller.service.FieldProvideService;
import com.gmail.chibitopoochan.soqlui.model.DescribeField;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.FormatUtils;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;
import com.gmail.chibitopoochan.soqlui.util.format.FormatDecoration;

import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

public class GenerateSOQLServiceInitializer implements ServiceInitializer<MainController> {
	private ConnectService connectService;
	private FieldProvideService fieldService;
	private TextArea soqlArea;
	private FormatDecoration decoration;

	public void setFormatDecoration(FormatDecoration decoration) {
		this.decoration = decoration;
	}

	@Override
	public void setController(MainController controller) {
		this.connectService = controller.getConnectService();
		this.fieldService = controller.getFieldService();
		this.soqlArea = controller.getSoqlArea();
	}

	@Override
	public void initialize() {
		fieldService.connectionLogicProperty().bind(connectService.connectionLogicProperty());
		fieldService.setOnSucceeded(this::succeeded);
		fieldService.setOnFailed(this::failed);
	}

	@Override
	public void succeeded(WorkerStateEvent e) {
		Platform.runLater(() -> {
			decoration.setTableAfter("from " + fieldService.getSObject());

			List<DescribeField> fieldList = fieldService.getDescribeFieldList();
			String soql = FormatUtils.format(decoration, () ->
				fieldList.stream().map(f -> {
					List<String> list = new ArrayList<>();
					list.add(f.getName());
					return list;
				}).collect(Collectors.toList())
			);

			soqlArea.setText(soql);

		});
		fieldService.reset();
	}

	@Override
	public void failed(WorkerStateEvent e) {
		Platform.runLater(() -> {
			// 例外を通知
			Throwable exception = e.getSource().getException();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(MessageHelper.getMessage(Message.Error.ERR_001, exception.toString()));
			alert.showAndWait();
			fieldService.reset();
		});

	}

}
