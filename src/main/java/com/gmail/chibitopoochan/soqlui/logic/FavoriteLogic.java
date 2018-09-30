package com.gmail.chibitopoochan.soqlui.logic;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.config.FavoriteSet;
import com.gmail.chibitopoochan.soqlui.model.SOQLFavorite;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;
import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;

/**
 * お気に入りのロジック
 * @author mamet
 *
 */
public class FavoriteLogic {
	// クラス共通の参照
	private static final Logger logger = LoggerFactory.getLogger(FavoriteLogic.class);

	private FavoriteSet favoriteSet;
	private List<SOQLFavorite> cachedList;

	/**
	 * 初期化。ファイルの読み込み
	 */
	public FavoriteLogic() {
		loadFavoriteList();
	}

	/**
	 * ファイルの読み込み
	 */
	public void loadFavoriteList() {
		favoriteSet = FavoriteSet.getInstance();
		cachedList = favoriteSet.getFavoriteList();
	}

	/**
	 * お気に入りの一覧を取得
	 * @return お気に入り
	 */
	public List<SOQLFavorite> getFavoriteList() {
		return cachedList;
	}

	/**
	 * お気に入りの追加
	 * @param favorite 追加するお気に入り
	 */
	public void addFavorite(SOQLFavorite favorite) {
		cachedList.add(favorite);
		storeFavorite();
	}

	/**
	 * お気に入りの削除
	 * @param favorite 削除するお気に入り
	 */
	public void removeFavorite(SOQLFavorite favorite) {
		cachedList.remove(favorite);
		storeFavorite();
	}

	/**
	 * お気に入りの永続化
	 */
	private void storeFavorite() {
		favoriteSet.setFavoriteList(cachedList);
		try {
			favoriteSet.storeFavorite();
		} catch (IllegalStateException | XMLStreamException | IOException e) {
			logger.error(MessageHelper.getMessage(Message.Error.ERR_003, e.getMessage()));
			e.printStackTrace();
		}
	}

}
