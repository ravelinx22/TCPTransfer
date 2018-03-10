package TCPClient;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import TCPServer.TCPServer;
import java.io.DataOutputStream;
import java.io.FileInputStream;

public class TCPClient extends Thread {
	
	/* Constants */
	// 3MB
	private final static String LARGE_FILE = "LARGE_FILE";
	// 15MB
	private final static String MEDIUM_FILE = "MEDIUM_FILE";
	// 45MB
	private final static String SMALL_FILE = "SMALL_FILE";
	
	/* Attributes */
	private Socket s;
	
	/* Constructors */
	public TCPClient(String host, int port) {
		try {
			s = new Socket(host, port);
			saveFile(s);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/* Methods */
	private void saveFile(Socket clientSock) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        in = clientSock.getInputStream();
        out = new FileOutputStream("./data/copy.zip");

        // TODO: Change buffer size
        byte[] bytes = new byte[16*1024];
       
        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        out.close();
        in.close();
        clientSock.close();
	}
	
	/* Main */
	public static void main(String[] args) {
		// TODO: Cambiar host
		TCPClient fc = new TCPClient("localhost", 1988);
	}
}
