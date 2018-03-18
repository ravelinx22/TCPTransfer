package TCPServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
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
	
	// TODO: Size setup
	private final static int BUFFER_SIZE = 16;
	private final static int MESSAGE_SIZE = 8000;
	
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
        	// Read requested size
    		Scanner sc = new Scanner(clientSock.getInputStream());
    		String size = sc.nextLine();
    		System.out.println(size);
    		
    		File file = null;
    		if(size.equals(LARGE_FILE))
    			file = new File("./data/large.zip");
    		else if(size.equals(MEDIUM_FILE))
    			file = new File("./data/medium.zip");
    		else if(size.equals(SMALL_FILE))
    			file = new File("./data/small.zip");
            
    		// Send real file size
            PrintStream p = new PrintStream(clientSock.getOutputStream());
            int realSize = getRealFileSize(file);
            p.println(realSize);
            
        	// Read requested size
    		String confirmation = sc.nextLine();
    		System.out.println(confirmation);
    		
            byte[] bytes = new byte[BUFFER_SIZE * 1024];    	
            
            InputStream in = new FileInputStream(file);
            OutputStream out = clientSock.getOutputStream();            
            
            System.out.println("Se empezo a enviar archivo");
            long initTime = System.currentTimeMillis();

            int accumulativeSize = 0;
    		int count;
    		while ((count = in.read(bytes, 0, MESSAGE_SIZE)) > 0) {
    			accumulativeSize += count;
    		 out.write(bytes, 0, count);
    		}
    		long totalTime = System.currentTimeMillis() - initTime;
    		System.out.println("Se termino de enviar archivo");
    		System.out.println("Enviar el archivo tomo " + totalTime + " ms y se enviaron "  + accumulativeSize);
    		
    		sc.close();
            out.close();
            in.close();
            clientSock.close();
        } catch(Exception e) {
        	System.out.println("Error");
        }
	}
	
	public int getRealFileSize(File file) throws Exception {
        InputStream in = new FileInputStream(file);
        byte[] bytes = new byte[BUFFER_SIZE * 1024];

        int count = 0;
		int acumulativeSize = 0;
		while ((count = in.read(bytes, 0, MESSAGE_SIZE)) > 0) {
   		 	acumulativeSize += count;
   		}
		
		return acumulativeSize;
	}
	
	/* Main */
	public static void main(String[] args) {
		TCPServer fs = new TCPServer(Integer.parseInt(args[0]));
		fs.start();
	}
	

}