package TCPClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient extends Thread {
	
	/* Constants */
	// 3MB
	private final static String LARGE_FILE = "LARGE_FILE";
	// 15MB
	private final static String MEDIUM_FILE = "MEDIUM_FILE";
	// 45MB
	private final static String SMALL_FILE = "SMALL_FILE";
	
	// TODO: Size setup
	private final static int BUFFER_SIZE = 16;
	
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
        // Send request size file
		InputStream in = null;
        OutputStream fOut = null;
        PrintStream p = new PrintStream(clientSock.getOutputStream());
        p.println(size);
        
        // Send real file size
		Scanner sc = new Scanner(clientSock.getInputStream());
		int realSize = Integer.parseInt(sc.nextLine());
		
		// Send confirmation
        p.println("RECEIVED_SIZE");
		
        in = clientSock.getInputStream();
        
        
        if(size.equals(LARGE_FILE))
        	fOut = new FileOutputStream("./copyLarge.zip");
        else if(size.equals(MEDIUM_FILE))
        	fOut = new FileOutputStream("./copyMedium.zip");
        else if(size.equals(SMALL_FILE))
        	fOut = new FileOutputStream("./copySmall.zip");
        // TODO: Change buffer size
        byte[] bytes = new byte[BUFFER_SIZE*1024];
       
        System.out.println("Starting to read file");
        int count;
        int i = 0;
        int acumulativeSize = 0;
        long initTime = System.currentTimeMillis();
        while ((count = in.read(bytes)) > 0) {
            fOut.write(bytes, 0, count);
            acumulativeSize += count;
            System.out.println(count+ " " + i++);
        }
        long totalTime = System.currentTimeMillis() - initTime;
        System.out.println("Ended reading file");
        System.out.println("Reading the file took " + totalTime + " ms");
       
        if(realSize == acumulativeSize) {
        	System.out.println("File received is complete");
        } else {
        	System.out.println("File received is incomplete, expected size is: " + realSize + " size received was: " + acumulativeSize);
        }
        
        sc.close();
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
	
	/* Main */
	public static void main(String args[]) throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.println("Escoger dirección IP del servidor:");
		String address = sc.nextLine();
		System.out.println("Escoger puerto:");
		int port = sc.nextInt();
		
		System.out.println("Choose file size: ");
		System.out.println("1. SMALL FILE");
		System.out.println("2. MEDIUM FILE");
		System.out.println("3. LARGE FILE");
		int option = -1;
		
		while(option <= 0 || option > 3) {
			System.out.println("Ingrese la opcion: ");
			option = sc.nextInt();
		}
		
		String size = SMALL_FILE;
		
		if(option == 2) {
			size = MEDIUM_FILE;
		} else if(option == 3) {
			size = LARGE_FILE;
		}
		
		System.out.println("Selected: " + size);
		
		sc.close();
		TCPClient tcpClient = new TCPClient(address, port);
		tcpClient.sendFileSize(size);
	}
}
