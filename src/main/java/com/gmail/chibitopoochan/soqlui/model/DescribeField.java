package com.gmail.chibitopoochan.soqlui.model;

import java.util.stream.Collectors;

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

	private String name;
	private String label;
	private int length;
	private String type;
	private String picklist;
	private String reference;

	public DescribeField(FieldMetaInfo info) {
		name = info.getName();
		label = info.getLabel();
		length = info.getLength();
		type = info.getType();
		picklist = info.getPicklist().stream().collect(Collectors.joining(";"));
		reference = info.getReferenceToList().stream().collect(Collectors.joining(";"));
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

	/**
	 * @return length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return picklist
	 */
	public String getPicklist() {
		return picklist;
	}

	/**
	 * @return reference
	 */
	public String getReference() {
		return reference;
	}

}
