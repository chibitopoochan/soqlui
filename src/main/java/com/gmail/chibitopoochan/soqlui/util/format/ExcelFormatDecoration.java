package com.gmail.chibitopoochan.soqlui.util.format;

public class ExcelFormatDecoration extends FormatDecoration {

	public ExcelFormatDecoration() {
		setTableBefore("<table border=\"1\">");
		setHeaderBefore("<tr bgcolor=\"#5BA5DC\">");
		setHeaderValueBefore("<th>");
		setHeaderValueAfter("</th>");
		setHeaderAfter("</tr>");
		setRowBefore("<tr>");
		setValueBefore("<td>");
		setValueAfter("</td>");
		setRowAfter("</tr>");
		setTableAfter("</table>");
	}

}
