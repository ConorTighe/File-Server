package ie.gmit.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ie.gmit.requests.Access;

public class PrintWorker extends Access{
	
	private static final long serialVersionUID = 1L;
	private final String message = "LIST FILES"; // message to communicate with server 

	String response;

	public PrintWorker() {
		// TODO Auto-generated constructor stub
	}

	public void run(){
		
		try {
			
			ObjectOutputStream out = new ObjectOutputStream(getSocket().getOutputStream());// output stream
			out.flush(); 
			ObjectInputStream in = new ObjectInputStream(getSocket().getInputStream()); // input stream
			
			System.out.println("Attemping to print file list..."); // inform user we are ready to print listing
			
			out.writeObject(message); //Serialise
			out.flush(); //Ensure all data sent by flushing buffers
			Thread.yield(); //Pause the current thread for a short time (not used much)
			
			//Deserialise / unmarshal response from server 
			setResponse((String) in.readObject()); //Deserialise
		//	getResponse();
			
			in.close();
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getResponse(){
		return response;
	}
	
	public void setResponse(String response){
		this.response = response;
	}

}
