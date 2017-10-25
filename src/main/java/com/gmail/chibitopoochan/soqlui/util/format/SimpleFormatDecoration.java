package com.gmail.chibitopoochan.soqlui.util.format;

public class SimpleFormatDecoration extends FormatDecoration {
	public SimpleFormatDecoration() {
		setHeaderAfter(System.lineSeparator());
		setRowAfter(System.lineSeparator());
		setValueBetween("\t");
		setMadatoryLastRowDecoration(false);
		setShowHeader(false);
	}
}
