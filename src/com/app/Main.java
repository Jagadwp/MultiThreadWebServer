package com.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.services.*;

public class Main {

	public static String reqMethod;
	public static String reqPath;
	public static String rootDir;
	public static String ipAddress;
	public static String port;
	public static String status;
	public static int dirIndex;

	public static void main(String[] args) {
		Server.getServerConfig();

		try {
			System.out.println(Integer.parseInt(port));
			ServerSocket server = new ServerSocket(Integer.parseInt(port));

			while (true) {
				Socket client = server.accept();

				BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

				String req = "";
				String line = br.readLine();
				req += line + "\n";

				while (!line.isEmpty()) {
					line = br.readLine();
					req += line + "\n";
				}

				// System.out.println(req + "\nline:" + line);
				Request.getReqPath(req);
				Request.getReqMethod(req);
				Server.getHost(req);

				dirIndex = 0;
				String fileContent = Content.getFileContent();
				String res = Content.generateResponse(fileContent);

				bw.write(res);
				bw.flush();

				client.close();
				Server.getServerConfig();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
