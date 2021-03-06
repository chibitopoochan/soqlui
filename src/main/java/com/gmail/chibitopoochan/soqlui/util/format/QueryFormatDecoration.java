package com.gmail.chibitopoochan.soqlui.util.format;

public class QueryFormatDecoration extends FormatDecoration {

	public QueryFormatDecoration() {
		setTableBefore("select");
		setHeaderBefore(System.lineSeparator());
		setHeaderAfter(System.lineSeparator());
		setValueBefore(",");
		setRowAfter(System.lineSeparator());
	}

	@Override
	public void setTableAfter(String value) {
		super.setTableAfter("from " + value);
	}

}
