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

	public static String requestMethod, requestPath, rootDir, ip, port, status;
	public static int pageType;

	public static void main(String[] args) {
		Server.getServerConfig();

		try {
			System.out.println(Integer.parseInt(port));
			ServerSocket server = new ServerSocket(Integer.parseInt(port));

			while (true) {
				Socket client = server.accept();

				BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

				String request = "";
				String input = br.readLine();
				request += input + "\n";

				while (!input.isEmpty()) {
					input = br.readLine();
					request += input + "\n";
				}

				// System.out.println("req:\n" + request + "\nline:" + input);
				Request.getRequestPath(request);
				Request.getRequestMethod(request);
				Server.getHost(request);

				pageType = 0;
				String fileContent = Content.getFileContent();
				String result = Content.getResponse(fileContent);

				bw.write(result);
				bw.flush();

				client.close();
				Server.getServerConfig();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
