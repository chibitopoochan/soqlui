package com.gmail.chibitopoochan.soqlui.util.format;

public class CSVFormatDecoration extends FormatDecoration {
	public CSVFormatDecoration() {
		setHeaderAfter(System.lineSeparator());
		setHeaderValueBefore("\"");
		setHeaderValueAfter("\"");
		setValueBefore("\"");
		setValueAfter("\"");
		setRowAfter(System.lineSeparator());
		setValueBetween(",");
	}

	/**
	 * ダブルクォーテーションのエスケープ
	 */
	@Override
	public String convertItem(String value) {
		return value.replaceAll("\"", "\"\"");
	}

}
