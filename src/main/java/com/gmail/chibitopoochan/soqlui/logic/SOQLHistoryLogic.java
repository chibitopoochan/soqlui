package com.gmail.chibitopoochan.soqlui.logic;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.config.SOQLHistorySet;
import com.gmail.chibitopoochan.soqlui.model.SOQLHistory;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;

/**
 * SOQL履歴のロジック.
 */
public class SOQLHistoryLogic {
	// クラス共通の参照
	private static final Logger logger = LoggerFactory.getLogger(SOQLHistoryLogic.class);
	private static final ResourceBundle config = ResourceBundle.getBundle(Configuration.RESOURCE);
	private static final int HISTORY_SIZE = Integer.parseInt(config.getString(Configuration.HISTORY_SIZE));

	private SOQLHistorySet historySet;
	private List<SOQLHistory> cachedList;

	public SOQLHistoryLogic() {
		try {
			// 接続情報を取得
			loadHistory();
		} catch (Exception e) {
			// 例外を通知
			logger.error("Initialize error", e);
		}
	}

	/**
	 * 履歴ファイルの読み込み
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public void loadHistory() throws IllegalStateException, IOException, XMLStreamException {
		historySet = SOQLHistorySet.getInstance();

		cachedList = historySet.getHistoryList();
	}

	public List<SOQLHistory> getHistoryList() {
		return cachedList;
	}

	public void addHistory(SOQLHistory history) {
		cachedList.add(history);
		while(cachedList.size() > HISTORY_SIZE) {
			cachedList.remove(0);
		}
		storeHistory();

	}

	/**
	 * ファイルへの永続化
	 */
	private void storeHistory() {
		// ファイルに書き出す
		historySet.setHistoryList(cachedList);
		try {
			historySet.storeHistory();
		} catch (IllegalStateException | XMLStreamException | IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void removeHistory(SOQLHistory history) {
		cachedList.remove(history);
		storeHistory();
	}

}
