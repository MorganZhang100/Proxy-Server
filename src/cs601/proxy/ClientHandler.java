import java.net.*;
import java.io.*;

public class ClientHandler implements Runnable {
	public static final int HTTP = 80;

	protected boolean debug = true;

	Socket clientSocket;

	public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        System.out.println("I'm created~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    @Override
	public void run() {
		try {
			System.out.println("I'm running!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			String processedRequestString = processRequest(clientSocket);
			String returnData = toHost(clientSocket, processedRequestString);
			returnDataToBrowser(clientSocket, returnData);
		}
		catch (IOException ioe) {
			ioe.printStackTrace(System.err);
		}
	}

	void returnDataToBrowser(Socket clientSocket, String returnData) throws IOException {
		System.out.println("[E]" + clientSocket);

		OutputStream toClient = clientSocket.getOutputStream();
		PrintStream toClientPOut = new PrintStream(toClient);

		System.out.println("before r");
		System.out.println(returnData);
		System.out.println("after r");

		toClientPOut.println(returnData);
		toClient.close();
		System.out.println("[F]");
	}

	String processRequest(Socket clientSocket) throws IOException {
		System.out.println("[A inside] Start on port: " + clientSocket.getPort());
		//OutputStream out = clientSocket.getOutputStream();
		InputStream in = clientSocket.getInputStream();
		DataInputStream din = new DataInputStream(in);
		String line = din.readLine();

		String processedLine = "";

		while (!line.equals("")) {

			String helpStringArray[] = line.split(":");
			if( helpStringArray[0].equals("User-Agent") || helpStringArray[0].equals("Proxy-Connection") || helpStringArray[0].equals("Referer") ) {
				//System.out.print("****");
			}
			else {
				processedLine = processedLine + line + "\n";
			}
			System.out.println(line);
			//System.out.println("--");
			line = din.readLine();

			// if( line.equals("") ) System.out.println(" >>>>> should end here");
			// else System.out.println("<<< continue");
		}

		System.out.println("^^^^^^^^^ ^^^^^^");
		System.out.println("[B inside] closing port: " + clientSocket.getPort());

		//din.close();
		//clientSocket.close();

		//System.out.println(processedLine);

		return processedLine;
	}

	String toHost(Socket clientSocket, String processedRequestString) throws IOException {
		System.out.println("[C inside] Start on port: " + clientSocket.getPort());
		String lines[] = processedRequestString.split("\n");
		String firstLine = lines[0];

		System.out.println(" #####  " + firstLine + " **** " );

		String elements[] = firstLine.split(" ");
		String elements2[] = elements[1].split("//");
		String elements3[] = elements2[1].split("/");

		String hostUrl = elements3[0];

		System.out.println(":::::: " + hostUrl);

		Socket toHostSocket = new Socket(hostUrl, 80);
		OutputStream toHostOut = toHostSocket.getOutputStream();
		PrintStream toHostPOut = new PrintStream(toHostOut);
		
		toHostPOut.println(processedRequestString);
		

		InputStream toHostIn = toHostSocket.getInputStream();
		DataInputStream toHostDIn = new DataInputStream(toHostIn);
		String line = toHostDIn.readLine();

		String returnData = "";

		int timer = 0;

		while (true) {

			// String helpStringArray[] = line.split(":");
			// if( helpStringArray[0].equals("User-Agent") || helpStringArray[0].equals("Proxy-Connection") || helpStringArray[0].equals("Referer") ) {
			// 	System.out.print("****");
			// }
			// else {
			// 	processedLine = processedLine + "\n" + line;
			// }
			if( line.equals("")) timer++;
			if( line.equals("") && timer == 2 ) break;

			returnData = returnData + line + "\n";

			System.out.println("-=> " + line);

			line = toHostDIn.readLine();
		}
		System.out.println("end print\n\n\n\n\n\n");

		toHostPOut.close();
		toHostDIn.close();

		System.out.println("[D inside] closing port: " + toHostSocket.getPort());

		toHostSocket.close();

		return returnData;
	}


 //    forwardRemoteDataToBrowser()
	// getHeaders()
	// makeUpstreamRequest()
	// openUpstreamSocket()
	// process()
	// readLine()
	// run()
	// writeHeaders()



}
