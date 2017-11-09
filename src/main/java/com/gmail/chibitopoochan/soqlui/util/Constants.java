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
		}

		public interface Information {
			String MSG_001 = "MSG.001";
		}

	}

	public interface Configuration {
		String RESOURCE = "Configuration";
		String CONNECT_SETTING_PATH = "CONNECT_SETTING_PATH";
		String ICON = "APPLICATION_ICON";
		String HISTORY_PATH = "HISTORY_PATH";
		String HISTORY_SIZE = "HISTORY_SIZE";
		String VIEW_SU01 = "VIEW_SU01";
		String VIEW_SU02 = "VIEW_SU02";
		String VIEW_SU03 = "VIEW_SU03";
		String TITLE_SU01 = "TITLE_SU01";
		String TITLE_SU02 = "TITLE_SU02";
		String TITLE_SU03 = "TITLE_SU03";
	}

}
