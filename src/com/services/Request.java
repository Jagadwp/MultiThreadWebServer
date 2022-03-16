package com.services;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.app.Main;


public class Request {
    public static void getRequestPath(String reqHeader) {
		String pattern = "GET ([^\\s]+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(reqHeader);
		if (m.find()) {
			Main.requestPath = m.group(1);
			Main.requestPath = Main.requestPath.substring(1);
		}
		if (Main.requestPath.equals("\\")) {
			Main.requestPath = "";
		}

		try {
			Main.requestPath = URLDecoder.decode(Main.requestPath, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static void getRequestMethod(String reqHeader) {
		//
		String pattern = "([^\\s]+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(reqHeader);
		if (m.find()) {
			Main.requestMethod = m.group(1);
		}
	}

    
	public static String getRequestHeader(String req) {
		//
		String header = req.split("\r\n\r\n")[0];
		return header;
	}

}
