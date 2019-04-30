package com.gmail.chibitopoochan.soqlui.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;

import com.gmail.chibitopoochan.soqlui.model.SOQLHistory;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

public class SOQLHistorySet {
	// クラス共通の参照
	private static final Logger logger = LogUtils.getLogger(SOQLHistorySet.class);
	private static final ResourceBundle config = ResourceBundle.getBundle(Configuration.RESOURCE);

	// XMLの要素定義
	public static final String HISTORIES_ELEMENT = "histories";
	public static final String HISTORY_ELEMENT = "history";
	public static final String CREATED_DATE = "createdDate";
	public static final String SOQL = "query";

	// Singletonのインスタンス
	private static SOQLHistorySet instance;

	// 解析関連の情報
	private Optional<String> filePath = Optional.empty();
	private List<SOQLHistory> historyList = new LinkedList<>();
	private SOQLHistory history;

	private SOQLHistorySet() {
		loadFilePath();
		try {
			loadHistory();
		} catch (IllegalStateException | IOException | XMLStreamException | ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * SOQLHistoryのファクトリメソッド
	 * @return
	 */
	public static SOQLHistorySet getInstance() {
		if(instance == null) {
			instance = new SOQLHistorySet();
		}

		return instance;

	}

	public void loadFilePath() {
		filePath = Optional.of(config.getString(Configuration.HISTORY_PATH));
		logger.info(MessageHelper.getMessage(Message.Information.MSG_001, filePath.get()));

	}

	/**
	 * 内部のインスタンスをクリア.
	 * 初期化処理を改めて実施する場合に実行
	 */
	public static void releaseInstance() {
		instance = null;
	}

	public List<SOQLHistory> getHistoryList() {
		return historyList;
	}

	public void setHistoryList(List<SOQLHistory> list) {
		this.historyList = list;
	}

	/**
	 * 履歴の書き込み
	 * @throws XMLStreamException XML操作の例外
	 * @throws IOException 入出力の例外
	 * @throws IllegalStateException 設定ファイル未指定
	 */
	public void storeHistory() throws XMLStreamException, IllegalStateException, IOException {
		// XMLファイルの出力
		XMLOutputFactory factory = XMLOutputFactory.newFactory();

		// 出力はtry-with-resoucesを使用
		// ※XMLStreamReaderはAutoClosable未実装
		try(OutputStream os = Files.newOutputStream(Paths.get(filePath.orElseThrow(IllegalStateException::new)))) {
			XMLStreamWriter writer = factory.createXMLStreamWriter(os);

			writer.writeStartElement(HISTORIES_ELEMENT);
			for(SOQLHistory setting : historyList) {
				write(writer, setting);
			}
			writer.writeEndElement();

			writer.flush();
			writer.close();

		}

	}

	/**
	 * XMLへの書き込み
	 * @param writer
	 * @param setting
	 * @throws XMLStreamException
	 */
	private void write(XMLStreamWriter writer, SOQLHistory history) throws XMLStreamException {
		writer.writeStartElement("", HISTORY_ELEMENT, "");

		// SOQL
		writer.writeStartElement("", SOQL, "");
		writer.writeCharacters(history.getQuery());
		writer.writeEndElement();

		// パスワード
		writer.writeStartElement("", CREATED_DATE, "");
		writer.writeCharacters(DateFormat.getDateTimeInstance().format(history.getCreatedDate()));
		writer.writeEndElement();

		writer.writeEndElement();

	}

	/**
	 * 接続設定の読み込み
	 * @throws IOException 入出力の例外
	 * @throws IllegalStateException 設定ファイル未指定
	 * @throws XMLStreamException XML操作の例外
	 * @throws ParseException 日付形式の例外
	 */
	public void loadHistory() throws IllegalStateException, IOException, XMLStreamException, ParseException {
		// 接続設定一覧を初期化
		historyList = new LinkedList<>();

		// XMLファイルの解析
		XMLInputFactory factory = XMLInputFactory.newFactory();

		// 入力はtry-with-resoucesを使用
		// ※XMLStreamReaderはAutoClosable未実装
		try(InputStream is = Files.newInputStream(
				Paths.get(filePath.orElseThrow(IllegalStateException::new)))) {
			XMLStreamReader reader = factory.createXMLStreamReader(is);

			// イベントごとに読み込み
			while(reader.hasNext()) {
				analyzeEvent(reader);
			}

			reader.close();

		}

	}

	/**
	 * XMLのイベント解析
	 * @param reader 読み込み対象のXML
	 * @throws XMLStreamException 解析時の例外
	 * @throws ParseException
	 */
	private void analyzeEvent(XMLStreamReader reader) throws XMLStreamException, ParseException {
		switch(reader.next()) {
		case XMLStreamConstants.START_ELEMENT:
			switch(reader.getLocalName()) {
			case HISTORY_ELEMENT:
				history = new SOQLHistory();
				break;

			case CREATED_DATE:
				history.setCreatedDate(DateFormat.getDateTimeInstance().parse(reader.getElementText()));
				break;

			case SOQL:
				history.setQuery(reader.getElementText());
				break;

			}
			break;

		case XMLStreamConstants.END_ELEMENT:
			if(reader.getLocalName().equals(HISTORY_ELEMENT)) {
				historyList.add(history);
			}
			break;

		}
	}

}
