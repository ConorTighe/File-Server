package ie.gmit.requests;

import java.io.Serializable;
import java.net.Socket;

public class Access implements Serializable, Runnable {
	
	/* Access and the classes that extact from it are serializable 
	 * so that the object can be easily saved to persistent 
	 * storage or streamed across a communication link.  */
	private static final long serialVersionUID = 1L;
	
	private Socket socket; 
	private String username;
	private String serverHost;
	private String downloadDir;
	private String serverPort;
	
	public Access() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	/*Getters and setter to pass information from XML to client to Server and ensure encapsulation */
	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getServerHost() {
		return serverHost;
	}
	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}
	public String getDownloadDir() {
		return downloadDir;
	}
	public void setDownloadDir(String downloadDir) {
		this.downloadDir = downloadDir;
	}
	public String getServerPort() {
		return serverPort;
	}
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	
	public String toString() {
		return  username + " will connect to " + serverHost + " on port " + serverPort + ", "
				+ "files will be downloaded to " + downloadDir + "\n";
	}

	@Override
	public void run() {
	
		// Workers will initilize run method
	}

}
