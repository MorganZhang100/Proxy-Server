import java.net.*;
import java.io.*;


public class ProxyServer {
	public static final boolean SingleThreaded = true;

	public static void main(String[] args) throws Exception {
		int portNumber = 8080;

		if( args.length > 0 ) portNumber = Integer.valueOf(args[0]);

		System.out.printf("\n\n\n\n\n\n aaa %d \n", portNumber);

		ServerSocket s = new ServerSocket(portNumber);

		while ( true ) {
			System.out.println("wait for connect");



			Socket channel = s.accept();

			System.out.println(" socket: " + channel );

			//Runnable handler = new ClientHandler(channel);
			ClientHandler handler = new ClientHandler(channel);

			if ( SingleThreaded ) {
				System.out.println(" before run");
				handler.run();
				System.out.println(" after run");
			}
			else {
				Thread t = new Thread(handler);
				t.start();
			}
		}
	}



}
