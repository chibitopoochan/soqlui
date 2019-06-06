package com.gmail.chibitopoochan.soqlui.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.model.ApplicationSetting;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

public class ApplicationSettingSet {
	// クラス共通の参照
	private static final Logger logger = LoggerFactory.getLogger(ApplicationSettingSet.class);
	private static final ResourceBundle config = ResourceBundle.getBundle(Configuration.RESOURCE);

	// XMLの要素定義
	private static final String SETTINGS_ELEMENT = "settings";
	private static final String SETTINGS_SOQL = "soql";
	private static final String SETTINGS_TAB_COUNT = "tabCount";
	private static final String SETTINGS_HISTORY_SIZE = "historySize";
	private static final String SETTINGS_RECORD_SIZE = "recordSize";
	private static final String SETTINGS_ADVANCE_QUERY = "advanceQuery";
	private static final String SETTINGS_CONNECTION = "connection";
	private static final String SETTINGS_LOGIN = "login";
	private static final String SETTINGS_BROWSER = "browser";
	private static final String SETTINGS_OBJECT = "object";
	private static final String SETTINGS_RECORD = "record";
	private static final String SETTINGS_USER = "user";
	private static final String SETTINGS_USER_BACK = "userBack";
	private static final String SETTINGS_USER_TARGET = "userTarget";
	private static final String SETTINGS_FORMAT = "format";
	private static final String SETTINGS_GENERAL = "general";
	private static final String SETTINGS_CSS = "css";
	private static final String SETTINGS_EDITOR = "editor";
	private static final String SETTINGS_DECORATION = "decolation";
	private static final String SETTINGS_DEBUG_MODE = "debugMode";
	private static final String SETTINGS_LOCAL = "local";

	// Singletonのインスタンス
	private static ApplicationSettingSet instance;

	// 解析関連の情報
	private Optional<String> filePath = Optional.empty();
	private ApplicationSetting setting;


	/**
	 * ファイルを読み込んだApplicationSetを作成
	 */
	private ApplicationSettingSet() {
		loadFilePath();
		try {
			loadSetting();
		} catch (IllegalStateException | IOException | XMLStreamException | ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ApplicationSettingSetのファクトリメソッド
	 * @return
	 */
	public static ApplicationSettingSet getInstance() {
		if(instance == null) {
			instance = new ApplicationSettingSet();
		}

		return instance;

	}

	/**
	 * ファイルパスの取得
	 */
	public void loadFilePath() {
		filePath = Optional.of(config.getString(Configuration.SETTING_PATH));
		logger.info(MessageHelper.getMessage(Message.Information.MSG_001, filePath.get()));

	}

	/**
	 * 内部のインスタンスをクリア.
	 * 初期化処理を改めて実施する場合に実行
	 */
	public static void releaseInstance() {
		instance = null;
	}

	public ApplicationSetting getSetting() {
		return setting;
	}

	public void setSetting(ApplicationSetting setting) {
		this.setting = setting;
	}

	/**
	 * 履歴の書き込み
	 * @throws XMLStreamException XML操作の例外
	 * @throws IOException 入出力の例外
	 * @throws IllegalStateException 設定ファイル未指定
	 */
	public void storeSetting() throws XMLStreamException, IllegalStateException, IOException {
		// XMLファイルの出力
		XMLOutputFactory factory = XMLOutputFactory.newFactory();

		// 出力はtry-with-resoucesを使用
		// ※XMLStreamReaderはAutoClosable未実装
		try(OutputStream os = Files.newOutputStream(Paths.get(filePath.orElseThrow(IllegalStateException::new)))) {
			XMLStreamWriter writer = factory.createXMLStreamWriter(os);

			writer.writeStartElement(SETTINGS_ELEMENT);
			write(writer, setting);
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
	private void write(XMLStreamWriter writer, ApplicationSetting setting) throws XMLStreamException {
		/** SOQL関連 */
		writer.writeStartElement("", SETTINGS_SOQL, "");

		// タブの数
		writer.writeStartElement("", SETTINGS_TAB_COUNT, "");
		writer.writeCharacters(String.valueOf(setting.getTabCount()));
		writer.writeEndElement();

		// 履歴の数
		writer.writeStartElement("", SETTINGS_HISTORY_SIZE, "");
		writer.writeCharacters(String.valueOf(setting.getHistorySize()));
		writer.writeEndElement();

		// レコードの初期数
		writer.writeStartElement("", SETTINGS_RECORD_SIZE, "");
		writer.writeCharacters(String.valueOf(setting.getRecordSize()));
		writer.writeEndElement();

		// SOQL拡張
		writer.writeStartElement("", SETTINGS_ADVANCE_QUERY, "");
		writer.writeCharacters(String.valueOf(setting.isAdvanceQuery()));
		writer.writeEndElement();

		writer.writeEndElement();

		/** 接続関連 */
		writer.writeStartElement("", SETTINGS_CONNECTION, "");

		// 接続URL
		writer.writeStartElement("", SETTINGS_LOGIN, "");
		writer.writeCharacters(setting.getConnectionURL());
		writer.writeEndElement();

		// ログインURL
		writer.writeStartElement("", SETTINGS_BROWSER, "");
		writer.writeCharacters(setting.getLoginURL());
		writer.writeEndElement();

		// オブジェクト参照URL
		writer.writeStartElement("", SETTINGS_OBJECT, "");
		writer.writeCharacters(setting.getObjectURL());
		writer.writeEndElement();

		// レコード参照URL
		writer.writeStartElement("", SETTINGS_RECORD, "");
		writer.writeCharacters(setting.getRecordURL());
		writer.writeEndElement();

		// 代理ログインURL
		writer.writeStartElement("", SETTINGS_USER, "");
		writer.writeCharacters(setting.getProxyLoginURL());
		writer.writeEndElement();

		// 代理ログイン時URL
		writer.writeStartElement("", SETTINGS_USER_TARGET, "");
		writer.writeCharacters(setting.getProxyTargetURL());
		writer.writeEndElement();

		// 代理ログイン後URL
		writer.writeStartElement("", SETTINGS_USER_BACK, "");
		writer.writeCharacters(setting.getProxyBackURL());
		writer.writeEndElement();

		// ローカライズ
		writer.writeStartElement("", SETTINGS_LOCAL, "");
		writer.writeCharacters(setting.getLocal());
		writer.writeEndElement();

		writer.writeEndElement();

		/** フォーマット関連 */
		writer.writeStartElement("", SETTINGS_FORMAT, "");

		// 項目一覧

		writer.writeEndElement();

		/** 全般 */
		writer.writeStartElement("", SETTINGS_GENERAL, "");

		// CSS
		writer.writeStartElement("", SETTINGS_CSS, "");
		writer.writeCharacters(String.valueOf(setting.isUseCSS()));
		writer.writeEndElement();

		// エディタ
		writer.writeStartElement("", SETTINGS_EDITOR, "");
		writer.writeCharacters(String.valueOf(setting.isUseEditor()));
		writer.writeEndElement();

		// デコレーション
		writer.writeStartElement("", SETTINGS_DECORATION, "");
		writer.writeCharacters(String.valueOf(setting.isUseDecorator()));
		writer.writeEndElement();

		// デバッグ
		writer.writeStartElement("", SETTINGS_DEBUG_MODE, "");
		writer.writeCharacters(String.valueOf(setting.isDebugMode()));
		writer.writeEndElement();

		writer.writeEndElement();

	}

	/**
	 * お気に入りの読み込み
	 * @throws IOException 入出力の例外
	 * @throws IllegalStateException 設定ファイル未指定
	 * @throws XMLStreamException XML操作の例外
	 * @throws ParseException 日付形式の例外
	 */
	public void loadSetting() throws IllegalStateException, IOException, XMLStreamException, ParseException {
		// 接続設定一覧を初期化
		setting = new ApplicationSetting();

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
			case SETTINGS_TAB_COUNT:
				setting.setTabCount(Integer.valueOf(reader.getElementText()));
				break;

			case SETTINGS_HISTORY_SIZE:
				setting.setHistorySize(Integer.valueOf(reader.getElementText()));
				break;

			case SETTINGS_RECORD_SIZE:
				setting.setRecordSize(Integer.valueOf(reader.getElementText()));
				break;

			case SETTINGS_ADVANCE_QUERY:
				setting.setAdvanceQuery(Boolean.valueOf(reader.getElementText()));
				break;

			case SETTINGS_LOGIN:
				setting.setConnectionURL(reader.getElementText());
				break;

			case SETTINGS_DEBUG_MODE:
				setting.setDebugMode(Boolean.valueOf(reader.getElementText()));
				break;

			case SETTINGS_BROWSER:
				setting.setLoginURL(reader.getElementText());
				break;

			case SETTINGS_OBJECT:
				setting.setObjectURL(reader.getElementText());
				break;

			case SETTINGS_RECORD:
				setting.setRecordURL(reader.getElementText());
				break;

			case SETTINGS_USER:
				setting.setProxyLoginURL(reader.getElementText());
				break;

			case SETTINGS_USER_BACK:
				setting.setProxyBackURL(reader.getElementText());
				break;

			case SETTINGS_USER_TARGET:
				setting.setProxyTargetURL(reader.getElementText());
				break;

			case SETTINGS_CSS:
				setting.setUseCSS(Boolean.valueOf(reader.getElementText()));
				break;

			case SETTINGS_EDITOR:
				setting.setUseEditor(Boolean.valueOf(reader.getElementText()));
				break;

			case SETTINGS_DECORATION:
				setting.setUseDecorator(Boolean.valueOf(reader.getElementText()));
				break;

			case SETTINGS_LOCAL:
				setting.setLocal(reader.getElementText());
				break;

			}
			break;

		case XMLStreamConstants.END_ELEMENT:
			break;

		}
	}


}
