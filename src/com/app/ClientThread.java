package com.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.services.*;

public class ClientThread extends Thread {
	public static String requestPath;
	public static String reqConnection;
	public static String rootDir;
	public static String ip;
	public static String port;
	public static String status;
	public BufferedReader br;
	public OutputStream os;
	public Socket sock;

	public ClientThread(Socket sock) {
		this.sock = sock;
	}

	@Override
	public void run() {
		Server.getServerConfig();

		try {
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			os = (sock.getOutputStream());

			String req = "";
			System.out.println("waiting...");
			String line = br.readLine();
			req += line + "\n";

			while (!line.isEmpty()) {
				line = br.readLine();
				req += line + "\n";
			}

			// System.out.println(req);
			Server.getHost(req);
			Request.getRequestPath(req);
			parseKeepAlive(req);

			if (reqConnection.contains("keep-alive")) {
				System.out.println("Connection -> Keep-Alive");
				sock.setKeepAlive(true);
			}

			setStatus();
			long contentLength = getFileLength();
			String res = Content.getResponse(contentLength);
			os.write(res.getBytes());

			if (contentLength > 0)
				deliverContent();

			System.out.println("New Connection");

			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public long getFileLength() {
		String fullPath = rootDir + requestPath;
		System.out.println("fullPath -> " + fullPath);
		Path path = Paths.get(fullPath);

		File f = new File(fullPath);
		if (Content.isFolder(fullPath)) {
			return Content.getDirPage(Content.getListDir(f)).length();
		}
		
		try {

			// size of a file (in bytes)
			long bytes = Files.size(path);
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void deliverContent() {
		String fullPath = rootDir + requestPath;

		File f = new File(fullPath);
		
		// File found
		if (f.isFile()) {
			FileInputStream fis;
			BufferedInputStream bis;


			try {
				fis = new FileInputStream(fullPath);

				bis = new BufferedInputStream(fis);
				byte[] bRes = new byte[1024];
				int c = bis.read(bRes);

				while (c != -1) {

					List<Thread> threads = new ArrayList<Thread>();

					DownloadParallel myThread = new DownloadParallel(os, bRes);
					myThread.start();
					threads.add(myThread);

					for (Thread thread : threads) {
						try {
							thread.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					c = bis.read(bRes);
				}

				bis.close();
				fis.close();
			} catch (FileNotFoundException e) {
				try {
					os.write((new String("File Not Found")).getBytes());
					os.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (f.exists()) {
			// Check index.html
			File f2 = new File(rootDir + requestPath + "\\index.html");
			if (f2.exists()) {
				FileInputStream fis;
				BufferedInputStream bis;
				try {
					fullPath += "\\index.html";
					// System.out.println(fullPath);
					fis = new FileInputStream(fullPath);

					bis = new BufferedInputStream(fis);
					byte[] bRes = new byte[1024];
					int c = bis.read(bRes);

					while (c != -1) {
						os.write((bRes));
						os.flush();
						c = bis.read(bRes);
					}

					bis.close();
					fis.close();
				} catch (FileNotFoundException e) {
					try {
						os.write((new String("File Not Found")).getBytes());
						os.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// No index.html
			else {
				try {
					os.write(Content.getDirPage(Content.getListDir(f)).getBytes());
					os.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// Not Found
		else {
			try {
				os.write((new String("File Not Found")).getBytes());
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
	}

	
	public void parseKeepAlive(String reqHeader) {
		String pattern = "Connection: ([^\\n]+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(reqHeader);
		if (m.find()) {
			reqConnection = m.group(1);
		}
	}

	public void setStatus() {
		String fullPath = rootDir + requestPath;
		File f = new File(fullPath);

		if (f.isFile()) {
			status = "200 OK";
		} else if (f.exists()) {
			status = "200 OK";
		} else {
			status = "404 Not Found";
		}
	}

	public void getServerConfig() {
		//
		FileInputStream fis;
		BufferedInputStream bis;
		String tmp = "";
		try {
			fis = new FileInputStream("config.txt");
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
			rootDir = m.group(1);
			rootDir = rootDir.substring(0, rootDir.length() - 1);
			ip = m.group(2);
			ip = ip.substring(0, ip.length() - 1);
			port = m.group(3);
		}
	}
}
