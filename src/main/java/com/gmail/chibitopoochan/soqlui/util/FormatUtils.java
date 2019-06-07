package com.gmail.chibitopoochan.soqlui.util;

import java.util.List;

import com.gmail.chibitopoochan.soqlui.util.format.FormatDecoration;
import com.gmail.chibitopoochan.soqlui.util.format.FormatValueProvider;

public class FormatUtils {

	public static String format(FormatDecoration decorator, FormatValueProvider provider) {
		StringBuilder tempText = new StringBuilder();
		List<List<String>> rowList = provider.provide();

		tempText.append(decorator.getTableBefore());
		rowList.forEach(columnList -> {
			if(decorator.isShowHeader() && columnList == rowList.get(0)) {
				tempText.append(decorator.getHeaderBefore());
				columnList.forEach(value -> {
					if(value != columnList.get(0)) {
						tempText.append(decorator.getValueBetween());
					}
					tempText.append(decorator.getHeaderValueBefore());
					tempText.append(decorator.convertItem(value));
					tempText.append(decorator.getHeaderValueAfter());

				});
				tempText.append(decorator.getHeaderAfter());
			} else {
				tempText.append(decorator.getRowBefore());
				tempText.append(decorator.getValueBefore());
				tempText.append(columnList.get(0));
				tempText.append(decorator.getValueAfter());
				columnList.subList(1, columnList.size()).forEach(value -> {
					tempText.append(decorator.getValueBetween());
					tempText.append(decorator.getValueBefore());
					tempText.append(decorator.convertItem(value == null ? "" : value));
					tempText.append(decorator.getValueAfter());
				});
				tempText.append(decorator.getRowAfter());
			}

		});
		tempText.append(decorator.getTableAfter());

		return tempText.toString();
	}

}