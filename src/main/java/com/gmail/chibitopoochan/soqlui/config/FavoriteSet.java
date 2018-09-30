package com.gmail.chibitopoochan.soqlui.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.model.SOQLFavorite;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

public class FavoriteSet {
	// クラス共通の参照
	private static final Logger logger = LoggerFactory.getLogger(FavoriteSet.class);
	private static final ResourceBundle config = ResourceBundle.getBundle(Configuration.RESOURCE);

	// XMLの要素定義
	public static final String FAVORITES_ELEMENT = "favorites";
	public static final String FAVORITE_ELEMENT = "favorite";
	public static final String NAME = "name";
	public static final String SOQL = "query";

	// Singletonのインスタンス
	private static FavoriteSet instance;

	// 解析関連の情報
	private Optional<String> filePath = Optional.empty();
	private List<SOQLFavorite> favoriteList = new LinkedList<>();
	private SOQLFavorite favorite;

	/**
	 * ファイルを読み込んだFavoriteSetを作成
	 */
	private FavoriteSet() {
		loadFilePath();
		try {
			loadFavorite();
		} catch (IllegalStateException | IOException | XMLStreamException | ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * FavoriteSetのファクトリメソッド
	 * @return
	 */
	public static FavoriteSet getInstance() {
		if(instance == null) {
			instance = new FavoriteSet();
		}

		return instance;

	}

	/**
	 * ファイルパスの取得
	 */
	public void loadFilePath() {
		filePath = Optional.of(config.getString(Configuration.FAVORITE_PATH));
		logger.info(MessageHelper.getMessage(Message.Information.MSG_001, filePath.get()));

	}

	/**
	 * 内部のインスタンスをクリア.
	 * 初期化処理を改めて実施する場合に実行
	 */
	public static void releaseInstance() {
		instance = null;
	}

	public List<SOQLFavorite> getFavoriteList() {
		return favoriteList;
	}

	public void setFavoriteList(List<SOQLFavorite> list) {
		this.favoriteList = list;
	}

	/**
	 * 履歴の書き込み
	 * @throws XMLStreamException XML操作の例外
	 * @throws IOException 入出力の例外
	 * @throws IllegalStateException 設定ファイル未指定
	 */
	public void storeFavorite() throws XMLStreamException, IllegalStateException, IOException {
		// XMLファイルの出力
		XMLOutputFactory factory = XMLOutputFactory.newFactory();

		// 出力はtry-with-resoucesを使用
		// ※XMLStreamReaderはAutoClosable未実装
		try(OutputStream os = Files.newOutputStream(Paths.get(filePath.orElseThrow(IllegalStateException::new)))) {
			XMLStreamWriter writer = factory.createXMLStreamWriter(os);

			writer.writeStartElement(FAVORITES_ELEMENT);
			for(SOQLFavorite setting : favoriteList) {
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
	private void write(XMLStreamWriter writer, SOQLFavorite favorite) throws XMLStreamException {
		writer.writeStartElement("", FAVORITE_ELEMENT, "");

		// SOQL
		writer.writeStartElement("", SOQL, "");
		writer.writeCharacters(favorite.getQuery());
		writer.writeEndElement();

		// 名称
		writer.writeStartElement("", NAME, "");
		writer.writeCharacters(favorite.getName());
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
	public void loadFavorite() throws IllegalStateException, IOException, XMLStreamException, ParseException {
		// 接続設定一覧を初期化
		favoriteList = new LinkedList<>();

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
			case FAVORITE_ELEMENT:
				favorite = new SOQLFavorite();
				break;

			case NAME:
				favorite.setName(reader.getElementText());
				break;

			case SOQL:
				favorite.setQuery(reader.getElementText());
				break;

			}
			break;

		case XMLStreamConstants.END_ELEMENT:
			if(reader.getLocalName().equals(FAVORITE_ELEMENT)) {
				favoriteList.add(favorite);
			}
			break;

		}
	}

}
