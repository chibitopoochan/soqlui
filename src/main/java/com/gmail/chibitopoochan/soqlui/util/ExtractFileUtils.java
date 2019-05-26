package com.gmail.chibitopoochan.soqlui.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtractFileUtils {
	private static final Logger logger = LoggerFactory.getLogger(ExtractFileUtils.class);

	public static void export(File file, String body) throws IOException {
		logger.debug("Extract to " + file.getPath());
		try(
		FileOutputStream stream = new FileOutputStream(file);
		Base64OutputStream writer = new Base64OutputStream(stream,false);
				){
			writer.write(body.getBytes());
		}
	}

}
