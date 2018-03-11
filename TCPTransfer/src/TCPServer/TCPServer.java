package TCPServer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import TCPClient.TCPClient;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;


public class TCPServer  extends Thread {
	
	/* Constants */
	// 3MB
	private final static String LARGE_FILE = "LARGE_FILE";
	// 15MB
	private final static String MEDIUM_FILE = "MEDIUM_FILE";
	// 45MB
	private final static String SMALL_FILE = "SMALL_FILE";
	
	/* Attributes */
	private ServerSocket ss;
	
	/* Constructor */
	public TCPServer(int port) {
		try {
			// TODO: Set maximum number of connections
			ss = new ServerSocket(port,10);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Thread related */
	public void run() {
		while (true) {
			try {
				Socket clientSock = ss.accept();
				// TODO: Change file
				sendFile("./data/45MB.zip", clientSock);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* Methods */
	public void sendFile(String fileUrl, Socket clientSock) throws IOException {		
        File file = new File(fileUrl);
        
        // TODO: Buffer size
        byte[] bytes = new byte[16 * 1024];
        InputStream in = new FileInputStream(file);
        OutputStream out = clientSock.getOutputStream();

		// TODO: Message size
		int messageSize = 8000;
		int count;
		while ((count = in.read(bytes, 0, messageSize)) > 0) {
		 out.write(bytes, 0, count);
		}

        out.close();
        in.close();
        clientSock.close();
	}
	
	/* Main */
	public static void main(String[] args) {
		TCPServer fs = new TCPServer(1988);
		fs.start();
	}
	

}