package com.gmail.chibitopoochan.soqlui.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.chibitopoochan.soqlui.config.ApplicationSettingSet;

import ch.qos.logback.classic.Level;


public class LogUtils {

	@SuppressWarnings("rawtypes")
	public static Logger getLogger(Class clazz) {
		ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(clazz);
		if(ApplicationSettingSet.getInstance().getSetting().isDebugMode()) {
			logger.setLevel(Level.DEBUG);
		}
		return logger;
	}
}
