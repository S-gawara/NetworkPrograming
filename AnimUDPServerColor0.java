import java.io.*;
import java.net.*;

public class AnimUDPServerColor0 {
    public static void main(String args[]){
        try{
            BufferedInputStream biStream;
//	    BufferedInputStream biStream2;
            int port = 8001; // ポートは 8001
            InetAddress clientAddress; // クライアントの IP アドレス
            int clientPort; // クライアントのポート番号
            String fname;
	    int count;

	    // ファイル情報のbuf
	    byte fbuf[] = new byte[20];

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
		// Requestからファイル名の取得
		fname = new String(req);
                clientAddress = receivePacket.getAddress();
                clientPort = receivePacket.getPort();
                socket.send(receivePacket); // Echo back（Ack）

                // sendPacket の IPaddress，PortNo，データ長設定
                sendPacket.setAddress(clientAddress);
                sendPacket.setPort(clientPort);
                sendPacket.setLength(1200);

//                biStream2 = new BufferedInputStream(new FileInputStream(fname));

		/*
		// ファイル情報
		biStream2.read(fbuf, 0, 12);
		int v = ((fbuf[0]&0xff) << 8) + (fbuf[1] & 0xff);
		if(v == 0xfffe){ // With header
		System.out.println("With header");
		// Color or not
		int c = ((fbuf[2]&0xff) << 8) + (fbuf[3] & 0xff);
		if(c == 1)      System.out.println("Gray scale");
		else if(c == 3) System.out.println("Color(3)");
		else            System.out.println("Unknown format");
		// Max Value
		int m = ((fbuf[4]&0xff) << 8) + (fbuf[5] & 0xff);
		System.out.println("Max Value = " + m);
		// width
		int w = ((fbuf[6]&0xff) << 8) + (fbuf[7] & 0xff);
		System.out.println("Width     = " + w);
		// height
		int h = ((fbuf[8]&0xff) << 8) + (fbuf[9] & 0xff);
		System.out.println("Height    = " + h);
		// frame #
		int f = ((fbuf[10]&0xff) << 8) + (fbuf[11] & 0xff);
		System.out.println("Frame #   = " + f);
		} else {
			System.out.println("Without header");
		}
		*/

		biStream = new BufferedInputStream(new FileInputStream(fname));

                for(;;){ // 永久ループ
		    for(int i = 0; i < 48; i++){
                        biStream.read(buf, 0, 1200); // ファイルから読み込み
                        socket.send(sendPacket); // クライアントに送信
                        socket.receive(receivePacket); // Ack の受信
                        if(buf[0] < 0) break; // 終了判定
                    }
		    if(buf[0] < 0) break;
	            socket.receive(receivePacket);
		    // クライアントから送られてきたcount数
		    count = new Int(req);
		    // Sytem.out.println("count: " + count);
		}    
                biStream.close();
            }
            // socket.close();
        }
        catch(Exception e){
            System.out.println("Exception : " + e);
        }
    }
}
