package com.services;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.app.Main;


public class Request {
    public static void getReqPath(String reqHeader) {
		//
		String pattern = "GET ([^\\s]+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(reqHeader);
		if (m.find()) {
			Main.reqPath = m.group(1);
			Main.reqPath = Main.reqPath.substring(1);
		}
		if (Main.reqPath.equals("\\")) {
			Main.reqPath = "";
		}

		try {
			Main.reqPath = URLDecoder.decode(Main.reqPath, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static void getReqMethod(String reqHeader) {
		//
		String pattern = "([^\\s]+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(reqHeader);
		if (m.find()) {
			Main.reqMethod = m.group(1);
		}
	}

    
	public static String getReqHeader(String req) {
		//
		String header = req.split("\r\n\r\n")[0];
		return header;
	}

}
