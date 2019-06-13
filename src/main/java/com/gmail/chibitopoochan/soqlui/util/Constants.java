package com.gmail.chibitopoochan.soqlui.util;
/**
 * システム全般で使用する定数を定義
 */
public interface Constants {

	/**
	 *  ユーザやログに表示するメッセージ
	 */
	public interface Message {
		String RESOURCE = "SOQLUI_Message";

		public interface Error {
			String ERR_001 = "ERR.001";
			String ERR_002 = "ERR.002";
			String ERR_003 = "ERR.003";
			String ERR_004 = "ERR.004";
			String ERR_005 = "ERR.005";
		}

		public interface Information {
			String MSG_001 = "MSG.001";
			String MSG_002 = "MSG.002";
			String MSG_003 = "MSG.003";
			String MSG_004 = "MSG.004";
		}

	}

	public interface Configuration {
		String RESOURCE = "Configuration";

		String FAVORITE_PATH = "FAVORITE_PATH";
		String CONNECT_SETTING_PATH = "CONNECT_SETTING_PATH";
		String HISTORY_PATH = "HISTORY_PATH";
		String PROXY_PATH = "PROXY_PATH";
		String FORMAT_PATH = "FORMAT_PATH";
		String SETTING_PATH = "SETTING_PATH";
		String HELP_PATH = "HELP_PATH";

		String ICON = "APPLICATION_ICON";
		String VERSION = "VERSION";

		String VIEW_SU01 = "VIEW_SU01";
		String VIEW_SU02 = "VIEW_SU02";
		String VIEW_SU03 = "VIEW_SU03";
		String VIEW_SU04 = "VIEW_SU04";
		String VIEW_SU05 = "VIEW_SU05";
		String VIEW_SU06 = "VIEW_SU06";

		String TITLE_SU01 = "TITLE_SU01";
		String TITLE_SU02 = "TITLE_SU02";
		String TITLE_SU03 = "TITLE_SU03";
		String TITLE_SU04 = "TITLE_SU04";
		String TITLE_SU05 = "TITLE_SU05";
		String TITLE_SU06 = "TITLE_SU06";

	}

}
