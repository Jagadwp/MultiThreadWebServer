package com.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.app.Main;

public class Content {

	public static String getFileContent() {
		String fullPath = Main.rootDir + Main.requestPath;
		String fileContent = "";

		File f = new File(fullPath);
		if (f.isFile()) {
			FileInputStream fis;
			BufferedInputStream bis;
			try {
				fis = new FileInputStream(fullPath);

				bis = new BufferedInputStream(fis);
				byte[] bRes = new byte[1024];
				int c = bis.read(bRes);

				while (c != -1) {
					fileContent += (new String(bRes));
					c = bis.read(bRes);
				}

				bis.close();
				fis.close();
			} catch (FileNotFoundException e) {
				Main.status = "404 Not Found";
				return "file tidak ditemukan";
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (f.exists()) {
			File f2 = new File(Main.rootDir + Main.requestPath + "\\index.html");
			if (f2.exists()) {
				FileInputStream fis;
				BufferedInputStream bis;
				Main.pageType = 1;
				try {
					fullPath += "\\index.html";
					fis = new FileInputStream(fullPath);

					bis = new BufferedInputStream(fis);
					byte[] bRes = new byte[1024];
					int c = bis.read(bRes);

					while (c != -1) {
						fileContent += (new String(bRes));
						c = bis.read(bRes);
					}

					bis.close();
					fis.close();
				} catch (FileNotFoundException e) {
					Main.status = "404 Not Found";
					return "file tidak ditemukan";
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Main.pageType = 2;
				fileContent = getDirPage(getListDir(f));
			}
		} else {
			Main.status = "404 Not Found";
			return "file tidak ditemukan";
		}

		Main.status = "200 OK";
		return fileContent;
	}

	public static String getDirPage(File[] listDir) {
		String fileContent = "";
		fileContent += "<!DOCTYPE html>\r\n" +
				"<html>\r\n" +
				"<head>\r\n" +
				"<title>/" + Main.requestPath + "</title>\r\n" +
				"</head>\r\n" +
				"<body>";

		for (File file : listDir) {
			fileContent += "<a href='";
			if (!Main.requestPath.isEmpty())
				fileContent += "/";
			fileContent += Main.requestPath + "/" + file.getName() + "'>" +
					Main.requestPath + "/" + file.getName() + "</a></br>";
		}
		fileContent += "</body>\r\n" +
				"</html>";
		return fileContent;
	}

	public static File[] getListDir(File dir) {
		File filesList[] = dir.listFiles();

		return filesList;
	}

	public static String getResponse(String content) {
		//
		String fullPath = Main.rootDir + Main.requestPath;
		String mimeType = "";
		if (Main.pageType == 1)
			fullPath += "\\index.html";

		if (Main.pageType != 2) {
			Path path = Paths.get(fullPath);
			Path fileName = path.getFileName();
			mimeType = getFileType(fileName.toString());
		} else if (Main.pageType == 2) {
			mimeType = "text/html";
		}

		if (mimeType.contains("image")) {
			byte[] fileContent;
			content = "";
			try {
				fileContent = FileUtils.readFileToByteArray(new File(fullPath));
				content = Base64.getEncoder().encodeToString(fileContent);
			} catch (IOException e) {
				e.printStackTrace();
			}

			content = "<img src='data:" + mimeType + ";base64," + content + "'>";

			mimeType = "text/html";
		}

		String response = "HTTP/1.0 " + Main.status + "\r\n";
		response += "Content-Type: " + mimeType + "\r\n";
		response += "Content-Length: " + content.length() + "\r\n";
		response += "\r\n";

		return response + content;
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
}
