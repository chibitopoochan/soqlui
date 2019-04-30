package com.gmail.chibitopoochan.soqlui.logic;

import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;

import com.gmail.chibitopoochan.soqlui.config.FavoriteSet;
import com.gmail.chibitopoochan.soqlui.model.SOQLFavorite;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

/**
 * お気に入りのロジック
 * @author mamet
 *
 */
public class FavoriteLogic {
	// クラス共通の参照
	private static final Logger logger = LogUtils.getLogger(FavoriteLogic.class);

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
	 * お気に入りの名前変更
	 * Nullの場合はエラー
	 * @param oldName 旧名称
	 * @param newName 新名称
	 */
	public void rename(String oldName, String newName) {
		cachedList.stream().filter(f -> f.getName().equals(oldName)).forEach(f -> f.setName(newName));
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
