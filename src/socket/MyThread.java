package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class MyThread {
	protected Socket socket;

	MyThread() {

	}

	MyThread(Socket socket) {
		this.socket = socket;
	}

	protected String getFieldValue(String s, String field) {
		int start = s.indexOf(field + ":") + field.length() + 1;
		int end = s.indexOf("\r\n", start);
		return s.substring(start, end);
	}

	protected String readPackage(int n, BufferedReader br) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			try {
				sb.append(br.readLine() + "\r\n");
			} catch (IOException e) {
				System.out.println("Read Packet Wrongly");
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	protected void handShake(String s, BufferedReader br, String host, String password) throws Exception{//���쳣�׵�run������
			String rightPassword = Server.userMap.get(host);
			if (rightPassword == null) {
				socket.getOutputStream().write("Invalid username".getBytes("utf-8"));
				throw new Exception("Invalid user name");
			}
			if (!Server.userMap.get(host).equals(password)) {
				socket.getOutputStream().write("Invalid password".getBytes("utf-8"));
				throw new Exception("Wrong password");
			}
			System.out.println("user confirtmed");

			// second handshake
			socket.getOutputStream().write(("HOST:" + host + "\r\n" + "FUCTION:PERMISSION\r\n").getBytes("utf-8"));
			System.out.println("PERSION sent");

			// thrid handshake
			s = readPackage(2, br);
			String confirmHost = getFieldValue(s, "HOST");
			if (!(confirmHost.equals(host) && s.indexOf("BUILD") != -1)) {
				socket.getOutputStream().write("different user".getBytes("utf-8"));
				throw new Exception("Wrong confirming");
			}
	}
}