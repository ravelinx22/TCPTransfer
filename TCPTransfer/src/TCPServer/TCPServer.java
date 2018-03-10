package TCPServer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import TCPClient.TCPClient;

import java.io.DataOutputStream;
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
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Thread related */
	public void run() {
		while (true) {
			try {
				Socket clientSock = ss.accept();
				sendFile("./data/cat.jpg", clientSock);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* Methods */
	public void sendFile(String file, Socket clientSock) throws IOException {
		DataOutputStream dos = new DataOutputStream(clientSock.getOutputStream());
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[4096];
		
		while (fis.read(buffer) > 0) {
			dos.write(buffer);
		}
		
		fis.close();
		dos.close();
	}
	
	/* Main */
	public static void main(String[] args) {
		TCPServer fs = new TCPServer(1988);
		fs.start();
	}
	

}