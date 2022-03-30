package com.services;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.app.ClientThread;

public class Server {
	public static void getServerConfig() {
		FileInputStream fis;
		BufferedInputStream bis;
		String tmp = "";
		try {
			fis = new FileInputStream("serverConfig.txt");
			bis = new BufferedInputStream(fis);

			byte[] c;
			c = new byte[bis.available()];
			bis.read(c);
			tmp = new String(c);

			bis.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String pattern = "rootDir=([^\n]+)\nip=([^\n]+)\nport=([\\d]+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(tmp);
		if (m.find()) {
			ClientThread.rootDir = m.group(1);
			ClientThread.rootDir = ClientThread.rootDir.substring(0, ClientThread.rootDir.length() - 1);
			ClientThread.ip = m.group(2);
			ClientThread.ip = ClientThread.ip.substring(0, ClientThread.ip.length() - 1);
			ClientThread.port = m.group(3);
		}
	}

	public static void getHost(String reqHeader) {
		String host = "";
		String pattern = "Host: ([^\n]+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(reqHeader);
		if (m.find()) {
			host = m.group(1);
		}

		if (!host.equals("localhost")) {
			FileInputStream fis;
			BufferedInputStream bis;
			String tmp = "";
			try {
				fis = new FileInputStream("serverConfig.txt");
				bis = new BufferedInputStream(fis);

				byte[] c;
				c = new byte[bis.available()];
				bis.read(c);
				tmp = new String(c);

				bis.close();
				fis.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			pattern = "VirtualHost: " + host + " ([^\n]+)";
			r = Pattern.compile(pattern);
			m = r.matcher(tmp);
			if (m.find()) {
				ClientThread.rootDir = m.group(1);
			}
		}
	}

}