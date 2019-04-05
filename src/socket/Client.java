package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
		public static void main(String[] args) {
			Client c = new Client();
			c.mainFuction("JumpingBear", "199806");
		}
		private String readPackage(int n,BufferedReader br) {
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<n;i++) {
				try {
					sb.append(br.readLine()+"\r\n");
				} catch (IOException e) {
					System.out.println("Read Packet Wrongly");
					e.printStackTrace();
				}
			}
			return sb.toString();
		}
		public void mainFuction(String userName, String password){
		    // Ҫ���ӵķ����IP��ַ�Ͷ˿�
		    String host = "127.0.0.1"; 
		    int port = 55533;
		    // �����˽�������
		    Socket socket;
			try {
				socket = new Socket(host, port);
				// �������Ӻ��������
				System.out.println("Client Built");
			    OutputStream outputStream = socket.getOutputStream();
			    String message="HOST:"+userName+"\r\n" + 
			    		"CODE:"+password+"\r\n" + "FUCTION:REQUIRE\r\n";
			    System.out.println("Sent");
			    socket.getOutputStream().write(message.getBytes("UTF-8"));
			    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
			    
			    String second = this.readPackage(2,br);
			    System.out.println("Get Permision");
			    int location = second.indexOf("FUCTION:PERMISSION");
			    if(location==-1) {
			    	throw new Exception("User authentication failed");
			    }
			    socket.getOutputStream().write(("HOST:"+userName+"\r\n" + 
			    		"FUCTION:BUILD\r\n").getBytes("utf-8"));
			    socket.getOutputStream().write(("HOST:"+userName+"\r\n"+"CONNECTION "));
			    socket.shutdownOutput();
			    socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		    
		  }
		private String getField(String s,String field) {
			int start = s.indexOf(field+":")+field.length()+1;
			int end = s.indexOf("\r\n",start);
			return s.substring(start, end);
		}
}
