package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class RecvingThread extends MyThread implements Runnable {
	RecvingThread(Socket socket) {
		super(socket);
	}

	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			/*
			 * byte[] bytes = new byte[1024]; int a = socket.getInputStream().read(bytes);
			 * String b = new String(bytes,0,a,"utf-8");
			 */


			String host = this.handShaking(socket);
			String s;
			if(isUser) {
			while (true) {
				s = readPackage(2, br);
				if (s.indexOf("MOVE") != -1 || s.indexOf("GETPICTURE") != -1 || s.indexOf("CEASE") != -1
						|| s.indexOf("STOPPICTURE") != -1) {
					this.storeCarInfo(s);
				}
				if (s.indexOf("DISCONNECT") != -1) {
					socket.getOutputStream().write("GET DISCONNECT\r\n".getBytes("utf-8"));

					byte[] bytes = new byte[1024];
					int len = socket.getInputStream().read(bytes);
					if (new String(bytes, 0, len, "utf-8").equals("ALREADY DISCONNECT")) {
						System.out.println(host + "log off");
						if(isUser) {
							synchronized (Server.clientState.get(host)) {
								Server.clientState.get(host).online = false;
							}
						}
						else {
							synchronized (Server.carState.get(host)) {
								Server.carState.get(host).online = false;
							}
						}
						socket.close();
						break;
					}
				}
			}
			}
			else {
				while (true) {
					s = readPackage(2, br);
					if (s.indexOf("LOCATION") != -1 || s.indexOf("PICTURE") != -1) {
						this.storeUserInfo(s);
					}

					if (s.indexOf("DISCONNECT") != -1) {
						socket.getOutputStream().write("GET DISCONNECT\r\n".getBytes("utf-8"));

						byte[] bytes = new byte[1024];
						int len = socket.getInputStream().read(bytes);
						if (new String(bytes, 0, len, "utf-8").equals("ALREADY DISCONNECT")) {
							System.out.println(host + "log off");
							synchronized (Server.carState.get(host)) {
								Server.carState.get(host).online = false;
							}
							socket.close();
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private void storeCarInfo(String s) {
		String command = s.split("\r\n")[1];
		String carName = command.split("\\s")[0];
		synchronized (Server.carState.get(carName)) {
			if (Server.carState.get(carName).online) {
				Server.carState.get(carName).info.add(s);
				Server.carState.get(carName).notifyAll();
			} else {
				// TODO ��UI�Ϸ�����Ϣ
			}
		}
	}
	private void storeUserInfo(String s) {
		String command = s.split("\r\n")[1];//�ڶ���
		int start = s.indexOf(":") + 1;//��ð�ŷָ�
		String userName = command.split("\\s")[0].substring(start);
		synchronized (Server.clientState.get(userName)) {
			if (Server.clientState.get(userName).online) {
				Server.clientState.get(userName).info.add(s);
				Server.clientState.get(userName).notifyAll();
			} else {
				// TODO
			}
		}
	}

}