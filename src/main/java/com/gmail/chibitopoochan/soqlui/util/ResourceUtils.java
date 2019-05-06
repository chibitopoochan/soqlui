package com.gmail.chibitopoochan.soqlui.util;

public class ResourceUtils {
	public static String getURL(Object obj, String path) {
		String url = obj.getClass().getResource(path).getPath();
		if(url.startsWith("file:/")) {
			url = "jar:" + url;
		} else {
			url = "file://" + url;
		}
		return url;
	}
}
