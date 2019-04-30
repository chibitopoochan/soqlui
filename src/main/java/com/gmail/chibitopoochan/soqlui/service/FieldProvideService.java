package com.gmail.chibitopoochan.soqlui.service;

import java.util.List;

import org.slf4j.Logger;

import com.gmail.chibitopoochan.soqlui.logic.ConnectionLogic;
import com.gmail.chibitopoochan.soqlui.model.DescribeField;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class FieldProvideService extends Service<Void> {
	// クラス共通の参照
	private static final Logger logger = LogUtils.getLogger(FieldProvideService.class);

	/**
	 * 接続ロジックのプロパティ.
	 * Salesforce接続の設定
	 */
	private ObjectProperty<ConnectionLogic> connectionLogic = new SimpleObjectProperty<ConnectionLogic>(this, "connectionLogic");
	public void setConnectionLogic(ConnectionLogic logic) {
		connectionLogic.set(logic);
	}

	public ConnectionLogic getConnectionLogic() {
		return connectionLogic.get();
	}

	public ObjectProperty<ConnectionLogic> connectionLogicProperty() {
		return connectionLogic;
	}

	/**
	 * オブジェクト名プロパティ.
	 * 項目を取得するオブジェクト名
	 */
	private StringProperty sObject = new SimpleStringProperty(this, "sObject");
	public void setSObject(String objectName) {
		sObject.set(objectName);
	}

	public String getSObject() {
		return sObject.get();
	}

	public StringProperty sObjectProperty() {
		return sObject;
	}

	/**
	 * 項目一覧のプロパティ
	 */
	private ListProperty<DescribeField> describeFieldList = new SimpleListProperty<>(this, "describeFieldList");
	public void setDescribeFieldList(List<DescribeField> list) {
		describeFieldList.setValue(FXCollections.observableArrayList(list));
	}

	public List<DescribeField> getDescribeFieldList() {
		return describeFieldList.getValue();
	}

	public ListProperty<DescribeField> describeFieldList() {
		return describeFieldList;
	}

	@Override
	protected Task<Void> createTask() {
		final ConnectionLogic useLogic = connectionLogic.get();
		final String objectName = sObject.get();

		return new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				logger.info(String.format("Get field infromation [%s]", objectName));
				setDescribeFieldList(useLogic.getFieldList(objectName));
				return null;
			}

		};
	}

}
