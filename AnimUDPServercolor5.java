import java.io.*;
import java.net.*;

public class AnimUDPServercolor5 extends Thread {
	public static void main(String args[]){
		private Socket s;

		public AnimUDPServercolor5(Socket s){
			this.s = s;
		}

		public void run(){
		try{
			BufferedInputStream biStream;
			int port = 8001; // ポートは 8001
			InetAddress clientAddress; // クライアントの IP アドレス
			int clientPort; // クライアントのポート番号
			String fname;
			int sltime;

			// 送信用 DatagramPacket
			byte buf[] = new byte[1200];
			DatagramPacket sendPacket = new DatagramPacket(buf, buf.length);

			// 受信用 DatagramPacket
			byte req[] = new byte[32];
			DatagramPacket receivePacket = new DatagramPacket(req, req.length);

			// ソケットの作成 (Port 8001)
			DatagramSocket socket = new DatagramSocket(port);
			System.out.println("Running...");


			while(true){
				socket.receive(receivePacket); // Request の受信
				fname = new String(req, 0, receivePacket.getLength());
				clientAddress = receivePacket.getAddress();
				clientPort = receivePacket.getPort();

				// 送信用socketの作成
				DatagramSocket socket2 = new DatagramSocket();
				socket2.send(receivePacket); // Echo back（Ack）

				// sendPacket の IPaddress，PortNo，データ長設定
				sendPacket.setAddress(clientAddress);
				sendPacket.setPort(clientPort);
				sendPacket.setLength(1200);


				biStream = new BufferedInputStream(new FileInputStream(fname));

				for(;;){ // 永久ループ
					for(int i = 0; i < 48; i++){
						biStream.read(buf, 0, 1200); // ファイルから読み込み
						socket2.send(sendPacket); // クライアントに送信
						socket2.receive(receivePacket); // Ack の受信

						// ackの受信
						String time = new String(req, 0, receivePacket.getLength());
						sltime = Integer.parseInt(time);
						Thread.sleep(sltime);

						if(buf[0] < 0) break; // 終了判定
					}
					if(buf[0] < 0) break;
	            			socket2.receive(receivePacket);
				}
				biStream.close();
				socket2.close();
			}
			// socket.close();
		}
		catch(Exception e){
			System.out.println("Exception : " + e);
		}
	}
}
