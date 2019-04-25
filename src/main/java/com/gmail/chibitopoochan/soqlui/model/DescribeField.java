package com.gmail.chibitopoochan.soqlui.model;

import java.util.Map;

import com.gmail.chibitopoochan.soqlexec.model.FieldMetaInfo;

/**
 * オブジェクトの項目情報
 */
public class DescribeField {
	public static final String FIELD_LIST_COLUMN_NAME = "Name";
	public static final String FIELD_LIST_COLUMN_LABEL = "Label";
	public static final String FIELD_LIST_COLUMN_LENGTH = "Length";
	public static final String FIELD_LIST_COLUMN_TYPE = "Type";
	public static final String FIELD_LIST_COLUMN_PICKLIST = "Picklist";
	public static final String FIELD_LIST_COLUMN_REF = "Reference";

	private Map<String, String> metaInfo;

	public DescribeField(FieldMetaInfo info) {
		metaInfo = info.getMetaInfo();
	}

	/**
	 * @return name
	 */
	public String getName() {
		return metaInfo.get("Name");
	}

	/**
	 * @return label
	 */
	public String getLabel() {
		return metaInfo.get("Label");
	}

	/**
	 * @return length
	 */
	public String getLength() {
		return metaInfo.get("Length");
	}

	/**
	 * @return type
	 */
	public String getType() {
		return metaInfo.get("Type");
	}

	/**
	 * @return picklist
	 */
	public String getPicklist() {
		return metaInfo.get("PicklistValues.Active");
	}

	/**
	 * @return reference
	 */
	public String getReference() {
		return metaInfo.get("ReferenceTo");
	}

	public Map<String,String> getMetaInfo(){
		return metaInfo;
	}

}
