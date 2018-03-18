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
import java.util.Scanner;

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
				System.out.println("Se conecto un cliente");
				// TODO: Change file
//				sendFile("./data/45MB.zip", clientSock);
				(new Atender(clientSock)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	class Atender extends Thread{
		Socket clientSock;
		public Atender(Socket clientSock) {
			this.clientSock = clientSock;
		}
		public void run () {
			try {
				sendFile(clientSock);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* Methods */
	public void sendFile(Socket clientSock) throws IOException {
        try {
    		Scanner sc = new Scanner(clientSock.getInputStream());
    		String size = sc.nextLine();
    		System.out.println(size);
    		
    		File file = null;
    		if(size.equals(LARGE_FILE))
    			file = new File("./data/45MB.zip");
    		else if(size.equals(MEDIUM_FILE))
    			file = new File("./data/15MB.zip");
    		else if(size.equals(SMALL_FILE))
    			file = new File("./data/3MB.zip");
            
            // TODO: Buffer size
            byte[] bytes = new byte[16 * 1024];
            InputStream in = new FileInputStream(file);
            OutputStream out = clientSock.getOutputStream();

            System.out.println("Se empezo a enviar archivo");
            long initTime = System.currentTimeMillis();
    		// TODO: Message size
    		int messageSize = 8000;
    		int count;
    		while ((count = in.read(bytes, 0, messageSize)) > 0) {
    		 out.write(bytes, 0, count);
    		}
    		long totalTime = System.currentTimeMillis() - initTime;
    		System.out.println("Se termino de enviar archivo");
    		System.out.println("Enviar el archivo tomo " + totalTime + " ms");
    		
    		sc.close();
            out.close();
            in.close();
            clientSock.close();
        } catch(Exception e) {
        	System.out.println("Error");
        }
	}
	
	/* Main */
	public static void main(String[] args) {
		TCPServer fs = new TCPServer(1988);
		fs.start();
	}
	

}