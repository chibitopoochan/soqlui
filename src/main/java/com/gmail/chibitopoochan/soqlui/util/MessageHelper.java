package com.gmail.chibitopoochan.soqlui.util;

import java.util.ResourceBundle;

import com.gmail.chibitopoochan.soqlui.util.Constants.Message;

/**
 * リソースファイル（メッセージ関連）のヘルパークラス
 */
public class MessageHelper {
	private static final ResourceBundle message = ResourceBundle.getBundle(Message.RESOURCE);
	private static final String LOGGER_REPLACE_MARK = "\\{\\}";
	private static final String FORMATTER_REPLACE_MARK = "\\%s";

	/**
	 * リソースファイルからメッセージを取得
	 * @param resource リソース名
	 * @param param パラメータ
	 * @return メッセージ
	 */
	public static String getMessage(String resource, String...param) {
		String template = message
				.getString(resource)
				.replaceAll(MessageHelper.LOGGER_REPLACE_MARK, MessageHelper.FORMATTER_REPLACE_MARK);

		if(param.length == 0) {
			return template;
		} else {
			return String.format(template, (Object[]) param);
		}
	}

}
