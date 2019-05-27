package com.gmail.chibitopoochan.soqlui.logic;

import java.util.Optional;

public class Base64Object {
	private static final Base64Object CONTENT_VERSION = new Base64Object("ContentVersion","Title", "FileExtension", "VersionData");
	private static final Base64Object DOCUMENT = new Base64Object("Document","Name", "ContentType", "Body");
	private static final Base64Object ATTACHMENT = new Base64Object("Attachment","Name", "ContentType", "Body");

	final String objectName;
	final String fileName;
	final String extentionName;
	final String bodyName;
	final String idName;

	/**
	 * キー情報を持つインスタンスを作成
	 */
	private Base64Object(String obj, String name, String extention, String body) {
		this.idName = "Id";
		this.objectName = obj;
		this.fileName = name;
		this.extentionName = extention;
		this.bodyName = body;
	}

	/**
	 * SOQLから該当するオブジェクトを検索
	 * @param soql 実行するSOQL
	 * @return 該当するインスタンス
	 */
	public static Optional<Base64Object> get(String soql) {
		if(CONTENT_VERSION.findExtractKeywords(soql)) return Optional.of(CONTENT_VERSION);
		if(DOCUMENT.findExtractKeywords(soql)) return Optional.of(DOCUMENT);
		if(ATTACHMENT.findExtractKeywords(soql)) return Optional.of(ATTACHMENT);
		return Optional.empty();
	}

	/**
	 *
	 * @param soql
	 * @return
	 */
	public boolean findExtractKeywords(String soql) {
		return soql.toLowerCase().contains(objectName.toLowerCase())
				&& soql.toLowerCase().contains(fileName.toLowerCase())
				&& soql.toLowerCase().contains(extentionName.toLowerCase())
				&& soql.toLowerCase().contains(bodyName.toLowerCase())
				&& soql.toLowerCase().contains(idName.toLowerCase());
	}

	public boolean isSameFileName(String keyword) {
		return keyword.toLowerCase().equals(fileName.toLowerCase());
	}

	public boolean isSameExtention(String keyword) {
		return keyword.toLowerCase().equals(extentionName.toLowerCase());
	}

	public boolean isSameBodyName(String keyword) {
		return keyword.toLowerCase().equals(bodyName.toLowerCase());
	}

	public boolean isSameIdName(String keyword) {
		return keyword.toLowerCase().equals(idName.toLowerCase());
	}

}