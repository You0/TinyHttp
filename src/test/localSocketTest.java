package test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

public class localSocketTest {

	public static void main(String[] args) throws IOException {
		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				
//				try {
//					ServerSocket socket = new ServerSocket(8080);
//					Socket client = socket.accept();
//					System.out.println(client.getInputStream().read());
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}).start();
		try{
			URL url = new URL("http://115.159.159.65:8080");
			SocketAddress address = new InetSocketAddress(url.getHost(), 8080);
			//System.out.println(url.getHost());
			Socket socket = new Socket();
			socket.connect(address, 5000);
			OutputStream s = socket.getOutputStream();
			s.write(new byte[]{'b'});
			//socket.shutdownOutput();
			System.out.println("Êä³öÁ÷");
			OutputStream stream = socket.getOutputStream();
			
			System.out.println(stream==s);
			
			socket.close();
			System.gc();
			socket.getInputStream();
			System.out.println(stream==s);
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		

		
	}
	
}
