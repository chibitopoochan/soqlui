package com.gmail.chibitopoochan.soqlui.model;

import java.util.Map;

import com.gmail.chibitopoochan.soqlexec.model.SObjectMetaInfo;

public class DescribeSObject {
	private String keyPrefix;
	private String name;
	private String label;
	private Map<String, String> metaInfo;

	public DescribeSObject(String name, String label, String keyPrefix) {
		this.name = name;
		this.label = label;
		this.keyPrefix = keyPrefix;
	}

	public DescribeSObject(SObjectMetaInfo m) {
		this(m.getName(), m.getLabel(), m.getKeyPrefix());
		this.metaInfo = m.getMetaInfo();
	}

	/**
	 * @return keyPrefix
	 */
	public String getKeyPrefix() {
		return keyPrefix;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return label
	 */
	public String getLabel() {
		return label;
	}

	public Map<String,String> getMetaInfo() {
		return metaInfo;
	}

}
