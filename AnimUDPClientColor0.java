import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

class AnimUDPClientColor0 {
public static void main(String[] args){
    AppFrame3 f = new AppFrame3(args[0], args[1]);
    f.setSize(640,480);
    f.addWindowListener(new WindowAdapter(){
    @Override public void windowClosing(WindowEvent e){
        System.exit(0);
    }});
        f.setVisible(true);
    }
}

class AppFrame3 extends Frame {
    String hostname;
    String fname;
    ImageSocket2 imgsock = null;
    AppFrame3(String hostname, String fname){
        this.hostname = hostname;
	this.fname = fname;
    }
    @Override public void update(Graphics g){
        paint(g);
    }
    @Override public void paint(Graphics g){
    if(imgsock != null){
        Image img = imgsock.loadNextFrame();
        if(img != null)
        g.drawImage(img, 10, 50, 480, 360, this);
        } else {
            imgsock = new ImageSocket2(hostname, fname);
        }
        repaint(1);
    }
}

class ImageSocket2 {
    DatagramSocket socket; // 通信用ソケット
    InetAddress serverAddress; // サーバアドレス保管
    BufferedImage bImage; // イメージ
    byte buf[]; // バッファ
    int port = 8001;
    byte ack[]; // Ack
    String fname;
    int count = 0;
    int w, h;
    byte bufw[];

    // SetDateを用いる
    DatagramPacket receivePacket, receivePacket2;
    DatagramPacket ackPacket;

    boolean fin = false; /**********/ // 終了フラグ
    ImageSocket2(String hostname, String fname){
        buf = new byte[160*120*3];
        bImage=new BufferedImage(160, 120, BufferedImage.TYPE_3BYTE_BGR);

        byte request[] = fname.getBytes();
        ack = "Ack".getBytes(); // Ack の準備

        try {
            socket = new DatagramSocket(); // ソケットの作成
            // 送信データ用 DatagramPacket の作成
            serverAddress = InetAddress.getByName(hostname);

            DatagramPacket sendPacket = new DatagramPacket(request,request.length, serverAddress, port);
            ackPacket = new DatagramPacket(ack, ack.length,serverAddress, port); // Ack の作成
	    receivePacket = new DatagramPacket(buf, 0, 1200);
	    receivePacket2 = new DatagramPacket(bufw, 0, 1200);

            socket.setSoTimeout(3000); // タイムアウトの設定 (3 秒)
            socket.send(sendPacket); // REQUEST の送信
            socket.receive(receivePacket); // 応答の受信
            receivePacket.setLength(1200); // 受信可能サイズの再設定

	    System.out.println("get!");

	    // wの受信
	    socket.receive(receivePacket2);
	    String with = new String(bufw, 0, receivePacket2.getLength());
	    w = Integer.parseInt(with);
	    System.out.println("w: " + w);

        }
        catch(Exception e){
            System.out.println("Exception : " + e);
        }
    }
    Image loadNextFrame(){
        if(fin) return null; /**********/
        try {
            int x,y,pixel,r,g,b;
	    int maxsize = 160 * 120 * 3;
	    int offset = 0;

	    for(;;){
                receivePacket.setData(buf, offset, maxsize - offset);
		socket.receive(receivePacket);
		socket.send(ackPacket);
		if(buf[0] < 0) break;
		offset += receivePacket.getLength();
		if(offset >= maxsize) break;
	    }

	    if(buf[0] < 0){
		    socket.close();
		    System.out.println("Done");
		    fin = true;
		    return null;
            }

	    // イメージの作成
            for(y = 0; y < 120; y++){
                for(x = 0; x < 160; x++){
                    r = (int)buf[y * 160 * 3 + x * 3 + 0] * 2;
                    g = (int)buf[y * 160 * 3 + x * 3 + 1] * 2;
                    b = (int)buf[y * 160 * 3 + x * 3 + 2] * 2;
                    pixel = new Color(r,g,b).getRGB();
                    bImage.setRGB(x, y, pixel);
                }
            }
	    // フレーム数を送信
	    count++;
	    ack = Integer.toString(count).getBytes();
	    ackPacket.setData(ack, 0, ack.length);
            socket.send(ackPacket); // receivePacket3 の Ack の送信 (描画後)
        }
        catch(Exception e){
            System.err.println("Exception2 : " + e);
        }
        return bImage;
    }
}
