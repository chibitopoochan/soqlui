package com.gmail.chibitopoochan.soqlui.model;

import java.util.Map;

public class SObjectRecord {
	private Map<String, String> record;

	public SObjectRecord(Map<String,String> record) {
		this.record = record;
	}

	public void setRecord(Map<String, String> line) {
		this.record = line;
	}

	public Map<String,String> getRecord() {
		return record;
	}

}
