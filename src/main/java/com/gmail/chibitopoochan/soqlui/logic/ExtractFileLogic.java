package com.gmail.chibitopoochan.soqlui.logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;

import com.gmail.chibitopoochan.soqlui.util.ExtractFileUtils;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;

public class ExtractFileLogic {
	private static final Logger logger = LogUtils.getLogger(ExtractFileLogic.class);

	private Optional<Base64Object> targetObject;
	private boolean canExtract;
	private boolean firstRecord;
	private File directory;
	private String fileName;
	private String extentionName;
	private String bodyName;

	/**
	 * SOQLから抽出可能かを判定
	 * @param directory CSVファイルの出力先
	 * @param soql 実行するSOQL
	 */
	public void init(Path directory, String soql) {
		targetObject = Base64Object.get(soql);
		firstRecord = true;

		if(targetObject.isPresent()) {
			canExtract = true;
			String name = directory.toFile().getName();
			name = name.substring(0,name.lastIndexOf("."));
			this.directory = new File(directory.getParent().toString(), name);
			logger.debug("It can extract files to " + directory.toString());
		} else {
			logger.debug("Cannot extract files");
		}
	}

	public boolean canExtract() {
		return canExtract;
	}

	public void extract(Map<String, String> record) throws IOException {
		// フォルダが無ければ作成
		if(!directory.exists()) {
			logger.debug("Create a directory...");
			directory.mkdir();
		}

		// 項目名が無ければ取得（大文字／小文字があるため）
		if(firstRecord) {
			logger.debug("Loading the field names");
			for(String key : record.keySet()) {
				if(targetObject.get().isSameFileName(key)) fileName = key;
				if(targetObject.get().isSameExtention(key)) extentionName = key;
				if(targetObject.get().isSameBodyName(key)) bodyName = key;
			}
			firstRecord = false;
		}

		// 出力先を構築
		String name = record.get(fileName);
		String extention = record.get(extentionName);
		File file = new File(directory, name + "." + extention);

		// ファイルを出力
		ExtractFileUtils.export(file, record.get(bodyName));

		// 元の項目は空にする
		record.put(bodyName, "");

	}

}
