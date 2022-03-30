package com.services;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.app.ClientThread;


public class Request {
    public static void getRequestPath(String reqHeader) {
		String pattern = "GET ([^\\s]+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(reqHeader);
		if (m.find()) {
			ClientThread.requestPath = m.group(1);
			ClientThread.requestPath = ClientThread.requestPath.substring(1);
		}
		if (ClientThread.requestPath.equals("\\")) {
			ClientThread.requestPath = "";
		}

		try {
			ClientThread.requestPath = URLDecoder.decode(ClientThread.requestPath, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	// public static void getRequestMethod(String reqHeader) {
	// 	String pattern = "([^\\s]+)";
	// 	Pattern r = Pattern.compile(pattern);
	// 	Matcher m = r.matcher(reqHeader);
	// 	if (m.find()) {
	// 		ClientThread.requestMethod = m.group(1);
	// 	}
	// }

    
	public static String getRequestHeader(String req) {
		String header = req.split("\r\n\r\n")[0];
		return header;
	}

}
