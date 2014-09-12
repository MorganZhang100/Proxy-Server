import java.net.*;
import java.io.*;


public class ProxyServer {
	public static final boolean SingleThreaded = false;

	public static void main(String[] args) throws Exception {
		int portNumber = 8080;

		if( args.length > 0 ) portNumber = Integer.valueOf(args[0]);
		ServerSocket s = new ServerSocket(portNumber);
		while ( true ) {
			Socket channel = s.accept();
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
