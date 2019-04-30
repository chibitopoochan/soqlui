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

import com.gmail.chibitopoochan.soqlui.model.ProxySetting;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

public class ProxySettingSet {
	// XMLの要素定義
	public static final String PROXY_ELEMENT = "proxy";
	public static final String PROXY_USING = "using";
	public static final String PROXY_USERNAME = "username";
	public static final String PROXY_PASSWORD = "password";
	public static final String PROXY_HOST = "host";
	public static final String PROXY_PORT = "port";

	// クラス共通の参照
	private static final Logger logger = LogUtils.getLogger(ProxySettingSet.class);
	private static final ResourceBundle config = ResourceBundle.getBundle(Configuration.RESOURCE);

	// Singletonのインスタンス
	private static ProxySettingSet instance;

	// 解析関連の情報
	private Optional<String> filePath = Optional.empty();
	private ProxySetting proxy;

	/**
	 * プロキシ設定情報を持つ
	 */
	private ProxySettingSet() {
		loadFilePath();
		try {
			loadSetting();
		} catch (IllegalStateException | IOException | XMLStreamException | ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public ProxySetting getProxySetting() {
		return proxy;
	}

	public void setProxySetting(ProxySetting proxy) {
		this.proxy = proxy;
	}

	/**
	 * ProxySettingSetのファクトリメソッド
	 * @return インスタンス
	 */
	public static ProxySettingSet getInstance() {
		if(instance == null) {
			instance = new ProxySettingSet();
		}

		return instance;
	}

	/**
	 * 設定ファイルのパス読み込み
	 */
	public void loadFilePath() {
		filePath = Optional.of(config.getString(Configuration.PROXY_PATH));
		logger.info(MessageHelper.getMessage(Message.Information.MSG_002, filePath.get()));
	}

	/**
	 * 設定の保存
	 * @throws XMLStreamException XML形式のエラー
	 * @throws IllegalStateException 入出力エラー
	 * @throws IOException 入出力エラー
	 */
	public void storeSetting() throws XMLStreamException, IllegalStateException, IOException {
		// XMLファイルの出力
		XMLOutputFactory factory = XMLOutputFactory.newFactory();

		// 出力はtry-with-resoucesを使用
		// ※XMLStreamReaderはAutoClosable未実装
		try(OutputStream os = Files.newOutputStream(Paths.get(filePath.orElseThrow(IllegalStateException::new)))) {
			XMLStreamWriter writer = factory.createXMLStreamWriter(os);

			writer.writeStartElement(PROXY_ELEMENT);

			// 利用有無
			writer.writeStartElement("", PROXY_USING, "");
			writer.writeCharacters(proxy.getUseProxy());
			writer.writeEndElement();

			// ユーザ名
			writer.writeStartElement("", PROXY_USERNAME, "");
			writer.writeCharacters(proxy.getUsername());
			writer.writeEndElement();

			// パスワード
			writer.writeStartElement("", PROXY_PASSWORD, "");
			writer.writeCharacters(proxy.getPassword());
			writer.writeEndElement();

			// ホスト
			writer.writeStartElement("", PROXY_HOST, "");
			writer.writeCharacters(proxy.getHost());
			writer.writeEndElement();

			// ポート
			writer.writeStartElement("", PROXY_PORT, "");
			writer.writeCharacters(proxy.getPort());
			writer.writeEndElement();

			writer.writeEndElement();

			writer.flush();
			writer.close();

		}

	}

	/**
	 * 設定の読み込み
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws ParseException
	 * @throws XMLStreamException
	 */
	public void loadSetting() throws IllegalStateException, IOException, XMLStreamException, ParseException {
		// Proxy設定を初期化
		proxy = new ProxySetting();

		// ファイルが存在しないなら読み込みを中断する
		if(Files.notExists(Paths.get(filePath.get()))) {
			return;
		}

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
			case PROXY_USING:
				proxy.setUseProxy(reader.getElementText());
				break;

			case PROXY_USERNAME:
				proxy.setUsername(reader.getElementText());
				break;

			case PROXY_PASSWORD:
				proxy.setPassword(reader.getElementText());
				break;

			case PROXY_HOST:
				proxy.setHost(reader.getElementText());
				break;

			case PROXY_PORT:
				proxy.setPort(reader.getElementText());
				break;

			}
			break;

		}
	}

}
