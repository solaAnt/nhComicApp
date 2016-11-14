package com.app.nhandroid.libs;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.app.nhandroid.activity.Config;

public class UrlUtils {

	public static String findComicName2(String name1, String content) {
		String result = null;
		String namePrex = "<div class=\"col-sm-6 col-xs-12\" id=\"info-block\">				<div id=\"info\">											<h1>";
		String endStr = "</h2>																										<div class=\"field-name\">";
		Pattern pat = Pattern.compile(namePrex + ".*" + endStr);
		Matcher mat = pat.matcher(content);
		while (mat.find()) {
			result = mat.group().replace(namePrex, "");
			result = result.replace(name1, "").replace(endStr, "");
			result = result.replace("</h1>																<h2>", "");
			System.out.println(result);
		}

		return result;
	}

	public static String findComicName1(String Content) {
		String result = null;
		String namePrex = "<div class=\"col-sm-6 col-xs-12\" id=\"info-block\">				<div id=\"info\">											<h1>";

		Pattern pat = Pattern.compile(namePrex + ".*</h1>");
		Matcher mat = pat.matcher(Content);
		while (mat.find()) {
			result = mat.group().replace(namePrex, "").replace("</h1>", "");
			System.out.println(result);
		}

		return result;
	}

	public static String findComicUrl(String Content) {
		String comicPageUrl = null;

		Pattern pat = Pattern.compile(Config.PATTERN+".*cover.jpg");
		Matcher mat = pat.matcher(Content);
		while (mat.find()) {
			comicPageUrl = "http://" + mat.group();
			comicPageUrl=comicPageUrl.replace("http://"+Config.PATTERN+"/", "");
			comicPageUrl=comicPageUrl.replace("/cover.jpg", "");
			System.out.println(comicPageUrl);
		}
		
		if(comicPageUrl!=null)
			return comicPageUrl;
		
		pat = Pattern.compile(Config.PATTERN+".*cover.png");
		mat = pat.matcher(Content);
		while (mat.find()) {
			comicPageUrl = "http://" + mat.group();
			comicPageUrl=comicPageUrl.replace("http://"+Config.PATTERN+"/", "");
			comicPageUrl=comicPageUrl.replace("/cover.png", "");
			System.out.println(comicPageUrl);
		}

		return comicPageUrl;
	}

	public static String getReturnData(String urlString)
			throws UnsupportedEncodingException {
		String res = "";
		try {
			URL url = new URL(urlString);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url
					.openConnection();
//			conn.setRequestProperty("User-Agent",
//					"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

			java.io.BufferedReader in = new java.io.BufferedReader(
					new java.io.InputStreamReader(conn.getInputStream(),
							"UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				res += line;
			}
			in.close();
		} catch (Exception e) {
			System.out.println("error in wapaction,and e is " + e.getMessage());
		}
		return res;
	}
}
