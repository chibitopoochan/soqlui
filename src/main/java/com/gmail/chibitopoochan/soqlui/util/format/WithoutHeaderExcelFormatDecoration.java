package com.gmail.chibitopoochan.soqlui.util.format;

public class WithoutHeaderExcelFormatDecoration extends ExcelFormatDecoration {

	public WithoutHeaderExcelFormatDecoration() {
		super();
		setHeaderBefore("<tr>");
		setShowHeader(false);
	}

}
