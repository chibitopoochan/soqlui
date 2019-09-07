package com.gmail.chibitopoochan.soqlui.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.slf4j.Logger;

public class ExtractFileUtils {
	private static final Logger logger = LogUtils.getLogger(ExtractFileUtils.class);

	public static void export(File file, String body) throws IOException {
		logger.debug("Extract to " + file.getPath());
		try(
		FileOutputStream stream = new FileOutputStream(file);
		Base64OutputStream writer = new Base64OutputStream(stream,false);
				){
			writer.write(body.getBytes());
		}
	}

	public static void export(File file, URL url, String sessionKey, Proxy proxy) {
		logger.debug(String.format("Download by [%s]", url));
		logger.debug(String.format("Authorization is [%s]", sessionKey));
		logger.debug(String.format("Extract to [%s]", file.getPath()));

		try(FileOutputStream stream = new FileOutputStream(file)){
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection(proxy);
			connection.setAllowUserInteraction(false);
			connection.setRequestProperty("Content-Type", "application/octet-stream");
			connection.setRequestProperty("Authorization", ":Bearer " + sessionKey);
			connection.setRequestMethod("GET");
			connection.connect();

			int code = connection.getResponseCode();
			if(code == HttpsURLConnection.HTTP_OK) {

		      // Input Stream
		      DataInputStream downloadStream = new DataInputStream(connection.getInputStream());

		      // Read Data
		      byte[] b = new byte[4096];
		      int readByte = 0;

		      while(-1 != (readByte = downloadStream.read(b))){
		    	  stream.write(b, 0, readByte);
		      }

		      downloadStream.close();
			} else {
				logger.debug(String.format("failed by response code : [%s]", code));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
