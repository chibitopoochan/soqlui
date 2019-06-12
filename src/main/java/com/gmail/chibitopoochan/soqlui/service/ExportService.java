package com.gmail.chibitopoochan.soqlui.service;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.gmail.chibitopoochan.soqlui.logic.ConnectionLogic;
import com.gmail.chibitopoochan.soqlui.logic.ExtractFileLogic;
import com.gmail.chibitopoochan.soqlui.util.FormatUtils;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;
import com.gmail.chibitopoochan.soqlui.util.format.CSVFormatDecoration;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ExportService extends Service<Void> {
	// クラス共通の参照
	private static final Logger logger = LogUtils.getLogger(SOQLExecuteService.class);

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

	private ObjectProperty<ExtractFileLogic> extractLogic = new SimpleObjectProperty<>(this, "extractLogic");
	public void setExtractLogic(ExtractFileLogic logic){
		extractLogic.set(logic);
	}

	public ExtractFileLogic getExtractLogic() {
		return extractLogic.get();
	}

	public ObjectProperty<ExtractFileLogic> extractLogicProperty() {
		return extractLogic;
	}

	/**
	 * SOQLプロパティ.
	 * 実行するSOQLの設定
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
	 * サブクエリの取得方法
	 * trueなら1列に表示
	 */
	private BooleanProperty join = new SimpleBooleanProperty(this, "join");
	public void setJoin(boolean join) {
		this.join.set(join);
	}

	public boolean isJoin() {
		return this.join.get();
	}

	public BooleanProperty join() {
		return join;
	}

	private BooleanProperty base64 = new SimpleBooleanProperty(this, "base64");

	public void setUseBase64(boolean use) {
		base64.set(use);
	}

	public boolean isUseBase64() {
		return base64.get();
	}

	public BooleanProperty useBase64() {
		return base64;
	}

	private Path exportPath;

	public void setExportPath(Path path) {
		this.exportPath = path;
	}

	public Path getExportPath() {
		return exportPath;
	}

	@Override
	protected Task<Void> createTask() {
		final ConnectionLogic useLogic = connectionLogic.get();
		final ExtractFileLogic useExtract = extractLogic.get();
		final String useSOQL = getSOQL();
		final boolean useAll = isAll();
		final int useBatchSize = getIntBatchSize();
		final boolean useJoin = isJoin();
		final boolean useBase64 = isUseBase64();

		return new Task<Void>(){

			@Override
			protected Void call() throws Exception {
				try(BufferedWriter out = Files.newBufferedWriter(exportPath, Charset.forName("UTF-8"))) {
					List<List<String>> rowList = new LinkedList<>();

					updateMessage("SOQL Executing...");
					logger.info("SOQL Executing...");
					List<Map<String, String>> recordList = useLogic.execute(useSOQL, useAll, useBatchSize, useJoin);
					int size = useLogic.getSize();
					int done = 0;

					if(!recordList.isEmpty()) {
						rowList.add(recordList.get(0).keySet().stream().collect(Collectors.toList()));
					}

					logger.info("Extract init");
					if(useBase64) {
						useExtract.init(exportPath, useSOQL);
					} else {
						useExtract.init(exportPath, useSOQL, connectionLogic.get().getServerURL(), connectionLogic.get().getSessionId());
					}

					while(!recordList.isEmpty()) {
						// ファイルを抽出
						if(useExtract.canExtract()) {
							for(Map<String, String> record : recordList) {
								useExtract.extract(record);
							}
						}

						// 値の表を構築
						recordList.forEach(record -> {
							rowList.add(record.values().stream().collect(Collectors.toList()));
						});

						// CSV形式に変換
						String csv = FormatUtils.format(new CSVFormatDecoration(), () -> rowList);
						out.write(csv);

						// 進捗を更新
						done += recordList.size();

						updateProgress(done, size);
						updateMessage(String.format("%d / %d", done, size));

						rowList.clear();

						// 次のレコードを取得
						recordList = useLogic.executeMore();
					}

					out.flush();
					updateMessage("Exported " + size);
					logger.info("Exported" + size);
				} catch (Exception e) {
					updateMessage("Cancelled");
					logger.info("Cancelled");
				}

				return null;
			}

		};
	}

}
