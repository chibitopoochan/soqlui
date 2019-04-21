package com.gmail.chibitopoochan.soqlui.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.logic.ConnectionLogic;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class SOQLExecuteService extends Service<List<Map<String, String>>> {
	// クラス共通の参照
	private static final Logger logger = LoggerFactory.getLogger(SOQLExecuteService.class);

	public static final int DEFAULT_BATCH_SIZE = 1000;
	public static final int MAX_BATCH_SIZE = 2000;

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
	 * SOQLプロパティ.
	 * 実行するSOQLの設定（独自構文を含む）
	 */
	private StringProperty soql = new SimpleStringProperty(this, "soql");
	public void setSOQL(String query) {
		soql.set(query);
	}

	public String getSOQL() {
		return soql.get();
	}

	public StringProperty soqlProperty() {
		return soql;
	}

	/**
	 * SOQLプロパティ.
	 * 実際に実行するSOQLの設定
	 */
	private StringProperty actualSOQL = new SimpleStringProperty(this, "actualSOQL");
	public void setActualSOQL(String query) {
		actualSOQL.set(query);
	}

	public String getActualSOQL() {
		return actualSOQL.get();
	}

	public StringProperty actualSOQL() {
		return actualSOQL;
	}

	/**
	 * ALLオプションのプロパティ.
	 * 削除済みレコードも取得するかの設定
	 */
	private BooleanProperty all = new SimpleBooleanProperty(this, "all");
	public void setAll(boolean getAll) {
		all.set(getAll);
	}

	public boolean isAll() {
		return all.get();
	}

	public BooleanProperty allProperty() {
		return all;
	}

	/**
	 * バッチサイズのプロパティ.
	 * 一度の実行で取得できるサイズ
	 */
	private StringProperty batchSize = new SimpleStringProperty(this, "batchSize");
	public void setBatchSize(String size) {
		batchSize.set(size);
	}

	public String getBatchSize() {
		return batchSize.get();
	}

	public StringProperty batchSizeProperty() {
		return batchSize;
	}

	public int getIntBatchSize() {
		int size = DEFAULT_BATCH_SIZE;

		try {
			size = Integer.parseInt(getBatchSize());
		} catch (Exception e) {
			logger.error("Invalid batch size:" + getBatchSize(), e);
		}

		if(size > MAX_BATCH_SIZE) {
			size = MAX_BATCH_SIZE;
		}

		return size;
	}

	/**
	 * SOQL実行
	 */
	@Override
	protected Task<List<Map<String, String>>> createTask() {
		final ConnectionLogic useLogic = connectionLogic.get();
		final String useSOQL = getActualSOQL();
		final boolean useAll = isAll();
		final int useBatchSize = getIntBatchSize();

		return new Task<List<Map<String, String>>>(){

			@Override
			protected List<Map<String, String>> call() throws Exception {
				updateMessage("SOQL Executing...");

				List<Map<String, String>> list = useLogic.execute(useSOQL, useAll, useBatchSize);
				if(!list.isEmpty()) {
					int toIndex = list.size() > useBatchSize ? useBatchSize : list.size();
					list = list.subList(0, toIndex);
				}

				updateMessage("Executed " + list.size());
				return list;
			}

		};

	}

}
