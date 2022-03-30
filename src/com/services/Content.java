package com.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;

// import org.apache.commons.io.FileUtils;

import com.app.ClientThread;

public class Content {
	public static String getDirPage(File[] listDir) {
		String fileContent = "";
		fileContent += "<!DOCTYPE html>\r\n" +
				"<html>\r\n" +
				"<head>\r\n" +
				"<title>/" + ClientThread.requestPath + "</title>\r\n" +
				"</head>\r\n" +
				"<body>";

		for (File file : listDir) {
			fileContent += "<a href='";
			if (!ClientThread.requestPath.isEmpty())
				fileContent += "/";
			fileContent += ClientThread.requestPath + "/" + file.getName() + "'>" +
					ClientThread.requestPath + "/" + file.getName() + "</a></br>";
		}
		fileContent += "</body>\r\n" +
				"</html>";
		return fileContent;
	}

	public static File[] getListDir(File dir) {
		File filesList[] = dir.listFiles();

		return filesList;
	}

	public static String getResponse(long contentLength) {
		String fullPath = ClientThread.rootDir + ClientThread.requestPath;
		String mimeType = "";

		if (isFolder(fullPath) && (new File(fullPath + "\\index.html")).exists())
			fullPath += "\\index.html";

		if (isFolder(fullPath) && !(new File(fullPath + "\\index.html")).exists()) {
			mimeType = "text/html";
		} else {
			Path path = Paths.get(fullPath);
			Path fileName = path.getFileName();
			mimeType = getFileType(fileName.toString());
			System.out.println(fileName.toString());
		}

		String response = "HTTP/1.0 " + ClientThread.status+ "\r\n";
		response += "Content-Type: " + mimeType + "\r\n";
		response += "Content-Length: " + contentLength + "\r\n";
		response += "Connection: " + ClientThread.reqConnection + "\r\n";

		if (ClientThread.reqConnection.contains("keep-alive")) {
			response += "Keep-Alive: timeout=2; max=1000\r\n";
		}

		if (mimeType.contains("image")) {
			File f = new File(fullPath);
			response += "Content-Disposition: inline; " + f.getName() + "\r\n";
		} else if (!isFolder(fullPath) && !mimeType.contains("text")) {
			File f = new File(fullPath);
			response += "Content-Disposition: attachment; " + f.getName() + "\r\n";
		}

		response += "\r\n";
		System.out.println(response);
		return response;
	}

	public static long getFileLength() {
		String fullPath = ClientThread.rootDir + ClientThread.requestPath;
		Path path = Paths.get(fullPath);

		File f = new File(fullPath);
		if (isFolder(fullPath)) {
			return getDirPage(getListDir(f)).length();
		}

		try {
			long bytes = Files.size(path);
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String getFileType(String filename) {

		FileInputStream fis;
		BufferedInputStream bis;
		String tmp = "";
		try {
			fis = new FileInputStream("extMimeList.txt");
			bis = new BufferedInputStream(fis);

			byte[] bRes = new byte[1024];
			int c = bis.read(bRes);

			while (c != -1) {
				tmp += (new String(bRes));
				c = bis.read(bRes);
			}

			bis.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String ext = "";
		try {
			ext = filename.substring(filename.indexOf('.'));
		} catch (StringIndexOutOfBoundsException e) {
			return "";
		}
		String pattern = ext + " ([^\n]+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(tmp);
		if (m.find()) {
			return m.group(1);
		}
		return "";
	}

	public static boolean isFolder(String path) {
		File f = new File(path);
		if (!f.isFile() && f.exists())
			return true;
		return false;
	}
}
