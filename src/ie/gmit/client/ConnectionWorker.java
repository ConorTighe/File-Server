package ie.gmit.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ie.gmit.requests.Access;

public class ConnectionWorker extends Access {
	
	private static final long serialVersionUID = 1L;
	
	public void run(){
		
		try {
			String message = "Connection Successful!"; // send message to server to see if it will reach HTTPRequest
			ObjectOutputStream out = new ObjectOutputStream(getSocket().getOutputStream()); // output stream
			out.writeObject(message); // send message
			out.flush();
			out.close();
		
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
