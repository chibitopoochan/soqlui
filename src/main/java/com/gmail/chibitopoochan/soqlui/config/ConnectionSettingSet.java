package com.gmail.chibitopoochan.soqlui.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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

import com.gmail.chibitopoochan.soqlui.model.ConnectionSetting;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

/**
 * 接続設定一覧
 */
public class ConnectionSettingSet {
	public static final String CONNECTIONS_ELEMENT = "connections";
	public static final String CONNECTION_ELEMENT = "connection";
	public static final String ENVIRONMENT = "environment";
	public static final String API_VERSION = "api";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String TOKEN = "token";
	public static final String NAME = "name";
	public static final String SELECTED = "selected";

	// クラス共通の参照
	private static final Logger logger = LogUtils.getLogger(ConnectionSettingSet.class);
	private static final ResourceBundle config = ResourceBundle.getBundle(Configuration.RESOURCE);

	// Singletonのインスタンス
	private static ConnectionSettingSet instance;

	// 読み込んだ接続設定
	private List<ConnectionSetting> connectionSettingList = new LinkedList<>();

	// 解析関連の情報
	private Optional<String> filePath = Optional.empty();
	private ConnectionSetting setting;
	private boolean loadSkip;

	/**
	 * ConnectionSettingSetの初期化
	 * @param withInit 初期化有無
	 * @throws IOException 入出力の例外
	 * @throws IllegalStateException 設定ファイル未指定
	 * @throws XMLStreamException XML操作の例外
	 */
	private ConnectionSettingSet(boolean withInit) throws IllegalStateException, IOException, XMLStreamException {
		loadSkip = !withInit;
		loadSettingFilePath();
		loadConfiguration();
		loadSkip = false;
	}

	/**
	 * 読み込み処理
	 * @return
	 */
	public boolean getSkipFlag() {
		return loadSkip;
	}

	/**
	 * ConnectionSettingSetのファクトリメソッド
	 * @param withInit 初期化有無
	 * @return ConnectionSettingSetのインスタンス
	 * @throws IOException 入出力の例外
	 * @throws IllegalStateException 設定ファイル未指定
	 * @throws XMLStreamException XML操作の例外
	 */
	public static ConnectionSettingSet getInstance(boolean withInit) throws IllegalStateException, IOException, XMLStreamException {
		if(instance == null) {
			instance = new ConnectionSettingSet(withInit);
		}

		return instance;
	}

	/**
	 * 内部のインスタンスをクリア.
	 * 初期化処理を改めて実施する場合に実行
	 */
	public static void releaseInstance() {
		instance = null;
	}

	/**
	 * 設定ファイルの接続設定一覧を取得
	 * @return 接続設定一覧
	 */
	public List<ConnectionSetting> getConnectionSettingList() {
		return connectionSettingList;
	}

	/**
	 * 設定ファイルに保存する接続設定一覧の設定
	 * <code>storeConfiguration()</code>が呼ばれるまでは永続化されない
	 * @param connectionSettingList 接続設定一覧
	 */
	public void setConnectionSetting(List<ConnectionSetting> connectionSettingList) {
		this.connectionSettingList = connectionSettingList;
	}

	/**
	 * 構成ファイルから設定ファイルのパスを読み込む
	 */
	public void loadSettingFilePath() {
		if(loadSkip) return;
		filePath = Optional.of(config.getString(Configuration.CONNECT_SETTING_PATH));
		logger.info(MessageHelper.getMessage(Message.Information.MSG_001, filePath.get()));
	}

	/**
	 * 設定ファイルのパスを取得
	 * @return 設定ファイルのパス
	 */
	public String getSettingFilePath() {
		return filePath.orElse("");
	}

	/**
	 * 設定ファイルのパスを設定
	 * @param path 設定ファイルの相対パス
	 */
	public void setSettingFilePath(String path) {
		filePath = Optional.of(path);
	}

	/**
	 * 接続設定の書き込み
	 * @throws XMLStreamException XML操作の例外
	 * @throws IOException 入出力の例外
	 * @throws IllegalStateException 設定ファイル未指定
	 */
	public void storeConfiguration() throws XMLStreamException, IllegalStateException, IOException {
		// XMLファイルの出力
		XMLOutputFactory factory = XMLOutputFactory.newFactory();

		// 出力はtry-with-resoucesを使用
		// ※XMLStreamReaderはAutoClosable未実装
		try(OutputStream os = Files.newOutputStream(Paths.get(filePath.orElseThrow(IllegalStateException::new)))) {
			XMLStreamWriter writer = factory.createXMLStreamWriter(os);

			writer.writeStartElement(CONNECTIONS_ELEMENT);
			for(ConnectionSetting setting : connectionSettingList) {
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
	private void write(XMLStreamWriter writer, ConnectionSetting setting) throws XMLStreamException {
		writer.writeStartElement("", CONNECTION_ELEMENT, "");
		writer.writeAttribute("", "", NAME, setting.getName());

		// ユーザ名
		writer.writeStartElement("", USERNAME, "");
		writer.writeCharacters(setting.getUsername());
		writer.writeEndElement();

		// パスワード
		writer.writeStartElement("", PASSWORD, "");
		writer.writeCharacters(setting.getPassword());
		writer.writeEndElement();

		// トークン
		writer.writeStartElement("", TOKEN, "");
		writer.writeCharacters(setting.getToken());
		writer.writeEndElement();

		// 環境
		writer.writeStartElement("", ENVIRONMENT, "");
		writer.writeCharacters(setting.getEnvironment());
		writer.writeEndElement();

		// API
		writer.writeStartElement("", API_VERSION, "");
		writer.writeCharacters(setting.getApiVersion());
		writer.writeEndElement();

		// 選択済み
		writer.writeStartElement("", SELECTED, "");
		writer.writeCharacters(Boolean.toString(setting.isSelected()));
		writer.writeEndElement();

		writer.writeEndElement();

	}

	/**
	 * 接続設定の読み込み
	 * @throws IOException 入出力の例外
	 * @throws IllegalStateException 設定ファイル未指定
	 * @throws XMLStreamException XML操作の例外
	 */
	public void loadConfiguration() throws IllegalStateException, IOException, XMLStreamException {
		if(loadSkip) return;

		// 接続設定一覧を初期化
		connectionSettingList = new LinkedList<>();

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
	 */
	private void analyzeEvent(XMLStreamReader reader) throws XMLStreamException {
		switch(reader.next()) {
		case XMLStreamConstants.START_ELEMENT:
			switch(reader.getLocalName()) {
			case CONNECTION_ELEMENT:
				setting = new ConnectionSetting();
				setting.setName(reader.getAttributeValue(null, NAME));
				break;

			case USERNAME:
				setting.setUsername(reader.getElementText());
				break;

			case PASSWORD:
				setting.setPassword(reader.getElementText());
				break;

			case TOKEN:
				setting.setToken(reader.getElementText());
				break;

			case ENVIRONMENT:
				setting.setEnvironment(reader.getElementText());
				break;

			case API_VERSION:
				setting.setApiVersion(reader.getElementText());
				break;

			case SELECTED:
				setting.setSelected(Boolean.parseBoolean(reader.getElementText()));
				break;
			}
			break;

		case XMLStreamConstants.END_ELEMENT:
			if(reader.getLocalName().equals(CONNECTION_ELEMENT)) {
				connectionSettingList.add(setting);
			}
			break;

		}
	}

}
