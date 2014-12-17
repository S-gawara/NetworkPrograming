import java.io.*;
import java.net.*;

public class AnimUDPServerColor0 {
    public static void main(String args[]){
        try{
            BufferedInputStream biStream;
            int port = 8001; // ポートは 8001
            InetAddress clientAddress; // クライアントの IP アドレス
            int clientPort; // クライアントのポート番号
            String fname;

	    // ファイル名はクライアントで指定
	    // if(args.length >= 1){
            //    fname = args[0]; // コマンドラインで指定されている場合
            // } else {
            //    fname = "bane.raw"; // 指定されていない場合は bane.raw
            // }

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
