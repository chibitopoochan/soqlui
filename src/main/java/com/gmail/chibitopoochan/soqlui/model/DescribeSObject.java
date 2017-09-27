package com.gmail.chibitopoochan.soqlui.model;

public class DescribeSObject {
	private String keyPrefix;
	private String name;
	private String label;

	public DescribeSObject(String name, String label, String keyPrefix) {
		this.name = name;
		this.label = label;
		this.keyPrefix = keyPrefix;
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

}
