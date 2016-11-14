package com.app.nhandroid.libs;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUtil {

	public static void error(String content) {
		System.out.println("unity error " + content);
	}

	public static void log(String content) {
		System.out.println("unity " + content);
	}

	public static boolean dwImg(String comicName, String url) {
		byte[] data = null;
		try {
			data = getImage(url);
			if (data != null) {

				String path = "c:/" + comicName + "/";

				int lastIndex = url.lastIndexOf("/");
				String fileName = url.substring(lastIndex + 1);

				File dirFile = new File(path);
				dirFile.mkdirs();

				String allPath = path + fileName;
				File file = new File(path + fileName);
				file.createNewFile();

//				saveMyBitmap(bitmap, allPath);
				return true;
			} else {
				log("null data");
				return false;
			}
		} catch (Exception e) {

			log("no url");
			return false;
		}
	}

	/**
	 * Get data from stream
	 * 
	 * @param inStream
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

	/**
	 * Get image from newwork
	 * 
	 * @param path
	 *            The path of image
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] getImage(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		InputStream inStream = conn.getInputStream();
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return readStream(inStream);
		}
		return null;
	}
}
