package com.gmail.chibitopoochan.soqlui.config;

import java.util.Optional;
import java.util.ResourceBundle;

import org.slf4j.Logger;

import com.gmail.chibitopoochan.soqlui.util.Constants.Configuration;
import com.gmail.chibitopoochan.soqlui.util.Constants.Message;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;
import com.gmail.chibitopoochan.soqlui.util.MessageHelper;

public class Format {
	// クラス共通の参照
	private static final Logger logger = LogUtils.getLogger(Format.class);
	private static final ResourceBundle config = ResourceBundle.getBundle(Configuration.RESOURCE);

	private static Format instance;

	// 解析関連の情報
	private Optional<String> filePath = Optional.empty();

	private Format() {
		loadFilePath();
	}

	public static Format getInstance() {
		if(instance == null) {
			instance = new Format();
		}

		return instance;

	}

	public void loadFilePath() {
		filePath = Optional.of(config.getString(Configuration.FORMAT_PATH));
		logger.info(MessageHelper.getMessage(Message.Information.MSG_001, filePath.get()));
	}

	public String getFilePath() {
		return filePath.orElseThrow(IllegalStateException::new);
	}

}
