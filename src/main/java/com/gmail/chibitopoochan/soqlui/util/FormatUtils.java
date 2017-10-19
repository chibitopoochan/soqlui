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
					tempText.append(value);
					tempText.append(decorator.getHeaderValueAfter());

				});
				tempText.append(decorator.getHeaderAfter());
			} else {
				tempText.append(decorator.getRowBefore());
				columnList.forEach(value -> {
					if(value != columnList.get(0)) {
						tempText.append(decorator.getValueBetween());
					}
					tempText.append(decorator.getValueBefore());
					tempText.append(value);
					tempText.append(decorator.getValueAfter());
				});
				tempText.append(decorator.getRowAfter());
			}

		});
		tempText.append(decorator.getTableAfter());

		return tempText.toString();
	}

}