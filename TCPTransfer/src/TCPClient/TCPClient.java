package TCPClient;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
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
			//saveFile(s);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/* Methods */
	private void saveFile(Socket clientSock, String size) throws IOException {
        InputStream in = null;
        OutputStream fOut = null;
        PrintStream p = new PrintStream(clientSock.getOutputStream());
        p.println(size);
        
        in = clientSock.getInputStream();
        
        if(size.equals(LARGE_FILE))
        	fOut = new FileOutputStream("./data/copy45MB.zip");
        else if(size.equals(MEDIUM_FILE))
        	fOut = new FileOutputStream("./data/copy15MB.zip");
        else if(size.equals(SMALL_FILE))
        	fOut = new FileOutputStream("./data/copy3MB.zip");
        // TODO: Change buffer size
        byte[] bytes = new byte[16*1024];
       
        System.out.println("Starting to read file");
        int count;
        int i = 0;
        long initTime = System.currentTimeMillis();
        while ((count = in.read(bytes)) > 0) {
            fOut.write(bytes, 0, count);
            System.out.println(count+ " " + i++);
        }
        long totalTime = System.currentTimeMillis() - initTime;
        System.out.println("Ended reading file");
        System.out.println("Reading the file took " + totalTime + " ms");
        
        p.close();
        fOut.close();
        in.close();
        clientSock.close();
	}
	
	public void sendFileSize(String size) throws IOException {
		saveFile(s, size);
	}
	
	public void stopConnection() {
		try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean socketClosed() {
		return s.isClosed();
	}

}
