package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Client {
	// Ҫ���ӵĵ�ַ
	private String host = "127.0.0.1";
	private int portForSending = 55533;
	private int portForRecving = 55532;
	private Socket sendingSocket = null;
	private Socket recvingSocket = null;
	// �û����͵�ַ
	private String userName;
	private String password;
	public final Map<String, Location> carLocation = Collections.synchronizedMap(new HashMap<>());

	private String readPackage(int n, BufferedReader br) {
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

	public void logOff() {
		try {
			if (sendingSocket != null) {
				BufferedReader br = new BufferedReader(new InputStreamReader(sendingSocket.getInputStream(), "utf-8"));
				;
				while (true) {
					sendingSocket.getOutputStream()
							.write(("HOST:" + userName + "\r\n" + "DISCONNECT\r\n").getBytes("utf-8"));
					if (br.readLine().equals("GET DISCONNECT")) {
						sendingSocket.getOutputStream().write("ALREADY DISCONNECT\r\n".getBytes("utf-8"));
						break;
					}
				}

				sendingSocket.close();
			}
			if (recvingSocket != null)
				recvingSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getPicture(String car) {
		try {
			sendingSocket.getOutputStream()
					.write(("HOST:" + userName + "\r\n" + "GETPICTURE:" + car).getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void moveCar(String car, String direction, String length) {
		try {
			sendingSocket.getOutputStream()
					.write(("HOST:" + userName + "\r\n" + "MOVE:" + car + " " + direction + " " + length + "\r\n")
							.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopCar(String car) {
		try {
			sendingSocket.getOutputStream().write(("HOST:" + userName + "\r\n" + "CEASE:" + car).getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String logOn(String userName, String password) {
		this.userName = userName;
		this.password = password;
		try {
			sendingSocket = new Socket(host, portForSending);

			System.out.println("Client init");
			// ��һ������
			String message = "HOST:" + userName + "\r\n" + "CODE:" + password + "\r\n" + "FUCTION:REQUIRE\r\n";
			System.out.println("First handshaking sent");
			sendingSocket.getOutputStream().write(message.getBytes("UTF-8"));

			// �ڶ�������
			System.out.println("Second handshaking get");
			BufferedReader br = new BufferedReader(new InputStreamReader(sendingSocket.getInputStream(), "utf-8"));
			String second = this.readPackage(2, br);
			int location = second.indexOf("FUCTION:PERMISSION");
			if (location == -1) {
				sendingSocket.close();
				return second.split("\r\n")[1];// ��õڶ������е���ʾ��Ϣ
			}

			// ����������
			System.out.println("Third handshaking sent");
			sendingSocket.getOutputStream()
					.write(("HOST:" + userName + "\r\n" + "FUCTION:BUILD\r\n").getBytes("utf-8"));
			// �������

			// ����socket����
			recvingSocket = new Socket(host, portForRecving);
			message = "HOST:" + userName + "\r\n" + "CODE:" + password + "\r\n" + "FUCTION:REQUIRE\r\n";
			recvingSocket.getOutputStream().write(message.getBytes("UTF-8"));
			System.out.println("First handshaking sent");

			System.out.println("Second handshaking get");
			br = new BufferedReader(new InputStreamReader(recvingSocket.getInputStream(), "utf-8"));
			second = this.readPackage(2, br);
			location = second.indexOf("FUCTION:PERMISSION");
			if (location == -1) {
				recvingSocket.close();
				return second.split("\r\n")[1];// ��õڶ������е���ʾ��Ϣ
			}

			System.out.println("Third handshaking sent");
			recvingSocket.getOutputStream()
					.write(("HOST:" + userName + "\r\n" + "FUCTION:BUILD\r\n").getBytes("utf-8"));
			new Thread(new ReadInfo(this)).start();//�ڽ�����ʱ�����̣߳����϶�ȡ����
			return "Connection built";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Data transfer wrongly";
	}
	class ReadInfo implements Runnable{
		Client client;
		ReadInfo(Client client){
			this.client = client;
		}
		@Override
		public void run() {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(client.recvingSocket.getInputStream()));
				while(!client.recvingSocket.isClosed()) {
					String command = this.readPackage(2, br);
					String host = this.getFieldValue(command, "HOST");
					if(command.indexOf("LOCATION")!=-1) {
						String location = this.getFieldValue(command, "LOCATION");
						String[] sarray = location.split("\\s");
						client.carLocation.put(host, new Location(Double.parseDouble(sarray[1]),
								Double.parseDouble(sarray[2])));
						//�˴�ֻʹ����һ�������������ܱ�֤ԭ����
					}
					else if(command.indexOf("PICTURE")!=-1) {
						//TODO
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		private String getFieldValue(String s, String field) {
			int start = s.indexOf(field + ":") + field.length() + 1;
			int end = s.indexOf("\r\n", start);
			return s.substring(start, end);
		}
		private String readPackage(int n, BufferedReader br) {
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
	}
}

