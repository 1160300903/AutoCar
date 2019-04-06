package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Random;

public class Car {
	private String host = "127.0.0.1";
	private int portForSending = 55533;
	private int portForRecving = 55532;
	private Socket sendingSocket = null;
	private Socket recvingSocket = null;
	private boolean imageModel = false;// ����ָ������С���Ƿ���Ҫ����ͼ����û�
	private String imageUser = "";// ����ָ��ͼ�񴫸�˭
	private Location location;
	// �û����͵�ַ
	private String carName;
	private String password;
	public static void main(String[] args) {
		//�õ�С����ʱ��˴����Ըĳ���args����С�����ֺ�����
		try {
			Car car = new Car("car1", "abc");
			new Thread(car.new CarRecvingInfo(car)).start();
			car.sendingInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public Car(String userName, String password) throws Exception {
		this.carName = userName;
		this.password = password;
		try {
			sendingSocket = new Socket(host, portForSending);

			System.out.println("Car init");
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
				System.out.println(second.split("\r\n")[1]);
				throw new Exception(second.split("\r\n")[1]);
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
				System.out.println(second.split("\r\n")[1]);
				throw new Exception(second.split("\r\n")[1]);
			}

			System.out.println("Third handshaking sent");
			recvingSocket.getOutputStream()
					.write(("HOST:" + userName + "\r\n" + "FUCTION:BUILD\r\n").getBytes("utf-8"));
			System.out.println("Connection built");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void refreshLocation() {
		Random r = new Random();
		if (this.location == null) {
			this.location = new Location(100 + r.nextDouble() * 100, r.nextDouble() * 100 + 100);
		} else {
			location.x = location.x + r.nextDouble() * 20;
			location.y = location.y + r.nextDouble() * 20;
		}
	}

	public void sendingInfo() {
		while (!this.sendingSocket.isClosed()) {
			try {
				refreshLocation();
				this.sendingSocket.getOutputStream().write(("HOST:" + this.carName + "\r\nLOCATION:ALL "
						+ this.location.x + " " + this.location.y + "\r\n").getBytes("utf-8"));
				Thread.sleep(500);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
	
	private void move() {
		
	}
	private void cease() {
		
	}
	class CarRecvingInfo implements Runnable {
		Car c;

		CarRecvingInfo(Car c) {
			this.c = c;
		}

		@Override
		public void run() {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(c.recvingSocket.getInputStream()));
				while (!c.recvingSocket.isClosed()) {
					String command = this.readPackage(2, br);
					String host = this.getFieldValue(command, "HOST");
					if (command.indexOf("MOVE") != -1) {
						c.move();
						//TODO
					} else if (command.indexOf("CEASE") != -1) {
						c.cease();
						// TODO
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
