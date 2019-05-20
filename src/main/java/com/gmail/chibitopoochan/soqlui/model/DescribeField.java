package com.gmail.chibitopoochan.soqlui.model;

import java.util.Map;

import com.gmail.chibitopoochan.soqlexec.model.FieldMetaInfo;
import com.gmail.chibitopoochan.soqlexec.model.FieldMetaInfo.Type;

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

	private Map<FieldMetaInfo.Type, String> metaInfo;

	public DescribeField(FieldMetaInfo info) {
		metaInfo = info.getMetaInfo();
	}

	/**
	 * @return name
	 */
	public String getName() {
		return metaInfo.get(Type.Name);
	}

	/**
	 * @return label
	 */
	public String getLabel() {
		return metaInfo.get(Type.Label);
	}

	/**
	 * @return length
	 */
	public String getLength() {
		return metaInfo.get(Type.Length);
	}

	/**
	 * @return type
	 */
	public String getType() {
		return metaInfo.get(Type.Type);
	}

	/**
	 * @return picklist
	 */
	public String getPicklist() {
		return metaInfo.get(Type.PicklistValues_Active);
	}

	/**
	 * @return reference
	 */
	public String getReference() {
		return metaInfo.get(Type.ReferenceTo);
	}

	public Map<FieldMetaInfo.Type,String> getMetaInfo(){
		return metaInfo;
	}

}
