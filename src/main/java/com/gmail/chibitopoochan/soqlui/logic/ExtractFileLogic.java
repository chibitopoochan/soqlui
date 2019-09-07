package com.gmail.chibitopoochan.soqlui.logic;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;

import com.gmail.chibitopoochan.soqlui.config.ApplicationSettingSet;
import com.gmail.chibitopoochan.soqlui.model.Base64Object;
import com.gmail.chibitopoochan.soqlui.util.ExtractFileUtils;
import com.gmail.chibitopoochan.soqlui.util.LogUtils;

public class ExtractFileLogic {
	private static final Logger logger = LogUtils.getLogger(ExtractFileLogic.class);
	private static final String REST_URL = ApplicationSettingSet.getInstance().getSetting().getRestBlobURL();
	private static final boolean ADVANCE_QUERY = ApplicationSettingSet.getInstance().getSetting().isAdvanceQuery();

	private Optional<Base64Object> targetObject;
	private boolean canExtract;
	private boolean firstRecord;
	private File directory;
	private String fileName;
	private String extentionName;
	private String bodyName;
	private String idName;
	private String instanceName;
	private String sessionKey;
	private String apiVersion;
	private Proxy proxy = Proxy.NO_PROXY;

	/**
	 * SOQLから抽出可能かを判定
	 * @param directory CSVファイルの出力先
	 * @param soql 実行するSOQL
	 */
	public void init(Path directory, String soql) {
		init(directory, soql, true);
	}

	/**
	 * SOQLから抽出可能かを判定
	 * @param directory CSVファイルの出力先
	 * @param soql 実行するSOQL
	 * @param instanceName インスタンスURL
	 * @param sessionKey セッションID
	 */
	public void init(Path directory, String soql, String instanceName, String apiVersion, String sessionKey) {
		this.instanceName = instanceName;
		this.sessionKey = sessionKey;
		this.apiVersion = apiVersion;
		init(directory, soql, false);

	}

	/**
	 * SOQLから抽出可能かを判定
	 * @param directory CSVファイルの出力先
	 * @param soql 実行するSOQL
	 * @param instanceName インスタンスURL
	 * @param sessionKey セッションID
	 */
	public void init(Path directory, String soql, String instanceName, String apiVersion, String sessionKey, String host, int port) {
		this.instanceName = instanceName;
		this.sessionKey = sessionKey;
		this.apiVersion = apiVersion;
		this.proxy  = new Proxy(Type.HTTP, new InetSocketAddress(host, port));
		init(directory, soql, false);

	}

	private void init(Path directory, String soql, boolean base64) {
		targetObject = Base64Object.get(soql, !base64);
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
		return canExtract && ADVANCE_QUERY;
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
				if(targetObject.get().isSameIdName(key)) idName = key;
			}
			firstRecord = false;
		}

		// IDのフォルダを作成
		File folder = new File(directory, record.get(idName));
		folder.mkdir();

		// 出力先を構築
		String name = record.get(fileName);
		if(record.containsKey(extentionName)){
			name += "." + record.get(extentionName);
		}
		File file = new File(folder, name);

		// ファイルを出力
		if(targetObject.get().isUndecode()) {
			URL url = new URL(String.format(REST_URL, instanceName, apiVersion,targetObject.get().objectName, record.get(idName), targetObject.get().bodyName));
			ExtractFileUtils.export(file, url, sessionKey, proxy);
		} else {
			ExtractFileUtils.export(file, record.get(bodyName));
			// 元の項目は空にする
			record.put(bodyName, "");
		}

	}

}
