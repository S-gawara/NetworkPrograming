import java.io.*;
import java.net.*;

public class dumpHeader {
    public static void main(String args[]){
        BufferedInputStream biStream;
        byte buf[] = new byte[20];
	try{
	    biStream = new BufferedInputStream(new FileInputStream(args[0]));
	    biStream.read(buf, 0, 12);
	    int v = ((buf[0]&0xff) << 8) + (buf[1] & 0xff);
	    if(v == 0xfffe){ // With header
	        System.out.println("With header");
		// Color or not
		int c = ((buf[2]&0xff) << 8) + (buf[3] & 0xff);	
		if(c == 1)      System.out.println("Gray scale");
	        else if(c == 3) System.out.println("Color(3)");
		else            System.out.println("Unknown format");
		// Max Value
		int m = ((buf[4]&0xff) << 8) + (buf[5] & 0xff);
		System.out.println("Max Value = " + m);
		// width
		int w = ((buf[6]&0xff) << 8) + (buf[7] & 0xff);
		System.out.println("Width     = " + w);
		// height
		int h = ((buf[8]&0xff) << 8) + (buf[9] & 0xff);
		System.out.println("Height    = " + h);
		// frame #
		int f = ((buf[10]&0xff) << 8) + (buf[11] & 0xff);
		System.out.println("Frame #   = " + f);
	        } else {
			System.out.println("Without header");
		}
	}catch(Exception e){
		System.out.println("Exception : " + e);
	}
    }
}
