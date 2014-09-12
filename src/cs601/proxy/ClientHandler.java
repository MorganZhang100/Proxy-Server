import java.net.*;
import java.io.*;

public class ClientHandler implements Runnable {
	public static final int HTTP = 80;

	protected boolean debug = true;

	Socket clientSocket;

	public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        System.out.println("I'm created~~~~~~~~~~~~");
    }

    @Override
	public void run() {
		try {
			System.out.println("I'm running!!!!!!!!!");
			String processedRequestString = processRequest(clientSocket);
			if(processedRequestString.equals("")) {
				System.out.println("End, because no command");
				return;
			}
			String returnData = toHost(clientSocket, processedRequestString);
		}
		catch (IOException ioe) {
			ioe.printStackTrace(System.err);
		}
	}

	String processRequest(Socket clientSocket) throws IOException {
		InputStream in = clientSocket.getInputStream();
		DataInputStream din = new DataInputStream(in);
		String line = din.readLine();
		String firstLine = line;

		if(line.equals("")) {
			System.out.println("End, because no command");
			clientSocket.close();
			return "";
		}

		String processedLine = "";
		int content_length = 0;

		while (line!=null && line.length()>0) {

			String helpStringArray[] = line.split(":");
			if( helpStringArray[0].toLowerCase().equals("user-agent") || helpStringArray[0].equals("proxy-connection") || helpStringArray[0].equals("referer") ) {
			}
			else {
				processedLine = processedLine + line + "\n";
			}

			if ( line.toLowerCase().startsWith("content-length") ) {
				int colon = line.indexOf(":");
				String opnd = line.substring(colon+1);
				content_length = Integer.valueOf(opnd.trim());
			}

			line = din.readLine();
		}

		String postData = null;
		if ( firstLine.startsWith("POST") ) {
			postData = read(din, content_length);
			System.out.println(" ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ " + postData);
			processedLine = processedLine + "\r\n" +postData;
		}

		return processedLine;
	}

	String toHost(Socket clientSocket, String processedRequestString) throws IOException {
		System.out.println("[C inside]" );
		String lines[] = processedRequestString.split("\n");
		String firstLine = lines[0];

		System.out.println(" #####  " + firstLine + " **** " );

		String elements[] = firstLine.split(" ");
		String elements2[] = elements[1].split("//");
		String elements3[] = elements2[1].split("/");

		String hostUrl = elements3[0];

		Socket toHostSocket = new Socket(hostUrl, 80);
		OutputStream toHostOut = toHostSocket.getOutputStream();
		PrintStream toHostPOut = new PrintStream(toHostOut);
				
		processedRequestString = processedRequestString.replace("HTTP/1.1","HTTP/1.0");
		processedRequestString = processedRequestString.replace("http://" + hostUrl ,"");
		
		System.out.println("*\n*\n*\n" + processedRequestString + "*\n*\n*\n");
		toHostPOut.println(processedRequestString);

		InputStream toHostIn = toHostSocket.getInputStream();
		DataInputStream toHostDIn = new DataInputStream(toHostIn);

		byte readByte[] = new byte[8];

		String returnData = "";

		int timer = 0;

		OutputStream toClient = clientSocket.getOutputStream();
		DataOutputStream toClientDataStream = new DataOutputStream(toClient);

		if( clientSocket.isClosed() ) {
			System.out.println(" ################### socket closed");
		}
		else System.out.println(" ################### socket open!!!!!!!!!!!");
		
		String responseHead = "";

		while (toHostDIn.read(readByte)!=-1) {
			toClientDataStream.write(readByte);
		}

		//System.out.println("\nend print\n\n\n\n\n\n");

		toHostPOut.close();
		toHostDIn.close();

		toClientDataStream.close();

		toHostSocket.close();
		clientSocket.close();

		return returnData;
	}

	public static String read(DataInputStream in, int n) throws IOException {
		StringBuilder buf = new StringBuilder();
		int c = in.read();
//		System.out.println("read "+(char)c);
		int i = 1;
		while ( c!=-1 && i < n) {
			buf.append((char)c);
			c = in.read();
//			System.out.println("read "+(char)c);
			i++;
		}
//		System.out.println("Done");
		return buf.toString();
	}

}
